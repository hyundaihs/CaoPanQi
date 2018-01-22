package com.dashuai.android.treasuremap.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dashuai.android.treasuremap.CPQApplication;
import com.dashuai.android.treasuremap.Constant;
import com.dashuai.android.treasuremap.R;
import com.dashuai.android.treasuremap.entity.HistoryStock;
import com.dashuai.android.treasuremap.entity.Record;
import com.dashuai.android.treasuremap.util.DialogUtil;

public class BuyActivity extends Activity implements OnClickListener {

	private HistoryStock historyStock;
	private TextView price, cBuyCount, cBuyCapital, buyCapital;
	private EditText buyCount, reason;
	private Button all, two, three, six, buy, close;

	private double priceNum, cBuyCapNum, buyCapNum;
	private int cBuyCountNum, buyCountNum;

	private double balance;
	private int cBuyNum;
	private int stockNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buy);
		CPQApplication.initActionBar(this, false, "买入");
		initData();
	}

	private void initData() {
		historyStock = (HistoryStock) getIntent().getExtras().getParcelable("stock");
		if (null == historyStock) {
			return;
		}
		stockNum = getIntent().getIntExtra("stock_num", 0);
		priceNum = historyStock.getLow() * 1.01;
		cBuyCapNum = balance = getIntent().getDoubleExtra("balance", 0);
		cBuyCountNum = cBuyNum = (int) (balance / priceNum);
		initViews();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.all:
			buyCount.setText(String.valueOf(cBuyNum));
			break;
		case R.id.two:
			buyCount.setText(String.valueOf(cBuyNum / 2));
			break;
		case R.id.three:
			buyCount.setText(String.valueOf(cBuyNum / 3));
			break;
		case R.id.six:
			buyCount.setText(String.valueOf(cBuyNum / 6));
			break;
		case R.id.buy:
			if (buyCountNum <= 0) {
				new DialogUtil(this).setErrorMessage("买入数量不能小于0");
				return;
			}
			saveRecord();
			Intent intent = new Intent();
			intent.putExtra("stock_num", (buyCountNum + stockNum));
			intent.putExtra("balance", cBuyCapNum);
			setResult(Constant.BUY, intent);
			finish();
			break;
		case R.id.close:
			finish();
			break;
		default:
			break;
		}
	}

	private void saveRecord() {
		Record record = new Record();
		record.setDate(historyStock.getDay());
		record.setHandle(Constant.BUY);
		record.setPrice(priceNum);
		record.setVolume(buyCountNum);
		record.setTurnover(buyCountNum * historyStock.getLow() * 1.01);
		Log.d("saveRecord", "Turnover = " + record.getTurnover());
		record.setReason(reason.getText().toString());
		// recordDao.add(recordDao.tabName, record, CPQApplication.getDB());
		CPQApplication.records.add(0,record);
	}

	private void initViews() {
		price = (TextView) findViewById(R.id.buy_price);
		cBuyCount = (TextView) findViewById(R.id.canbuy_count);
		cBuyCapital = (TextView) findViewById(R.id.canbuy_capital);
		buyCount = (EditText) findViewById(R.id.buy_count);
		buyCapital = (TextView) findViewById(R.id.buy_capital);
		reason = (EditText) findViewById(R.id.buy_reason);
		all = (Button) findViewById(R.id.all);
		two = (Button) findViewById(R.id.two);
		three = (Button) findViewById(R.id.three);
		six = (Button) findViewById(R.id.six);
		buy = (Button) findViewById(R.id.buy);
		close = (Button) findViewById(R.id.close);
		all.setOnClickListener(this);
		two.setOnClickListener(this);
		three.setOnClickListener(this);
		six.setOnClickListener(this);
		buy.setOnClickListener(this);
		close.setOnClickListener(this);
		buyCount.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				int count = s.length() <= 0 ? 0
						: Integer.parseInt(s.toString());
				if (count > cBuyNum) {
					buyCount.setTextColor(Color.RED);
					buyCapital.setTextColor(Color.RED);
				} else {
					buyCount.setTextColor(Color.WHITE);
					buyCapital.setTextColor(Color.WHITE);
				}
				count(count);
			}
		});
		fillViews();
	}

	private void count(int buyCount) {
		buyCountNum = buyCount;
		cBuyCountNum = cBuyNum - buyCountNum;
		buyCapNum = buyCountNum * priceNum;
		cBuyCapNum = balance - buyCapNum;
		fillViews();
	}

	private void fillViews() {
		price.setText(CPQApplication.round(priceNum, 2));
		cBuyCount.setText(cBuyCountNum + "");
		cBuyCapital.setText(CPQApplication.round(cBuyCapNum));
		buyCapital.setText(CPQApplication.round(buyCapNum));
	}

}
