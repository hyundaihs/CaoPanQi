package com.dashuai.android.treasuremap.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.dashuai.android.treasuremap.Constant;
import com.dashuai.android.treasuremap.R;
import com.dashuai.android.treasuremap.ui.AboutActivity;
import com.dashuai.android.treasuremap.ui.DeviceActivity;
import com.dashuai.android.treasuremap.ui.EmulatorActivity;
import com.dashuai.android.treasuremap.ui.HelpActivity;
import com.dashuai.android.treasuremap.ui.ScoreActivity;
import com.dashuai.android.treasuremap.ui.StockWarnActivity;
import com.dashuai.android.treasuremap.ui.VideosActivity;
import com.dashuai.android.treasuremap.util.AppVersionUtil;

/**
 * 个人
 * 
 * @author kevin
 * 
 */
public class MineFragment extends Fragment implements OnClickListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_mine, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();
		getView().findViewById(R.id.mine_warn).setOnClickListener(this);
		getView().findViewById(R.id.mine_case).setOnClickListener(this);
		getView().findViewById(R.id.mine_check_v).setOnClickListener(this);
		getView().findViewById(R.id.mine_check_score).setOnClickListener(this);
		getView().findViewById(R.id.mine_help).setOnClickListener(this);
		getView().findViewById(R.id.mine_about).setOnClickListener(this);
		getView().findViewById(R.id.mine_emulator).setOnClickListener(this);
		getView().findViewById(R.id.mine_device).setOnClickListener(this);
	}

	private Uri uri = Uri.parse("http://goodcpq.com/d/spal.mp4");

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.mine_warn:
			intent.setClass(getActivity(), StockWarnActivity.class);
			startActivity(intent);
			break;
		case R.id.mine_case:
			intent.setClass(getActivity(), VideosActivity.class);
			startActivity(intent);
			break;
		case R.id.mine_check_v:
			intent.setClass(getActivity(), EmulatorActivity.class);
			startActivity(intent);
			break;
		case R.id.mine_check_score:
			intent.setClass(getActivity(), ScoreActivity.class);
			startActivity(intent);
			break;
		case R.id.mine_device:
			intent.setClass(getActivity(), DeviceActivity.class);
			startActivity(intent);
			break;
		case R.id.mine_help:
			AppVersionUtil util = new AppVersionUtil(getActivity(),
					Constant.APK_PATH, Constant.URL_IP + Constant.CHECK_V);
			util.checkVersion(true);
			break;
		case R.id.mine_about:
			intent.setClass(getActivity(), HelpActivity.class);
			startActivity(intent);
			break;
		case R.id.mine_emulator:
			intent.setClass(getActivity(), AboutActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
}
