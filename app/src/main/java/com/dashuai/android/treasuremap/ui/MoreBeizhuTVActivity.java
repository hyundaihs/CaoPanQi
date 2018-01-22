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
import android.widget.Button;
import android.widget.GridView;
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

public class MoreBeizhuTVActivity extends Activity implements Reply, View.OnClickListener {

    private GridView gridView;
    private int statue;
    private List<Stock> data;
    private MyAdapter adapter;
    private RequestUtil requestUtil;
    private boolean isDesc;// 是否倒序
    private int sort = SortUtil.SORT_BY_NONE;
    private Button zdf, zf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_beizhu_tv);
        gridView = (GridView) findViewById(R.id.more_beizhu_gridView);
        zdf = (Button) findViewById(R.id.more_beizhu_zdf);
        zf = (Button) findViewById(R.id.more_beizhu_zf);
        statue = getIntent().getIntExtra("statue", 0);
        CPQApplication.initActionBar(this, true, Constant.STATUS.get(statue));
        zdf.setOnClickListener(this);
        zf.setOnClickListener(this);
        init();
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
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Intent intent = new Intent();
                intent.putExtra("index", arg2);
                CPQApplication.setDetailsResource(data);
                CPQApplication.go2details(intent, MoreBeizhuTVActivity.this);
            }
        });
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("beizhu", statue);
        map.put("cyb", CPQApplication.CLIENT == Constant.CY ? 1 : 0);
        map.put("cxb", CPQApplication.CLIENT == Constant.CX ? 1 : 0);
        requestUtil.postRequest(Constant.URL_IP + Constant.NOW_BY_BEIZHU, map,
                0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.more_beizhu_zdf:
                isDesc = !isDesc;
                sort = SortUtil.SORT_BY_INCREASE;
                SortUtil.sort(data, sort, isDesc);
                adapter.notifyDataSetChanged();
                break;
            case R.id.more_beizhu_zf:
                isDesc = !isDesc;
                sort = SortUtil.SORT_BY_ZHENFU;
                SortUtil.sort(data, sort, isDesc);
                adapter.notifyDataSetChanged();
                break;
        }
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
            TextView name, code, dq, zde, zdf, beizhu, zf, zhenF;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.layout_protfolio_tv_item, parent, false);
            viewHolder.name = (TextView) convertView
                    .findViewById(R.id.protfolio_item_name);
            viewHolder.code = (TextView) convertView
                    .findViewById(R.id.protfolio_item_code);
            viewHolder.dq = (TextView) convertView
                    .findViewById(R.id.protfolio_item_dq);
            viewHolder.zde = (TextView) convertView
                    .findViewById(R.id.protfolio_item_zde);
            viewHolder.zdf = (TextView) convertView
                    .findViewById(R.id.protfolio_item_zdf);
            viewHolder.beizhu = (TextView) convertView
                    .findViewById(R.id.protfolio_item_beizhu);
            viewHolder.zf = (TextView) convertView
                    .findViewById(R.id.protfolio_item_zf);
            viewHolder.zhenF = convertView.findViewById(R.id.protfolio_item_zhenf);
            convertView.setTag(viewHolder);
            Stock stock = getItem(position);
            viewHolder.name.setText(stock.getName());
            viewHolder.code.setText(stock.getCode());
            viewHolder.dq.setText(ArrowUtil.getStockPrice(context, stock));
            viewHolder.zhenF.setVisibility(View.VISIBLE);
            viewHolder.zhenF.setText(CPQApplication.half_up(stock.getZf()) + "%");
            viewHolder.zf.setVisibility(stock.isGZ()
                    && (stock.isXC() || stock.isDT()) ? View.VISIBLE : stock
                    .getIs_sf() == 1 ? View.VISIBLE : View.GONE);
            viewHolder.beizhu.setText(ArrowUtil.getColorString(stock.isHZ() ? "*" : " ", "884898", (stock.isGZ()
                    && (stock.isXC() || stock.isDT()) ? "**" : (stock.getIs_sf() == 1 ? "*" : "  "))
                    + Constant.STATUS.get(stock.getBeizhu())));
            if (stock.getZdf() > 0) {
                convertView.setBackgroundResource(R.drawable.orange_bg);
                viewHolder.zdf.setText("+"
                        + CPQApplication.half_up(stock.getZdf()) + "%");
                viewHolder.zde.setText("+"
                        + CPQApplication.half_up(stock.getZde()));
            } else if (stock.getZdf() < 0) {
                viewHolder.zdf.setText(CPQApplication.half_up(stock.getZdf())
                        + "%");
                viewHolder.zde.setText(CPQApplication.half_up(stock.getZde()));
                convertView.setBackgroundResource(R.drawable.green_bg);
            } else {
                viewHolder.zdf.setText(CPQApplication.half_up(stock.getZdf())
                        + "%");
                viewHolder.zde.setText(CPQApplication.half_up(stock.getZde()));
                convertView.setBackgroundResource(R.drawable.gray_bg);

            }
            return convertView;
        }
    }

    @Override
    public void onSuccess(JSONObject response, int what) {
        data.clear();
        data.addAll(JsonUtil.getStocks(response));
        SortUtil.sort(data, sort, isDesc);
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
                SortUtil.sort(data, sort, isDesc);
                adapter.notifyDataSetChanged();
                CPQApplication.initActionBar(MoreBeizhuTVActivity.this, true,
                        Constant.STATUS.get(statue) + "(" + data.size() + ")" );
            } else if (intent.getAction().equals(Constant.ACTION_NET_UNCONNECT)) {
                String errorStr = intent.getStringExtra("error");
                // loadingView.showError(errorStr);
            } else if (intent.getAction().equals(Constant.ACTION_LOADING)) {
                // loadingView.showProgress();
            }
        }
    };

}