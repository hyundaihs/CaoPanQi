package com.dashuai.android.treasuremap.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.dashuai.android.treasuremap.CPQApplication;
import com.dashuai.android.treasuremap.R;
import com.dashuai.android.treasuremap.adapter.DataAdapter;
import com.dashuai.android.treasuremap.adapter.RecordAdapter;
import com.dashuai.android.treasuremap.db.RecordDao;
import com.dashuai.android.treasuremap.entity.Record;
import com.dashuai.android.treasuremap.entity.Score;

import java.util.List;

/**
 * Created by kevin on 17/7/29.
 */
public class ScoreDetailsActivity extends Activity {

    Score score;
    List<Record> records;
    RecordDao recordDao;
    private TextView codeName, date, level, startEnd, totalAsset;
    private ListView recordList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socre_details);
        CPQApplication.initActionBar(this, true, "详细成绩");
        init();
        fillViews();
    }

    private void init() {
        score = (Score) getIntent().getExtras().get("score");
        recordDao = new RecordDao(CPQApplication.getDB());
        records = recordDao.querysByFangzhenId(score.getFangzhenId());
        codeName = (TextView) findViewById(R.id.score_details_code_name);
        date = (TextView) findViewById(R.id.score_details_date);
        level = (TextView) findViewById(R.id.score_details_level);
        startEnd = (TextView) findViewById(R.id.score_details_start_end_time);
        totalAsset = (TextView) findViewById(R.id.score_details_total_asset);
        recordList = (ListView) findViewById(R.id.score_details_recordList);

    }

    private void fillViews() {
        codeName.setText(score.getName() + "(" + score.getCode() + ")");
        date.setText("考试日期：" + score.getDate());
        if (score.getLevel() == 2) {
            level.setText("等级：高级");
        } else if (score.getLevel() == 1) {
            level.setText("等级：中级");
        } else {
            level.setText("等级：初级");
        }
        startEnd.setText("起始时间：" + score.getStartDate() + " 到 " + score.getEndDate());
        totalAsset.setText("总资产：" + CPQApplication.round(score.getTotalAssets(), 2));
        recordList.setAdapter(new RecordAdapter(this, records));
    }
}
