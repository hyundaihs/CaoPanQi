package com.dashuai.android.treasuremap.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.dashuai.android.treasuremap.CPQApplication;
import com.dashuai.android.treasuremap.Constant;
import com.dashuai.android.treasuremap.R;
import com.dashuai.android.treasuremap.db.StockDao;
import com.dashuai.android.treasuremap.entity.HistoryStock;
import com.dashuai.android.treasuremap.entity.Stock;
import com.dashuai.android.treasuremap.util.ArrowUtil;
import com.dashuai.android.treasuremap.util.DialogUtil;
import com.dashuai.android.treasuremap.util.JsonUtil;
import com.dashuai.android.treasuremap.util.RequestUtil;
import com.dashuai.android.treasuremap.util.RequestUtil.Reply;
import com.dashuai.android.treasuremap.util.SPUtil;
import com.dashuai.android.treasuremap.widget.ChartView;
import com.dashuai.android.treasuremap.widget.LineChart;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockDetailsTVActivity extends FragmentActivity implements Reply, OnClickListener {

    private TextView currPrice, zde, zdf, open, close, empty, date, high, low;
    private TextView p1, p2, p3;
    private TextView s1, s2, s3;
    private TextView p1_w, p2_w, p3_w;
    private TextView s1_w, s2_w, s3_w;
    private TextView p1_m, p2_m, p3_m;
    private TextView s1_m, s2_m, s3_m;
    private TextView statu, statu_w, statu_m;
    private Button add;
    private RequestUtil requestUtil;
    // private MyLoadingView loadingView;
    private ArrayList<HistoryStock> data;
    private ArrayList<HistoryStock> dataD;
    private ArrayList<HistoryStock> dataW;
    private ArrayList<HistoryStock> dataM;
    private ListView listView;
    private MyAdapter adapter;

    private ChartView press, support;
    private LineChart lineChart;
    private Button changeModel;
    private boolean isSeries;
    private StockDao stockDao;
    private int index;
    private Button last, next;
    private int type = 0;//0 day,1 week,2 month;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details_tv);
        initViews();
        show();
    }

    private void initViews() {
        index = getIntent().getIntExtra("index", -1);
        stockDao = new StockDao(CPQApplication.getDB());
        requestUtil = new RequestUtil(this, this);
        data = new ArrayList<HistoryStock>();
        dataD = new ArrayList<HistoryStock>();
        dataW = new ArrayList<HistoryStock>();
        dataM = new ArrayList<HistoryStock>();
        currPrice = (TextView) findViewById(R.id.stock_details_currprice);
        zde = (TextView) findViewById(R.id.stock_details_zde);
        zdf = (TextView) findViewById(R.id.stock_details_zdf);
        add = (Button) findViewById(R.id.stock_details_add);
        open = (TextView) findViewById(R.id.stock_details_open);
        close = (TextView) findViewById(R.id.stock_details_close);
        empty = (TextView) findViewById(R.id.stock_details_empty);
        date = (TextView) findViewById(R.id.stock_details_date);
        p1 = (TextView) findViewById(R.id.stock_details_p1);
        p2 = (TextView) findViewById(R.id.stock_details_p2);
        p3 = (TextView) findViewById(R.id.stock_details_p3);
        s1 = (TextView) findViewById(R.id.stock_details_s1);
        s2 = (TextView) findViewById(R.id.stock_details_s2);
        s3 = (TextView) findViewById(R.id.stock_details_s3);
        p1_w = (TextView) findViewById(R.id.stock_details_p1_w);
        p2_w = (TextView) findViewById(R.id.stock_details_p2_w);
        p3_w = (TextView) findViewById(R.id.stock_details_p3_w);
        s1_w = (TextView) findViewById(R.id.stock_details_s1_w);
        s2_w = (TextView) findViewById(R.id.stock_details_s2_w);
        s3_w = (TextView) findViewById(R.id.stock_details_s3_w);
        p1_m = (TextView) findViewById(R.id.stock_details_p1_m);
        p2_m = (TextView) findViewById(R.id.stock_details_p2_m);
        p3_m = (TextView) findViewById(R.id.stock_details_p3_m);
        s1_m = (TextView) findViewById(R.id.stock_details_s1_m);
        s2_m = (TextView) findViewById(R.id.stock_details_s2_m);
        s3_m = (TextView) findViewById(R.id.stock_details_s3_m);
        high = (TextView) findViewById(R.id.stock_details_high);
        low = (TextView) findViewById(R.id.stock_details_low);
        statu = (TextView) findViewById(R.id.stock_details_statu);
        statu_w = (TextView) findViewById(R.id.stock_details_statu_w);
        statu_m = (TextView) findViewById(R.id.stock_details_statu_m);
        last = (Button) findViewById(R.id.stock_details_last);
        next = (Button) findViewById(R.id.stock_details_next);
        statu.setOnClickListener(this);
        statu_w.setOnClickListener(this);
        statu_m.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.stock_details_statu:
                type = 0;
                break;
            case R.id.stock_details_statu_w:
                type = 1;
                break;
            case R.id.stock_details_statu_m:
                type = 2;
                break;
        }
        refreshViews();
    }

    private void refresh(int num) {
        index = index + num;
        show();
    }

    private void switchButtons() {
        last.setVisibility(index > 0
                && index <= CPQApplication.getDetailsResourceCount() - 1 ? View.VISIBLE
                : View.GONE);
        next.setVisibility(index >= 0
                && index < CPQApplication.getDetailsResourceCount() - 1 ? View.VISIBLE
                : View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CPQApplication.isDetailsOpen = true;
        IntentFilter filter_dynamic = new IntentFilter();
        filter_dynamic.addAction(Constant.ACTION_STOCK_DETAILS);
        filter_dynamic.addAction(Constant.ACTION_NET_UNCONNECT);
        registerReceiver(dynamicReceiver, filter_dynamic);
    }

    @Override
    protected void onStop() {
        CPQApplication.isDetailsOpen = false;
        unregisterReceiver(dynamicReceiver);
        super.onStop();
    }

    private void show() {
        if (index < 0) {
            CPQApplication.stockDetails = (Stock) getIntent().getExtras()
                    .getParcelable("stock");
        } else if (index < CPQApplication.getDetailsResourceCount()) {
            CPQApplication.stockDetails = CPQApplication.getDetailsStock(index);
        } else {
            return;
        }
        CPQApplication.initActionBar(this, true,
                CPQApplication.stockDetails.getName() + "("
                        + CPQApplication.stockDetails.getCode() + ")", "预警",
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(StockDetailsTVActivity.this,
                                SetWarnActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("stock", CPQApplication.stockDetails);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });

        last.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                refresh(-1);
            }
        });
        next.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                refresh(1);
            }
        });
        // loadingView = (MyLoadingView)
        // findViewById(R.id.stock_details_loadingView);
        switchButtons();

        press = (ChartView) findViewById(R.id.chart_press);
        support = (ChartView) findViewById(R.id.chart_support);
        changeModel = (Button) findViewById(R.id.changeModel);
        lineChart = (LineChart) findViewById(R.id.chart_linechart);

        changeModel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (changeModel.getText().equals("连续")) {
                    isSeries = true;
                } else {
                    isSeries = false;
                }
                initData();
                changeModel.setText(changeModel.getText().equals("连续") ? "全部"
                        : "连续");
            }
        });

        listView = (ListView) findViewById(R.id.stock_details_listview);
        adapter = new MyAdapter(this, data);
        listView.setAdapter(adapter);
        fillViews();
        getData();
    }

    private void initData() {
        List<HistoryStock> list = new ArrayList<HistoryStock>();
        for (int i = 0; i < (data.size() >= 30 ? 30 : data.size()); i++) {
            list.add(data.get(i));
        }
        press.setData(list);
        press.setSeries(isSeries);
        press.setPress(true);
        support.setData(list);
        support.setSeries(isSeries);
        support.setPress(false);
        initLineChart(list);
    }

    private void initLineChart(List<HistoryStock> list) {
        List<String> hData = new ArrayList<String>();
        List<Double> vData = new ArrayList<Double>();
        for (int i = 0; i < list.size(); i++) {
            hData.add(String.valueOf(i + 1));
            vData.add(list.get(i).getZf());
        }
        lineChart.setvData(vData);
        lineChart.sethData(hData);
    }

    @SuppressWarnings("deprecation")
    private void fillViews() {
        add.setText(stockDao.isColleted(CPQApplication.stockDetails.getCode()) ? "取消\n自选"
                : "加入\n自选");
        add.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (add.getText().toString().equals("加入\n自选")) {
                    if (CPQApplication.protfolios.size() >= 50) {
                        new DialogUtil(StockDetailsTVActivity.this).setErrorMessage("最多添加50只自选股");
                        return;
                    }
                    CPQApplication.stockDetails.setRisePrice(0);
                    CPQApplication.stockDetails.setFallPrice(0);
                    CPQApplication.stockDetails.setRiseIncrease(0);
                    CPQApplication.stockDetails.setFallIncrease(0);
                    CPQApplication.stockDetails.setHistoryWarn(1);

                    stockDao.add( CPQApplication.stockDetails);
                    new SPUtil(StockDetailsTVActivity.this).setWarn(
                            CPQApplication.stockDetails.getCode(), true);
                    add.setText("取消\n自选");
                } else {
                    new StockDao(CPQApplication.getDB())
                            .deleteByCode(CPQApplication.stockDetails.getCode());
                    add.setText("加入\n自选");
                }
                CPQApplication.setStocks();
            }
        });
        currPrice.setText(ArrowUtil.getStockPrice(this, CPQApplication.stockDetails));
        double increaseValue = CPQApplication.stockDetails.getZdf();
        zde.setText(CPQApplication.half_up(CPQApplication.stockDetails.getZde()));
        if (increaseValue < 0) {
            zdf.setText(CPQApplication.half_up(increaseValue) + "%");
            currPrice.setTextColor(getResources().getColor(R.color.green));
            zdf.setTextColor(getResources().getColor(R.color.green));
            zde.setTextColor(getResources().getColor(R.color.green));
        } else if (increaseValue == 0) {
            zdf.setText("+" + CPQApplication.half_up(increaseValue) + "%");
            currPrice.setTextColor(getResources().getColor(
                    android.R.color.white));
            zdf.setTextColor(getResources().getColor(android.R.color.white));
            zde.setTextColor(getResources().getColor(android.R.color.white));
        } else {
            zdf.setText("+" + CPQApplication.half_up(increaseValue) + "%");
            currPrice.setTextColor(getResources().getColor(R.color.orange));
            zdf.setTextColor(getResources().getColor(R.color.orange));
            zde.setTextColor(getResources().getColor(R.color.orange));
        }
        open.setText(CPQApplication.half_up(CPQApplication.stockDetails.getKp()));
        close.setText(CPQApplication.half_up(CPQApplication.stockDetails
                .getZrsp()));
        empty.setText(CPQApplication.half_up(CPQApplication.stockDetails
                .getDk()));
        p1.setText(CPQApplication.half_up(CPQApplication.stockDetails.getP1()));
        p2.setText(CPQApplication.half_up(CPQApplication.stockDetails.getP2()));
        p3.setText(CPQApplication.half_up(CPQApplication.stockDetails.getP3()));
        s1.setText(CPQApplication.half_up(CPQApplication.stockDetails.getS1()));
        s2.setText(CPQApplication.half_up(CPQApplication.stockDetails.getS2()));
        s3.setText(CPQApplication.half_up(CPQApplication.stockDetails.getS3()));
        p1_w.setText(CPQApplication.half_up(CPQApplication.stockDetails.getP1_w()));
        p2_w.setText(CPQApplication.half_up(CPQApplication.stockDetails.getP2_w()));
        p3_w.setText(CPQApplication.half_up(CPQApplication.stockDetails.getP3_w()));
        s1_w.setText(CPQApplication.half_up(CPQApplication.stockDetails.getS1_w()));
        s2_w.setText(CPQApplication.half_up(CPQApplication.stockDetails.getS2_w()));
        s3_w.setText(CPQApplication.half_up(CPQApplication.stockDetails.getS3_w()));
        p1_m.setText(CPQApplication.half_up(CPQApplication.stockDetails.getP1_m()));
        p2_m.setText(CPQApplication.half_up(CPQApplication.stockDetails.getP2_m()));
        p3_m.setText(CPQApplication.half_up(CPQApplication.stockDetails.getP3_m()));
        s1_m.setText(CPQApplication.half_up(CPQApplication.stockDetails.getS1_m()));
        s2_m.setText(CPQApplication.half_up(CPQApplication.stockDetails.getS2_m()));
        s3_m.setText(CPQApplication.half_up(CPQApplication.stockDetails.getS3_m()));
        date.setText(CPQApplication.stockDetails.getYcday());
        high.setText(CPQApplication.half_up(CPQApplication.stockDetails
                .getHigh()));
        low.setText(CPQApplication.half_up(CPQApplication.stockDetails.getLow()));
        int status = CPQApplication.stockDetails.getBeizhu();
        int status_w = CPQApplication.stockDetails.getBeizhu_w();
        int status_m = CPQApplication.stockDetails.getBeizhu_m();

//		statu.setText(Constant.getStatus(status)
//				+ (CPQApplication.stockDetails.isGZ()
//						&& (CPQApplication.stockDetails.isXC() || CPQApplication.stockDetails
//								.isDT()) ? "**" : (CPQApplication.stockDetails
//						.getIs_sf() == 1 ? "*" : "")));
        statu.setText(ArrowUtil.getColorString(CPQApplication.stockDetails.isHZ() ? "*" : " ", "884898", Constant.getStatus(status)
                + (CPQApplication.stockDetails.isGZ()
                && (CPQApplication.stockDetails.isXC() || CPQApplication.stockDetails
                .isDT()) ? "**" : (CPQApplication.stockDetails
                .getIs_sf() == 1 ? "*" : "")), true));
        statu_w.setText(ArrowUtil.getColorString(CPQApplication.stockDetails.isHZ() ? "*" : " ", "884898", Constant.getStatus(status_w)
                + (CPQApplication.stockDetails.isGZ()
                && (CPQApplication.stockDetails.isXC() || CPQApplication.stockDetails
                .isDT()) ? "**" : (CPQApplication.stockDetails
                .getIs_sf() == 1 ? "*" : "")), true));
        statu_m.setText(ArrowUtil.getColorString(CPQApplication.stockDetails.isHZ() ? "*" : " ", "884898", Constant.getStatus(status_m)
                + (CPQApplication.stockDetails.isGZ()
                && (CPQApplication.stockDetails.isXC() || CPQApplication.stockDetails
                .isDT()) ? "**" : (CPQApplication.stockDetails
                .getIs_sf() == 1 ? "*" : "")), true));
    }

    private class MyAdapter extends BaseAdapter {
        private Context context;
        private List<HistoryStock> data;

        public MyAdapter(Context context, List<HistoryStock> data) {
            this.context = context;
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public HistoryStock getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class ViewHoler {
            TextView date, high, low, close, amplitude, empty;
        }

        @SuppressWarnings("deprecation")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHoler viewHoler;
            if (null == convertView) {
                viewHoler = new ViewHoler();
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.layout_stock_details_item, parent, false);
                viewHoler.date = (TextView) convertView
                        .findViewById(R.id.stock_details_item_date);
                viewHoler.high = (TextView) convertView
                        .findViewById(R.id.stock_details_item_high);
                viewHoler.low = (TextView) convertView
                        .findViewById(R.id.stock_details_item_low);
                viewHoler.close = (TextView) convertView
                        .findViewById(R.id.stock_details_item_close);
                viewHoler.amplitude = (TextView) convertView
                        .findViewById(R.id.stock_details_item_amplitude);
                viewHoler.empty = (TextView) convertView
                        .findViewById(R.id.stock_details_item_empty);
                convertView.setTag(viewHoler);
            } else {
                viewHoler = (ViewHoler) convertView.getTag();
            }
            HistoryStock historyStock = getItem(position);
            viewHoler.date.setText(historyStock.getDay().substring(5));
            viewHoler.high.setText(CPQApplication.half_up(historyStock
                    .getHigh()));
            viewHoler.low
                    .setText(CPQApplication.half_up(historyStock.getLow()));
            viewHoler.close.setText(CPQApplication.half_up(historyStock
                    .getClose()));
            viewHoler.amplitude.setText(CPQApplication.half_up(historyStock
                    .getZf()) + "%");
            viewHoler.empty
                    .setText(CPQApplication.half_up(historyStock.getJj()));
            TextPaint tp;
            switch (historyStock.getHeightstatus()) {
                case 0:
                    viewHoler.high.setTextColor(getResources().getColor(
                            android.R.color.white));
                    tp = viewHoler.high.getPaint();
                    tp.setFakeBoldText(false);
                    break;
                case 1:
                    viewHoler.high.setTextColor(getResources().getColor(
                            R.color.orange));
                    tp = viewHoler.high.getPaint();
                    tp.setFakeBoldText(false);
                    break;
                case 2:
                    viewHoler.high.setTextColor(getResources().getColor(
                            R.color.orange));
                    tp = viewHoler.high.getPaint();
                    tp.setFakeBoldText(true);
                    break;
                default:
                    break;
            }
            if (historyStock.isTk()) {
                viewHoler.high.setBackgroundResource(R.drawable.white_stroke);
            } else if (historyStock.isIshighseries()) {
                viewHoler.high.setBackgroundResource(R.drawable.red_stroke);
            } else {
                viewHoler.high
                        .setBackgroundResource(android.R.color.transparent);
            }
            switch (historyStock.getLowstatus()) {
                case 0:
                    viewHoler.low.setTextColor(getResources().getColor(
                            android.R.color.white));
                    tp = viewHoler.low.getPaint();
                    tp.setFakeBoldText(false);
                    break;
                case 1:
                    viewHoler.low.setTextColor(getResources().getColor(
                            R.color.green));
                    tp = viewHoler.low.getPaint();
                    tp.setFakeBoldText(false);
                    break;
                case 2:
                    viewHoler.low.setTextColor(getResources().getColor(
                            android.R.color.holo_blue_dark));
                    tp = viewHoler.low.getPaint();
                    tp.setFakeBoldText(true);
                    break;
                default:
                    break;
            }
            if (historyStock.isIslowseries()) {
                viewHoler.low.setBackgroundResource(R.drawable.green_stroke);
            } else {
                viewHoler.low
                        .setBackgroundResource(android.R.color.transparent);
            }
            return convertView;
        }
    }

    private void getData() {
        // loadingView.showProgress();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("codes", CPQApplication.stockDetails.getCode());
        requestUtil.postRequest(Constant.URL_IP + Constant.HISTORY_BY_CODES,
                map, 0);
        requestUtil.postRequest(Constant.URL_IP + Constant.HISTORY_BY_CODES_MW,
                map, 1);
    }

    private BroadcastReceiver dynamicReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constant.ACTION_STOCK_DETAILS)) {
                // loadingView.hide();
                fillViews();
            } else if (intent.getAction().equals(Constant.ACTION_NET_UNCONNECT)) {
                String errorStr = intent.getStringExtra("error");
                // loadingView.showError(errorStr);
            } else if (intent.getAction().equals(Constant.ACTION_LOADING)) {
                // loadingView.showProgress();
            }
        }
    };

    @Override
    public void onSuccess(JSONObject response, int what) {
        // loadingView.hide();
        if (what == 0) {
            dataD.clear();
            dataD.addAll(JsonUtil.getHistoryStocks(response));
        } else {
            dataW.clear();
            dataM.clear();
            dataW.addAll(JsonUtil.getWeekHistoryStocks(response));
            dataM.addAll(JsonUtil.getMonthHistoryStocks(response));
        }
        refreshViews();
    }
    private void refreshViews(){
        data.clear();
        switch (type) {
            case 1:
                data.addAll(dataW);
                break;
            case 2:
                data.addAll(dataM);
                break;
            default:
                data.addAll(dataD);
                break;
        }
        adapter.notifyDataSetChanged();
        initData();
    }

    @Override
    public void onErrorResponse(String errorStr, int what) {
        // loadingView.showError(errorStr);
    }

    @Override
    public void onFailed(String errorStr, int what) {
        // loadingView.showError(errorStr);
    }

}
