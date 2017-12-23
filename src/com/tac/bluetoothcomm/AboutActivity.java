package com.tac.bluetoothcomm;

import com.ronoid.bluetoothcomm.R;

import android.app.Activity;
import android.os.Bundle;

/**
 * 测试vscode
 * xxx
 */
public class AboutActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_about);
		SysApplication.getInstance().addActivity(this);
	}

}
