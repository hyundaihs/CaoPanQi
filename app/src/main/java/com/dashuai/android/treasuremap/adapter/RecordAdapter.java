package com.dashuai.android.treasuremap.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dashuai.android.treasuremap.CPQApplication;
import com.dashuai.android.treasuremap.Constant;
import com.dashuai.android.treasuremap.R;
import com.dashuai.android.treasuremap.entity.Record;

import java.util.List;

public class RecordAdapter extends BaseAdapter {
	private List<Record> data;
	private LayoutInflater inflater;
	private Resources resources;

	public RecordAdapter(Context context, List<Record> data) {
		this.data = data;
		inflater = LayoutInflater.from(context);
		resources = context.getResources();
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Record getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private class ViewHolder {
		TextView date, handle, price, volume, turnover;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (null == convertView) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.layout_record_item, parent,
					false);
			viewHolder.date = (TextView) convertView.findViewById(R.id.date);
			viewHolder.handle = (TextView) convertView
					.findViewById(R.id.handle);
			viewHolder.price = (TextView) convertView.findViewById(R.id.price);
			viewHolder.volume = (TextView) convertView
					.findViewById(R.id.volume);
			viewHolder.turnover = (TextView) convertView
					.findViewById(R.id.turnover);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Record record = getItem(position);
		viewHolder.date.setText(record.getDate().substring(2));
		viewHolder.handle.setText(Constant.BUY == record.getHandle() ? "买入"
				: "卖出");
		viewHolder.handle
				.setTextColor(Constant.BUY == record.getHandle() ? resources
						.getColor(R.color.orange) : resources
						.getColor(R.color.green));
		viewHolder.price.setText(CPQApplication.round(record.getPrice(), 2));
		viewHolder.volume.setText(String.valueOf(record.getVolume()));
		viewHolder.turnover.setText(CPQApplication.round(record.getTurnover()));
		return convertView;
	}

}
