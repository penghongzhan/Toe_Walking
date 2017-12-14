package com.tac.bluetoothcomm;

import java.io.*;
import java.util.ArrayList;

import android.os.Environment;
import com.ronoid.bluetoothcomm.R;
import com.tac.db.DatabaseHelper;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Toe_Walking_Welcome extends Activity {
	private Button button01;
	private EditText ET_name;
	private EditText ET_age;
	private EditText ET_sex;
	private EditText ET_value;
	private ImageView title_pic;
	private ImageView welcome_pic;
	
	private String name;
	private String age;
	private String sex;
//	private String value;
	private ListView history_list;
	private ArrayAdapter<String> historyAdapter;
	private ArrayList<String> history = new ArrayList<String>();
	private Cursor cursor;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		button01=(Button)findViewById(R.id.start); 
		title_pic=(ImageView)findViewById(R.id.title_pic);
		title_pic.setBackgroundResource(R.drawable.title02);
		welcome_pic=(ImageView)findViewById(R.id.welcome_pic);
		welcome_pic.setBackgroundResource(R.drawable.welcome1);
		button01.setOnClickListener(new begin());
        ET_name=(EditText)findViewById(R.id.info_name_edit);
    	ET_age=(EditText)findViewById(R.id.info_age_edit);
    	ET_sex=(EditText)findViewById(R.id.info_sex_edit);
    	ET_value=(EditText)findViewById(R.id.info_value_edit);
    	ET_value.setInputType(EditorInfo.TYPE_CLASS_PHONE); 
    	history_list = (ListView)findViewById(R.id.history_list);
    	historyAdapter = new ArrayAdapter<String>(this, 
                R.layout.list_new);
    	history_list.setAdapter(historyAdapter);
    	history_list.setOnItemClickListener(mNewDeviceClickListener);
    	
    	DatabaseHelper dbHelper = new DatabaseHelper(Toe_Walking_Welcome.this,"toe_walking.db");
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		cursor = db.query("user", null, null, null, null, null, null, null);
		if(cursor.getCount()!=0){
			if(cursor.getCount()>3){
				cursor.moveToPosition(cursor.getCount()-3);
				for(int i=0;i<3;i++){
					historyAdapter.add(cursor.getString(cursor.getColumnIndex("name"))+"_"+cursor.getString(cursor.getColumnIndex("age"))+"_"+cursor.getString(cursor.getColumnIndex("sex")));
					cursor.moveToNext();
				}			
				db.close();
				}
			else {
				cursor.moveToFirst();
			for(int i=0;i<cursor.getCount();i++){
				historyAdapter.add(cursor.getString(cursor.getColumnIndex("name"))+"_"+cursor.getString(cursor.getColumnIndex("age"))+"_"+cursor.getString(cursor.getColumnIndex("sex")));
				cursor.moveToNext();
			}			
			db.close();
			}
		}

		try {
			File file = new File("/sdcard/toe_walking");
			if (!file.exists()) {
				file.mkdir();
			}
			String valueProp = "/sdcard/toe_walking/threshold.properties";
			File file_th = new File(valueProp);
//			if (!file_th.exists()) {
				if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
					FileOutputStream outStream_th = new FileOutputStream(file_th, false);
					OutputStreamWriter writer_th = new OutputStreamWriter(outStream_th, "UTF-8");
					writer_th.write("sharp=0.3\ninout=-0.3,0.12\nleftright=0.4,0.6\ntemperature=-3.5,3.5");
					writer_th.close();
					outStream_th.close();
				}
//			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		private OnItemClickListener mNewDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
        	Intent intent = new Intent();
        	DatabaseHelper dbHelper = new DatabaseHelper(Toe_Walking_Welcome.this,"toe_walking.db");
    		SQLiteDatabase db = dbHelper.getReadableDatabase();
    		cursor = db.query("user", null, null, null, null, null, null, null);
    		
    		
    		if(cursor.getCount()!=0){
    			if(cursor.getCount()<3){
    			cursor.moveToPosition(arg2);
    			//Log.e("Warning", ""+arg2);
    			ET_name.setText("");
    			ET_age.setText("");
    			ET_sex.setText("");
    			//ET_value.setText("");
    			ET_name.append(cursor.getString(cursor.getColumnIndex("name")));
    			ET_age.append(cursor.getString(cursor.getColumnIndex("age")));
    			ET_sex.append(cursor.getString(cursor.getColumnIndex("sex")));
    			//ET_value.append(cursor.getString(cursor.getColumnIndex("value")));
    			db.close();
    			}else{
    				cursor.moveToPosition(cursor.getCount()-3+arg2);
        			//Log.e("Warning", ""+arg2);
        			ET_name.setText("");
        			ET_age.setText("");
        			ET_sex.setText("");
        			//ET_value.setText("");
        			ET_name.append(cursor.getString(cursor.getColumnIndex("name")));
        			ET_age.append(cursor.getString(cursor.getColumnIndex("age")));
        			ET_sex.append(cursor.getString(cursor.getColumnIndex("sex")));
        			//ET_value.append(cursor.getString(cursor.getColumnIndex("value")));
        			db.close();
    			}
    			
    		}   	       	        	
//        	intent.putExtra("name", name);
//			intent.putExtra("age", age);
//			intent.putExtra("sex", sex);
//			intent.putExtra("value", value);
//			intent.setClass(Toe_Walking_Welcome.this,BluetoothComm.class);
//			Toe_Walking_Welcome.this.startActivity(intent);
//       
//            finish();
        }
    };
	class begin implements OnClickListener{
        @Override
    	public void onClick(View v) {
        	
        	Intent intent = new Intent(); 
        	name=ET_name.getText().toString();
    		age=ET_age.getText().toString();
    		sex=ET_sex.getText().toString();
//    		value=ET_value.getText().toString();
        	if(name.equals("")&&age.equals("")){
    			Toast.makeText(getBaseContext(), "输入错误！请输入姓名和生日", Toast.LENGTH_SHORT).show();			
    		}else {
//    			if(value.equals("")){
//        			Toast.makeText(getBaseContext(), "输入错误！请输入正确阈值（1~50）", Toast.LENGTH_SHORT).show();
//        		}else if(isNumeric(value)){
//        			if(Integer.valueOf(value).intValue()>0&&Integer.valueOf(value).intValue()<=50){
        				intent.putExtra("name", name);
        				intent.putExtra("age", age);
        				intent.putExtra("sex", sex);
//        				intent.putExtra("value", value);
        				intent.setClass(Toe_Walking_Welcome.this,FunctionSelectorActivity.class);
        				Toe_Walking_Welcome.this.startActivity(intent);
        				finish();        							
//            		}else{
//            			Toast.makeText(getBaseContext(), "输入错误！请输入正确阈值（1~50）", Toast.LENGTH_SHORT).show();
//    				}
//    				}else {
//    			Toast.makeText(getBaseContext(), "输入错误！请输入正确阈值", Toast.LENGTH_SHORT).show();
//    				};
    		}
			
		}
    }
	class Name_Iuput implements OnClickListener, android.content.DialogInterface.OnClickListener{		
        @Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub        
		}
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			LayoutInflater inflater = getLayoutInflater();
        	View layout = inflater.inflate(R.layout.info_input,
        	(ViewGroup) findViewById(R.id.info_input)); 
        	ET_name=(EditText)layout.findViewById(R.id.info_name_edit);
        	ET_age=(EditText)layout.findViewById(R.id.info_age_edit);
        	ET_value=(EditText)layout.findViewById(R.id.info_sex_edit);
        	ET_sex=(EditText)layout.findViewById(R.id.info_value_edit);			
        	new AlertDialog.Builder(Toe_Walking_Welcome.this)   
            .setTitle("请输入测试者的信息")            
            .setView(layout)          
            .setPositiveButton("确定", new check())
            .setNegativeButton("取消", null)
            .show();
		}		
	}
	class check implements OnClickListener, android.content.DialogInterface.OnClickListener{
        @Override
    	public void onClick(View v) {       	
		}
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			
			Intent intent=getIntent();						
			intent.putExtra("name", name);
			intent.putExtra("age", age);
			intent.putExtra("sex", sex);
//			intent.putExtra("value", value);
			intent.setClass(Toe_Walking_Welcome.this, FunctionSelectorActivity.class);
			Toe_Walking_Welcome.this.startActivity(intent);
			//finish();
		}
    }
	public static boolean isNumeric(String str){
		  for (int i = str.length();--i>=0;){   
		   if (!Character.isDigit(str.charAt(i))){
		    return false;
		   }
		  }
		  return true;
		 }
	
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event) {  
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {  
            dialog();  
            return true;  
        }  
        return true;  
    }  
	protected void dialog() {  
        AlertDialog.Builder builder = new Builder(Toe_Walking_Welcome.this);  
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

	@Override
	public void onBackPressed() {
		finish();
	}
}
