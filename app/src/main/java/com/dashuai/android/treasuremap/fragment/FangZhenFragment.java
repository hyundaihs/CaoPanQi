package com.dashuai.android.treasuremap.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.dashuai.android.treasuremap.CPQApplication;
import com.dashuai.android.treasuremap.Constant;
import com.dashuai.android.treasuremap.R;
import com.dashuai.android.treasuremap.db.FangzhenDao;
import com.dashuai.android.treasuremap.entity.FangzhenBig;
import com.dashuai.android.treasuremap.entity.FangzhenSmall;
import com.dashuai.android.treasuremap.entity.Stock;
import com.dashuai.android.treasuremap.util.DialogUtil;
import com.dashuai.android.treasuremap.util.JsonUtil;
import com.dashuai.android.treasuremap.util.RequestUtil;
import com.dashuai.android.treasuremap.widget.MyGridView;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kevin on 17/4/15.
 */
public class FangZhenFragment extends Fragment implements View.OnClickListener {
    private MyGridView fangzhenList;
    // private MyLoadingView loadingView;
    private List<FangzhenBig> data = new ArrayList<FangzhenBig>();
    private MyAdapter adapter;
    private Button delete, edit;
    private CheckBox checkAll;
    private TextView checkNum;
    private boolean isEditable;// 是否编辑
    private RequestUtil requestUtil;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fangzhen, container,
                false);
        fangzhenList = (MyGridView) view.findViewById(R.id.protfolios);
        delete = (Button) view.findViewById(R.id.fangzhen_delete);
        edit = (Button) view.findViewById(R.id.fangzhen_edit);
        checkNum = (TextView) view.findViewById(R.id.fangzhen_checknum);
        checkAll = (CheckBox) view.findViewById(R.id.fangzhen_checkall);
        delete.setOnClickListener(this);
        edit.setOnClickListener(this);
        checkAll.setOnClickListener(this);
        // loadingView = (MyLoadingView) view
        // .findViewById(R.id.fangzhen_loadingView);
        checkAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    adapter.checkAll();
                } else {
                    adapter.cleanAll();
                }
                adapter.notifyDataSetChanged();
            }
        });
        requestUtil = new RequestUtil(getActivity(), new RequestUtil.Reply() {
            @Override
            public void onSuccess(JSONObject response, int what) {
                Stock stock = JsonUtil.getStock(JsonUtil.getJsonObject(response, "retRes"));
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putParcelable("stock", stock);
                intent.putExtras(bundle);
                CPQApplication.go2details(intent, getActivity());
            }

            @Override
            public void onErrorResponse(String error, int what) {

            }

            @Override
            public void onFailed(String error, int what) {

            }
        });
        init();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fangzhen_delete:
                DialogUtil dialogUtil = new DialogUtil(getActivity());
                dialogUtil.setMessage("提示", "确定要从仿真中删除选中的"
                                + adapter.getChecked().size() + "项吗？", "确定",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FangzhenDao fangzhenDao = new FangzhenDao(CPQApplication.getDB());
                                List<Integer> list = adapter.getChecked();
                                List<FangzhenBig> remove = new ArrayList<FangzhenBig>();
                                for (int i = 0; i < list.size(); i++) {
                                    remove.add(data.get(list.get(i)));
                                    int id = data.get(list.get(i)).getStockId();
                                    fangzhenDao.deleteByStockId(id);
                                }
                                CPQApplication.setFangzhens();
                                for (int i = 0; i < remove.size(); i++) {
                                    data.remove(remove.get(i));
                                }
                                setEditable(false);
                                adapter.notifyDataSetChanged();
                            }
                        }, "取消", null);
                break;
            case R.id.fangzhen_edit:
                setEditable(!isEditable);
                adapter.notifyDataSetChanged();
                break;

            default:
                break;
        }
    }

    private void setEditable(boolean editable) {
        if (editable) {
            isEditable = true;
            edit.setText("完成");
            checkAll.setChecked(false);
            checkNum.setText("");
        } else {
            isEditable = false;
            edit.setText("编辑");
            adapter.cleanAll();
            checkNum.setText("");
        }
        checkAll.setVisibility(isEditable ? View.VISIBLE : View.GONE);
        delete.setVisibility(isEditable ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter_dynamic = new IntentFilter();
        filter_dynamic.addAction(Constant.ACTION_FANGZHEN);
        filter_dynamic.addAction(Constant.ACTION_NET_UNCONNECT);
        getActivity().registerReceiver(dynamicReceiver, filter_dynamic);
        if (null != data && null != adapter
                && null != CPQApplication.fangzhenBigs) {
            data.clear();
            data.addAll(CPQApplication.fangzhenBigs);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(dynamicReceiver);
    }

    private void init() {
        if (null != CPQApplication.fangzhenBigs) {
            data.clear();
            data.addAll(CPQApplication.fangzhenBigs);
        }
        adapter = new MyAdapter(getActivity(), data);
        fangzhenList.setAdapter(adapter);
        fangzhenList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("codes", data.get(position).getCodes());
                requestUtil.postRequest(Constant.URL_IP + Constant.NOW_BY_ONECODE, map, 0);
            }
        });
    }

    private class MyAdapter extends BaseAdapter {
        private List<FangzhenBig> data;
        private Context context;
        private List<Integer> checked;

        public MyAdapter(Context context, List<FangzhenBig> data) {
            this.data = data;
            this.context = context;
            checked = new ArrayList<Integer>();
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            if (isEditable) {
                checkNum.setText(checked.size() + "项被选中");
            }
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public FangzhenBig getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class ViewHolder {
            TextView code, name, success, status, base;
            CheckBox check;
            TextView[] lows, dates;

            public ViewHolder() {
                lows = new TextView[6];
                dates = new TextView[6];
            }
        }

        public boolean isChecked(Integer integer) {
            return checked.contains(integer);
        }

        public void checkFangzhenBig(Integer integer) {
            if (!checked.contains(integer)) {
                checked.add(integer);
            }
        }

        public void unCheckFangzhenBig(Integer integer) {
            if (checked.contains(integer)) {
                checked.remove(integer);
            }
        }

        public void checkAll() {
            checked.clear();
            for (int i = 0; i < getCount(); i++) {
                checkFangzhenBig(i);
            }
        }

        public void cleanAll() {
            checked.clear();
        }

        public List<Integer> getChecked() {
            return checked;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (null == convertView) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.layout_fangzhen_item, parent, false);
                viewHolder.name = (TextView) convertView
                        .findViewById(R.id.fangzhen_item_name);
                viewHolder.code = (TextView) convertView
                        .findViewById(R.id.fangzhen_item_code);
                viewHolder.success = (TextView) convertView
                        .findViewById(R.id.fangzhen_item_success);
                viewHolder.status = (TextView) convertView
                        .findViewById(R.id.fangzhen_item_status);
                viewHolder.base = (TextView) convertView
                        .findViewById(R.id.fangzhen_item_base);
                viewHolder.lows[0] = (TextView) convertView
                        .findViewById(R.id.fangzhen_item_start);
                viewHolder.lows[1] = (TextView) convertView
                        .findViewById(R.id.fangzhen_item_low1);
                viewHolder.lows[2] = (TextView) convertView
                        .findViewById(R.id.fangzhen_item_low2);
                viewHolder.lows[3] = (TextView) convertView
                        .findViewById(R.id.fangzhen_item_low3);
                viewHolder.lows[4] = (TextView) convertView
                        .findViewById(R.id.fangzhen_item_low4);
                viewHolder.lows[5] = (TextView) convertView
                        .findViewById(R.id.fangzhen_item_low5);
                viewHolder.check = (CheckBox) convertView
                        .findViewById(R.id.fangzhen_item_checkbox);
                viewHolder.dates[0] = (TextView) convertView
                        .findViewById(R.id.fangzhen_item_start_date);
                viewHolder.dates[1] = (TextView) convertView
                        .findViewById(R.id.fangzhen_item_date1);
                viewHolder.dates[2] = (TextView) convertView
                        .findViewById(R.id.fangzhen_item_date2);
                viewHolder.dates[3] = (TextView) convertView
                        .findViewById(R.id.fangzhen_item_date3);
                viewHolder.dates[4] = (TextView) convertView
                        .findViewById(R.id.fangzhen_item_date4);
                viewHolder.dates[5] = (TextView) convertView
                        .findViewById(R.id.fangzhen_item_date5);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.check.setVisibility(isEditable ? View.VISIBLE
                    : View.GONE);
            final FangzhenBig fangzhenBig = getItem(position);
            viewHolder.name.setText(fangzhenBig.getTitle());
            viewHolder.code.setText(fangzhenBig.getCodes());
            viewHolder.success.setText(FangzhenBig.RESULTS[fangzhenBig.getResult_status()]);
            viewHolder.success.setTextColor(Color.WHITE);
            viewHolder.status.setText(Constant.getStatus(fangzhenBig.getBeizhu()));
            viewHolder.base.setText(CPQApplication.half_up(fangzhenBig.getBase_low()));
            List<FangzhenSmall> list = fangzhenBig.getLists();
            if (fangzhenBig.getResult_status() == 0) {
                for (int i = 0; i < list.size(); i++) {
                    FangzhenSmall fangzhenSmall = list.get(i);
                    if (i == 0) {
                        if (fangzhenSmall.getStlow() == 0) {
                            viewHolder.lows[i].setText(CPQApplication.half_up(fangzhenSmall.getLow_1())
                                    + "-" + CPQApplication.half_up(fangzhenSmall.getLow_2()));
                            viewHolder.lows[i].setTextColor(getResources().getColor(FangzhenBig.RESULT_COLORS[0]));
                            viewHolder.dates[i].setTextColor(getResources().getColor(FangzhenBig.RESULT_COLORS[0]));
                        } else {
                            viewHolder.lows[i].setText(CPQApplication.half_up(fangzhenSmall.getStlow()));
                            viewHolder.lows[i].setTextColor(getResources().getColor(FangzhenBig.RESULT_COLORS[1]));
                            viewHolder.dates[i].setTextColor(getResources().getColor(FangzhenBig.RESULT_COLORS[1]));
                        }
                    } else {
                        viewHolder.lows[i].setText(CPQApplication.half_up(fangzhenSmall.getLow())
                                + "-" + CPQApplication.half_up(fangzhenSmall.getHigh()));
                        viewHolder.lows[i].setTextColor(getResources().getColor(FangzhenBig.RESULT_COLORS[fangzhenSmall.getResult_status()]));
                        viewHolder.dates[i].setTextColor(getResources().getColor(FangzhenBig.RESULT_COLORS[fangzhenSmall.getResult_status()]));
                    }
                    viewHolder.dates[i].setText(fangzhenSmall.getDay());
                }
            } else {
                for (int i = 0; i < list.size(); i++) {
                    FangzhenSmall fangzhenSmall = list.get(i);
                    if (i == 0) {
                        if (fangzhenSmall.getStlow() == 0) {
                            viewHolder.lows[i].setText(CPQApplication.half_up(fangzhenSmall.getLow_1())
                                    + "-" + CPQApplication.half_up(fangzhenSmall.getLow_2()));
                        } else {
                            viewHolder.lows[i].setText(CPQApplication.half_up(fangzhenSmall.getStlow()));
                        }
                    } else {
                        viewHolder.lows[i].setText(CPQApplication.half_up(fangzhenSmall.getLow())
                                + "-" + CPQApplication.half_up(fangzhenSmall.getHigh()));

                    }
                    viewHolder.dates[i].setText(fangzhenSmall.getDay());
                    viewHolder.lows[i].setTextColor(getResources().getColor(FangzhenBig.RESULT_COLORS[0]));
                    viewHolder.dates[i].setTextColor(getResources().getColor(FangzhenBig.RESULT_COLORS[0]));
                }
            }
            if (fangzhenBig.getResult_status() == 1) {
                convertView
                        .setBackgroundResource(isChecked(position) ? R.drawable.orange_bg_checked
                                : R.drawable.orange_bg);
            } else if (fangzhenBig.getResult_status() == 2) {
                convertView
                        .setBackgroundResource(isChecked(position) ? R.drawable.green_bg_checked
                                : R.drawable.green_bg);
            } else {
                convertView
                        .setBackgroundResource(isChecked(position) ? R.drawable.gray_bg_checked
                                : R.drawable.gray_bg);
            }
            viewHolder.check.setChecked(isChecked(position));
            viewHolder.check
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {
                            if (isChecked) {
                                checkFangzhenBig(position);
                            } else {
                                unCheckFangzhenBig(position);
                            }
                            notifyDataSetChanged();
                        }
                    });
            return convertView;
        }
    }

    private BroadcastReceiver dynamicReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constant.ACTION_FANGZHEN)) {
                // loadingView.hide();
                data.clear();
                data.addAll(CPQApplication.fangzhenBigs);
                adapter.notifyDataSetChanged();
            } else if (intent.getAction().equals(Constant.ACTION_NET_UNCONNECT)) {
                String errorStr = intent.getStringExtra("error");
                // loadingView.showError(errorStr);
            } else if (intent.getAction().equals(Constant.ACTION_LOADING)) {
                // loadingView.showProgress();
            } else if (intent.getAction().equals(Constant.ACTION_WARN)) {
                String errorStr = intent.getStringExtra("warn");
                // loadingView.showError(errorStr);
            }
        }
    };
}
