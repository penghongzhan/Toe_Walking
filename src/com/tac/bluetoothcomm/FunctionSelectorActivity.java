package com.tac.bluetoothcomm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.ronoid.bluetoothcomm.R;

/**
 * Created by Hong on 2017/10/15.
 */
public class FunctionSelectorActivity extends Activity {
	private Button sharpButton = null;
	private Button InOutButton = null;
	private String name;
	private String age;
	private String sex;
	private String value;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.function);
		SysApplication.getInstance().addActivity(this);
		sharpButton = (Button) findViewById(R.id.sharpButton);
		InOutButton = (Button) findViewById(R.id.inOutButton);
		Intent intent = getIntent();
		name = intent.getStringExtra("name");
		age = intent.getStringExtra("age");
		sex = intent.getStringExtra("sex");
		value = intent.getStringExtra("value");

		sharpButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FunctionSelectorActivity.this, BluetoothComm_sharp.class);
				Log.i("name", "FunctionSelectorActivity : " + name);
				intent.putExtra("name", name);
				intent.putExtra("age", age);
				intent.putExtra("sex", sex);
				intent.putExtra("value", value);
				startActivity(intent);
			}
		});

		InOutButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FunctionSelectorActivity.this, BluetoothComm_inout.class);
				Log.i("name", "FunctionSelectorActivity : " + name);
				intent.putExtra("name", name);
				intent.putExtra("age", age);
				intent.putExtra("sex", sex);
				intent.putExtra("value", value);
				startActivity(intent);
			}
		});
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(FunctionSelectorActivity.this);
		builder.setMessage("确定要返回吗?");
		builder.setTitle("提示");
		builder.setPositiveButton("确认",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent = new Intent();
						intent.setClass(FunctionSelectorActivity.this, Toe_Walking_Welcome.class);
						startActivity(intent);
						finish();
					}
				});
		builder.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}
}
