package com.dashuai.android.treasuremap.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dashuai.android.treasuremap.R;

public class MyLoadingView extends LinearLayout {
	private TextView textView;
	private ProgressBar progressBar;

	public MyLoadingView(Context context) {
		super(context);
	}

	public MyLoadingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.layout_loading, this,
				true);
		textView = (TextView) findViewById(R.id.textView);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		this.setGravity(Gravity.CENTER_VERTICAL);
	}

	public void showProgress() {
		showProgress("正在加载中...");
	}

	public void showProgress(String text) {
		progressBar.setVisibility(View.VISIBLE);
		textView.setVisibility((null == text || "".equals(text)) ? View.GONE
				: View.VISIBLE);
		textView.setTextColor(Color.WHITE);
		if (null != text) {
			textView.setText(text);
		}
	}

	public void showMessage(String text) {
		progressBar.setVisibility(View.GONE);
		textView.setVisibility((null == text || "".equals(text)) ? View.GONE
				: View.VISIBLE);
		textView.setTextColor(Color.WHITE);
		if (null != text) {
			textView.setText(text);
		}
	}

	public void showError(String text) {
		progressBar.setVisibility(View.GONE);
		textView.setVisibility((null == text || "".equals(text)) ? View.GONE
				: View.VISIBLE);
		textView.setTextColor(Color.RED);
		if (null != text) {
			textView.setText(text);
		}
	}

	public boolean isVisible() {
		return progressBar.getVisibility() == View.VISIBLE
				|| textView.getVisibility() == View.VISIBLE;
	}

	public void hide() {
		progressBar.setVisibility(View.GONE);
		textView.setVisibility(View.GONE);
	}
}
