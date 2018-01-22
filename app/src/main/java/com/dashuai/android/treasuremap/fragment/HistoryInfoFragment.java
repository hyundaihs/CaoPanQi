package com.dashuai.android.treasuremap.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dashuai.android.treasuremap.CPQApplication;
import com.dashuai.android.treasuremap.R;
import com.dashuai.android.treasuremap.entity.HistoryStock;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

public class HistoryInfoFragment extends Fragment {

    private List<HistoryStock> data;
    private ListView listView;
    private MyAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_info, container,
                false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void init() {
        data = getArguments().getParcelableArrayList("data");
        if (null == data || data.size() <= 0) {
            return;
        }
        listView = (ListView) getView().findViewById(
                R.id.stock_details_listview);
        adapter = new MyAdapter(getActivity(), data);
        listView.setAdapter(adapter);
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
            viewHoler.date
                    .setText(historyStock.getDay().trim().length() <= 5 ? historyStock
                            .getDay() : historyStock.getDay().substring(5));
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
}
