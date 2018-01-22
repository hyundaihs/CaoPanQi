package com.dashuai.android.treasuremap.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.dashuai.android.treasuremap.CPQApplication;
import com.dashuai.android.treasuremap.Constant;
import com.dashuai.android.treasuremap.R;
import com.dashuai.android.treasuremap.entity.DeviceStatus;
import com.dashuai.android.treasuremap.util.Installation;
import com.dashuai.android.treasuremap.util.JsonUtil;
import com.dashuai.android.treasuremap.util.RequestUtil;
import com.dashuai.android.treasuremap.util.RequestUtil.Reply;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DeviceActivity extends Activity implements Reply {

	private TextView type, status, time, registe;
	private RequestUtil requestUtil;
	private String[] types = { "未知", "已注册", "已禁用", "已过期", "未注册" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device);
		CPQApplication.initActionBar(this, true, "设备状态");
		init();
	}

	private void init() {
		requestUtil = new RequestUtil(this, this);
		type = (TextView) findViewById(R.id.device_type);
		status = (TextView) findViewById(R.id.device_status);
		time = (TextView) findViewById(R.id.device_time);
		registe = (TextView) findViewById(R.id.device_registe);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("device_id", Installation.id(this.getBaseContext()));
		map.put("type_id", CPQApplication.getID_KEY());
		requestUtil.postRequest(Constant.URL_IP + Constant.CHECK_TIME, map, 0);
		registe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(DeviceActivity.this,
						RegisteActivity.class));
			}
		});
	}

	@Override
	public void onSuccess(JSONObject response, int what) {
		DeviceStatus deviceStatus = JsonUtil.getDeviceStatus(response);
		type.setText(getString(R.string.app_name));
		status.setText(types[deviceStatus.getType()]);
		switch (deviceStatus.getType()) {
		case 1:
			status.setTextColor(getResources().getColor(R.color.green));
			registe.setVisibility(View.GONE);
			break;
		case 2:
			status.setTextColor(getResources().getColor(R.color.orange));
			registe.setVisibility(View.GONE);
			break;
		case 3:
			status.setTextColor(getResources().getColor(R.color.orange));
			registe.setVisibility(View.VISIBLE);
			break;
		case 4:
			status.setTextColor(Color.WHITE);
			registe.setVisibility(View.VISIBLE);
			break;
		default:
			status.setText("未注册");
			status.setTextColor(Color.WHITE);
			registe.setVisibility(View.GONE);
			break;
		}
		time.setText(deviceStatus.getDays() + "");
		if (deviceStatus.getDays() <= 0) {
			time.setTextColor(Color.WHITE);
		} else if (deviceStatus.getDays() < 30) {
			time.setTextColor(getResources().getColor(R.color.orange));
		} else {
			time.setTextColor(getResources().getColor(R.color.green));
		}
	}

	@Override
	public void onErrorResponse(String error, int what) {

	}

	@Override
	public void onFailed(String error, int what) {

	}
}
