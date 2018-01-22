package com.dashuai.android.treasuremap.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

public class SellActivity extends Activity implements OnClickListener {

    private HistoryStock historyStock;
    private TextView price, cSellCount, cSellCapital, sellCapital;
    private EditText sellCount, reason;
    private Button all, two, three, six, sell, close;

    private double priceNum, cBuyCapNum, sellCapNum;
    private int cSellCountNum, sellCountNum;

    private double balance;
    private int cSellNum;
    private int stockNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);
        CPQApplication.initActionBar(this, false, "卖出");
        initData();
    }

    private void initData() {
        historyStock = (HistoryStock) getIntent().getExtras().getParcelable("stock");
        if (null == historyStock) {
            return;
        }
        stockNum = getIntent().getIntExtra("stock_num", 0);
        priceNum = historyStock.getHigh() * 0.99;
        cBuyCapNum = balance = getIntent().getDoubleExtra("balance", 0);
        int temp = 0;
        for (int i = 0; i < CPQApplication.records.size(); i++) {
            Record record = CPQApplication.records.get(i);
            if (record.getDate().equals(historyStock.getDay())
                    && record.getHandle() == 1) {
                temp += record.getVolume();
            }
        }
        cSellCountNum = cSellNum = (stockNum - temp);
        initViews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.all:
                sellCount.setText(String.valueOf(cSellNum));
                break;
            case R.id.two:
                sellCount.setText(String.valueOf(cSellNum / 2));
                break;
            case R.id.three:
                sellCount.setText(String.valueOf(cSellNum / 3));
                break;
            case R.id.six:
                sellCount.setText(String.valueOf(cSellNum / 6));
                break;
            case R.id.sell:
                if (sellCountNum <= 0) {
                    new DialogUtil(this).setErrorMessage("卖出数量不能小于0");
                    return;
                }
                saveRecord();
                Intent intent = new Intent();
                intent.putExtra("stock_num", cSellCountNum);
                intent.putExtra("balance", cBuyCapNum);
                setResult(Constant.SELL, intent);
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
        record.setHandle(Constant.SELL);
        record.setPrice(priceNum);
        record.setVolume(sellCountNum);
        record.setTurnover(sellCountNum * historyStock.getHigh() * 0.99);
        record.setReason(reason.getText().toString());
        // recordDao.add(recordDao.tabName, record, CPQApplication.getDB());
        CPQApplication.records.add(0,record);
    }

    private void initViews() {
        price = (TextView) findViewById(R.id.sell_price);
        cSellCount = (TextView) findViewById(R.id.cansell_count);
        cSellCapital = (TextView) findViewById(R.id.cansell_capital);
        sellCount = (EditText) findViewById(R.id.sell_count);
        sellCapital = (TextView) findViewById(R.id.sell_capital);
        reason = (EditText) findViewById(R.id.sell_reason);
        all = (Button) findViewById(R.id.all);
        two = (Button) findViewById(R.id.two);
        three = (Button) findViewById(R.id.three);
        six = (Button) findViewById(R.id.six);
        sell = (Button) findViewById(R.id.sell);
        close = (Button) findViewById(R.id.close);
        all.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        six.setOnClickListener(this);
        sell.setOnClickListener(this);
        close.setOnClickListener(this);
        sellCount.addTextChangedListener(new TextWatcher() {

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
                if (count > cSellNum) {
                    sellCount.setTextColor(Color.RED);
                    sellCapital.setTextColor(Color.RED);
                } else {
                    sellCount.setTextColor(Color.WHITE);
                    sellCapital.setTextColor(Color.WHITE);
                }
                count(count);
            }
        });
        fillViews();
    }

    private void count(int sellCount) {
        sellCountNum = sellCount;
        cSellCountNum = cSellNum - sellCountNum;
        sellCapNum = sellCountNum * priceNum;
        cBuyCapNum = balance + sellCapNum;
        fillViews();
    }

    private void fillViews() {
        price.setText(CPQApplication.round(priceNum, 2));
        cSellCount.setText(cSellCountNum + "");
        cSellCapital.setText(CPQApplication.round(cBuyCapNum));
        sellCapital.setText(CPQApplication.round(sellCapNum));
    }
}
