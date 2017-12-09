package com.tac.bluetoothcomm;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.os.*;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import com.ronoid.bluetoothcomm.R;
import com.tac.bluetoothcomm.leftright.DeviceListActivity;
import com.tac.bluetoothcomm.leftright.DeviceListActivity2;
import com.tac.db.DatabaseHelper;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;

@SuppressLint({"SdCardPath", "SimpleDateFormat"})
public class BluetoothComm_leftRight_t extends Activity {
	// Debugging
	private static final String TAG = "BluetoothComm_leftright_t";
	private static final boolean D = true;
	//������������requestCode
	static final int REQUEST_ENABLE_BT = 1;
	//�������ӵ�requestCode
	static final int REQUEST_CONNECT_DEVICE = 2;
	//bluetoothCommService ��������Ϣ״̬
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";
	private static final Context context = null;
	//�����豸
	private BluetoothDevice device01 = null;
	private BluetoothDevice device02 = null;

	private EditText txEdit;
	private EditText rxEdit;
	private EditText inputEdit;
	//���ӵ��豸
	private TextView connectDevices;
	private TextView connectDevices02;
	//���Ͱ���
	private Button sendButton;
	//��ս��ռ�¼����
	private Button clearRxButton;
	//������ͼ�¼����
	private Button clearTxButton;
	//�Ͽ����Ӱ���
	private Button disconnectButton;
	private Button clearAll;
	//��������������
	private BluetoothAdapter bluetooth;
	//����һ���������ڷ������
	private BluetoothCommService_left mCommService_left = null;
	private BluetoothCommService_right mCommService_right = null;
	private ArrayBlockingQueue<String> mCommService_left_queue = new ArrayBlockingQueue<String>(120);
	private ArrayBlockingQueue<String> mCommService_right_queue = new ArrayBlockingQueue<String>(120);
	private boolean isLeftGetData = false;
	private boolean isRightGetData = false;
	private final static int R0 = 2000;

	private StringBuffer mOutStringBuffer = new StringBuffer("");

	private String mConnectedDeviceName = null;
	// Array adapter for the conversation thread
	private ArrayAdapter<String> mConversationArrayAdapter;

	private char[] linedatachar1;
	private char[] linedatachar2;
	//private ArrayList<Integer> linedataint;
	private int count = 0;
//	private int value_int = 0;
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
	private boolean istiming_left = false;
	private boolean istiming_right = false;
	/** ��ʶ��һ·�����Ƿ����ӳɹ� */
	private boolean isBlu01 = false;
	/** ��ʶ�ڶ�·�����Ƿ����ӳɹ� */
	private boolean isBlu02 = false;
	private ImageView logo;
	private static String test_name;
	private static String test_age;
	private static String test_sex;
	private String value;
//	private double value_num;
	private double smaller;
	private double bigger;
	private String file_name = null;
	private FileOutputStream outStream_left;
	private FileOutputStream outStream_right;
	private OutputStreamWriter writer_left;
	private OutputStreamWriter writer_right;
	private ArrayList<Integer> count2 = new ArrayList<Integer>();
	private TextView time;
	private TextView detected_num;
//	private TextView sharp_num;
	private TextView left_num_tv;
	private TextView right_num_tv;
	private double time_stop = 0;
	private double time_start = 0;
	private long time_on_h = 0;
	private long time_on_m = 0;
	private long time_on_s = 0;
	private int detected_num_int = 0;
//	private int sharp_num_int = 0;
	private int left_num_int = 0;
//	private int right_num_int = 0;
	private Button back;

	public static final int ONE = 0xa21;
	public static final int TWO = 0xa22;
	public static final int THREE = 0xa23;
	public static final int FOUR = 0xa24;
	public static final int FIVE = 0xa25;

	private Handler returnMessage=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			// TODO: Implement this method

			int device = msg.what;
			String address =String.valueOf((String) msg.obj);
			super.handleMessage(msg);
			switch(device){
				case ONE:
					Log.i("devicelist","�յ��豸1����ֵ");
					Log.i("��ַ",address);
					// Get the BLuetoothDevice object
					BluetoothDevice device1 = bluetooth.getRemoteDevice(address);
					// Attempt to connect to the device
					mCommService_left.connect(device1);
					break;
				case TWO:
					// Get the BLuetoothDevice object
					BluetoothDevice device2 = bluetooth.getRemoteDevice(address);
					// Attempt to connect to the device
					mCommService_right.connect(device2);
					break;

				default:

					break;
			}
		}

	};

	/**
	 * Called when the activity is first created.
	 */
	@SuppressLint("SdCardPath")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_leftright_t);
		DeviceListActivity.DoMsg(returnMessage);
		DeviceListActivity2.DoMsg(returnMessage);
		SysApplication.getInstance().addActivity(this);
		Intent intent = getIntent();
		test_name = intent.getStringExtra("name");
		Log.i("name", "BluetoothComm_sharp : " + test_name);
		test_age = intent.getStringExtra("age");
		test_sex = intent.getStringExtra("sex");
		value = intent.getStringExtra("value");
		Log.e("value", value);
		String[] valueSplit = value.split(",");
		smaller = Double.valueOf(valueSplit[0]);
		bigger = Double.valueOf(valueSplit[1]);
		insertinfo(test_name, test_age, test_sex, 0);
		SimpleDateFormat sDateFormat = new SimpleDateFormat("MM��dd��HHʱmm��");
		String date = sDateFormat.format(new java.util.Date());
		file_name = test_name + "_" + test_age + "_" + test_sex + "_" + value + "_" + date;
		try {
			File file = new File("/sdcard/toe_walking");
			if (!file.exists()) {
				file.mkdir();
			}
			String name_left = "/sdcard/toe_walking/" + file_name + "_left_t.txt";
			String name_right = "/sdcard/toe_walking/" + file_name + "_right_t.txt";
			if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
				outStream_left = new FileOutputStream(name_left, true);
				writer_left = new OutputStreamWriter(outStream_left, "UTF-8");

				outStream_right = new FileOutputStream(name_right, true);
				writer_right = new OutputStreamWriter(outStream_right, "UTF-8");
			} else {
				Toast.makeText(context, "sd������������sd����״̬", Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("m", "file write error");
		}
//		value_num = (it / 100);
//		value_num = 0.3;

//		Log.e("Test_out_ch2", value_num + "");
		//��ÿؼ�
		time = (TextView) findViewById(R.id.detected_time_leftright_t);
		detected_num = (TextView) findViewById(R.id.detected_num_leftright_t);
		left_num_tv = (TextView) findViewById(R.id.left_num_t);
		right_num_tv = (TextView) findViewById(R.id.right_num_t);
		back = (Button) findViewById(R.id.backButton_leftright_t);
		back.setOnClickListener(new back_to_welcome());
		disconnectButton = (Button) findViewById(R.id.disconnectButton_leftright_t);
		disconnectButton.setOnClickListener(new btnClickedListener());
		connectDevices = (TextView) findViewById(R.id.connected_device_leftright_t);
		connectDevices02 = (TextView) findViewById(R.id.connected_device_leftright02_t);
		logo = (ImageView) findViewById(R.id.logo_leftright_t);
		logo.setBackgroundResource(R.drawable.title02);
		AppPublicLeftRight.power_all_ch1_1 = 0;
		AppPublicLeftRight.power_all_ch2_1 = 0;
		AppPublicLeftRight.power_all_ch1_2 = 0;
		AppPublicLeftRight.power_all_ch2_2 = 0;
		/** ���ڸ��½���Ķ�ʱ�� */
		timer01 = new Timer();
		/** ���½���Ĵ����߼�����ʱ���ᶨʱ���� */
		handler01 = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case 1: {
//						if (!isRightGetData || !isLeftGetData) {
//							return;
//						}
						DecimalFormat nf = new DecimalFormat("00");
						DecimalFormat nf1 = new DecimalFormat("00000");
						if (istiming_left && istiming_right) {
							time_stop = SystemClock.elapsedRealtime();
							//time_on_h=(long)(time_stop-time_start)/3600000;
							time_on_m = (long) (time_stop - time_start) / 60000;
							time_on_s = (long) (time_stop - time_start - 60000 * time_on_m) / 1000;
						}
						time.setText("" + nf.format(time_on_m) + ":" + nf.format(time_on_s));
						detected_num.setText("" + nf1.format(detected_num_int) + "��");
//						sharp_num.setText("" + nf1.format(sharp_num_int) + "��");
						left_num_tv.setText("" + nf1.format(left_num_int) + "��");
//						right_num_tv.setText("" + nf1.format(right_num_int) + "��");
						XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
						// 2,������ʾ
						XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
						// 2.1, ��������
						XYSeries series1 = new XYSeries("����");
						XYSeries series2 = new XYSeries("����");
						XYSeriesRenderer xyRenderer1 = new XYSeriesRenderer();
						XYSeriesRenderer xyRenderer2 = new XYSeriesRenderer();
						LinearLayout layout;
						renderer.removeAllRenderers();
						int n1 = 0;
						double temp = 0;
						if (AppPublicLeftRight.linedataint_ch1_1.size() > 0 && AppPublicLeftRight.linedataint_ch1_1.size() <= 400) {
							for (int i = 0; i < AppPublicLeftRight.linedataint_ch1_1.size(); i++) {
								//n1=AppPublicLeftRight.linedataint_ch1_1.get(i)*100+AppPublicLeftRight.linedataint_ch1_2.get(i);
								//temp=3.412*Math.exp((0.0016*n1));
								series1.add(i, AppPublicLeftRight.power_ch1.get(i));
							}
							;
						} else if (AppPublicLeftRight.linedataint_ch1_1.size() > 400) {
							for (int i = AppPublicLeftRight.linedataint_ch1_1.size() - 400; i < AppPublicLeftRight.linedataint_ch1_1.size(); i++) {
								//n1=AppPublicLeftRight.linedataint_ch1_1.get(i)*100+AppPublicLeftRight.linedataint_ch1_2.get(i);
								//temp=3.412*Math.exp((0.0016*n1));
								series1.add((i - AppPublicLeftRight.linedataint_ch1_1.size() + 400), AppPublicLeftRight.power_ch1.get(i));
							}
						}
						;
						if (AppPublicLeftRight.linedataint_ch2_1.size() > 0 && AppPublicLeftRight.linedataint_ch2_1.size() <= 400) {
							for (int i = 0; i < AppPublicLeftRight.linedataint_ch2_1.size(); i++) {
								//n1=AppPublicLeftRight.linedataint_ch2_1.get(i)*100+AppPublicLeftRight.linedataint_ch2_2.get(i);
								//temp=3.412*Math.exp((0.0016*n1));
								series2.add(i, AppPublicLeftRight.power_ch2.get(i));
//                    		AppPublicLeftRight.power_ch2.add(temp);
//                    		Log.e("Test_out_ch2", AppPublicLeftRight.power_ch2.get(i)+"");
							}
						} else if (AppPublicLeftRight.linedataint_ch2_1.size() > 400) {
							for (int i = AppPublicLeftRight.linedataint_ch2_1.size() - 400; i < AppPublicLeftRight.linedataint_ch2_1.size(); i++) {
								//n1=AppPublicLeftRight.linedataint_ch2_1.get(i)*100+AppPublicLeftRight.linedataint_ch2_2.get(i);
								//temp=3.412*Math.exp((0.0016*n1));
								series2.add((i - AppPublicLeftRight.linedataint_ch2_1.size() + 400), AppPublicLeftRight.power_ch2.get(i));
//                    			AppPublicLeftRight.power_ch2.add(temp);
//                    			Log.e("Test_out_ch2", AppPublicLeftRight.power_ch2.get(i)+"");
							}
						}
						;
						//System.gc();
						dataset.addSeries(series1);
						dataset.addSeries(series2);
						// 3.1������ɫ
						// 1, ������ʾ����Ⱦͼ
						xyRenderer1.setColor(Color.GREEN);
						// 3.2���õ����ʽ
						xyRenderer1.setPointStyle(PointStyle.POINT);
						// 3.3, ��Ҫ���Ƶĵ���ӵ����������
						//SimpleSeriesRenderer seriesrenderer =renderer.getSeriesRendererAt(1);
						xyRenderer1.setDisplayChartValues(false);
						xyRenderer1.setLineWidth(2);
						xyRenderer1.setDisplayChartValuesDistance(1);//���������۵��ľ��룬ʹ�����е��ֵ������ʾ
						xyRenderer2.setColor(Color.RED);
						// 3.2���õ����ʽ
						xyRenderer2.setPointStyle(PointStyle.POINT);
						// 3.3, ��Ҫ���Ƶĵ���ӵ����������
						//SimpleSeriesRenderer seriesrenderer =renderer.getSeriesRendererAt(1);
						xyRenderer2.setDisplayChartValues(false);
						xyRenderer2.setLineWidth(2);
						xyRenderer2.setDisplayChartValuesDistance(1);//���������۵��ľ��룬ʹ�����е��ֵ������ʾ
						renderer.setYAxisMin(0);
						renderer.setYAxisMax(250);
						renderer.setXAxisMin(0);
						renderer.setXAxisMax(400);
						renderer.setShowLabels(true);
						renderer.setYLabels(10);
						//renderer.setXLabels(100);
						renderer.setLabelsColor(Color.WHITE);
						renderer.setXTitle("ʱ��");
						renderer.setYTitle("�¶�(��)");
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
						//�Ƴ�ԭ�е�LinearLayout�е���ͼ�ؼ�
						layout = (LinearLayout) findViewById(R.id.lineChar_leftright_t);
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
		/** ��ʱ����ִ���߼� */
		task01 = new TimerTask() {

			public void run() {
				Message message = new Message();
				message.what = 1;
				handler01.sendMessage(message);
			}
		};
		timer01.schedule(task01, 50, 300);
		//��ñ��������豸
		bluetooth = BluetoothAdapter.getDefaultAdapter();
		if (bluetooth == null) {//�豸û�������豸
			Toast.makeText(this, "û���ҵ�����������", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (!bluetooth.isEnabled()) {
			//����������豸
			Toast.makeText(this, "����δ�򿪣��������", Toast.LENGTH_LONG).show();
			//    		Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//    		startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		} else {
			/** ��activity��ʼ����ɵ�ʱ�򣬻ᴴ������service���� */
			if (mCommService_left == null) {
				mCommService_left = new BluetoothCommService_left(this, mHandler_left);
			}
			if (mCommService_right == null) {
				mCommService_right = new BluetoothCommService_right(this, mHandler_right);
			}
		}
	}

	@Override
	protected synchronized void onResume() {
		super.onResume();
//		/** ����ִ�е������������豸�Ĵ��룬��������serviceֻ��ִ��һ�� */
//		if (!mCommService_left_status) {
//			/** �����һ�������豸û�����ӣ��������� */
//			if (mCommService_left != null) {
//				// Only if the state is STATE_NONE, do we know that we haven't started already
//				if (mCommService_left.getState() == BluetoothCommService.STATE_NONE) {
//					// Start the Bluetooth services�����������߳�
//					mCommService_left.start();
//					mCommService_left_status = true;
//				}
//			}
//		} else if (!mCommService_right_status) {
//			/** ����ڶ��������豸û�����ӣ���ô�������� */
//			if (mCommService_right != null) {
//				if (mCommService_right.getState() == BluetoothCommService.STATE_NONE){
//					mCommService_right.start();
//					mCommService_right_status = true;
//				}
//			}
//		}
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
		if (mCommService_left != null) mCommService_left.stop();
		if (mCommService_right != null) mCommService_right.stop();
		if (D) Log.e(TAG, "--- ON DESTROY ---");
		//SysApplication.getInstance().exit();
		try {
			writer_left.close();
			outStream_left.close();

			writer_right.close();
			outStream_right.close();
		} catch (IOException e) {
			e.printStackTrace();
		}//�ǵùر�
	}

	private class btnClickedListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.disconnectButton_leftright_t) {
				if (mCommService_left != null) {
					mCommService_left.stop();
					//timer01.cancel();
				}
				if (mCommService_right != null) {
					mCommService_right.stop();
				}
			}
		}
	}

	private void ensureDiscoverable() {
		if (bluetooth.getScanMode() !=
					BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			//��ɼ�ʱ��Ϊ300s
			discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
	}

	//�����˵�ѡ��
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu_leftright, menu);
		return true;
	}

	//�˵�����
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
			case R.id.scan_left:
				// Launch the ScanDeviceActivity to see devices and do scan
				Intent serverIntent_left = new Intent(this, DeviceListActivity.class);
				startActivityForResult(serverIntent_left, REQUEST_CONNECT_DEVICE);
				return true;
			case R.id.scan_right:
				// Launch the ScanDeviceActivity to see devices and do scan
				Intent serverIntent_right = new Intent(this, DeviceListActivity2.class);
				startActivityForResult(serverIntent_right, REQUEST_CONNECT_DEVICE);
				return true;
			case R.id.discoverable:
				ensureDiscoverable();
				return true;
			case R.id.about:
				Intent intent = new Intent(BluetoothComm_leftRight_t.this, AboutActivity.class);
				startActivity(intent);
				return true;
			case R.id.exit:
				if (mCommService_left != null) {
					mCommService_left.stop();
				}
				if (mCommService_right != null) {
					mCommService_right.stop();
				}
				if (bluetooth == null) {
					bluetooth.enable();
				}
//	        	try {
//	        		DecimalFormat nf = new DecimalFormat("00");
//	                DecimalFormat nf1 = new DecimalFormat("00000");
//					writer_left.write("���ʱ�䣺"+nf.format(time_on_m)+":"+nf.format(time_on_s)+"������:"+nf1.format(detected_num_int)+"��"+"�������:"+nf1.format(sharp_num_int)+"��");
//	        	  	writer_left.flush();
//	        	  	//count=count+1;
//				} catch (IOException e) {
//					e.printStackTrace();
//				};
				timer01.cancel();
				SysApplication.getInstance().exit();
				finish();
				return true;
		}
		return false;
	}

	private final Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			int leftSize = mCommService_left_queue.size();
			int rightSize = mCommService_right_queue.size();
			if (leftSize > 0 && rightSize > 0) {
//				int size = leftSize < rightSize ? leftSize : rightSize;
//				for (int i=0; i< size; i++) {
					count++;
					String leftValue = mCommService_left_queue.poll();
					String[] leftSplit = leftValue.split("\\+");
					String rightValue = mCommService_right_queue.poll();
					String[] rightSplit = rightValue.split("\\+");

					if (AppPublicLeftRight.power_ch1.size() > 50000) {
						AppPublicLeftRight.linedataint_ch1_1 = new ArrayList<Integer>();
						AppPublicLeftRight.linedataint_ch1_2 = new ArrayList<Integer>();
						AppPublicLeftRight.linedataint_ch2_1 = new ArrayList<Integer>();
						AppPublicLeftRight.linedataint_ch2_2 = new ArrayList<Integer>();
						AppPublicLeftRight.countarray = new ArrayList<Integer>();
						AppPublicLeftRight.power_ch1 = new ArrayList<Double>();
						AppPublicLeftRight.power_ch2 = new ArrayList<Double>();
						AppPublicLeftRight.difference_ch1 = new ArrayList<Double>();
						AppPublicLeftRight.difference_ch2 = new ArrayList<Double>();
					}
					//System.gc();
					/** ��һ����������ֵ */
					double temp1 = Double.valueOf(leftSplit[0]);
					/** �ڶ�����������ֵ */
					double temp2 = Double.valueOf(leftSplit[1]);
					AppPublicLeftRight.linedataint_ch1_1.add(Integer.valueOf(leftSplit[2]));
					AppPublicLeftRight.linedataint_ch1_2.add(Integer.valueOf(leftSplit[3]));
					//�Խ��յ�������ֵ����ת��������list��
					double temp12 = temp1 + temp2;
					AppPublicLeftRight.power_ch1.add(temp12);

					/** ��������������ֵ */
					double temp3 = Double.valueOf(rightSplit[0]);
					/** ���ĸ���������ֵ */
					double temp4 = Double.valueOf(rightSplit[1]);
					AppPublicLeftRight.linedataint_ch2_1.add(Integer.valueOf(rightSplit[2]));
					AppPublicLeftRight.linedataint_ch2_2.add(Integer.valueOf(rightSplit[3]));
					double temp34 = temp3 + temp4;
					AppPublicLeftRight.power_ch2.add(temp34);

					//Log.e("power_ch2", temp+"");
					if (AppPublicLeftRight.power_ch2.size() > 1) {
						//int n2=AppPublicLeftRight.linedataint_ch1_1.get(i-1)*100+AppPublicLeftRight.linedataint_ch1_2.get(i-1);
						AppPublicLeftRight.difference_ch2.add(temp34 - AppPublicLeftRight.power_ch2.get(AppPublicLeftRight.power_ch2.size() - 2));
						//System.gc();
					}

					//���list����ֵ
					if (AppPublicLeftRight.power_ch1.size() > 1) {
						//int n2=AppPublicLeftRight.linedataint_ch1_1.get(i-1)*100+AppPublicLeftRight.linedataint_ch1_2.get(i-1);
						//��list�����һ��Ԫ�ؼ�ȥǰһ��Ԫ�ص�ֵ���ַ�����list��
						//ǰ�㴫������ֵ����һ��ֵ�Ĳ�ֵ
						AppPublicLeftRight.difference_ch1.add(temp12 - AppPublicLeftRight.power_ch1.get(AppPublicLeftRight.power_ch1.size() - 2));
						if (AppPublicLeftRight.difference_ch1.size() > 1) {
							if (/*AppPublicLeftRight.difference_ch1.get(AppPublicLeftRight.difference_ch1.size() - 2) < -2 && AppPublicLeftRight.difference_ch1.get(AppPublicLeftRight.difference_ch1.size() - 1) > -2 && temp12 < 10 && */count > 180) {
								detected_num_int = detected_num_int + 1;
								if (isstart1 == false) {
									isstart1 = true;
									AppPublicLeftRight.power_all_ch1_1 = 0;
									AppPublicLeftRight.power_all_ch2_1 = 0;
								} else if (isstart1 == true) {
									Log.e("Warning", "power_all_ch1__1" + "__" + AppPublicLeftRight.power_all_ch1_1);
									Log.e("Warning", "power_all_ch2__1" + "__" + AppPublicLeftRight.power_all_ch2_1);
									//boolean judge=(boolean)(((AppPublicLeftRight.power_all_ch2_1/(AppPublicLeftRight.power_all_ch1_1+AppPublicLeftRight.power_all_ch2_1)))<(30/100));
									if ((AppPublicLeftRight.power_all_ch1_1 - AppPublicLeftRight.power_all_ch2_1) < smaller) {
										mMediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.warning);
										mMediaPlayer.start();
										count = 0;
										left_num_int = left_num_int + 1;
										//isenable=false;
										Log.e("Warning", "mediaplay");
									} else if ((AppPublicLeftRight.power_all_ch1_1 - AppPublicLeftRight.power_all_ch2_1) > bigger) {
										mMediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.warning);
										mMediaPlayer.start();
										count = 0;
//										right_num_int = right_num_int + 1;
										left_num_int = left_num_int + 1;
										//isenable=false;
										Log.e("Warning", "mediaplay");
									}
									isstart1 = false;
									AppPublicLeftRight.power_all_ch1_1 = 0;
									AppPublicLeftRight.power_all_ch2_1 = 0;
								}
								if (isstart2 == false) {
									isstart2 = true;
									AppPublicLeftRight.power_all_ch1_2 = 0;
									AppPublicLeftRight.power_all_ch2_2 = 0;
								} else if (isstart2 == true) {
									Log.e("Warning", "power_all_ch1__2" + "__" + AppPublicLeftRight.power_all_ch1_2);
									Log.e("Warning", "power_all_ch2__2" + "__" + AppPublicLeftRight.power_all_ch2_2);
									//boolean judge=(boolean)(((AppPublicLeftRight.power_all_ch2_2/(AppPublicLeftRight.power_all_ch1_2+AppPublicLeftRight.power_all_ch2_2)))<(30/100));
									if ((AppPublicLeftRight.power_all_ch1_1 - AppPublicLeftRight.power_all_ch2_1) < smaller) {
										mMediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.warning);
										mMediaPlayer.start();
										count = 0;
										left_num_int = left_num_int + 1;
										//isenable=false;
										Log.e("Warning", "mediaplay");
									} else if ((AppPublicLeftRight.power_all_ch1_1 - AppPublicLeftRight.power_all_ch2_1) > bigger) {
										mMediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.warning);
										mMediaPlayer.start();
										count = 0;
//										right_num_int = right_num_int + 1;
										left_num_int = left_num_int + 1;
										//isenable=false;
										Log.e("Warning", "mediaplay");
									}
									isstart2 = false;
									AppPublicLeftRight.power_all_ch1_2 = 0;
									AppPublicLeftRight.power_all_ch2_2 = 0;
								}
							}
						}
						//System.gc();
					}
					if (isstart1) {
						AppPublicLeftRight.power_all_ch1_1 = AppPublicLeftRight.power_all_ch1_1 + temp12;
						AppPublicLeftRight.power_all_ch2_1 = AppPublicLeftRight.power_all_ch2_1 + temp34;
					}
					if (isstart2) {
						AppPublicLeftRight.power_all_ch1_2 = AppPublicLeftRight.power_all_ch1_2 + temp12;
						AppPublicLeftRight.power_all_ch2_2 = AppPublicLeftRight.power_all_ch2_2 + temp34;
					}
//				}
			}
		}
	};

	// The Handler that gets information back from the BluetoothChatService
	private final Handler mHandler_left = new Handler() {
		String str;

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MESSAGE_STATE_CHANGE:
					if (D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
					switch (msg.arg1) {
						case BluetoothCommService_left.STATE_CONNECTED:
							connectDevices.setText(R.string.title_connected_to);
							connectDevices.append(mConnectedDeviceName);
							time_start = SystemClock.elapsedRealtime();
							istiming_left = true;
							if (!isLeftGetData) {
								isLeftGetData = true;
							}
							//    mConversationArrayAdapter.clear();
							break;
						case BluetoothCommService_left.STATE_CONNECTING:
							connectDevices.setText(R.string.title_connecting);
							break;
						case BluetoothCommService_left.STATE_LISTEN:
						case BluetoothCommService_left.STATE_NONE:
							connectDevices.setText(R.string.title_not_connected);
							istiming_left = false;
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

					linedatachar1 = str.toCharArray();
					//System.gc();
					int length = 40 * 2;
					for (int j = 0; j < linedatachar1.length - length; j++) {
						String stemp1 = String.valueOf(linedatachar1[j + 0]);
						String stemp2 = String.valueOf(linedatachar1[j + 1]);
						String stemp3 = String.valueOf(linedatachar1[j + 2]);
						String stemp4 = String.valueOf(linedatachar1[j + 3]);
						/** ������ĸ�ֵ�Ǽ����ֽڣ�������������У���ã���ʱû�� */
						String stemp5 = String.valueOf(linedatachar1[j + 4]);
						String stemp6 = String.valueOf(linedatachar1[j + 5]);
						String stemp7 = String.valueOf(linedatachar1[j + 6]);
						String stemp8 = String.valueOf(linedatachar1[j + 7]);
						/** ĩβ��У��λ���������ֽڣ���Ӧ6��16���Ƶ��� */
						String stemp75 = String.valueOf(linedatachar1[j + 74]);
						String stemp76 = String.valueOf(linedatachar1[j + 75]);
						String stemp77 = String.valueOf(linedatachar1[j + 76]);
						String stemp78 = String.valueOf(linedatachar1[j + 77]);
						String stemp79 = String.valueOf(linedatachar1[j + 78]);
						String stemp80 = String.valueOf(linedatachar1[j + 79]);
						double temp1 = 0;
						double temp2 = 0;
						if ("5".equals(stemp1) && "5".equals(stemp2)
									&& "A".equals(stemp3) && "A".equals(stemp4)
									&& "F".equals(stemp75) && "F".equals(stemp76)
									&& "F".equals(stemp77) && "F".equals(stemp78)
									&& "F".equals(stemp79) && "F".equals(stemp80)) {
							/** ������01 ��һ��ֵ */
							String sensor01_1_s = String.valueOf(linedatachar1[j + 8]) + String.valueOf(linedatachar1[j + 9]);
							int sensor01_1 = Integer.parseInt(sensor01_1_s, 16);
							/** ������01 �ڶ���ֵ */
							String sensor01_2_s = String.valueOf(linedatachar1[j + 10]) + String.valueOf(linedatachar1[j + 11]);
							int sensor01_2 = Integer.parseInt(sensor01_2_s, 16);
							/** ������02 ��һ��ֵ */
							String sensor02_1_s = String.valueOf(linedatachar1[j + 12]) + String.valueOf(linedatachar1[j + 13]);
//							int sensor02_1 = Integer.parseInt(sensor02_1_s, 16);
							/** ������02 �ڶ���ֵ */
							String sensor02_2_s = String.valueOf(linedatachar1[j + 14]) + String.valueOf(linedatachar1[j + 15]);
//							int sensor02_2 = Integer.parseInt(sensor02_2_s, 16);
							/** ������03 ��һ��ֵ */
							String sensor03_1_s = String.valueOf(linedatachar1[j + 16]) + String.valueOf(linedatachar1[j + 17]);
//							int sensor03_1 = Integer.parseInt(sensor03_1_s, 16);
							/** ������03 �ڶ���ֵ */
							String sensor03_2_s = String.valueOf(linedatachar1[j + 18]) + String.valueOf(linedatachar1[j + 19]);
//							int sensor03_2 = Integer.parseInt(sensor03_2_s, 16);
							/** ������04 ��һ��ֵ */
							String sensor04_1_s = String.valueOf(linedatachar1[j + 20]) + String.valueOf(linedatachar1[j + 21]);
//							int sensor04_1 = Integer.parseInt(sensor04_1_s, 16);
							/** ������04 �ڶ���ֵ */
							String sensor04_2_s = String.valueOf(linedatachar1[j + 22]) + String.valueOf(linedatachar1[j + 23]);
//							int sensor04_2 = Integer.parseInt(sensor04_2_s, 16);
							/** ������05 ��һ��ֵ */
							String sensor05_1_s = String.valueOf(linedatachar1[j + 24]) + String.valueOf(linedatachar1[j + 25]);
//							int sensor05_1 = Integer.parseInt(sensor05_1_s, 16);
							/** ������05 �ڶ���ֵ */
							String sensor05_2_s = String.valueOf(linedatachar1[j + 26]) + String.valueOf(linedatachar1[j + 27]);
//							int sensor05_2 = Integer.parseInt(sensor05_2_s, 16);
							/** ������06 ��һ��ֵ */
							String sensor06_1_s = String.valueOf(linedatachar1[j + 28]) + String.valueOf(linedatachar1[j + 29]);
//							int sensor06_1 = Integer.parseInt(sensor06_1_s, 16);
							/** ������06 �ڶ���ֵ */
							String sensor06_2_s = String.valueOf(linedatachar1[j + 30]) + String.valueOf(linedatachar1[j + 31]);
//							int sensor06_2 = Integer.parseInt(sensor06_2_s, 16);
							/** ������07 ��һ��ֵ */
							String sensor07_1_s = String.valueOf(linedatachar1[j + 32]) + String.valueOf(linedatachar1[j + 33]);
//							int sensor07_1 = Integer.parseInt(sensor07_1_s, 16);
							/** ������07 �ڶ���ֵ */
							String sensor07_2_s = String.valueOf(linedatachar1[j + 34]) + String.valueOf(linedatachar1[j + 35]);
//							int sensor07_2 = Integer.parseInt(sensor07_2_s, 16);
							/** ������08 ��һ��ֵ */
							String sensor08_1_s = String.valueOf(linedatachar1[j + 36]) + String.valueOf(linedatachar1[j + 37]);
//							int sensor08_1 = Integer.parseInt(sensor08_1_s, 16);
							/** ������08 �ڶ���ֵ */
							String sensor08_2_s = String.valueOf(linedatachar1[j + 38]) + String.valueOf(linedatachar1[j + 39]);
//							int sensor08_2 = Integer.parseInt(sensor08_2_s, 16);

							try {
								String str = 1 + String.valueOf(linedatachar1[j + 8]) + String.valueOf(linedatachar1[j + 9]) + String.valueOf(linedatachar1[j + 10]) + String.valueOf(linedatachar1[j + 11]) + " ";
								str += 2 + String.valueOf(linedatachar1[j + 12]) + String.valueOf(linedatachar1[j + 13]) + String.valueOf(linedatachar1[j + 14]) + String.valueOf(linedatachar1[j + 15]) + " ";
								str += 3 + String.valueOf(linedatachar1[j + 16]) + String.valueOf(linedatachar1[j + 17]) + String.valueOf(linedatachar1[j + 18]) + String.valueOf(linedatachar1[j + 19]) + " ";
								str += 4 + String.valueOf(linedatachar1[j + 20]) + String.valueOf(linedatachar1[j + 21]) + String.valueOf(linedatachar1[j + 22]) + String.valueOf(linedatachar1[j + 23]) + " ";
								str += 5 + String.valueOf(linedatachar1[j + 24]) + String.valueOf(linedatachar1[j + 25]) + String.valueOf(linedatachar1[j + 26]) + String.valueOf(linedatachar1[j + 27]) + " ";
								str += 6 + String.valueOf(linedatachar1[j + 28]) + String.valueOf(linedatachar1[j + 29]) + String.valueOf(linedatachar1[j + 30]) + String.valueOf(linedatachar1[j + 31]) + " ";
								str += 7 + String.valueOf(linedatachar1[j + 32]) + String.valueOf(linedatachar1[j + 33]) + String.valueOf(linedatachar1[j + 34]) + String.valueOf(linedatachar1[j + 35]) + " ";
								str += 8 + String.valueOf(linedatachar1[j + 36]) + String.valueOf(linedatachar1[j + 37]) + String.valueOf(linedatachar1[j + 38]) + String.valueOf(linedatachar1[j + 39]) + " ";
								writer_left.write(str);
								writer_left.flush();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							//00 18
							double sensor01 = getTemperaturValue(sensor01_1_s, sensor01_2_s);
							double sensor02 = getTemperaturValue(sensor02_1_s, sensor02_2_s);
							double sensor03 = getTemperaturValue(sensor03_1_s, sensor03_2_s);
							double sensor04 = getTemperaturValue(sensor04_1_s, sensor04_2_s);
							double sensor05 = getTemperaturValue(sensor05_1_s, sensor05_2_s);
							double sensor06 = getTemperaturValue(sensor06_1_s, sensor06_2_s);
							double sensor07 = getTemperaturValue(sensor07_1_s, sensor07_2_s);
							double sensor08 = getTemperaturValue(sensor08_1_s, sensor08_2_s);

							temp1 = sensor01 + sensor02 + sensor03 + sensor04;
							temp2 = sensor05 + sensor06 + sensor07 + sensor08;

							if (isLeftGetData && isRightGetData) {
								StringBuilder leftResult = new StringBuilder(String.valueOf(temp1))
																   .append("+")
																   .append(String.valueOf(temp2))
																   .append("+")
																   .append(String.valueOf(sensor01_1))
																   .append("+")
																   .append(String.valueOf(sensor01_2));
								mCommService_left_queue.add(leftResult.toString());
								//�����յ�������ֵ����list��
//								AppPublicLeftRight.linedataint_ch1_1.add(sensor01_1);
//								AppPublicLeftRight.linedataint_ch1_2.add(sensor01_2);
								mHandler.obtainMessage().sendToTarget();
							}
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

	private final Handler mHandler_right = new Handler() {
		String str;

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MESSAGE_STATE_CHANGE:
					if (D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
					switch (msg.arg1) {
						case BluetoothCommService_right.STATE_CONNECTED:
							connectDevices02.setText(R.string.title_connected_to);
							connectDevices02.append(mConnectedDeviceName);
							time_start = SystemClock.elapsedRealtime();
							istiming_right = true;
							if (!isRightGetData) {
								isRightGetData = true;
							}
							//    mConversationArrayAdapter.clear();
							break;
						case BluetoothCommService_right.STATE_CONNECTING:
							connectDevices02.setText(R.string.title_connecting);
							break;
						case BluetoothCommService_right.STATE_LISTEN:
						case BluetoothCommService_right.STATE_NONE:
							connectDevices02.setText(R.string.title_not_connected);
							istiming_right = false;
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

					linedatachar1 = str.toCharArray();
					//System.gc();
					int length = 40 * 2;
					for (int j = 0; j < linedatachar1.length - length; j++) {
						String stemp1 = String.valueOf(linedatachar1[j + 0]);
						String stemp2 = String.valueOf(linedatachar1[j + 1]);
						String stemp3 = String.valueOf(linedatachar1[j + 2]);
						String stemp4 = String.valueOf(linedatachar1[j + 3]);
						/** ������ĸ�ֵ�Ǽ����ֽڣ�������������У���ã���ʱû�� */
						String stemp5 = String.valueOf(linedatachar1[j + 4]);
						String stemp6 = String.valueOf(linedatachar1[j + 5]);
						String stemp7 = String.valueOf(linedatachar1[j + 6]);
						String stemp8 = String.valueOf(linedatachar1[j + 7]);
						/** ĩβ��У��λ���������ֽڣ���Ӧ6��16���Ƶ��� */
						String stemp75 = String.valueOf(linedatachar1[j + 74]);
						String stemp76 = String.valueOf(linedatachar1[j + 75]);
						String stemp77 = String.valueOf(linedatachar1[j + 76]);
						String stemp78 = String.valueOf(linedatachar1[j + 77]);
						String stemp79 = String.valueOf(linedatachar1[j + 78]);
						String stemp80 = String.valueOf(linedatachar1[j + 79]);
						double temp3 = 0;
						double temp4 = 0;
						if ("5".equals(stemp1) && "5".equals(stemp2)
									&& "A".equals(stemp3) && "A".equals(stemp4)
									&& "F".equals(stemp75) && "F".equals(stemp76)
									&& "F".equals(stemp77) && "F".equals(stemp78)
									&& "F".equals(stemp79) && "F".equals(stemp80)) {
							/** ������01 ��һ��ֵ */
							String sensor01_1_s = String.valueOf(linedatachar1[j + 8]) + String.valueOf(linedatachar1[j + 9]);
							int sensor01_1 = Integer.parseInt(sensor01_1_s, 16);
							/** ������01 �ڶ���ֵ */
							String sensor01_2_s = String.valueOf(linedatachar1[j + 10]) + String.valueOf(linedatachar1[j + 11]);
							int sensor01_2 = Integer.parseInt(sensor01_2_s, 16);
							/** ������02 ��һ��ֵ */
							String sensor02_1_s = String.valueOf(linedatachar1[j + 12]) + String.valueOf(linedatachar1[j + 13]);
//							int sensor02_1 = Integer.parseInt(sensor02_1_s, 16);
							/** ������02 �ڶ���ֵ */
							String sensor02_2_s = String.valueOf(linedatachar1[j + 14]) + String.valueOf(linedatachar1[j + 15]);
//							int sensor02_2 = Integer.parseInt(sensor02_2_s, 16);
							/** ������03 ��һ��ֵ */
							String sensor03_1_s = String.valueOf(linedatachar1[j + 16]) + String.valueOf(linedatachar1[j + 17]);
//							int sensor03_1 = Integer.parseInt(sensor03_1_s, 16);
							/** ������03 �ڶ���ֵ */
							String sensor03_2_s = String.valueOf(linedatachar1[j + 18]) + String.valueOf(linedatachar1[j + 19]);
//							int sensor03_2 = Integer.parseInt(sensor03_2_s, 16);
							/** ������04 ��һ��ֵ */
							String sensor04_1_s = String.valueOf(linedatachar1[j + 20]) + String.valueOf(linedatachar1[j + 21]);
//							int sensor04_1 = Integer.parseInt(sensor04_1_s, 16);
							/** ������04 �ڶ���ֵ */
							String sensor04_2_s = String.valueOf(linedatachar1[j + 22]) + String.valueOf(linedatachar1[j + 23]);
//							int sensor04_2 = Integer.parseInt(sensor04_2_s, 16);
							/** ������05 ��һ��ֵ */
							String sensor05_1_s = String.valueOf(linedatachar1[j + 24]) + String.valueOf(linedatachar1[j + 25]);
//							int sensor05_1 = Integer.parseInt(sensor05_1_s, 16);
							/** ������05 �ڶ���ֵ */
							String sensor05_2_s = String.valueOf(linedatachar1[j + 26]) + String.valueOf(linedatachar1[j + 27]);
//							int sensor05_2 = Integer.parseInt(sensor05_2_s, 16);
							/** ������06 ��һ��ֵ */
							String sensor06_1_s = String.valueOf(linedatachar1[j + 28]) + String.valueOf(linedatachar1[j + 29]);
//							int sensor06_1 = Integer.parseInt(sensor06_1_s, 16);
							/** ������06 �ڶ���ֵ */
							String sensor06_2_s = String.valueOf(linedatachar1[j + 30]) + String.valueOf(linedatachar1[j + 31]);
//							int sensor06_2 = Integer.parseInt(sensor06_2_s, 16);
							/** ������07 ��һ��ֵ */
							String sensor07_1_s = String.valueOf(linedatachar1[j + 32]) + String.valueOf(linedatachar1[j + 33]);
//							int sensor07_1 = Integer.parseInt(sensor07_1_s, 16);
							/** ������07 �ڶ���ֵ */
							String sensor07_2_s = String.valueOf(linedatachar1[j + 34]) + String.valueOf(linedatachar1[j + 35]);
//							int sensor07_2 = Integer.parseInt(sensor07_2_s, 16);
							/** ������08 ��һ��ֵ */
							String sensor08_1_s = String.valueOf(linedatachar1[j + 36]) + String.valueOf(linedatachar1[j + 37]);
//							int sensor08_1 = Integer.parseInt(sensor08_1_s, 16);
							/** ������08 �ڶ���ֵ */
							String sensor08_2_s = String.valueOf(linedatachar1[j + 38]) + String.valueOf(linedatachar1[j + 39]);
//							int sensor08_2 = Integer.parseInt(sensor08_2_s, 16);

							try {
								String str = 1 + String.valueOf(linedatachar1[j + 8]) + String.valueOf(linedatachar1[j + 9]) + String.valueOf(linedatachar1[j + 10]) + String.valueOf(linedatachar1[j + 11]) + " ";
								str += 2 + String.valueOf(linedatachar1[j + 12]) + String.valueOf(linedatachar1[j + 13]) + String.valueOf(linedatachar1[j + 14]) + String.valueOf(linedatachar1[j + 15]) + " ";
								str += 3 + String.valueOf(linedatachar1[j + 16]) + String.valueOf(linedatachar1[j + 17]) + String.valueOf(linedatachar1[j + 18]) + String.valueOf(linedatachar1[j + 19]) + " ";
								str += 4 + String.valueOf(linedatachar1[j + 20]) + String.valueOf(linedatachar1[j + 21]) + String.valueOf(linedatachar1[j + 22]) + String.valueOf(linedatachar1[j + 23]) + " ";
								str += 5 + String.valueOf(linedatachar1[j + 24]) + String.valueOf(linedatachar1[j + 25]) + String.valueOf(linedatachar1[j + 26]) + String.valueOf(linedatachar1[j + 27]) + " ";
								str += 6 + String.valueOf(linedatachar1[j + 28]) + String.valueOf(linedatachar1[j + 29]) + String.valueOf(linedatachar1[j + 30]) + String.valueOf(linedatachar1[j + 31]) + " ";
								str += 7 + String.valueOf(linedatachar1[j + 32]) + String.valueOf(linedatachar1[j + 33]) + String.valueOf(linedatachar1[j + 34]) + String.valueOf(linedatachar1[j + 35]) + " ";
								str += 8 + String.valueOf(linedatachar1[j + 36]) + String.valueOf(linedatachar1[j + 37]) + String.valueOf(linedatachar1[j + 38]) + String.valueOf(linedatachar1[j + 39]) + " ";
								writer_right.write(str);
								writer_right.flush();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							double sensor01 = getTemperaturValue(sensor01_1_s, sensor01_2_s);
							double sensor02 = getTemperaturValue(sensor02_1_s, sensor02_2_s);
							double sensor03 = getTemperaturValue(sensor03_1_s, sensor03_2_s);
							double sensor04 = getTemperaturValue(sensor04_1_s, sensor04_2_s);
							double sensor05 = getTemperaturValue(sensor05_1_s, sensor05_2_s);
							double sensor06 = getTemperaturValue(sensor06_1_s, sensor06_2_s);
							double sensor07 = getTemperaturValue(sensor07_1_s, sensor07_2_s);
							double sensor08 = getTemperaturValue(sensor08_1_s, sensor08_2_s);

							temp3 = sensor01 + sensor02 + sensor03 + sensor04;
							temp4 = sensor05 + sensor06 + sensor07 + sensor08;

							if (isLeftGetData && isRightGetData) {

								StringBuilder rightResult = new StringBuilder(String.valueOf(temp3))
																	.append("+")
																	.append(String.valueOf(temp4))
																	.append("+")
																	.append(String.valueOf(sensor01_1))
																	.append("+")
																	.append(String.valueOf(sensor01_2));
								mCommService_right_queue.add(rightResult.toString());
								//�����յ�������ֵ����list��
//								AppPublicLeftRight.linedataint_ch2_1.add(sensor01_1);
//								AppPublicLeftRight.linedataint_ch2_2.add(sensor01_2);
								mHandler.obtainMessage().sendToTarget();
							}
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


	/** ���㴫������ֵ */
	private double getSensorValue(int sensor01, int sensor02) {
		int n1 = sensor01 * 100 + sensor02;
		return  3.412 * Math.exp((0.0016 * n1));
	}

	/**
	 * R=R0��1+3.90802��10^-3��T��
	 * @param sensor01
	 * @param sensor02
	 * @return
	 */
	private double getTemperaturValue(String sensor01, String sensor02) {
		double sensorValue = Integer.valueOf(sensor01 + sensor02, 16);
		return ((sensorValue / R0) - 1) / 3.90802 * 1000;
	}

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
		//����ContentValues����
		ContentValues values = new ContentValues();
		//��ö����в����ֵ�ԣ����м���������ֵ��ϣ�����뵽��һ�е�ֵ��ֵ��������ݿ⵱�е���������һ��
		DatabaseHelper dbHelper = new DatabaseHelper(BluetoothComm_leftRight_t.this, "toe_walking.db");
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		cursor = db.query("user", null, null, null, null, null, null, null);
		//SimpleDateFormat sDateFormat = new SimpleDateFormat("MM��dd��hhʱmm��");
		//String date = sDateFormat.format(new java.util.Date());
		//String text =(str);
		values.put("id", cursor.getCount() + 1);
		values.put("name", name);
		values.put("age", age);
		values.put("sex", sex);
		values.put("value", value);
		//values.put("left",n);
		db = dbHelper.getWritableDatabase();
		//����insert�������Ϳ��Խ����ݲ��뵽���ݿ⵱��
		db.insert("user", null, values);
		db.close();
	}

	@SuppressLint("SimpleDateFormat")
	private void R_insertData(int n) {
		DecimalFormat nf = new DecimalFormat("0.0000");
		//����ContentValues����
		ContentValues values = new ContentValues();
		//��ö����в����ֵ�ԣ����м���������ֵ��ϣ�����뵽��һ�е�ֵ��ֵ��������ݿ⵱�е���������һ��
		DatabaseHelper dbHelper = new DatabaseHelper(BluetoothComm_leftRight_t.this, "toe_walking.db");
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		cursor = db.query("user", null, null, null, null, null, null, null);
		SimpleDateFormat sDateFormat = new SimpleDateFormat("MM��dd��hhʱmm��");
		String date = sDateFormat.format(new java.util.Date());
		//String text =(str);
		values.put("id", cursor.getCount() + 1);
		values.put("right", n);
		db = dbHelper.getWritableDatabase();
		//����insert�������Ϳ��Խ����ݲ��뵽���ݿ⵱��
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
				writer.close();//�ǵùر�
				outStream.close();
			} else {
				Toast.makeText(context, "sd������������sd����״̬", Toast.LENGTH_LONG).show();
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
		Builder builder = new Builder(BluetoothComm_leftRight_t.this);
		builder.setMessage("ȷ��Ҫ�˳���?");
		builder.setTitle("��ʾ");
		builder.setPositiveButton("ȷ��",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						android.os.Process.killProcess(android.os.Process.myPid());
					}
				});
		builder.setNegativeButton("ȡ��",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	protected void back() {
		Builder builder = new Builder(BluetoothComm_leftRight_t.this);
		builder.setMessage("ȷ��Ҫ������?");
		builder.setTitle("��ʾ");
		builder.setPositiveButton("ȷ��",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
//						Intent intent = new Intent();
//						intent.putExtra("name", test_name);
//						intent.putExtra("age", test_age);
//						intent.putExtra("sex", test_sex);
//						intent.putExtra("value", value);
//						intent.setClass(BluetoothComm_sharp.this, FunctionSelectorActivity.class);
//						BluetoothComm_sharp.this.startActivity(intent);
						if (mCommService_left != null) {
							mCommService_left.stop();
						}
						if (mCommService_right != null) {
							mCommService_right.stop();
						}
						finish();
					}
				});
		builder.setNegativeButton("ȡ��",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}
}