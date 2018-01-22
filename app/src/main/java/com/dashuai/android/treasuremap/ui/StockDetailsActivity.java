package com.dashuai.android.treasuremap.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dashuai.android.treasuremap.CPQApplication;
import com.dashuai.android.treasuremap.Constant;
import com.dashuai.android.treasuremap.R;
import com.dashuai.android.treasuremap.adapter.HistoryInfoIndicatorAdapter;
import com.dashuai.android.treasuremap.db.StockDao;
import com.dashuai.android.treasuremap.entity.HistoryStock;
import com.dashuai.android.treasuremap.entity.Stock;
import com.dashuai.android.treasuremap.util.ArrowUtil;
import com.dashuai.android.treasuremap.util.JsonUtil;
import com.dashuai.android.treasuremap.util.RequestUtil;
import com.dashuai.android.treasuremap.util.RequestUtil.Reply;
import com.dashuai.android.treasuremap.util.SPUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockDetailsActivity extends FragmentActivity implements Reply {

    private TextView currPrice, zde, zdf, open, close, empty, date, p1, p2, p3,
            s1, s2, s3, high, low, statu;
    private Button add;
    private RequestUtil requestUtil;
    // private MyLoadingView loadingView;
    private ArrayList<HistoryStock> dataD;
    private ArrayList<HistoryStock> dataW;
    private ArrayList<HistoryStock> dataM;
    private StockDao stockDao;
    private ViewPager divinePager;
    private int index;
    private Button last, next;
    private List<View> viewList;
    private MyViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details);
        initViews();
        show();
    }

    private void initViews() {
        index = getIntent().getIntExtra("index", -1);
        stockDao = new StockDao(CPQApplication.getDB());
        requestUtil = new RequestUtil(this, this);
        dataD = new ArrayList<HistoryStock>();
        dataW = new ArrayList<HistoryStock>();
        dataM = new ArrayList<HistoryStock>();
        currPrice = (TextView) findViewById(R.id.stock_details_currprice);
        zde = (TextView) findViewById(R.id.stock_details_zde);
        zdf = (TextView) findViewById(R.id.stock_details_zdf);
        add = (Button) findViewById(R.id.stock_details_add);
        open = (TextView) findViewById(R.id.stock_details_open);
        close = (TextView) findViewById(R.id.stock_details_close);
        divinePager = (ViewPager) findViewById(R.id.stock_details_divine_pager);
        viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
        for (int i = 0; i < 3; i++) {
            viewList.add(View.inflate(this, R.layout.stock_details_divine, null));
        }
        high = (TextView) findViewById(R.id.stock_details_high);
        low = (TextView) findViewById(R.id.stock_details_low);
        last = (Button) findViewById(R.id.stock_details_last);
        next = (Button) findViewById(R.id.stock_details_next);
        adapter = new MyViewPagerAdapter(viewList);
        divinePager.setAdapter(adapter);
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
        super.onStop();
        CPQApplication.isDetailsOpen = false;
        unregisterReceiver(dynamicReceiver);
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
                        Intent intent = new Intent(StockDetailsActivity.this,
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
        fillViews();
        getData();
    }

    private void fillViews() {
        add.setText(stockDao.isColleted(CPQApplication.stockDetails.getCode()) ? "取消\n自选"
                : "加入\n自选");
        add.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (add.getText().toString().equals("加入\n自选")) {
                    CPQApplication.stockDetails.setRisePrice(0);
                    CPQApplication.stockDetails.setFallPrice(0);
                    CPQApplication.stockDetails.setRiseIncrease(0);
                    CPQApplication.stockDetails.setFallIncrease(0);
                    CPQApplication.stockDetails.setHistoryWarn(1);

                    stockDao.add(CPQApplication.stockDetails);
                    stockDao.update(CPQApplication.stockDetails);
                    new SPUtil(StockDetailsActivity.this).setWarn(
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
        //currPrice.setText(ArrowUtil.getStockPrice(this, CPQApplication.stockDetails));
        currPrice.setText(CPQApplication.half_up(CPQApplication.stockDetails.getDq()));
        double increaseValue = CPQApplication.stockDetails.getZdf();
        if (increaseValue < 0) {
            currPrice.setTextColor(getResources().getColor(R.color.green));
            zdf.setText(CPQApplication.half_up(increaseValue) + "%");
            zdf.setTextColor(getResources().getColor(R.color.green));
            zde.setTextColor(getResources().getColor(R.color.green));
            zde.setText(CPQApplication.half_up(CPQApplication.stockDetails
                    .getZde()));
        } else if (increaseValue == 0) {
            currPrice.setTextColor(getResources().getColor(
                    android.R.color.white));
            zdf.setText(CPQApplication.half_up(increaseValue) + "%");
            zdf.setTextColor(getResources().getColor(android.R.color.white));
            zde.setTextColor(getResources().getColor(android.R.color.white));
            zde.setText(CPQApplication.half_up(CPQApplication.stockDetails
                    .getZde()));
        } else {
            currPrice.setTextColor(getResources().getColor(R.color.orange));
            zdf.setText("+" + CPQApplication.half_up(increaseValue) + "%");
            zdf.setTextColor(getResources().getColor(R.color.orange));
            zde.setTextColor(getResources().getColor(R.color.orange));
            zde.setText("+"
                    + CPQApplication.half_up(CPQApplication.stockDetails
                    .getZde()));
        }
        open.setText(CPQApplication.half_up(CPQApplication.stockDetails.getKp()));
        close.setText(CPQApplication.half_up(CPQApplication.stockDetails
                .getZrsp()));
        high.setText(CPQApplication.half_up(CPQApplication.stockDetails
                .getHigh()));
        low.setText(CPQApplication.half_up(CPQApplication.stockDetails.getLow()));
        adapter.notifyDataSetChanged();
    }

    public class MyViewPagerAdapter extends PagerAdapter {
        private List<View> mListViews;

        public MyViewPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;//构造方法，参数是我们的页卡，这样比较方便。
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mListViews.get(position));//删除页卡
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {  //这个方法用来实例化页卡
            View view = mListViews.get(position);
            empty = (TextView) view.findViewById(R.id.stock_details_empty);
            date = (TextView) view.findViewById(R.id.stock_details_date);
            p1 = (TextView) view.findViewById(R.id.stock_details_p1);
            p2 = (TextView) view.findViewById(R.id.stock_details_p2);
            p3 = (TextView) view.findViewById(R.id.stock_details_p3);
            s1 = (TextView) view.findViewById(R.id.stock_details_s1);
            s2 = (TextView) view.findViewById(R.id.stock_details_s2);
            s3 = (TextView) view.findViewById(R.id.stock_details_s3);
            statu = (TextView) view.findViewById(R.id.stock_details_statu);
            int status = 0;
            switch (position) {
                case 0:
                    empty.setText(CPQApplication.half_up(CPQApplication.stockDetails
                            .getDk()));
                    p1.setText(CPQApplication.half_up(CPQApplication.stockDetails.getP1()));
                    p2.setText(CPQApplication.half_up(CPQApplication.stockDetails.getP2()));
                    p3.setText(CPQApplication.half_up(CPQApplication.stockDetails.getP3()));
                    s1.setText(CPQApplication.half_up(CPQApplication.stockDetails.getS1()));
                    s2.setText(CPQApplication.half_up(CPQApplication.stockDetails.getS2()));
                    s3.setText(CPQApplication.half_up(CPQApplication.stockDetails.getS3()));
                    date.setText(CPQApplication.stockDetails.getYcday());
                    status = CPQApplication.stockDetails.getBeizhu();
                    break;
                case 1:
                    empty.setText(CPQApplication.half_up(CPQApplication.stockDetails
                            .getDk_w()));
                    p1.setText(CPQApplication.half_up(CPQApplication.stockDetails.getP1_w()));
                    p2.setText(CPQApplication.half_up(CPQApplication.stockDetails.getP2_w()));
                    p3.setText(CPQApplication.half_up(CPQApplication.stockDetails.getP3_w()));
                    s1.setText(CPQApplication.half_up(CPQApplication.stockDetails.getS1_w()));
                    s2.setText(CPQApplication.half_up(CPQApplication.stockDetails.getS2_w()));
                    s3.setText(CPQApplication.half_up(CPQApplication.stockDetails.getS3_w()));
                    date.setText("周");
                    status = CPQApplication.stockDetails.getBeizhu_w();
                    break;
                case 2:
                    empty.setText(CPQApplication.half_up(CPQApplication.stockDetails
                            .getDk_m()));
                    p1.setText(CPQApplication.half_up(CPQApplication.stockDetails.getP1_m()));
                    p2.setText(CPQApplication.half_up(CPQApplication.stockDetails.getP2_m()));
                    p3.setText(CPQApplication.half_up(CPQApplication.stockDetails.getP3_m()));
                    s1.setText(CPQApplication.half_up(CPQApplication.stockDetails.getS1_m()));
                    s2.setText(CPQApplication.half_up(CPQApplication.stockDetails.getS2_m()));
                    s3.setText(CPQApplication.half_up(CPQApplication.stockDetails.getS3_m()));
                    date.setText("月");
                    status = CPQApplication.stockDetails.getBeizhu_m();
                    break;
            }
            statu.setText(ArrowUtil.getColorString(CPQApplication.stockDetails.isHZ() ? "*" : " ", "884898", Constant.STATUS.get(status)
                    + (CPQApplication.stockDetails.isGZ()
                    && (CPQApplication.stockDetails.isXC() || CPQApplication.stockDetails
                    .isDT()) ? "**" : (CPQApplication.stockDetails
                    .getIs_sf() == 1 ? "*" : "")), true));
            switch (status) {
                case Constant.LAYOUT:
                case Constant.PULL_UP:
                case Constant.OPEN_POSITION:
                    statu.setTextColor(getResources().getColor(R.color.green));
                    break;
                case Constant.UNDER_WEIGHT:
                    statu.setTextColor(getResources().getColor(R.color.orange));
                    break;
                default:
                    statu.setTextColor(getResources().getColor(android.R.color.white));
                    break;
            }
            container.addView(view, 0);//添加页卡
            return mListViews.get(position);
        }

        @Override
        public int getCount() {
            return mListViews.size();//返回页卡的数量
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;//官方提示这样写
        }
    }

    private void getData() {
        // loadingView.showProgress();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("codes", CPQApplication.stockDetails.getCode());
        if (CPQApplication.CLIENT == Constant.TEACHER) {
            map.put("pagesize", -1);
        }
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

    private void initViewPager() {
        // ViewPager的adapter
        final FragmentPagerAdapter adapter = new HistoryInfoIndicatorAdapter(
                getSupportFragmentManager(), dataD, dataW, dataM);
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        viewPager.setAdapter(adapter);

        // 实例化TabPageIndicator然后设置ViewPager与之关联
//        TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);
//        indicator.setViewPager(viewPager);
    }

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
        initViewPager();
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
