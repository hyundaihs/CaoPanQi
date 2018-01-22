package com.dashuai.android.treasuremap.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.dashuai.android.treasuremap.CPQApplication;
import com.dashuai.android.treasuremap.R;

import java.util.ArrayList;
import java.util.HashMap;

public class HelpActivity extends Activity {

	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		CPQApplication.initActionBar(this, true, "帮助");
		init();
	}

	private void init() {
		listView = (ListView) findViewById(R.id.help_listview);
		String[] questions = getResources().getStringArray(
				R.array.help_question);
		String[] answers = getResources().getStringArray(R.array.help_answer);
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < questions.length; i++) {
			HashMap<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("question", questions[i]);
			hashMap.put("answer", answers[i]);
			list.add(hashMap);
		}
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, list,
				R.layout.layout_help_item,
				new String[] { "question", "answer" }, new int[] {
						R.id.question, R.id.answer });
		listView.setAdapter(simpleAdapter);
	}
}
