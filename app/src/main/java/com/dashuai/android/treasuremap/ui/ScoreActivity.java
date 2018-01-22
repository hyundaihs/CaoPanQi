package com.dashuai.android.treasuremap.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.dashuai.android.treasuremap.CPQApplication;
import com.dashuai.android.treasuremap.R;
import com.dashuai.android.treasuremap.db.ScoreDao;
import com.dashuai.android.treasuremap.entity.Score;
import com.dashuai.android.treasuremap.util.DialogUtil;

import java.util.ArrayList;
import java.util.List;

public class ScoreActivity extends Activity implements View.OnClickListener {

    private ListView listView;
    private ScoreDao scoreDao;
    private MyAdapter adapter;
    private Button edit, delete;
    private CheckBox checkAll;
    private List<Score> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        CPQApplication.initActionBar(this, true, "考试成绩");
        listView = (ListView) findViewById(R.id.score_listview);
        edit = (Button) findViewById(R.id.score_edit);
        delete = (Button) findViewById(R.id.score_delete);
        checkAll = (CheckBox) findViewById(R.id.score_checkAll);
        scoreDao = new ScoreDao(CPQApplication.getDB());
        data = scoreDao.querySortById();
        adapter = new MyAdapter(this, data);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter.isEdited()) {
                    return;
                }
                Score score = adapter.getItem(position);
                Intent intent = new Intent(ScoreActivity.this, ScoreDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("score", score);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        edit.setText(adapter.isEdited() ? "完成" : "编辑");
        edit.setOnClickListener(this);
        delete.setOnClickListener(this);
        checkAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    adapter.checkAll();
                } else {
                    adapter.clearCheck();
                }
            }
        });
    }

    private void setEditable(boolean editable) {
        adapter.setEdited(editable);
        if (editable) {
            delete.setVisibility(View.VISIBLE);
            checkAll.setVisibility(View.VISIBLE);
        } else {
            delete.setVisibility(View.GONE);
            checkAll.setVisibility(View.GONE);
            adapter.clearCheck();
        }
        edit.setText(editable ? "完成" : "编辑");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.score_edit:
                setEditable(!adapter.isEdited());
                break;
            case R.id.score_delete:
                new DialogUtil(this).setMessage("提示", "确定删除选中项吗？", "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<Integer> list = adapter.getChecked();
                        for (int i = 0; i < list.size(); i++) {
                            Score score = data.get((int) list.get(i));
                            if (scoreDao.delete(score) > 0) {
                                data.remove(score);
                            }
                        }
                        setEditable(false);
                        adapter.clearCheck();
                    }
                }, "取消", null);
                break;
        }
    }

    class MyAdapter extends BaseAdapter {

        private List<Score> data;
        private Context context;
        private List<Integer> checked;
        private boolean isEdited;

        private MyAdapter(Context context, List<Score> data) {
            this.data = data;
            this.context = context;
            checked = new ArrayList<Integer>();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Score getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public boolean isEdited() {
            return isEdited;
        }

        public void setEdited(boolean isEdited) {
            this.isEdited = isEdited;
            clearCheck();
            notifyDataSetChanged();
        }

        public List<Integer> getChecked() {
            return checked;
        }

        public void setCheck(Integer integer) {
            check(integer);
            notifyDataSetChanged();
        }

        public void setUnCheck(Integer integer) {
            unCheck(integer);
            notifyDataSetChanged();
        }

        public boolean isChecked(Integer integer) {
            return checked.contains(integer);
        }

        private void check(Integer index) {
            if (!checked.contains(index)) {
                checked.add(index);
            }
        }

        private void unCheck(Integer index) {
            if (checked.contains(index)) {
                checked.remove(index);
            }
        }

        public void checkAll() {
            for (int i = 0; i < getCount(); i++) {
                check(i);
            }
            notifyDataSetChanged();
        }

        public void clearCheck() {
            checked.clear();
            notifyDataSetChanged();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.layout_warn_log_item, parent, false);
            }
            TextView time = (TextView) convertView.findViewById(R.id.warn_log_item_time);
            TextView message = (TextView) convertView.findViewById(R.id.warn_log_item_message);
            CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.warn_log_item_check);
            Score score = getItem(position);
            time.setText(score.getDate());
            message.setText(score.getScore());
            time.setTextColor(Color.WHITE);
            message.setTextColor(Color.WHITE);
            if (isEdited) {
                checkBox.setVisibility(View.VISIBLE);
                checkBox.setChecked(isChecked(position));
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            check(position);
                        } else {
                            unCheck(position);
                        }
                    }
                });
            } else {
                checkBox.setVisibility(View.GONE);
            }
            return convertView;
        }
    }
}
