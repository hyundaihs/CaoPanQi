package com.dashuai.android.treasuremap.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dashuai.android.treasuremap.CPQApplication;
import com.dashuai.android.treasuremap.R;
import com.dashuai.android.treasuremap.entity.Stock;
import com.dashuai.android.treasuremap.util.SPUtil;

import java.util.List;

public class StockWarnActivity extends Activity implements
		OnCheckedChangeListener {

	private ListView listView;
	private SPUtil spUtil;
	private MyAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		spUtil = new SPUtil(this);
		CPQApplication
				.initActionBar(this, true, "预警", spUtil.isAllWarn(), this);

	}

	@Override
	protected void onResume() {
		init();
		super.onResume();
	}

	private void init() {
		if (null == CPQApplication.protfolios) {
			return;
		}
		listView = (ListView) findViewById(R.id.help_listview);
		adapter = new MyAdapter(this, CPQApplication.protfolios);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(StockWarnActivity.this,
						SetWarnActivity.class);
				intent.putExtra("position", position);
				startActivity(intent);
			}
		});
	}

	class MyAdapter extends BaseAdapter {

		private List<Stock> data;
		private Context context;

		private MyAdapter(Context context, List<Stock> data) {
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
			TextView textView;
			ImageView imageView;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final ViewHolder viewHolder;
			if (null == convertView) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(
						R.layout.layout_stock_warn_item, parent, false);
				viewHolder.textView = (TextView) convertView
						.findViewById(R.id.stock_warn_item_textview);
				viewHolder.imageView = (ImageView) convertView
						.findViewById(R.id.stock_warn_item_imageview);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			final Stock stock = getItem(position);
			viewHolder.textView.setText(stock.getName() + "(" + stock.getCode()
					+ ")");
			viewHolder.imageView
					.setVisibility(spUtil.isWarn(stock.getCode()) ? View.VISIBLE
							: View.GONE);
			return convertView;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		for (int i = 0; i < CPQApplication.protfolios.size(); i++) {
			spUtil.setWarn(CPQApplication.protfolios.get(i).getCode(),
					isChecked);
		}
		adapter.notifyDataSetChanged();
		spUtil.setAllWarn(isChecked);
	}
}
