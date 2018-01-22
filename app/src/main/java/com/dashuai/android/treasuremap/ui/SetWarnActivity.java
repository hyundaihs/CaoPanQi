package com.dashuai.android.treasuremap.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.dashuai.android.treasuremap.CPQApplication;
import com.dashuai.android.treasuremap.Constant;
import com.dashuai.android.treasuremap.R;
import com.dashuai.android.treasuremap.db.StockDao;
import com.dashuai.android.treasuremap.util.DialogUtil;
import com.dashuai.android.treasuremap.util.SPUtil;

public class SetWarnActivity extends Activity implements
		OnCheckedChangeListener {

	private int position;
	private TextView name, currPrice, increase;
	private TextView empty, date, p1, p2, p3, s1, s2, s3;
	private Switch risePriceSwitch, fallPriceSwitch, riseIncreaseSwitch,
			fallIncreaseSwitch, predictionSwitch;
	private EditText risePriceEdit, fallPriceEdit, riseIncreaseEdit,
			fallIncreaseEdit;
	private SPUtil spUtil;
	private DialogUtil dialogUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_warn);
		CPQApplication.initActionBar(this, true, "预警设置", "提交",
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						submit();
					}
				});
		init();
	}

	@Override
	protected void onResume() {
		super.onResume();
		CPQApplication.isDetailsOpen = true;
		IntentFilter filter_dynamic = new IntentFilter();
		filter_dynamic.addAction(Constant.ACTION_STOCK_DETAILS);
		registerReceiver(dynamicReceiver, filter_dynamic);
	}

	@Override
	protected void onStop() {
		CPQApplication.isDetailsOpen = false;
		unregisterReceiver(dynamicReceiver);
		super.onStop();
	}

	private void init() {
		spUtil = new SPUtil(this);
		dialogUtil = new DialogUtil(this);
		position = getIntent().getIntExtra("position", -1);
		if (position >= 0) {
			CPQApplication.stockDetails = CPQApplication.protfolios
					.get(position);
		}
		name = (TextView) findViewById(R.id.set_warn_name);
		currPrice = (TextView) findViewById(R.id.set_warn_currPrice);
		increase = (TextView) findViewById(R.id.set_warn_increase);
		empty = (TextView) findViewById(R.id.set_warn_empty);
		date = (TextView) findViewById(R.id.set_warn_date);
		p1 = (TextView) findViewById(R.id.set_warn_p1);
		p2 = (TextView) findViewById(R.id.set_warn_p2);
		p3 = (TextView) findViewById(R.id.set_warn_p3);
		s1 = (TextView) findViewById(R.id.set_warn_s1);
		s2 = (TextView) findViewById(R.id.set_warn_s2);
		s3 = (TextView) findViewById(R.id.set_warn_s3);
		risePriceEdit = (EditText) findViewById(R.id.rise_price_edit);
		risePriceSwitch = (Switch) findViewById(R.id.rise_price_switch);
		fallPriceEdit = (EditText) findViewById(R.id.fall_price_edit);
		fallPriceSwitch = (Switch) findViewById(R.id.fall_price_switch);
		riseIncreaseEdit = (EditText) findViewById(R.id.rise_increase_edit);
		riseIncreaseSwitch = (Switch) findViewById(R.id.rise_increase_switch);
		fallIncreaseEdit = (EditText) findViewById(R.id.fall_increase_edit);
		fallIncreaseSwitch = (Switch) findViewById(R.id.fall_increase_switch);
		predictionSwitch = (Switch) findViewById(R.id.prediction_switch);
		initViews();
	}

	private void initViews() {
		risePriceEdit.setText(CPQApplication.stockDetails.getRisePrice() + "");
		fallPriceEdit.setText(CPQApplication.stockDetails.getFallPrice() + "");
		riseIncreaseEdit.setText(CPQApplication.stockDetails.getRiseIncrease()
				+ "");
		fallIncreaseEdit.setText(CPQApplication.stockDetails.getFallIncrease()
				+ "");

		risePriceSwitch.setOnCheckedChangeListener(this);
		fallPriceSwitch.setOnCheckedChangeListener(this);
		riseIncreaseSwitch.setOnCheckedChangeListener(this);
		fallIncreaseSwitch.setOnCheckedChangeListener(this);
		predictionSwitch.setOnCheckedChangeListener(this);

		risePriceSwitch
				.setChecked(CPQApplication.stockDetails.getRisePrice() <= 0 ? false
						: true);
		fallPriceSwitch
				.setChecked(CPQApplication.stockDetails.getFallPrice() <= 0 ? false
						: true);
		riseIncreaseSwitch.setChecked(CPQApplication.stockDetails
				.getRiseIncrease() == 0 ? false : true);
		fallIncreaseSwitch.setChecked(CPQApplication.stockDetails
				.getFallIncrease() == 0 ? false : true);
		predictionSwitch.setChecked(CPQApplication.stockDetails
				.getHistoryWarn() == 1 ? true : false);

		empty.setText(CPQApplication.half_up(CPQApplication.stockDetails
				.getDk()));
		date.setText(CPQApplication.stockDetails.getYcday());
		p1.setText(CPQApplication.half_up(CPQApplication.stockDetails.getP1()));
		p2.setText(CPQApplication.half_up(CPQApplication.stockDetails.getP2()));
		p3.setText(CPQApplication.half_up(CPQApplication.stockDetails.getP3()));
		s1.setText(CPQApplication.half_up(CPQApplication.stockDetails.getS1()));
		s2.setText(CPQApplication.half_up(CPQApplication.stockDetails.getS2()));
		s3.setText(CPQApplication.half_up(CPQApplication.stockDetails.getS3()));
		fillViews();
	}

	private void fillViews() {
		name.setText(CPQApplication.stockDetails.getName());
		currPrice.setText(CPQApplication.half_up(CPQApplication.stockDetails
				.getDq()));
		double value = CPQApplication.stockDetails.getZdf();
		if (value > 0) {
			increase.setText("+" + CPQApplication.half_up(value) + "%");
			increase.setTextColor(getResources().getColor(R.color.orange));
			currPrice.setTextColor(getResources().getColor(R.color.orange));
		} else if (value == 0) {
			increase.setText(CPQApplication.half_up(value) + "%");
			increase.setTextColor(getResources()
					.getColor(android.R.color.white));
			currPrice.setTextColor(getResources().getColor(
					android.R.color.white));
		} else {
			increase.setText(CPQApplication.half_up(value) + "%");
			increase.setTextColor(getResources().getColor(R.color.green));
			currPrice.setTextColor(getResources().getColor(R.color.green));
		}

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.rise_price_switch:
			risePriceEdit.setEnabled(isChecked);
			break;
		case R.id.fall_price_switch:
			fallPriceEdit.setEnabled(isChecked);
			break;
		case R.id.rise_increase_switch:
			riseIncreaseEdit.setEnabled(isChecked);
			break;
		case R.id.fall_increase_switch:
			fallIncreaseEdit.setEnabled(isChecked);
			break;
		default:
			break;
		}
	}

	private void submit() {
		if (risePriceSwitch.isChecked()
				&& risePriceEdit.getText().toString().trim().length() <= 0) {
			dialogUtil.setErrorMessage("上涨价格不能为空");
			return;
		} else if (fallPriceSwitch.isChecked()
				&& fallPriceEdit.getText().toString().trim().length() <= 0) {
			dialogUtil.setErrorMessage("下跌价格不能为空");
			return;
		} else if (riseIncreaseSwitch.isChecked()
				&& riseIncreaseEdit.getText().toString().trim().length() <= 0) {
			dialogUtil.setErrorMessage("涨幅不能为空");
			return;
		} else if (fallIncreaseSwitch.isChecked()
				&& fallIncreaseEdit.getText().toString().trim().length() <= 0) {
			dialogUtil.setErrorMessage("跌幅不能为空");
			return;
		} else {
			CPQApplication.stockDetails.setRisePrice(risePriceSwitch
					.isChecked() ? Double.parseDouble(risePriceEdit.getText()
					.toString()) : 0);
			CPQApplication.stockDetails.setFallPrice(fallPriceSwitch
					.isChecked() ? Double.parseDouble(fallPriceEdit.getText()
					.toString()) : 0);
			CPQApplication.stockDetails.setRiseIncrease(riseIncreaseSwitch
					.isChecked() ? Double.parseDouble(riseIncreaseEdit
					.getText().toString()) : 0);
			CPQApplication.stockDetails.setFallIncrease(fallIncreaseSwitch
					.isChecked() ? -Double.parseDouble(fallIncreaseEdit
					.getText().toString()) : 0);
			CPQApplication.stockDetails.setHistoryWarn(predictionSwitch
					.isChecked() ? 1 : 0);
			// if (predictionSwitch.isChecked()) {
			// stock.setP1(Double.parseDouble(p1.getText().toString()));
			// stock.setP2(Double.parseDouble(p2.getText().toString()));
			// stock.setP3(Double.parseDouble(p3.getText().toString()));
			// stock.setS1(Double.parseDouble(s1.getText().toString()));
			// stock.setS2(Double.parseDouble(s2.getText().toString()));
			// stock.setS3(Double.parseDouble(s3.getText().toString()));
			// } else {
			// stock.setP1(0);
			// stock.setP2(0);
			// stock.setP3(0);
			// stock.setS1(0);
			// stock.setS2(0);
			// stock.setS3(0);
			// }
			new StockDao(CPQApplication.getDB())
					.update(CPQApplication.stockDetails);
			spUtil.setWarn(CPQApplication.stockDetails.getCode() + "risePrice_"
					+ CPQApplication.stockDetails.getRisePrice(), false);
			spUtil.setWarn(CPQApplication.stockDetails.getCode() + "fallPrice_"
					+ CPQApplication.stockDetails.getFallPrice(), false);
			spUtil.setWarn(
					CPQApplication.stockDetails.getCode() + "riseIncrease_"
							+ CPQApplication.stockDetails.getRiseIncrease(),
					false);
			spUtil.setWarn(
					CPQApplication.stockDetails.getCode() + "fallIncrease_"
							+ CPQApplication.stockDetails.getFallIncrease(),
					false);
			// 设置平仓建仓报警归零，即设置为当前值
			spUtil.setWarnText(CPQApplication.stockDetails.getCode(),
					CPQApplication.stockDetails.getDq());
			/**
			 * 设置是否有开启报警
			 */
			spUtil.setWarn(
					CPQApplication.stockDetails.getCode(),
					CPQApplication.stockDetails.getRisePrice() > 0
							|| CPQApplication.stockDetails.getFallPrice() > 0
							|| CPQApplication.stockDetails.getRiseIncrease() > 0
							|| CPQApplication.stockDetails.getFallIncrease() < 0
							|| CPQApplication.stockDetails.getHistoryWarn() == 1);
			CPQApplication.setStocks();
			finish();
		}
	}

	private BroadcastReceiver dynamicReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Constant.ACTION_STOCK_DETAILS)) {
				fillViews();
			}
		}
	};
}
