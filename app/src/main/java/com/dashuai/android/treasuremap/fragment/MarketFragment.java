package com.dashuai.android.treasuremap.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dashuai.android.treasuremap.CPQApplication;
import com.dashuai.android.treasuremap.Constant;
import com.dashuai.android.treasuremap.R;
import com.dashuai.android.treasuremap.db.BZListStatusDao;
import com.dashuai.android.treasuremap.entity.BZListStatus;
import com.dashuai.android.treasuremap.entity.BzList;
import com.dashuai.android.treasuremap.entity.Stock;
import com.dashuai.android.treasuremap.ui.MoreBeizhuActivity;
import com.dashuai.android.treasuremap.util.ArrowUtil;
import com.dashuai.android.treasuremap.util.DialogUtil;
import com.dashuai.android.treasuremap.util.SPUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kevin
 */
public class MarketFragment extends Fragment {

    private DialogUtil dialogUtil;
    private LinearLayout layout;
    private List<MyAdapter> adapters;
    private List<BZListStatus> bzStatus;
    private BZListStatusDao bzListStatusDao;
    private boolean isEditable = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bzListStatusDao = new BZListStatusDao(CPQApplication.getDB());
        bzStatus = bzListStatusDao.querySortByFlag();
        return inflater.inflate(R.layout.fragment_market, container, false);
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
        filter_dynamic.addAction(Constant.ACTION_BZLIST);
        filter_dynamic.addAction(Constant.ACTION_NET_UNCONNECT);
        getActivity().registerReceiver(dynamicReceiver, filter_dynamic);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(dynamicReceiver);
    }

    private void init() {
        dialogUtil = new DialogUtil(getActivity());
        layout = (LinearLayout) getView().findViewById(R.id.market_layout);
        fillViews();
    }

    public void edit() {
        isEditable = true;
        fillViews();
    }

    public void finish() {
        isEditable = false;
        fillViews();
    }

    @Override
    public void onDestroy() {
        finish();
        super.onDestroy();
    }

    private void fillViews() {
        if (null == CPQApplication.bzList) {
            return;
        }
        for (int i = 0; i < bzStatus.size(); i++) {
            BZListStatus bzListStatus = bzStatus.get(i);
            for (int j = 0; j < CPQApplication.bzList.size(); j++) {
                if (CPQApplication.bzList.get(j).getBeizhu() == bzListStatus.getBeizhu()) {
                    createView(i, bzListStatus, CPQApplication.bzList.get(j));
                }
            }
        }
    }

    private void createView(int i, BZListStatus bzListStatus, final BzList bzList) {
        View view;
        if (layout.getChildCount() < bzStatus.size()) {
            adapters = new ArrayList<MyAdapter>();
            view = getLayoutInflater(getArguments()).inflate(
                    R.layout.layout_market_item, null, false);
            layout.addView(view);
        } else {
            adapters.get(i).notifyDataSetChanged();
            view = layout.getChildAt(i);
        }
        fillView(i, view, bzListStatus, bzList);

    }

    private void fillView(final int i, View view, final BZListStatus bzListStatus, final BzList bzList) {
        final TextView beizhu = (TextView) view.findViewById(R.id.market_beizhu);
        TextView up = (TextView) view.findViewById(R.id.market_up);
        TextView down = (TextView) view.findViewById(R.id.market_down);
        TextView more = (TextView) view.findViewById(R.id.market_more);
        final ListView listview = (ListView) view.findViewById(R.id.market_listview);
        if (!isEditable) {
            up.setVisibility(View.GONE);
            down.setVisibility(View.GONE);
            listview.setVisibility(bzListStatus.isOpen() ? View.VISIBLE : View.GONE);
            beizhu.setText((bzListStatus.isOpen() ? "-" : "+") + bzListStatus.getName());
            beizhu.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    listview.setVisibility(listview.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                    beizhu.setText((listview.getVisibility() == View.VISIBLE ? "-" : "+") + bzListStatus.getName());
                    bzListStatus.setIsOpen(bzListStatus.isOpen() ? false : true);
                    bzListStatusDao.update(bzListStatus);
                }
            });
            more.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(),
                            MoreBeizhuActivity.class);
                    intent.putExtra("statue", bzListStatus.getBeizhu());
                    startActivity(intent);
                }
            });
            beizhu.setText((listview.getVisibility() == View.VISIBLE ? "-" : "+") + bzListStatus.getName());
        } else {
            listview.setVisibility(View.GONE);
            beizhu.setText(bzListStatus.getName());
            more.setVisibility(View.GONE);
            up.setVisibility(i == 0 ? View.GONE : View.VISIBLE);
            down.setVisibility(i == bzStatus.size() - 1 ? View.GONE : View.VISIBLE);
            beizhu.setOnClickListener(null);
            up.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tempFlag = bzStatus.get(i - 1).getFlag();
                    bzStatus.get(i - 1).setFlag(bzStatus.get(i).getFlag());
                    bzStatus.get(i).setFlag(tempFlag);
                    BZListStatus temp = bzStatus.get(i - 1);
                    bzStatus.remove(i - 1);
                    bzStatus.add(i, temp);
                    bzListStatusDao.update(bzStatus.get(i - 1));
                    bzListStatusDao.update(bzStatus.get(i));
                    fillViews();
                }
            });
            down.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tempFlag = bzStatus.get(i + 1).getFlag();
                    bzStatus.get(i + 1).setFlag(bzStatus.get(i).getFlag());
                    bzStatus.get(i).setFlag(tempFlag);
                    BZListStatus temp = bzStatus.get(i + 1);
                    bzStatus.remove(i + 1);
                    bzStatus.add(i, temp);
                    bzListStatusDao.update(bzStatus.get(i + 1));
                    bzListStatusDao.update(bzStatus.get(i));
                    fillViews();
                }
            });
        }
        final MyAdapter adapter = new MyAdapter(getActivity(),
                bzList.getLists());
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                Intent intent = new Intent();
                intent.putExtra("index", arg2);
                CPQApplication.setDetailsResource(bzList.getLists());
                CPQApplication.go2details(intent, getActivity());
            }
        });
        adapters.add(adapter);
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
            TextView name, code, currPrice, increase, statu;
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
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Stock stock = getItem(position);
            viewHolder.name.setText(stock.getName());
            viewHolder.code.setText(stock.getCode());
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

    private BroadcastReceiver dynamicReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constant.ACTION_BZLIST)) {
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

}
