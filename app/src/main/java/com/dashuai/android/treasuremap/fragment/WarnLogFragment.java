package com.dashuai.android.treasuremap.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.dashuai.android.treasuremap.CPQApplication;
import com.dashuai.android.treasuremap.Constant;
import com.dashuai.android.treasuremap.R;
import com.dashuai.android.treasuremap.db.StockLogDao;
import com.dashuai.android.treasuremap.entity.Stock;
import com.dashuai.android.treasuremap.entity.StockLog;
import com.dashuai.android.treasuremap.util.DialogUtil;
import com.dashuai.android.treasuremap.util.JsonUtil;
import com.dashuai.android.treasuremap.util.RequestUtil;
import com.dashuai.android.treasuremap.util.RequestUtil.Reply;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WarnLogFragment extends Fragment {

    private ListView listView;
    private List<StockLog> data;
    private MyAdapter adapter;
    private DialogUtil dialogUtil;
    private Button button, hideHB, hidePC, hideJC, hideFZ;
    private RequestUtil requestUtil;
    private int count = 50;// 列表长度
    private MyHandler handler;
    private StockLogDao stockLogDao;
    private boolean isShow;
    private boolean isHideHongbao;
    private boolean isHidePingcang;
    private boolean isHideJiancang;
    private boolean isHideFangzhen;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        isShow = false;
        return inflater.inflate(R.layout.activity_warn_log, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter_dynamic = new IntentFilter();
        filter_dynamic.addAction(Constant.ACTION_HONG_BAO);
        filter_dynamic.addAction(Constant.ACTION_NET_UNCONNECT);
        getActivity().registerReceiver(dynamicReceiver, filter_dynamic);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(dynamicReceiver);
    }

    private void init() {
        if (null == CPQApplication.getDB()) {
            return;
        }
        handler = new MyHandler();
        stockLogDao = new StockLogDao(CPQApplication.getDB());
        requestUtil = new RequestUtil(getActivity(), new Reply() {

            @Override
            public void onSuccess(JSONObject response, int what) {
                dialogUtil.dismiss();
                Stock stock = JsonUtil.getStock(JsonUtil.getJsonObject(
                        response, "retRes"));
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putParcelable("stock", stock);
                intent.putExtras(bundle);
                CPQApplication.go2details(intent, getActivity());
            }

            @Override
            public void onFailed(String error, int what) {
                dialogUtil.setErrorMessage(error);
            }

            @Override
            public void onErrorResponse(String error, int what) {
                dialogUtil.setErrorMessage(error);
            }
        });
        dialogUtil = new DialogUtil(getActivity());
        data = new ArrayList<StockLog>();
        listView = (ListView) getView().findViewById(R.id.warn_log_listview);
        button = (Button) getView().findViewById(R.id.warn_log_button);
        hideHB = (Button) getView().findViewById(R.id.hide_hongbao);
        hidePC = (Button) getView().findViewById(R.id.hide_pingcang);
        hideJC = (Button) getView().findViewById(R.id.hide_jiancang);
        hideFZ = (Button) getView().findViewById(R.id.hide_fangzhen);
        adapter = new MyAdapter(getActivity(), data);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (!CPQApplication.isLogined()) {
                    return;
                }
                StockLog stockLog = (StockLog) arg0.getItemAtPosition(arg2);
                search(stockLog.getCode());
            }
        });
        refresh(count);
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                count += 50;
                refresh(count);
            }
        });
        hideHB.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                isHideHongbao = !isHideHongbao;
                if (isHideHongbao) {
                    hideHB.setText("显示关注");
                } else {
                    hideHB.setText("隐藏关注");
                }
                refresh(count);
            }
        });
        hidePC.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                isHidePingcang = !isHidePingcang;
                if (isHidePingcang) {
                    hidePC.setText("显示平仓");
                } else {
                    hidePC.setText("隐藏平仓");
                }
                refresh(count);
            }
        });
        hideJC.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                isHideJiancang = !isHideJiancang;
                if (isHideJiancang) {
                    hideJC.setText("显示建仓");
                } else {
                    hideJC.setText("隐藏建仓");
                }
                refresh(count);
            }
        });
        hideFZ.setVisibility(CPQApplication.VERSION == Constant.PHONE ? View.GONE : View.VISIBLE);
        hideFZ.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isHideFangzhen = !isHideFangzhen;
                if (isHideFangzhen) {
                    hideFZ.setText("显示仿真");
                } else {
                    hideFZ.setText("隐藏仿真");
                }
                refresh(count);
            }
        });
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            refresh(count);
            handler.sendEmptyMessageDelayed(0, 5000);
        }
    }

    private void search(String code) {
        dialogUtil.setProgress();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("codes", code.trim());
        requestUtil.postRequest(Constant.URL_IP + Constant.NOW_BY_ONECODE, map,
                0);
    }

    public void refresh(int line) {
        // handler.removeMessages(0);
        List<StockLog> list = stockLogDao.queryAll(line, isHideHongbao,
                isHidePingcang, isHideJiancang, isHideFangzhen);
        data.clear();
        data.addAll(list);
        adapter.notifyDataSetChanged();
        if (data.size() <= 0 && !isShow) {
            dialogUtil.setErrorMessage("没有更多了!");
            isShow = true;
            button.setVisibility(View.GONE);
            return;
        }
        // handler.sendEmptyMessageDelayed(0, 5000);
    }

    class MyAdapter extends BaseAdapter {

        private List<StockLog> data;
        private Context context;

        private MyAdapter(Context context, List<StockLog> data) {
            this.data = data;
            this.context = context;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public StockLog getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.layout_warn_log_item, parent, false);
            }
            TextView time = (TextView) convertView
                    .findViewById(R.id.warn_log_item_time);
            TextView message = (TextView) convertView
                    .findViewById(R.id.warn_log_item_message);
            CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.warn_log_item_check);
            checkBox.setVisibility(View.GONE);
            StockLog stockLog = getItem(position);
            time.setText(stockLog.getTime());
            message.setText(stockLog.getMessage());
            if (stockLog.getMessage().contains("平仓3")) {
                message.setTextColor(getResources().getColor(R.color.orange));
            } else if (stockLog.getMessage().contains("建仓3")) {
                message.setTextColor(getResources().getColor(R.color.blue));
            } else if (stockLog.getMessage().contains("关注")) {
                message.setTextColor(Color.YELLOW);
            } else {
                message.setTextColor(Color.WHITE);
            }
            return convertView;
        }
    }

    private BroadcastReceiver dynamicReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constant.ACTION_HONG_BAO)) {
                // loadingView.hide();
                refresh(count);
            } else if (intent.getAction().equals(Constant.ACTION_NET_UNCONNECT)) {
                String errorStr = intent.getStringExtra("error");
                // loadingView.showError(errorStr);
            } else if (intent.getAction().equals(Constant.ACTION_LOADING)) {
                // loadingView.showProgress();
            }
        }
    };
}
