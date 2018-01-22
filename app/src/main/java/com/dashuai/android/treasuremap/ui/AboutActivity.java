package com.dashuai.android.treasuremap.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.dashuai.android.treasuremap.CPQApplication;
import com.dashuai.android.treasuremap.Constant;
import com.dashuai.android.treasuremap.R;
import com.dashuai.android.treasuremap.util.DialogUtil;
import com.dashuai.android.treasuremap.util.Installation;
import com.dashuai.android.treasuremap.util.SPUtil;

public class AboutActivity extends Activity {

	private TextView textView;
	private long lastClick;
	private int count;
	private DialogUtil dialogUtil;
	private Toast toast;
	private SPUtil spUtil;

	@SuppressLint("ShowToast")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CPQApplication.initActionBar(this, true, "关于");
		setContentView(R.layout.activity_about);
		toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		spUtil = new SPUtil(this);
		dialogUtil = new DialogUtil(this);
		textView = (TextView) findViewById(R.id.app_name);
		textView.setText(getResources().getString(R.string.app_name) + "v"
				+ CPQApplication.getVersionName(this));
		Drawable drawable = getResources().getDrawable(
				CPQApplication.VERSION == Constant.PHONE ? R.drawable.app_icon
						: R.drawable.app_icon_tv);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(),
				drawable.getMinimumHeight());
		textView.setCompoundDrawables(drawable, null, null, null);
		textView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogUtil.setErrorMessage("您的机器码是"
						+ Installation.id(AboutActivity.this.getBaseContext()));
				//
				// if (spUtil.isVip()) {
				// toast.setText("您已经是VIP测试模式了");
				// toast.show();
				// return;
				// }
				// if (lastClick == 0) {
				// lastClick = System.currentTimeMillis();
				// return;
				// }
				// long curr = System.currentTimeMillis();
				// if (curr - lastClick <= 2000) {
				// lastClick = curr;
				// count++;
				// } else {
				// lastClick = 0;
				// count = 0;
				// }
				// if (count >= 5) {
				// dialogUtil.setErrorMessage("您已开启了VIP测试模式，请重启应用体验",
				// new DialogInterface.OnClickListener() {
				//
				// @Override
				// public void onClick(DialogInterface dialog,
				// int which) {
				// spUtil.setVip(true);
				// }
				// });
				// lastClick = 0;
				// count = 0;
				// return;
				// }
				// if (count >= 2) {
				// toast.setText("再点击" + (5 - count) + "次开启VIP测试模式");
				// toast.show();
				// }
			}
		});
	}
}
