package com.dashuai.android.treasuremap.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.dashuai.android.treasuremap.CPQApplication;
import com.dashuai.android.treasuremap.Constant;
import com.dashuai.android.treasuremap.R;
import com.dashuai.android.treasuremap.entity.HistoryStock;
import com.dashuai.android.treasuremap.entity.Stock;
import com.dashuai.android.treasuremap.ui.VideosActivity;
import com.dashuai.android.treasuremap.util.DialogUtil;
import com.dashuai.android.treasuremap.util.JsonUtil;
import com.dashuai.android.treasuremap.util.RequestUtil;
import com.dashuai.android.treasuremap.util.RequestUtil.Reply;
import com.dashuai.android.treasuremap.widget.ChartView;
import com.dashuai.android.treasuremap.widget.LineChart;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class IF_TV_Fragment extends Fragment implements Reply {

	private TextView currPrice, zde, zdf, open, close, empty, date, p1, p2, p3,
			s1, s2, s3, high, low, statu;
	private Button add;
	private DialogUtil dialogUtil;
	private RequestUtil requestUtil;
	// private MyLoadingView loadingView;
	private List<HistoryStock> data;
	private ListView listView;
	private MyAdapter adapter;

	private ChartView press, support;
	private LineChart lineChart;
	private Button changeModel;
	private boolean isSeries;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_stock_details_tv, container,
				false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
	}

	@Override
	public void onResume() {
		super.onResume();
		CPQApplication.isIfOpen = true;
		IntentFilter filter_dynamic = new IntentFilter();
		filter_dynamic.addAction(Constant.ACTION_IF);
		filter_dynamic.addAction(Constant.ACTION_NET_UNCONNECT);
		getActivity().registerReceiver(dynamicReceiver, filter_dynamic);
	}

	@Override
	public void onStop() {
		CPQApplication.isIfOpen = false;
		getActivity().unregisterReceiver(dynamicReceiver);
		super.onStop();
	}

	private void init() {
		dialogUtil = new DialogUtil(getActivity());
		requestUtil = new RequestUtil(getActivity(), this);
		data = new ArrayList<HistoryStock>();
		currPrice = (TextView) getView().findViewById(
				R.id.stock_details_currprice);
		zde = (TextView) getView().findViewById(R.id.stock_details_zde);
		zdf = (TextView) getView().findViewById(R.id.stock_details_zdf);
		add = (Button) getView().findViewById(R.id.stock_details_add);
		add.setText("视频\n教程");
		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), VideosActivity.class);
				startActivity(intent);
			}
		});
		open = (TextView) getView().findViewById(R.id.stock_details_open);
		close = (TextView) getView().findViewById(R.id.stock_details_close);
		empty = (TextView) getView().findViewById(R.id.stock_details_empty);
		date = (TextView) getView().findViewById(R.id.stock_details_date);
		p1 = (TextView) getView().findViewById(R.id.stock_details_p1);
		p2 = (TextView) getView().findViewById(R.id.stock_details_p2);
		p3 = (TextView) getView().findViewById(R.id.stock_details_p3);
		s1 = (TextView) getView().findViewById(R.id.stock_details_s1);
		s2 = (TextView) getView().findViewById(R.id.stock_details_s2);
		s3 = (TextView) getView().findViewById(R.id.stock_details_s3);
		high = (TextView) getView().findViewById(R.id.stock_details_high);
		low = (TextView) getView().findViewById(R.id.stock_details_low);
		statu = (TextView) getView().findViewById(R.id.stock_details_statu);
		// loadingView = (MyLoadingView) getView().findViewById(
		// R.id.stock_details_loadingView);

		press = (ChartView) getView().findViewById(R.id.chart_press);
		support = (ChartView) getView().findViewById(R.id.chart_support);
		changeModel = (Button) getView().findViewById(R.id.changeModel);
		lineChart = (LineChart) getView().findViewById(R.id.chart_linechart);

		changeModel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (changeModel.getText().equals("连续")) {
					isSeries = true;
				} else {
					isSeries = false;
				}
				initData();
				changeModel.setText(changeModel.getText().equals("连续") ? "全部"
						: "连续");
			}
		});

		listView = (ListView) getView().findViewById(
				R.id.stock_details_listview);
		adapter = new MyAdapter(getActivity(), data);
		listView.setAdapter(adapter);
		getData();
	}

	private void initData() {
		press.setData(data);
		press.setSeries(isSeries);
		press.setPress(true);
		support.setData(data);
		support.setSeries(isSeries);
		support.setPress(false);
		initLineChart();
	}

	private void initLineChart() {
		List<String> hData = new ArrayList<String>();
		List<Double> vData = new ArrayList<Double>();
		for (int i = 0; i < data.size(); i++) {
			hData.add(String.valueOf(i + 1));
			vData.add(data.get(i).getZf());
		}
		lineChart.setvData(vData);
		lineChart.sethData(hData);
	}

	@SuppressWarnings("deprecation")
	private void fillViews(Stock stock) {
		currPrice.setText(CPQApplication.half_up(stock.getDq()));
		double increaseValue = stock.getZdf();
		zde.setText(CPQApplication.half_up(stock.getZde()));
		if (increaseValue < 0) {
			zdf.setText(CPQApplication.half_up(increaseValue) + "%");
			currPrice.setTextColor(getResources().getColor(R.color.green));
			zdf.setTextColor(getResources().getColor(R.color.green));
			zde.setTextColor(getResources().getColor(R.color.green));
		} else if (increaseValue == 0) {
			zdf.setText("+" + CPQApplication.half_up(increaseValue) + "%");
			currPrice.setTextColor(getResources().getColor(
					android.R.color.white));
			zdf.setTextColor(getResources().getColor(android.R.color.white));
			zde.setTextColor(getResources().getColor(android.R.color.white));
		} else {
			zdf.setText("+" + CPQApplication.half_up(increaseValue) + "%");
			currPrice.setTextColor(getResources().getColor(R.color.orange));
			zdf.setTextColor(getResources().getColor(R.color.orange));
			zde.setTextColor(getResources().getColor(R.color.orange));
		}
		open.setText(CPQApplication.half_up(stock.getKp()));
		close.setText(CPQApplication.half_up(stock.getZrsp()));
		empty.setText(CPQApplication.half_up(stock.getDk()));
		p1.setText(CPQApplication.half_up(stock.getP1()));
		p2.setText(CPQApplication.half_up(stock.getP2()));
		p3.setText(CPQApplication.half_up(stock.getP3()));
		s1.setText(CPQApplication.half_up(stock.getS1()));
		s2.setText(CPQApplication.half_up(stock.getS2()));
		s3.setText(CPQApplication.half_up(stock.getS3()));
		date.setText(stock.getYcday());
		high.setText(CPQApplication.half_up(stock.getHigh()));
		low.setText(CPQApplication.half_up(stock.getLow()));
		int status = stock.getBeizhu();
		statu.setText(Constant.STATUS.get(status));
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
			viewHoler.date.setText(historyStock.getDay().substring(5));
			viewHoler.high.setText(CPQApplication.half_up(historyStock
					.getHigh()));
			viewHoler.low
					.setText(CPQApplication.half_up(historyStock.getLow()));
			viewHoler.close.setText(CPQApplication.half_up(historyStock
					.getClose()));
			viewHoler.amplitude.setText(CPQApplication.half_up(historyStock
					.getZf()) + "%");
			viewHoler.empty
					.setText(CPQApplication.half_up(historyStock.getDk()));
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
			if (historyStock.isIshighseries()) {
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

	private void getData() {
		// loadingView.showProgress();
		requestUtil.postRequest(Constant.URL_IP + Constant.HISTORY_GZ, 0);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		try {
			Field childFragmentManager = Fragment.class
					.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);

		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private BroadcastReceiver dynamicReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Constant.ACTION_IF)) {
				// loadingView.hide();
				fillViews(CPQApplication.gzStock);
			} else if (intent.getAction().equals(Constant.ACTION_NET_UNCONNECT)) {
				String errorStr = intent.getStringExtra("error");
				// loadingView.showError(errorStr);
			} else if (intent.getAction().equals(Constant.ACTION_LOADING)) {
				// loadingView.showProgress();
			}
		}
	};

	@Override
	public void onSuccess(JSONObject response, int what) {
		// loadingView.hide();
		data.clear();
		data.addAll(JsonUtil.getHistoryStocks(response));
		adapter.notifyDataSetChanged();
		initData();
	}

	@Override
	public void onErrorResponse(String errorStr, int what) {
		// loadingView.showError(errorStr);
	}

	@Override
	public void onFailed(String errorStr, int what) {
		// loadingView.showError(errorStr);
	}

}
