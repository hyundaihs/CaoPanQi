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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.dashuai.android.treasuremap.CPQApplication;
import com.dashuai.android.treasuremap.Constant;
import com.dashuai.android.treasuremap.R;
import com.dashuai.android.treasuremap.db.StockDao;
import com.dashuai.android.treasuremap.entity.Stock;
import com.dashuai.android.treasuremap.util.ArrowUtil;
import com.dashuai.android.treasuremap.util.DialogUtil;
import com.dashuai.android.treasuremap.util.SortUtil;
import com.dashuai.android.treasuremap.widget.MyGridView;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的自选
 *
 * @author kevin
 */
public class ProtfolioTVFragment extends Fragment implements OnClickListener {

    private MyGridView protfolios;
    // private MyLoadingView loadingView;
    private List<Stock> data = new ArrayList<Stock>();
    private MyAdapter adapter;
    private Button delete, edit, sort, left, right;
    private CheckBox checkAll;
    private TextView checkNum;
    private boolean isEditable;// 是否编辑
    private boolean isSortable;// 是否排序
    private int sortType = SortUtil.SORT_BY_NONE;
    private boolean isDesc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_protfolio_tv, container,
                false);
        protfolios = (MyGridView) view.findViewById(R.id.protfolios);
        delete = (Button) view.findViewById(R.id.protfolio_delete);
        edit = (Button) view.findViewById(R.id.protfolio_edit);
        sort = (Button) view.findViewById(R.id.protfolio_sort);
        left = (Button) view.findViewById(R.id.protfolio_left);
        right = (Button) view.findViewById(R.id.protfolio_right);
        checkNum = (TextView) view.findViewById(R.id.protfolio_checknum);
        checkAll = (CheckBox) view.findViewById(R.id.protfolio_checkall);
        delete.setOnClickListener(this);
        edit.setOnClickListener(this);
        checkAll.setOnClickListener(this);
        sort.setOnClickListener(this);
        left.setOnClickListener(this);
        right.setOnClickListener(this);
        // loadingView = (MyLoadingView) view
        // .findViewById(R.id.protfolio_loadingView);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.protfolio_sort:
//                if (isSortable) {
//                    isSortable = false;
//                    sort.setText("排序");
//                    adapter.cleanAll();
//                    left.setVisibility(View.GONE);
//                    right.setVisibility(View.GONE);
//                } else {
//                    isSortable = true;
//                    sort.setText("完成");
//                    checkAll.setChecked(false);
//                }
//                edit.setVisibility(isSortable ? View.GONE : View.VISIBLE);
                isDesc = isDesc ? false : true;
                sortType = SortUtil.SORT_BY_INCREASE;
                data.clear();
                SortUtil.sort(CPQApplication.protfolios, SortUtil.SORT_BY_INCREASE,
                        isDesc);
                data.addAll(CPQApplication.protfolios);
                adapter.notifyDataSetChanged();
                break;
            case R.id.protfolio_left:
                moveLeft();
                break;
            case R.id.protfolio_right:
                moveRight();
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
            case R.id.protfolio_edit:
                setEditable(isEditable ? false : true);
//                sort.setVisibility(isEditable ? View.GONE : View.VISIBLE);
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

    public void moveLeft() {
        int index = adapter.getSortIndex();
        new StockDao(CPQApplication.getDB()).exchange(data.get(index), data.get(index - 1));
        int id = data.get(index).getId();
        data.get(index).setId(data.get(index - 1).getId());
        data.get(index - 1).setId(id);
        CPQApplication.setStocks();
        Stock stock = data.get(index);
        data.remove(index);
        data.add(index - 1, stock);
        adapter.setSortIndex(index - 1);
        adapter.notifyDataSetChanged();
    }

    public void moveRight() {
        int index = adapter.getSortIndex();
        new StockDao(CPQApplication.getDB()).exchange(data.get(index), data.get(index + 1));
        int id = data.get(index).getId();
        data.get(index).setId(data.get(index + 1).getId());
        data.get(index + 1).setId(id);
        CPQApplication.setStocks();
        Stock stock = data.get(index);
        data.remove(index);
        data.add(index + 1, stock);
        adapter.setSortIndex(index + 1);
        adapter.notifyDataSetChanged();
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
        protfolios.setAdapter(adapter);
        protfolios.setOnItemClickListener(new OnItemClickListener() {

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
        private int sortIndex = -1;

        public MyAdapter(Context context, List<Stock> data) {
            this.data = data;
            this.context = context;
            checked = new ArrayList<Stock>();
        }

        @Override
        public void notifyDataSetChanged() {
            if (sortIndex >= 0) {
                checked.clear();
                checked.add(getItem(sortIndex));
            }
            super.notifyDataSetChanged();
            if (isEditable) {
                checkNum.setText(checked.size() + "项被选中");
            }
            if (isSortable) {
                if (sortIndex < 0) {
                    left.setVisibility(View.GONE);
                    right.setVisibility(View.GONE);
                } else if (sortIndex == 0) {
                    left.setVisibility(View.GONE);
                    right.setVisibility(View.VISIBLE);
                } else if (sortIndex == getCount() - 1) {
                    left.setVisibility(View.VISIBLE);
                    right.setVisibility(View.GONE);
                } else {
                    left.setVisibility(View.VISIBLE);
                    right.setVisibility(View.VISIBLE);
                }
            }
        }

        public int getSortIndex() {
            return sortIndex;
        }

        public void setSortIndex(int index) {
            sortIndex = index;
            checked.clear();
            checked.add(getItem(index));
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
            TextView name, code, dq, zde, zdf, beizhu, zf;
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
            sortIndex = -1;
        }

        public List<Stock> getChecked() {
            return checked;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (null == convertView) {
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
                viewHolder.check = (CheckBox) convertView
                        .findViewById(R.id.protfolio_item_checkbox);
                viewHolder.zf = (TextView) convertView
                        .findViewById(R.id.protfolio_item_zf);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.check.setVisibility(isEditable || isSortable ? View.VISIBLE
                    : View.GONE);
            final Stock stock = getItem(position);
            viewHolder.name.setText(stock.getName());
            viewHolder.code.setText(stock.getCode());
            viewHolder.dq.setText(ArrowUtil.getStockPrice(getActivity(), stock));
            viewHolder.zf.setVisibility(stock.isGZ()
                    && (stock.isXC() || stock.isDT()) ? View.VISIBLE : stock
                    .getIs_sf() == 1 ? View.VISIBLE : View.GONE);
//			viewHolder.beizhu.setText(Constant.STATUS.get(stock.getBeizhu()));
            viewHolder.beizhu.setText(ArrowUtil.getColorString(stock.isHZ() ? "*" : "", "884898", (stock.isGZ()
                    && (stock.isXC() || stock.isDT()) ? "**" : (stock.getIs_sf() == 1 ? "*" : "  "))
                    + Constant.STATUS.get(stock.getBeizhu())));
            if (stock.getZdf() > 0) {
                viewHolder.zdf.setText("+"
                        + CPQApplication.half_up(stock.getZdf()) + "%");
                viewHolder.zde.setText("+"
                        + CPQApplication.half_up(stock.getZde()));
                convertView
                        .setBackgroundResource(isChecked(stock) ? R.drawable.orange_bg_checked
                                : R.drawable.orange_bg);
            } else if (stock.getZdf() < 0) {
                viewHolder.zdf.setText(CPQApplication.half_up(stock.getZdf())
                        + "%");
                viewHolder.zde.setText(CPQApplication.half_up(stock.getZde()));
                convertView
                        .setBackgroundResource(isChecked(stock) ? R.drawable.green_bg_checked
                                : R.drawable.green_bg);
            } else {
                viewHolder.zdf.setText(CPQApplication.half_up(stock.getZdf())
                        + "%");
                viewHolder.zde.setText(CPQApplication.half_up(stock.getZde()));
                convertView
                        .setBackgroundResource(isChecked(stock) ? R.drawable.gray_bg_checked
                                : R.drawable.gray_bg);
            }
            viewHolder.check.setChecked(isChecked(stock));
            if (isSortable) {
                viewHolder.check.setChecked(position == sortIndex);
            }
            viewHolder.check
                    .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {
                            if (isChecked) {
                                if (isSortable) {
                                    sortIndex = position;
                                    checked.clear();
                                }
                                checkStock(stock);
                            } else {
                                if (isSortable) {
                                    left.setVisibility(View.GONE);
                                    right.setVisibility(View.GONE);
                                }
                                unCheckStock(stock);
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
            if (intent.getAction().equals(Constant.ACTION_PROTFOLIO)) {
                // loadingView.hide();
                if (isSortable) {
                    return;
                }
                data.clear();
                SortUtil.sort(CPQApplication.protfolios, sortType, isDesc);
                data.addAll(CPQApplication.protfolios);
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
