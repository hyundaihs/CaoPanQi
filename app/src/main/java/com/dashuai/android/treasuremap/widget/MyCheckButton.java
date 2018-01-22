package com.dashuai.android.treasuremap.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dashuai.android.treasuremap.R;

public class MyCheckButton extends LinearLayout implements OnClickListener {

	private TextView title, name;
	private boolean checked;
	private OnClickTouch onClickTouch;

	public MyCheckButton(Context context) {
		super(context);
	}

	public MyCheckButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.layout_my_check, this,
				true);
		title = (TextView) findViewById(R.id.title);
		name = (TextView) findViewById(R.id.name);
		this.setGravity(Gravity.CENTER_VERTICAL);
		setOnClickListener(this);
		setChecked(false);
	}

	public OnClickTouch getOnClickTouch() {
		return onClickTouch;
	}

	public void setOnClickTouch(OnClickTouch onClickTouch) {
		this.onClickTouch = onClickTouch;
	}

	public void setChecked(boolean isChecked) {
		if (isChecked) {
			LinearLayout view = (LinearLayout) getParent();
			for (int i = 0; i < view.getChildCount(); i++) {
				MyCheckButton m = (MyCheckButton) view.getChildAt(i);
				m.setChecked(false);
			}
			if (null != onClickTouch) {
				onClickTouch.onClick(this);
			}
		}
		this.checked = isChecked;
		title.setTextColor(isChecked ? getResources().getColor(R.color.blue)
				: getResources().getColor(R.color.gray_text));
		name.setTextColor(isChecked ? getResources().getColor(R.color.blue)
				: getResources().getColor(R.color.gray_text));
	}

	public boolean isChecked() {
		return checked;
	}

	public void setText(String titleStr, String nameStr) {
		title.setText(titleStr);
		name.setText(nameStr);
	}

	@Override
	public void onClick(View v) {
		setChecked(true);
	}

	public interface OnClickTouch {
		void onClick(View view);
	}

}
