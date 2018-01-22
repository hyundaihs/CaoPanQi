package com.dashuai.android.treasuremap.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dashuai.android.treasuremap.CPQApplication;
import com.dashuai.android.treasuremap.Constant;
import com.dashuai.android.treasuremap.R;
import com.dashuai.android.treasuremap.entity.Stock;
import com.dashuai.android.treasuremap.util.ArrowUtil;
import com.dashuai.android.treasuremap.util.JsonUtil;
import com.dashuai.android.treasuremap.util.RequestUtil;
import com.dashuai.android.treasuremap.util.RequestUtil.Reply;
import com.dashuai.android.treasuremap.util.SortUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoreBeizhuActivity extends Activity implements Reply, View.OnClickListener {

    private ListView listView;
    private int statue;
    private List<Stock> data;
    private MyAdapter adapter;
    private RequestUtil requestUtil;
    private TextView stock, price, increase, beizhu, zhenF;
    private int sortType = SortUtil.SORT_BY_NONE;
    private boolean isDesc;// 是否倒序

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_beizhu);
        statue = getIntent().getIntExtra("statue", 0);
        CPQApplication.initActionBar(this, true, Constant.STATUS.get(statue));
        initViews();
        init();
    }

    private void initViews() {
        listView = (ListView) findViewById(R.id.more_beizhu_listview);
        stock = (TextView) findViewById(R.id.more_beizhu_stock);
        price = (TextView) findViewById(R.id.more_beizhu_price);
        increase = (TextView) findViewById(R.id.more_beizhu_increase);
        beizhu = (TextView) findViewById(R.id.more_beizhu_statu);
        zhenF = (TextView) findViewById(R.id.more_beizhu_zf);
        stock.setOnClickListener(this);
        price.setOnClickListener(this);
        increase.setOnClickListener(this);
        beizhu.setOnClickListener(this);
        zhenF.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.more_beizhu_stock:
                isDesc = isDesc ? false : true;
                sortType = SortUtil.SORT_BY_STOCK;
                SortUtil.sort(data, SortUtil.SORT_BY_STOCK, isDesc);
                adapter.notifyDataSetChanged();
                break;
            case R.id.more_beizhu_price:
                isDesc = isDesc ? false : true;
                sortType = SortUtil.SORT_BY_PRICE;
                SortUtil.sort(data, SortUtil.SORT_BY_PRICE, isDesc);
                adapter.notifyDataSetChanged();
                break;
            case R.id.more_beizhu_increase:
                isDesc = isDesc ? false : true;
                sortType = SortUtil.SORT_BY_INCREASE;
                SortUtil.sort(data, SortUtil.SORT_BY_INCREASE, isDesc);
                adapter.notifyDataSetChanged();
                break;
            case R.id.more_beizhu_zf:
                isDesc = isDesc ? false : true;
                sortType = SortUtil.SORT_BY_ZHENFU;
                SortUtil.sort(data, SortUtil.SORT_BY_ZHENFU, isDesc);
                adapter.notifyDataSetChanged();
                break;
            case R.id.more_beizhu_statu:
                isDesc = isDesc ? false : true;
                sortType = SortUtil.SORT_BY_STATUS;
                SortUtil.sort(data, SortUtil.SORT_BY_STATUS, isDesc);
                adapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter_dynamic = new IntentFilter();
        filter_dynamic.addAction(Constant.ACTION_MORE_BZ);
        filter_dynamic.addAction(Constant.ACTION_NET_UNCONNECT);
        registerReceiver(dynamicReceiver, filter_dynamic);
    }

    @Override
    public void onStop() {
        super.onStop();
        CPQApplication.setBzcode(-1);
        unregisterReceiver(dynamicReceiver);
    }

    private void init() {
        requestUtil = new RequestUtil(this, this);
        data = new ArrayList<Stock>();
        adapter = new MyAdapter(this, data);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Intent intent = new Intent();
                intent.putExtra("index", arg2);
                CPQApplication.setDetailsResource(data);
                CPQApplication.go2details(intent, MoreBeizhuActivity.this);
            }
        });
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("beizhu", statue);
        requestUtil.postRequest(Constant.URL_IP + Constant.NOW_BY_BEIZHU, map,
                0);
    }

    private class MyAdapter extends BaseAdapter {
        private List<Stock> data;
        private Context context;

        public MyAdapter(Context context, List<Stock> data) {
            this.data = data;
            this.context = context;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Stock getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class ViewHolder {
            TextView name, code, currPrice, increase, statu, zf;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (null == convertView) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.layout_protfolio_item, parent, false);
                viewHolder.name = (TextView) convertView
                        .findViewById(R.id.protfolio_item_name);
                viewHolder.code = (TextView) convertView
                        .findViewById(R.id.protfolio_item_code);
                viewHolder.currPrice = (TextView) convertView
                        .findViewById(R.id.protfolio_item_currPrice);
                viewHolder.increase = (TextView) convertView
                        .findViewById(R.id.protfolio_item_increase);
                viewHolder.statu = (TextView) convertView
                        .findViewById(R.id.protfolio_item_statu);
                viewHolder.zf = convertView.findViewById(R.id.protfolio_item_zf);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Stock stock = getItem(position);
            viewHolder.name.setText(stock.getName());
            viewHolder.code.setText(stock.getCode());
            viewHolder.zf.setVisibility(View.VISIBLE);
            viewHolder.zf.setText(CPQApplication.half_up(stock.getZf()) + "%");
            int statu = stock.getBeizhu();
            viewHolder.statu.setText(ArrowUtil.getColorString(stock.isHZ() ? "*" : " ", "884898", (stock.isGZ()
                    && (stock.isXC() || stock.isDT()) ? "**" : (stock.getIs_sf() == 1 ? "*" : "  "))
                    + Constant.STATUS.get(statu)));
            switch (statu) {
                case 1:
                case 2:
                case 3:
                    viewHolder.statu.setTextColor(getResources().getColor(
                            R.color.green));
                    break;
                case 4:
                    viewHolder.statu.setTextColor(getResources().getColor(
                            R.color.orange));
                    break;

                default:
                    viewHolder.statu.setTextColor(getResources().getColor(
                            R.color.gray_text));
                    break;
            }
            //viewHolder.currPrice.setText(ArrowUtil.getStockPrice(context, stock));
            viewHolder.currPrice.setText(CPQApplication.half_up(stock.getDq()));
            double increase = stock.getZdf();

            if (increase < 0) {
                viewHolder.increase.setText(CPQApplication.half_up(increase)
                        + "%");
                viewHolder.increase.setBackgroundResource(R.color.green);
            } else if (increase == 0) {
                viewHolder.increase.setText(CPQApplication.half_up(increase)
                        + "%");
                viewHolder.increase.setBackgroundResource(R.color.gray_bg);
            } else {
                viewHolder.increase.setText("+"
                        + CPQApplication.half_up(increase) + "%");
                viewHolder.increase.setBackgroundResource(R.color.orange);
            }
            return convertView;
        }
    }

    @Override
    public void onSuccess(JSONObject response, int what) {
        data.clear();
        data.addAll(JsonUtil.getStocks(response));
        adapter.notifyDataSetChanged();
        CPQApplication.setBzcode(statue);
        CPQApplication.initActionBar(this, true, Constant.STATUS.get(statue) + "("
                + adapter.getCount() + ")");
    }

    @Override
    public void onErrorResponse(String error, int what) {

    }

    @Override
    public void onFailed(String error, int what) {

    }

    private BroadcastReceiver dynamicReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constant.ACTION_MORE_BZ)) {
                // loadingView.hide();
                data.clear();
                data.addAll(CPQApplication.getMoreBZ());
                SortUtil.sort(data, sortType, isDesc);
                adapter.notifyDataSetChanged();
                CPQApplication.initActionBar(MoreBeizhuActivity.this, true,
                        Constant.STATUS.get(statue) + "(" + data.size() + ")");
            } else if (intent.getAction().equals(Constant.ACTION_NET_UNCONNECT)) {
                String errorStr = intent.getStringExtra("error");
                // loadingView.showError(errorStr);
            } else if (intent.getAction().equals(Constant.ACTION_LOADING)) {
                // loadingView.showProgress();
            }
        }
    };

}
