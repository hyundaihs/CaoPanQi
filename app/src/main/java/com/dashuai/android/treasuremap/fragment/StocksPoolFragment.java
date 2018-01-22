package com.dashuai.android.treasuremap.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.dashuai.android.treasuremap.CPQApplication;
import com.dashuai.android.treasuremap.Constant;
import com.dashuai.android.treasuremap.R;
import com.dashuai.android.treasuremap.entity.Stock;
import com.dashuai.android.treasuremap.entity.StockPool;
import com.dashuai.android.treasuremap.entity.SzData;
import com.dashuai.android.treasuremap.util.DialogUtil;
import com.dashuai.android.treasuremap.util.Installation;
import com.dashuai.android.treasuremap.util.JsonUtil;
import com.dashuai.android.treasuremap.util.RequestUtil;
import com.dashuai.android.treasuremap.util.RequestUtil.Reply;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 股池
 *
 * @author kevin
 */

public class StocksPoolFragment extends Fragment implements Reply {
    private List<String> days = new ArrayList<String>();
    private List<StockPool> stocksData = new ArrayList<StockPool>();
    private ListView stocks;
    private Spinner stocksSpinner;
    private TextView percent, orientation, press1, press2, support1, support2,
            date, content;
    private RequestUtil requestUtil;
    private ArrayAdapter<String> spinnerAdapter;
    private MyAdapter adapter;
    private DialogUtil dialogUtil;
    private View szView;
    private TextView foreground;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stocks_pool, container,
                false);
        szView = getActivity().getLayoutInflater().inflate(R.layout.layout_sz,
                null);
        foreground = (TextView) view.findViewById(R.id.foreground);
        stocks = (ListView) view.findViewById(R.id.stocks);
        stocksSpinner = (Spinner) view.findViewById(R.id.stocks_list);
        loadSzView();
        init();
        return view;
    }

    private void loadSzView() {
        percent = (TextView) szView.findViewById(R.id.percent);
        orientation = (TextView) szView.findViewById(R.id.orientation);
        press1 = (TextView) szView.findViewById(R.id.press_1);
        press2 = (TextView) szView.findViewById(R.id.press_2);
        support1 = (TextView) szView.findViewById(R.id.support_1);
        support2 = (TextView) szView.findViewById(R.id.support_2);
        content = (TextView) szView.findViewById(R.id.content);
        date = (TextView) szView.findViewById(R.id.date);
    }

    private void checked() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("device_id", Installation.id(getActivity().getBaseContext()));
        map.put("type_id", CPQApplication.getID_KEY());
        requestUtil.postRequest(Constant.URL_IP + Constant.CHECK_GC, map, 2);
    }

    private void getSZ() {
        // dialogUtil.setProgress();
        Map<String, Object> map = new HashMap<String, Object>();
        requestUtil.postRequest(Constant.URL_IP + Constant.SZSJ, map, 0);
    }

    private void init() {
        dialogUtil = new DialogUtil(getActivity());
        requestUtil = new RequestUtil(getActivity(), this);
        checked();
        getSZ();
        initSpinner();
        initList();
    }

    private void initList() {
        adapter = new MyAdapter(stocksData);
        stocks.addHeaderView(szView, null, false);
        stocks.setAdapter(adapter);
        stocks.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                StockPool stockPool = adapter.getItem(position - 1 < 0 ? 0
                        : position - 1);
                Stock stock = new Stock();
                stock.setCode(stockPool.getCodes());
                stock.setName(stockPool.getTitle());
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putParcelable("stock", stock);
                intent.putExtras(bundle);
                CPQApplication.go2details(intent, getActivity());
            }
        });
    }

    private void initSpinner() {
        spinnerAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.layout_spinner_item, days);
        stocksSpinner.setAdapter(spinnerAdapter);
        stocksSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                getStocks(spinnerAdapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getStocks(String date) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("days", date);
        requestUtil.postRequest(Constant.URL_IP + Constant.GCSJ, map, 1);
    }

    private class MyAdapter extends BaseAdapter {
        public List<StockPool> data;

        public MyAdapter(List<StockPool> data) {
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public StockPool getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class ViewHolder {
            TextView days, codes, title, yqmb, jgqj, cw, fx, beizhu;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (null == convertView) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(getActivity()).inflate(
                        R.layout.layout_stocks_item, parent, false);
                viewHolder.days = (TextView) convertView
                        .findViewById(R.id.stock_date);
                viewHolder.codes = (TextView) convertView
                        .findViewById(R.id.stock_code);
                viewHolder.title = (TextView) convertView
                        .findViewById(R.id.stock_name);
                viewHolder.yqmb = (TextView) convertView
                        .findViewById(R.id.stock_target);
                viewHolder.jgqj = (TextView) convertView
                        .findViewById(R.id.stock_range);
                viewHolder.cw = (TextView) convertView
                        .findViewById(R.id.stock_ratio);
                viewHolder.fx = (TextView) convertView
                        .findViewById(R.id.stock_direction);
                viewHolder.beizhu = (TextView) convertView
                        .findViewById(R.id.stock_remark);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            StockPool stockPool = getItem(position);
            viewHolder.days.setText(stockPool.getDays());
            viewHolder.codes.setText(stockPool.getCodes());
            viewHolder.title.setText(stockPool.getTitle());
            viewHolder.yqmb.setText(stockPool.getYqmb());
            viewHolder.jgqj.setText(stockPool.getJgqj());
            viewHolder.cw.setText(stockPool.getCw());
            viewHolder.fx.setText(stockPool.getFx());
            viewHolder.beizhu.setText(stockPool.getBeizhu());
            return convertView;
        }

    }

    private void initSzData(SzData szData) {
        percent.setText(szData.getCw());
        orientation.setText(szData.getFx());
        date.setText(szData.getDay());
        press1.setText(szData.getY1());
        press2.setText(szData.getY2());
        support1.setText(szData.getZ1());
        support2.setText(szData.getZ2());
        content.setText(szData.getBeizhu());
    }

    @Override
    public void onSuccess(JSONObject response, int what) {
        switch (what) {
            case 0:
                SzData szData = JsonUtil.getSzData(response);
                initSzData(szData);
                if (szData.getDays().size() > 0) {
                    getStocks(szData.getDays().get(0));
                    days.clear();
                    days.addAll(szData.getDays());
                    spinnerAdapter.notifyDataSetChanged();
                } else {
                    dialogUtil.dismiss();
                }
                break;
            case 1:
                dialogUtil.dismiss();
                stocksData.clear();
                stocksData.addAll(JsonUtil.getStockPools(response));
                adapter.notifyDataSetChanged();
                break;
            case 2:
                foreground.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onErrorResponse(String error, int what) {
        // dialogUtil.setErrorMessage(error);
    }

    @Override
    public void onFailed(String error, int what) {
        if (what == 2) {
            foreground.setVisibility(View.VISIBLE);
            foreground.setText("查看股池，请联系工作人员");
        }
        // dialogUtil.setErrorMessage(error);
    }
}
