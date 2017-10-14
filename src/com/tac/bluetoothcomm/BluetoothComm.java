package com.tac.bluetoothcomm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;


import com.ronoid.bluetoothcomm.R;
import com.tac.db.DatabaseHelper;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint({"SdCardPath", "SimpleDateFormat"})
public class BluetoothComm extends Activity {
	// Debugging
	private static final String TAG = "BluetoothComm";
	private static final boolean D = true;
	//请求开启蓝牙的requestCode
	static final int REQUEST_ENABLE_BT = 1;
	//请求连接的requestCode
	static final int REQUEST_CONNECT_DEVICE = 2;
	//bluetoothCommService 传来的消息状态
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";
	private static final Context context = null;
	//蓝牙设备
	private BluetoothDevice device = null;

	private EditText txEdit;
	private EditText rxEdit;
	private EditText inputEdit;
	//连接的设备
	private TextView connectDevices;
	//发送按键
	private Button sendButton;
	//清空接收记录按键
	private Button clearRxButton;
	//清楚发送记录按键
	private Button clearTxButton;
	//断开连接按键
	private Button disconnectButton;
	private Button clearAll;
	//本地蓝牙适配器
	private BluetoothAdapter bluetooth;
	//创建一个蓝牙串口服务对象
	private BluetoothCommService mCommService = null;

	private StringBuffer mOutStringBuffer = new StringBuffer("");

	private String mConnectedDeviceName = null;
	// Array adapter for the conversation thread
	private ArrayAdapter<String> mConversationArrayAdapter;

	private char[] linedatachar1;
	private char[] linedatachar2;
	//private ArrayList<Integer> linedataint;
	private int count = 0;
	private int value_int = 0;
	private Cursor cursor;
	private Timer timer01;
	private TimerTask task01;
	private Handler handler01;
	private Timer timer02;
	private TimerTask task02;
	private Handler handler02;
	private GraphicalView Gview;
	//private double power_all_ch1=0;
	//private double power_all_ch2=0;
	private MediaPlayer mMediaPlayer = null;
	private boolean isenable = true;
	private boolean isstart1 = false;
	private boolean isstart2 = true;
	private boolean isopen = false;
	private boolean istiming = false;
	private ImageView logo;
	private static String test_name;
	private static String test_age;
	private static String test_sex;
	private String value;
	private double value_num;
	private String file_name = null;
	private FileOutputStream outStream;
	private OutputStreamWriter writer;
	private ArrayList<Integer> count2 = new ArrayList<Integer>();
	private TextView time;
	private TextView detected_num;
	private TextView sharp_num;
	private double time_stop = 0;
	private double time_start = 0;
	private long time_on_h = 0;
	private long time_on_m = 0;
	private long time_on_s = 0;
	private int detected_num_int = 0;
	private int sharp_num_int = 0;
	private Button back;

	/**
	 * Called when the activity is first created.
	 */
	@SuppressLint("SdCardPath")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		SysApplication.getInstance().addActivity(this);
		Intent intent = getIntent();
		test_name = intent.getStringExtra("name");
		test_age = intent.getStringExtra("age");
		test_sex = intent.getStringExtra("sex");
		value = intent.getStringExtra("value");
		value_int = Integer.valueOf(value).intValue();
		SimpleDateFormat sDateFormat = new SimpleDateFormat("MM月dd日HH时mm分");
		String date = sDateFormat.format(new java.util.Date());
		file_name = test_name + "_" + test_age + "_" + test_sex + "_" + value_int + "_" + date;
		double it = Double.valueOf(value).doubleValue();
		Log.e("value", (int) it + "");
		insertinfo(test_name, test_age, test_sex, (int) it);
		try {
			File file = new File("/sdcard/toe_walking");
			if (!file.exists()) {
				file.mkdir();
			}
			String name = "/sdcard/toe_walking/" + file_name + ".txt";
			if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
				outStream = new FileOutputStream(name, true);
				writer = new OutputStreamWriter(outStream, "UTF-8");
			} else {
				Toast.makeText(context, "sd卡不可用请检查sd卡的状态", Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("m", "file write error");
		}
		value_num = (it / 100);
		Log.e("Test_out_ch2", value_num + "");
		//获得控件
		//sendButton = (Button)findViewById(R.id.sendButton);
		//sendButton.setOnClickListener(new btnClickedListener());
		//clearRxButton = (Button)findViewById(R.id.clearRx);
		//clearRxButton.setOnClickListener(new btnClickedListener());
		//clearTxButton = (Button)findViewById(R.id.clearTx);
		//clearTxButton.setOnClickListener(new btnClickedListener());
		time = (TextView) findViewById(R.id.detected_time);
		detected_num = (TextView) findViewById(R.id.detected_num);
		sharp_num = (TextView) findViewById(R.id.sharp_num);
		back = (Button) findViewById(R.id.backButton);
		back.setOnClickListener(new back_to_welcome());
		disconnectButton = (Button) findViewById(R.id.disconnectButton);
		disconnectButton.setOnClickListener(new btnClickedListener());
		//clearAll = (Button)findViewById(R.id.clearALL);
		//clearAll.setOnClickListener(new btnClickedListener());
		//txEdit = (EditText)findViewById(R.id.tx_history);
		//rxEdit = (EditText)findViewById(R.id.rx_history);
		//inputEdit = (EditText)findViewById(R.id.inputEdit);
		connectDevices = (TextView) findViewById(R.id.connected_device);
		logo = (ImageView) findViewById(R.id.logo);
		logo.setBackgroundResource(R.drawable.title);
		//AppPublic.linedataint=new ArrayList<Integer>();
		//sv=(LineSurfaceView)findViewById(R.id.sv);
		AppPublic.power_all_ch1_1 = 0;
		AppPublic.power_all_ch2_1 = 0;
		AppPublic.power_all_ch1_2 = 0;
		AppPublic.power_all_ch2_2 = 0;
		timer01 = new Timer();
		handler01 = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case 1: {
						DecimalFormat nf = new DecimalFormat("00");
						DecimalFormat nf1 = new DecimalFormat("00000");
						if (istiming) {
							time_stop = SystemClock.elapsedRealtime();
							//time_on_h=(long)(time_stop-time_start)/3600000;
							time_on_m = (long) (time_stop - time_start) / 60000;
							time_on_s = (long) (time_stop - time_start - 60000 * time_on_m) / 1000;
							;
						}
						;
						time.setText("" + nf.format(time_on_m) + ":" + nf.format(time_on_s));
						detected_num.setText("" + nf1.format(detected_num_int) + "次");
						sharp_num.setText("" + nf1.format(sharp_num_int) + "次");
						XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
						// 2,进行显示
						XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
						// 2.1, 构建数据
						XYSeries series1 = new XYSeries("前足");
						XYSeries series2 = new XYSeries("足跟");
						XYSeriesRenderer xyRenderer1 = new XYSeriesRenderer();
						XYSeriesRenderer xyRenderer2 = new XYSeriesRenderer();
						LinearLayout layout;
						renderer.removeAllRenderers();
						int n1 = 0;
						double temp = 0;
						if (AppPublic.linedataint_ch1_1.size() > 0 && AppPublic.linedataint_ch1_1.size() <= 400) {
							for (int i = 0; i < AppPublic.linedataint_ch1_1.size(); i++) {
								//n1=AppPublic.linedataint_ch1_1.get(i)*100+AppPublic.linedataint_ch1_2.get(i);
								//temp=3.412*Math.exp((0.0016*n1));
								series1.add(i, AppPublic.power_ch1.get(i));
							}
							;
						} else if (AppPublic.linedataint_ch1_1.size() > 400) {
							for (int i = AppPublic.linedataint_ch1_1.size() - 400; i < AppPublic.linedataint_ch1_1.size(); i++) {
								//n1=AppPublic.linedataint_ch1_1.get(i)*100+AppPublic.linedataint_ch1_2.get(i);
								//temp=3.412*Math.exp((0.0016*n1));
								series1.add((i - AppPublic.linedataint_ch1_1.size() + 400), AppPublic.power_ch1.get(i));
							}
						}
						;
						if (AppPublic.linedataint_ch2_1.size() > 0 && AppPublic.linedataint_ch2_1.size() <= 400) {
							for (int i = 0; i < AppPublic.linedataint_ch2_1.size(); i++) {
								//n1=AppPublic.linedataint_ch2_1.get(i)*100+AppPublic.linedataint_ch2_2.get(i);
								//temp=3.412*Math.exp((0.0016*n1));
								series2.add(i, AppPublic.power_ch2.get(i));
//                    		AppPublic.power_ch2.add(temp);
//                    		Log.e("Test_out_ch2", AppPublic.power_ch2.get(i)+"");
							}
						} else if (AppPublic.linedataint_ch2_1.size() > 400) {
							for (int i = AppPublic.linedataint_ch2_1.size() - 400; i < AppPublic.linedataint_ch2_1.size(); i++) {
								//n1=AppPublic.linedataint_ch2_1.get(i)*100+AppPublic.linedataint_ch2_2.get(i);
								//temp=3.412*Math.exp((0.0016*n1));
								series2.add((i - AppPublic.linedataint_ch2_1.size() + 400), AppPublic.power_ch2.get(i));
//                    			AppPublic.power_ch2.add(temp);
//                    			Log.e("Test_out_ch2", AppPublic.power_ch2.get(i)+"");
							}
						}
						;
						//System.gc();
						dataset.addSeries(series1);
						dataset.addSeries(series2);
						// 3.1设置颜色
						// 1, 构造显示用渲染图
						xyRenderer1.setColor(Color.GREEN);
						// 3.2设置点的样式
						xyRenderer1.setPointStyle(PointStyle.POINT);
						// 3.3, 将要绘制的点添加到坐标绘制中
						//SimpleSeriesRenderer seriesrenderer =renderer.getSeriesRendererAt(1);
						xyRenderer1.setDisplayChartValues(false);
						xyRenderer1.setLineWidth(2);
						xyRenderer1.setDisplayChartValuesDistance(1);//设置两个折点间的距离，使得所有点的值都能显示
						xyRenderer2.setColor(Color.RED);
						// 3.2设置点的样式
						xyRenderer2.setPointStyle(PointStyle.POINT);
						// 3.3, 将要绘制的点添加到坐标绘制中
						//SimpleSeriesRenderer seriesrenderer =renderer.getSeriesRendererAt(1);
						xyRenderer2.setDisplayChartValues(false);
						xyRenderer2.setLineWidth(2);
						xyRenderer2.setDisplayChartValuesDistance(1);//设置两个折点间的距离，使得所有点的值都能显示
						renderer.setYAxisMin(0);
						renderer.setYAxisMax(250);
						renderer.setXAxisMin(0);
						renderer.setXAxisMax(400);
						renderer.setShowLabels(true);
						renderer.setYLabels(10);
						//renderer.setXLabels(100);
						renderer.setLabelsColor(Color.WHITE);
						renderer.setXTitle("时间");
						renderer.setYTitle("压强(kPa)");
						renderer.setAxisTitleTextSize(16);
						renderer.setLabelsTextSize(15);
						renderer.setAxisTitleTextSize(16);
						renderer.setLegendTextSize(15);
						renderer.setShowGrid(true);
						renderer.setFitLegend(true);
						//renderer.setZoomLimits(new double[] { 0, 200, 0, 100 });
						//renderer.setPanLimits(new double[] { 0, 200, 0, 100 });
						renderer.addSeriesRenderer(xyRenderer1);
						renderer.addSeriesRenderer(xyRenderer2);
						renderer.setBackgroundColor(Color.BLACK);
						Gview = ChartFactory.getCubeLineChartView(getApplicationContext(), dataset, renderer, 0.3f);
						Gview.setBackgroundColor(Color.BLACK);
						//移除原有的LinearLayout中的视图控件
						layout = (LinearLayout) findViewById(R.id.lineChar);
						layout.removeAllViewsInLayout();
						layout.setBackgroundResource(R.drawable.lightsteelblue);
						layout.addView(Gview);

						System.gc();
					}
					;
					break;
				}
				super.handleMessage(msg);
			}

		};
		task01 = new TimerTask() {

			public void run() {
				Message message = new Message();
				message.what = 1;
				handler01.sendMessage(message);
			}
		};
		timer01.schedule(task01, 50, 300);
		//获得本地蓝牙设备
		bluetooth = BluetoothAdapter.getDefaultAdapter();
		if (bluetooth == null) {//设备没有蓝牙设备
			Toast.makeText(this, "没有找到蓝牙适配器", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (!bluetooth.isEnabled()) {
			//请求打开蓝牙设备
			Toast.makeText(this, "蓝牙未打开，请打开蓝牙", Toast.LENGTH_LONG).show();
			//    		Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//    		startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		} else {
			if (mCommService == null) {
				mCommService = new BluetoothCommService(this, mHandler);
			}
		}
	}

	@Override
	protected synchronized void onResume() {
		super.onResume();
		if (mCommService != null) {
			// Only if the state is STATE_NONE, do we know that we haven't started already
			if (mCommService.getState() == BluetoothCommService.STATE_NONE) {
				// Start the Bluetooth services，开启监听线程
				mCommService.start();
			}
		}
	}

	@Override
	public synchronized void onPause() {
		super.onPause();
		if (D) Log.e(TAG, "- ON PAUSE -");
	}

	@Override
	public void onStop() {
		super.onStop();
		if (bluetooth != null) {
			//bluetooth.disable();
			//SysApplication.getInstance().exit();
		}
		if (D) Log.e(TAG, "-- ON STOP --");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Stop the Bluetooth chat services
		if (mCommService != null) mCommService.stop();
		if (D) Log.e(TAG, "--- ON DESTROY ---");
		//SysApplication.getInstance().exit();
		try {
			writer.close();
			outStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//记得关闭   	    
	}

	/**
	 * onActivityResult方法，当启动startActivityForResult返回之后调用，
	 * 根据用户的操作来执行相应的操作
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_ENABLE_BT:
				if (resultCode == Activity.RESULT_OK) {
					if (D) Log.d(TAG, "打开蓝牙设备");
					Toast.makeText(this, "成功打开蓝牙", Toast.LENGTH_SHORT).show();
				} else {
					if (D) Log.d(TAG, "不允许打开蓝牙设备");
					Toast.makeText(this, "不能打开蓝牙,程序即将关闭", Toast.LENGTH_SHORT).show();
					finish();//用户不打开设备，程序结束
				}
				break;
			case REQUEST_CONNECT_DEVICE:
				// When DeviceListActivity returns with a device to connect
				if (resultCode == Activity.RESULT_OK) {//用户选择连接的设备
					// Get the device MAC address
					String address = data.getExtras()
											 .getString(ScanDeviceActivity.EXTRA_DEVICE_ADDRESS);
					// Get the BLuetoothDevice object
					device = bluetooth.getRemoteDevice(address);
					//尝试连接设备
					mCommService.connect(device);
				}
				break;
		}
		return;
	}

	private class btnClickedListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.disconnectButton) {
				if (mCommService != null) {
					mCommService.stop();
					//timer01.cancel();
				}
			}
//			if(v.getId() == R.id.sendButton) {
//				if(device == null){
//					Toast.makeText(BluetoothComm.this, "请连接设备！", Toast.LENGTH_LONG).show();
//					inputEdit.setText("");
//				}
//				else {
//					String txString = inputEdit.getText()+"";
//					//inputEdit.setText("");
//					txEdit.append(txString);
//					sendMessage(txString);
//				}
//			} else if(v.getId() == R.id.clearRx){
//				rxEdit.setText("");
//			} else if(v.getId() == R.id.clearTx){
//				txEdit.setText("");
//			} else if(v.getId() == R.id.disconnectButton){
//				if(mCommService!=null){
//					mCommService.stop();
//					timer01.cancel();
//				}
//			} else if(v.getId() ==R.id.clearALL){
//				inputEdit.setText("");
//			}
		}
	}

	private void ensureDiscoverable() {
		if (bluetooth.getScanMode() !=
					BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			//最长可见时间为300s
			discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
	}

	//创建菜单选项
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
		return true;
	}

	//菜单项被点击
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
			case R.id.scan:
				// Launch the ScanDeviceActivity to see devices and do scan
				Intent serverIntent = new Intent(this, ScanDeviceActivity.class);
				startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
				return true;
			case R.id.discoverable:
				ensureDiscoverable();
				return true;
			case R.id.about:
				Intent intent = new Intent(BluetoothComm.this, AboutActivity.class);
				startActivity(intent);
				return true;
			case R.id.exit:
				if (mCommService != null) {
					mCommService.stop();
				}
				if (bluetooth == null) {
					bluetooth.enable();
				}
//	        	try {
//	        		DecimalFormat nf = new DecimalFormat("00");
//	                DecimalFormat nf1 = new DecimalFormat("00000");
//					writer.write("检测时间："+nf.format(time_on_m)+":"+nf.format(time_on_s)+"检测次数:"+nf1.format(detected_num_int)+"次"+"尖足次数:"+nf1.format(sharp_num_int)+"次");						
//	        	  	writer.flush();
//	        	  	//count=count+1;
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				};
				timer01.cancel();
				SysApplication.getInstance().exit();
				finish();
				return true;
		}
		return false;
	}

	/**
	 * Sends a message.
	 *
	 * @param message A string of text to send.
	 */
	private void sendMessage(String message) {
		//没有连接设备，不能发送
		if (mCommService.getState() != BluetoothCommService.STATE_CONNECTED) {
			Toast.makeText(this, R.string.nodevice, Toast.LENGTH_SHORT).show();
			return;
		}
		// Check that there's actually something to send
		if (message.length() > 0) {
			// Get the message bytes and tell the BluetoothChatService to write
			byte[] send = message.getBytes();
			mCommService.write(send);

			// Reset out string buffer to zero and clear the edit text field
			mOutStringBuffer.setLength(0);
		}
	}

	// The Handler that gets information back from the BluetoothChatService
	private final Handler mHandler = new Handler() {
		String str;

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MESSAGE_STATE_CHANGE:
					if (D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
					switch (msg.arg1) {
						case BluetoothCommService.STATE_CONNECTED:
							connectDevices.setText(R.string.title_connected_to);
							connectDevices.append(mConnectedDeviceName);
							time_start = SystemClock.elapsedRealtime();
							istiming = true;
							//    mConversationArrayAdapter.clear();
							break;
						case BluetoothCommService.STATE_CONNECTING:
							connectDevices.setText(R.string.title_connecting);
							break;
						case BluetoothCommService.STATE_LISTEN:
						case BluetoothCommService.STATE_NONE:
							connectDevices.setText(R.string.title_not_connected);
							istiming = false;
							break;
					}
					break;
				case MESSAGE_WRITE:
					byte[] writeBuf = (byte[]) msg.obj;
					// construct a string from the buffer
					//  String writeMessage = new String(writeBuf);
					//   mConversationArrayAdapter.add("Me:  " + writeMessage);
					break;
				case MESSAGE_READ:
					byte[] readBuf = (byte[]) msg.obj;
					// construct a string from the valid bytes in the buffer
					String readMessage = new String(readBuf, 0, msg.arg1);
					StringBuilder stringBuilder = new StringBuilder("");
					if (msg.arg1 > 0) {
						str = bytesToHexString(readBuf);
						//Log.e(TAG, str);
					}
					//rxEdit.append(str);
					//             mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
					if (AppPublic.power_ch1.size() > 50000) {
						AppPublic.linedataint_ch1_1 = new ArrayList<Integer>();
						AppPublic.linedataint_ch1_2 = new ArrayList<Integer>();
						AppPublic.linedataint_ch2_1 = new ArrayList<Integer>();
						AppPublic.linedataint_ch2_2 = new ArrayList<Integer>();
						AppPublic.countarray = new ArrayList<Integer>();
						AppPublic.power_ch1 = new ArrayList<Double>();
						AppPublic.power_ch2 = new ArrayList<Double>();
						AppPublic.difference_ch1 = new ArrayList<Double>();
						AppPublic.difference_ch2 = new ArrayList<Double>();
					}
					linedatachar1 = str.toCharArray();
					//System.gc();
					for (int j = 0; j < linedatachar1.length - 8; j++) {
						String stemp1 = String.valueOf(linedatachar1[j]);
						String stemp2 = String.valueOf(linedatachar1[j + 1]);
						String stemp7 = String.valueOf(linedatachar1[j + 6]);
						String stemp8 = String.valueOf(linedatachar1[j + 7]);
						double temp1 = 0;
						double temp2 = 0;
						if (stemp1.equals("6") && stemp2.equals("4") && stemp7.equals("F") && stemp8.equals("A")) {
							//发送过来的第一个值
							int temp_int1 = Integer.parseInt(String.valueOf(linedatachar1[j + 2]) + String.valueOf(linedatachar1[j + 3]), 16);
							//发送过来的第二个值
							int temp_int2 = Integer.parseInt(String.valueOf(linedatachar1[j + 4]) + String.valueOf(linedatachar1[j + 5]), 16);
							try {
								writer.write(1 + String.valueOf(linedatachar1[j + 2]) + String.valueOf(linedatachar1[j + 3]) + String.valueOf(linedatachar1[j + 4]) + String.valueOf(linedatachar1[j + 5]) + " ");
								writer.flush();
								count = count + 1;
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							;
							//System.gc();
							//将接收到的两个值放在list中
							AppPublic.linedataint_ch1_1.add(temp_int1);
							AppPublic.linedataint_ch1_2.add(temp_int2);

							//对接收到的两个值进行转化，放在list中
							int n1 = temp_int1 * 100 + temp_int2;
							temp1 = 3.412 * Math.exp((0.0016 * n1));
							AppPublic.power_ch1.add(temp1);

							//如果list中有值
							if (AppPublic.power_ch1.size() > 1) {
								//int n2=AppPublic.linedataint_ch1_1.get(i-1)*100+AppPublic.linedataint_ch1_2.get(i-1);
								//该list的最后一个元素减去前一个元素的值，又放在了list中
								//前足传感器的值和上一个值的差值
								AppPublic.difference_ch1.add(temp1 - AppPublic.power_ch1.get(AppPublic.power_ch1.size() - 2));
								if (AppPublic.difference_ch1.size() > 1) {
									if (AppPublic.difference_ch1.get(AppPublic.difference_ch1.size() - 2) < -2 && AppPublic.difference_ch1.get(AppPublic.difference_ch1.size() - 1) > -2 && temp1 < 10 && count > 18) {
										detected_num_int = detected_num_int + 1;
										if (isstart1 == false) {
											isstart1 = true;
											AppPublic.power_all_ch1_1 = 0;
											AppPublic.power_all_ch2_1 = 0;
										} else if (isstart1 == true) {
											Log.e("Warning", "power_all_ch1__1" + "__" + AppPublic.power_all_ch1_1);
											Log.e("Warning", "power_all_ch2__1" + "__" + AppPublic.power_all_ch2_1);
											//boolean judge=(boolean)(((AppPublic.power_all_ch2_1/(AppPublic.power_all_ch1_1+AppPublic.power_all_ch2_1)))<(30/100));
											if ((((AppPublic.power_all_ch2_1 / (AppPublic.power_all_ch1_1 + AppPublic.power_all_ch2_1))) < (value_num))) {
												mMediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.warning);
												mMediaPlayer.start();
												count = 0;
												sharp_num_int = sharp_num_int + 1;
												//isenable=false;
												Log.e("Warning", "mediaplay");
											}
											;
											isstart1 = false;
											AppPublic.power_all_ch1_1 = 0;
											AppPublic.power_all_ch2_1 = 0;
										}
										;
										if (isstart2 == false) {
											isstart2 = true;
											AppPublic.power_all_ch1_2 = 0;
											AppPublic.power_all_ch2_2 = 0;
										} else if (isstart2 == true) {
											Log.e("Warning", "power_all_ch1__2" + "__" + AppPublic.power_all_ch1_2);
											Log.e("Warning", "power_all_ch2__2" + "__" + AppPublic.power_all_ch2_2);
											//boolean judge=(boolean)(((AppPublic.power_all_ch2_2/(AppPublic.power_all_ch1_2+AppPublic.power_all_ch2_2)))<(30/100));
											if ((((AppPublic.power_all_ch2_2 / (AppPublic.power_all_ch1_2 + AppPublic.power_all_ch2_2))) < (value_num))) {
												mMediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.warning);
												mMediaPlayer.start();
												count = 0;
												sharp_num_int = sharp_num_int + 1;
												//isenable=false;
												Log.e("Warning", "mediaplay");
											}
											;
											isstart2 = false;
											AppPublic.power_all_ch1_2 = 0;
											AppPublic.power_all_ch2_2 = 0;
										}
									}
								}
								//System.gc();
							}
						} else if (stemp1.equals("6") && stemp2.equals("5") && stemp7.equals("F") && stemp8.equals("A")) {
							int temp_int1 = Integer.parseInt(String.valueOf(linedatachar1[j + 2]) + String.valueOf(linedatachar1[j + 3]), 16);
							int temp_int2 = Integer.parseInt(String.valueOf(linedatachar1[j + 4]) + String.valueOf(linedatachar1[j + 5]), 16);
							try {
								writer.write(2 + String.valueOf(linedatachar1[j + 2]) + String.valueOf(linedatachar1[j + 3]) + String.valueOf(linedatachar1[j + 4]) + String.valueOf(linedatachar1[j + 5]) + " ");
								writer.flush();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							;
							//System.gc();
							AppPublic.linedataint_ch2_1.add(temp_int1);
							AppPublic.linedataint_ch2_2.add(temp_int2);
							int n1 = temp_int1 * 100 + temp_int2;
							temp2 = 3.412 * Math.exp((0.0016 * n1));
							AppPublic.power_ch2.add(temp2);
							//Log.e("power_ch2", temp+"");
							if (AppPublic.power_ch2.size() > 1) {
								//int n2=AppPublic.linedataint_ch1_1.get(i-1)*100+AppPublic.linedataint_ch1_2.get(i-1);
								AppPublic.difference_ch2.add(temp2 - AppPublic.power_ch2.get(AppPublic.power_ch2.size() - 2));
								//System.gc();
							}
						}
						if (isstart1) {
							AppPublic.power_all_ch1_1 = AppPublic.power_all_ch1_1 + temp1;
							AppPublic.power_all_ch2_1 = AppPublic.power_all_ch2_1 + temp2;
						}
						if (isstart2) {
							AppPublic.power_all_ch1_2 = AppPublic.power_all_ch1_2 + temp1;
							AppPublic.power_all_ch2_2 = AppPublic.power_all_ch2_2 + temp2;
						}
					}
					//System.gc();
					break;
				case MESSAGE_DEVICE_NAME:
					// save the connected device's name
					mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
					Toast.makeText(getApplicationContext(), "Connected to "
																	+ mConnectedDeviceName, Toast.LENGTH_SHORT).show();
					break;
				case MESSAGE_TOAST:
					Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
							Toast.LENGTH_SHORT).show();
					break;
			}
		}
	};

	public static String bytesToHexString(byte[] bytes) {
		String result = "";
		char temp[];
		for (int i = 0; i < bytes.length; i++) {
			String hexString = Integer.toHexString(bytes[i] & 0xFF);
			if (hexString.length() == 1) {
				hexString = '0' + hexString;
			}
			result += hexString.toUpperCase();
		}
		return result;
	}

	@SuppressLint("SimpleDateFormat")
	private void insertinfo(String name, String age, String sex, int value) {
		//DecimalFormat nf = new DecimalFormat("0.0000");
		//生成ContentValues对象
		ContentValues values = new ContentValues();
		//想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致
		DatabaseHelper dbHelper = new DatabaseHelper(BluetoothComm.this, "toe_walking.db");
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		cursor = db.query("user", null, null, null, null, null, null, null);
		//SimpleDateFormat sDateFormat = new SimpleDateFormat("MM月dd日hh时mm分");      
		//String date = sDateFormat.format(new java.util.Date());
		//String text =(str);
		values.put("id", cursor.getCount() + 1);
		values.put("name", name);
		values.put("age", age);
		values.put("sex", sex);
		values.put("value", value);
		//values.put("left",n);		
		db = dbHelper.getWritableDatabase();
		//调用insert方法，就可以将数据插入到数据库当中
		db.insert("user", null, values);
		db.close();
	}

	@SuppressLint("SimpleDateFormat")
	private void R_insertData(int n) {
		DecimalFormat nf = new DecimalFormat("0.0000");
		//生成ContentValues对象
		ContentValues values = new ContentValues();
		//想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致
		DatabaseHelper dbHelper = new DatabaseHelper(BluetoothComm.this, "toe_walking.db");
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		cursor = db.query("user", null, null, null, null, null, null, null);
		SimpleDateFormat sDateFormat = new SimpleDateFormat("MM月dd日hh时mm分");
		String date = sDateFormat.format(new java.util.Date());
		//String text =(str);
		values.put("id", cursor.getCount() + 1);
		values.put("right", n);
		db = dbHelper.getWritableDatabase();
		//调用insert方法，就可以将数据插入到数据库当中
		db.insert("user", null, values);
		db.close();

	}

	public static void put(String s, String file_name) {
		try {
			File file = new File("/sdcard/toe_walking");
			if (!file.exists()) {
				file.mkdir();
			}
			String name = "/sdcard/toe_walking/" + file_name;
			if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
				FileOutputStream outStream = new FileOutputStream(name, true);
				OutputStreamWriter writer = new OutputStreamWriter(outStream, "UTF-8");
				writer.write(s);
				writer.write(" ");
				writer.flush();
				writer.close();//记得关闭
				outStream.close();
			} else {
				Toast.makeText(context, "sd卡不可用请检查sd卡的状态", Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("m", "file write error");
		}
	}

	class back_to_welcome implements OnClickListener {
		@Override
		public void onClick(View v) {
			back();
		}
	}

	@Override
	public void onBackPressed() {

		back();

	}

	;

	public void onHomePressed() {
		dialog();
	}

	;

	protected void dialog() {
		AlertDialog.Builder builder = new Builder(BluetoothComm.this);
		builder.setMessage("确定要退出吗?");
		builder.setTitle("提示");
		builder.setPositiveButton("确认",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						android.os.Process.killProcess(android.os.Process.myPid());
					}
				});
		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	protected void back() {
		AlertDialog.Builder builder = new Builder(BluetoothComm.this);
		builder.setMessage("确定要返回吗?");
		builder.setTitle("提示");
		builder.setPositiveButton("确认",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent = new Intent();
						intent.setClass(BluetoothComm.this, Toe_Walking_Welcome.class);
						BluetoothComm.this.startActivity(intent);
						if (mCommService != null) {
							mCommService.stop();

						}
						;
//    			try {
//    	    		DecimalFormat nf = new DecimalFormat("00");
//    	            DecimalFormat nf1 = new DecimalFormat("00000");
//    				writer.write("检测时间："+nf.format(time_on_m)+":"+nf.format(time_on_s)+"检测次数:"+nf1.format(detected_num_int)+"次"+"尖足次数:"+nf1.format(sharp_num_int)+"次");						
//    	    	  	writer.flush();
//    	    	  	//count=count+1;
//    			} catch (IOException e) {
//    				// TODO Auto-generated catch block
//    				e.printStackTrace();
//    			};    
						finish();
					}
				});
		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}
}