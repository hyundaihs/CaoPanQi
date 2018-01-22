package com.dashuai.android.treasuremap.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.dashuai.android.treasuremap.R;
import com.dashuai.android.treasuremap.entity.HistoryStock;
import com.dashuai.android.treasuremap.widget.ChartView;
import com.dashuai.android.treasuremap.widget.LineChart;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class HistoryChartFragment extends Fragment {

	private List<HistoryStock> data = new ArrayList<HistoryStock>();
	private ChartView press, support;
	private LineChart lineChart;
	private Button changeModel;
	private boolean isSeries;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_history_chart, container,
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
		List<HistoryStock> list =  getArguments().getParcelableArrayList("data");
		if (null == list || list.size() <= 0) {
			return;
		}
		data.clear();
		for (int i = 0; i < (list.size() >= 30 ? 30 : list.size()); i++) {
			data.add(list.get(i));
		}
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
		initData();
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

}
