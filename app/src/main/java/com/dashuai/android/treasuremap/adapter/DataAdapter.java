package com.dashuai.android.treasuremap.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dashuai.android.treasuremap.CPQApplication;
import com.dashuai.android.treasuremap.R;
import com.dashuai.android.treasuremap.entity.HistoryStock;

import java.util.List;

public class DataAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<HistoryStock> data;
    private Resources resources;
    private int select;
    private int highSeries;
    private int lowSeries;
    private Level level = Level.BASIC;

    public int getSelect() {
        return select;
    }

    public void setSelect(int select) {
        this.select = select;
    }

    public DataAdapter(Context context, List<HistoryStock> data) {
        resources = context.getResources();
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHoler viewHoler;
        highSeries = getHighSeries();
        lowSeries = getLowSeries();
        if (null == convertView) {
            viewHoler = new ViewHoler();
            convertView = inflater.inflate(R.layout.layout_stock_details_item,
                    parent, false);
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
        HistoryStock stock = getItem(position);
        if (position >= select) {
            viewHoler.date.setText(stock.getDay().trim().length() <= 2 ? stock
                    .getDay() : stock.getDay().substring(2));
            viewHoler.high.setText(CPQApplication.round(stock.getHigh(), 2));
            viewHoler.low.setText(CPQApplication.round(stock.getLow(), 2));
            viewHoler.close.setText(CPQApplication.round(stock.getClose(), 2));
            viewHoler.amplitude.setText(CPQApplication.round(stock.getZf(), 2)
                    + "%");
            viewHoler.empty.setText(CPQApplication.round(stock.getJj(), 2));
            TextPaint tp;
            switch (stock.getHeightstatus()) {
                case 0:
                    viewHoler.high.setTextColor(resources
                            .getColor(android.R.color.white));
                    tp = viewHoler.high.getPaint();
                    tp.setFakeBoldText(false);
                    break;
                case 1:
                    viewHoler.high.setTextColor(resources.getColor(R.color.orange));
                    tp = viewHoler.high.getPaint();
                    tp.setFakeBoldText(false);
                    break;
                case 2:
                    viewHoler.high.setTextColor(resources.getColor(R.color.orange));
                    tp = viewHoler.high.getPaint();
                    tp.setFakeBoldText(true);
                    break;
                default:
                    break;
            }
            switch (stock.getLowstatus()) {
                case 0:
                    viewHoler.low.setTextColor(resources
                            .getColor(android.R.color.white));
                    tp = viewHoler.low.getPaint();
                    tp.setFakeBoldText(false);
                    break;
                case 1:
                    viewHoler.low.setTextColor(resources.getColor(R.color.green));
                    tp = viewHoler.low.getPaint();
                    tp.setFakeBoldText(false);
                    break;
                case 2:
                    viewHoler.low.setTextColor(resources
                            .getColor(android.R.color.holo_blue_dark));
                    tp = viewHoler.low.getPaint();
                    tp.setFakeBoldText(true);
                    break;
                default:
                    break;
            }
            /**
             * 此处红框绿框为三天之后才能显示
             */
            if (stock.isTk()) {
                viewHoler.high.setBackgroundResource(R.drawable.white_stroke);
            } else if (highSeries == -1) {
                viewHoler.high
                        .setBackgroundResource(android.R.color.transparent);
            } else if ((highSeries == -2 && stock.isIshighseries())
                    || (position > highSeries && stock.isIshighseries())) {
                viewHoler.high.setBackgroundResource(R.drawable.red_stroke);
            } else {
                viewHoler.high
                        .setBackgroundResource(android.R.color.transparent);
            }
            if (lowSeries == -1) {
                viewHoler.low
                        .setBackgroundResource(android.R.color.transparent);
            } else if ((lowSeries == -2 && stock.isIslowseries())
                    || (position > lowSeries && stock.isIslowseries())) {
                viewHoler.low.setBackgroundResource(R.drawable.green_stroke);
            } else {
                viewHoler.low
                        .setBackgroundResource(android.R.color.transparent);
            }


            // if (stock.isIshighseries()) {
            // viewHoler.high.setBackgroundResource(R.drawable.red_stroke);
            // } else {
            // viewHoler.high
            // .setBackgroundResource(android.R.color.transparent);
            // }
            // if (stock.isIslowseries()) {
            // viewHoler.low.setBackgroundResource(R.drawable.green_stroke);
            // } else {
            // viewHoler.low
            // .setBackgroundResource(android.R.color.transparent);
            // }
        } else {
            viewHoler.date.setText(stock.getDay().trim().length() <= 2 ? stock
                    .getDay() : stock.getDay().substring(2));
            viewHoler.high.setText("**");
            viewHoler.high.setTextColor(Color.WHITE);
            viewHoler.low.setTextColor(Color.WHITE);
            viewHoler.low.setText("**");
            viewHoler.close.setText("**");
            viewHoler.amplitude.setText("**" + "%");
            viewHoler.empty.setText("**");
            viewHoler.high.setBackgroundResource(android.R.color.transparent);
            viewHoler.low.setBackgroundResource(android.R.color.transparent);
        }
        if (position == select) {
            convertView.setBackgroundResource(R.drawable.list_select_bg);
            if (level == Level.BASIC) {
                viewHoler.high.setVisibility(View.VISIBLE);
                viewHoler.close.setVisibility(View.VISIBLE);
                viewHoler.amplitude.setVisibility(View.VISIBLE);
            } else if (level == Level.MIDLL) {
                viewHoler.high.setVisibility(View.VISIBLE);
                viewHoler.close.setVisibility(View.INVISIBLE);
                viewHoler.amplitude.setVisibility(View.INVISIBLE);
            } else {
                viewHoler.high.setVisibility(View.INVISIBLE);
                viewHoler.close.setVisibility(View.INVISIBLE);
                viewHoler.amplitude.setVisibility(View.INVISIBLE);
            }
        } else {
            viewHoler.high.setVisibility(View.VISIBLE);
            viewHoler.close.setVisibility(View.VISIBLE);
            viewHoler.amplitude.setVisibility(View.VISIBLE);
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }
        return convertView;
    }

    /*
     * -1 高位所有的不显示框框 －2 所有高位都显示框框
     */
    private int getHighSeries() {
        HistoryStock stock = data.get(select);
        if (select > data.size() - 3) {
            return -1;
        }
        if (!stock.isIshighseries()) {
            return -2;
        }
        for (int i = 0; i < 3; i++) {
            HistoryStock s = data.get(select + i);
            if (!s.isIshighseries()) {
                return select + i;
            }
        }
        return -2;
    }

    private int getLowSeries() {
        HistoryStock stock = data.get(select);
        if (select > data.size() - 5) {
            return -1;
        }
        if (!stock.isIslowseries()) {
            return -2;
        }
        for (int i = 0; i < 5; i++) {
            HistoryStock s = data.get(select + i);
            if (!s.isIslowseries()) {
                return select + i;
            }
        }
        return -2;
    }

    public enum Level {
        BASIC, MIDLL, HIGH
    }

}
