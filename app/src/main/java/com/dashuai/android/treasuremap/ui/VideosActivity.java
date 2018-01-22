package com.dashuai.android.treasuremap.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.dashuai.android.treasuremap.CPQApplication;
import com.dashuai.android.treasuremap.Constant;
import com.dashuai.android.treasuremap.R;
import com.dashuai.android.treasuremap.util.JsonUtil;
import com.dashuai.android.treasuremap.util.RequestUtil;
import com.dashuai.android.treasuremap.util.RequestUtil.Reply;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VideosActivity extends Activity implements Reply {

	private List<HashMap<String, String>> videos = new ArrayList<HashMap<String, String>>();
	private ListView videoListview;
	private TextView empty;
	private RequestUtil requestUtil;
	private SimpleAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_videos);
		CPQApplication.initActionBar(this, true, "视频教程");
		init();
	}

	private void init() {
		requestUtil = new RequestUtil(this, this);
		videoListview = (ListView) findViewById(R.id.videoListview);
		empty = (TextView) findViewById(R.id.empty);
		requestUtil.postRequest(Constant.URL_IP + Constant.VIDEOS, 0);
		adapter = new SimpleAdapter(this, videos, R.layout.layout_video_item,
				new String[] { "title" }, new int[] { R.id.video_title });
		videoListview.setAdapter(adapter);
		videoListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (CPQApplication.VERSION == Constant.PHONE) {
					Uri uri = Uri.parse(videos.get(arg2).get("url"));
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(uri, "video/*");
					startActivity(intent);
				} else {
					Intent intent = new Intent(VideosActivity.this,
							VideoActivity.class);
					Uri uri = Uri.parse(videos.get(arg2).get("url"));
					intent.setData(uri);
					startActivity(intent);
				}

			}
		});
	}

	@Override
	public void onSuccess(JSONObject response, int what) {
		Log.d("onSuccess", response.toString());
		videos.clear();
		videos.addAll(JsonUtil.getVideos(response));
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onErrorResponse(String error, int what) {
		videos.clear();
		empty.setText(error);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onFailed(String error, int what) {
		videos.clear();
		empty.setText(error);
		adapter.notifyDataSetChanged();
	}
}
