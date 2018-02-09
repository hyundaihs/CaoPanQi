package com.dashuai.android.treasuremap.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

import com.dashuai.android.treasuremap.CPQApplication;
import com.dashuai.android.treasuremap.Constant;
import com.dashuai.android.treasuremap.R;
import com.dashuai.android.treasuremap.db.StockDao;
import com.dashuai.android.treasuremap.entity.SortType;
import com.dashuai.android.treasuremap.entity.Stock;
import com.dashuai.android.treasuremap.ui.SearchStockActivity;
import com.dashuai.android.treasuremap.util.ArrowUtil;
import com.dashuai.android.treasuremap.util.CalendarUtil;
import com.dashuai.android.treasuremap.util.DialogUtil;
import com.dashuai.android.treasuremap.util.SortUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的自选
 *
 * @author kevin
 */
public class ProtfolioFragment extends Fragment implements OnClickListener {

    private ListView listView;
    private TextView add, refreshTime;
    // private MyLoadingView loadingView;
    private List<Stock> data = new ArrayList<Stock>();
    private MyAdapter adapter;
    private TextView stock, price, increase, beizhu, edit, delete;
    private CheckBox checkAll;
    private int sortType = SortUtil.SORT_BY_NONE;
    private boolean isDesc;// 是否倒序
    private boolean isEditable;// 是否编辑

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_protfolio, container,
                false);
        listView = (ListView) view.findViewById(R.id.protfolio_listview);
        add = (TextView) view.findViewById(R.id.protfolio_add);
        refreshTime = (TextView) view.findViewById(R.id.protfolio_time);
        stock = (TextView) view.findViewById(R.id.protfolio_stock);
        price = (TextView) view.findViewById(R.id.protfolio_price);
        increase = (TextView) view.findViewById(R.id.protfolio_increase);
        beizhu = (TextView) view.findViewById(R.id.protfolio_statu);
        checkAll = (CheckBox) view.findViewById(R.id.protfolio_checkbox);
        edit = (TextView) view.findViewById(R.id.protfolio_edit);
        delete = (TextView) view.findViewById(R.id.protfolio_delete);
        // loadingView = (MyLoadingView) view
        // .findViewById(R.id.protfolio_loadingView);
        add.setOnClickListener(this);
        stock.setOnClickListener(this);
        price.setOnClickListener(this);
        increase.setOnClickListener(this);
        beizhu.setOnClickListener(this);
        edit.setOnClickListener(this);
        delete.setOnClickListener(this);
        checkAll.setOnCheckedChangeListener(new OnCheckedChangeListener() {

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
        init();
        return view;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
        if (editable) {
            edit.setText("完成");
            checkAll.setChecked(false);
            refreshTime.setText("");
        } else {
            edit.setText("编辑");
            adapter.cleanAll();
            refreshTime.setText("");
        }
        checkAll.setVisibility(isEditable ? View.VISIBLE : View.GONE);
        delete.setVisibility(isEditable ? View.VISIBLE : View.GONE);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.protfolio_add:
                startActivity(new Intent(getActivity(), SearchStockActivity.class));
                break;
            case R.id.protfolio_stock:
                isDesc = !isDesc;
                sortType = SortUtil.SORT_BY_STOCK;
                SortUtil.sort(CPQApplication.protfolios, SortUtil.SORT_BY_STOCK,
                        isDesc);
                adapter.notifyDataSetChanged();
                break;
            case R.id.protfolio_price:
                isDesc = !isDesc;
                sortType = SortUtil.SORT_BY_PRICE;
                SortUtil.sort(CPQApplication.protfolios, SortUtil.SORT_BY_PRICE,
                        isDesc);
                adapter.notifyDataSetChanged();
                break;
            case R.id.protfolio_increase:
                isDesc = !isDesc;
                sortType = SortUtil.SORT_BY_INCREASE;
                SortUtil.sort(CPQApplication.protfolios, SortUtil.SORT_BY_INCREASE,
                        isDesc);
                adapter.notifyDataSetChanged();
                break;
            case R.id.protfolio_statu:
                isDesc = !isDesc;
                sortType = SortUtil.SORT_BY_STATUS;
                SortUtil.sort(CPQApplication.protfolios, SortUtil.SORT_BY_STATUS,
                        isDesc);
                adapter.notifyDataSetChanged();
                break;
            case R.id.protfolio_edit:
                setEditable(!isEditable);
                adapter.notifyDataSetChanged();
                break;
            case R.id.protfolio_delete:
                DialogUtil dialogUtil = new DialogUtil(getActivity());
                dialogUtil.setMessage("提示", "确定要从自选中删除选中的"
                                + adapter.getChecked().size() + "项吗？", "确定",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new StockDao(CPQApplication.getDB())
                                        .deleteByCodes(adapter.getChecked());
                                CPQApplication.setStocks();
                                for (int i = 0; i < adapter.getChecked().size(); i++) {
                                    data.remove(adapter.getChecked().get(i));
                                }
                                adapter.cleanAll();
                                setEditable(false);
                                adapter.notifyDataSetChanged();
                            }
                        }, "取消", null);
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter_dynamic = new IntentFilter();
        filter_dynamic.addAction(Constant.ACTION_PROTFOLIO);
        filter_dynamic.addAction(Constant.ACTION_NET_UNCONNECT);
        getActivity().registerReceiver(dynamicReceiver, filter_dynamic);
        if (null != data && null != adapter
                && null != CPQApplication.protfolios) {
            data.clear();
            data.addAll(CPQApplication.protfolios);
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(dynamicReceiver);
    }

    private void init() {
        if (null != CPQApplication.protfolios) {
            data.clear();
            data.addAll(CPQApplication.protfolios);
        }
        adapter = new MyAdapter(getActivity(), data);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("index", position);
                CPQApplication.setDetailsResource(data);
                CPQApplication.go2details(intent, getActivity());
            }
        });
    }

    private class MyAdapter extends BaseAdapter {
        private List<Stock> data;
        private Context context;
        private List<Stock> checked;
        private boolean ignoreChange = false;

        public MyAdapter(Context context, List<Stock> data) {
            this.data = data;
            this.context = context;
            checked = new ArrayList<Stock>();
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

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            if (isEditable) {
                refreshTime.setText(checked.size() + "项被选中");
            }
        }

        private class ViewHolder {
            TextView name, code, currPrice, increase, statu;
            CheckBox check;
        }

        public boolean isChecked(Stock stock) {
            return checked.contains(stock);
        }

        public void checkStock(Stock stock) {
            if (!checked.contains(stock)) {
                checked.add(stock);
            }
        }

        public void unCheckStock(Stock stock) {
            if (checked.contains(stock)) {
                checked.remove(stock);
            }
        }

        public void checkAll() {
            checked.clear();
            checked.addAll(data);
        }

        public void cleanAll() {
            checked.clear();
        }

        public List<Stock> getChecked() {
            return checked;
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
                viewHolder.check = (CheckBox) convertView
                        .findViewById(R.id.protfolio_item_checkbox);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final Stock stock = getItem(position);
            viewHolder.check.setVisibility(isEditable ? View.VISIBLE
                    : View.GONE);
            viewHolder.name.setText(stock.getName());
            viewHolder.code.setText(stock.getCode());
            int statu = stock.getBeizhu();
            viewHolder.statu.setText(ArrowUtil.getColorString(stock.isHZ() ? "*" : "", "884898", (stock.isGZ()
                    && (stock.isXC() || stock.isDT()) ? "**" : (stock.getIs_sf() == 1 ? "*" : "  "))
                    + Constant.getStatus(statu)));
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
            //viewHolder.currPrice.setText(ArrowUtil.getStockPrice(getActivity(),stock));
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
            ignoreChange = true;
            viewHolder.check.setChecked(isChecked(stock));
            ignoreChange = false;
            viewHolder.check
                    .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {
                            if (!ignoreChange) {
                                if (isChecked) {
                                    checkStock(stock);
                                } else {
                                    unCheckStock(stock);
                                }
                                notifyDataSetChanged();
                            }
                        }
                    });
            return convertView;
        }
    }

    private BroadcastReceiver dynamicReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constant.ACTION_PROTFOLIO)) {
                // loadingView.hide();
                data.clear();
                SortUtil.sort(CPQApplication.protfolios, sortType, isDesc);
                data.addAll(CPQApplication.protfolios);
                adapter.notifyDataSetChanged();
                if (!isEditable) {
                    refreshTime.setText("上次更新："
                            + new CalendarUtil().format(CalendarUtil.STANDARD));
                }
            } else if (intent.getAction().equals(Constant.ACTION_NET_UNCONNECT)) {
                String errorStr = intent.getStringExtra("error");
                // loadingView.showError(errorStr);
            } else if (intent.getAction().equals(Constant.ACTION_LOADING)) {
                // loadingView.showProgress();
            }
        }
    };

}
