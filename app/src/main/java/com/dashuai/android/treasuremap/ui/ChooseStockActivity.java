package com.dashuai.android.treasuremap.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.dashuai.android.treasuremap.CPQApplication;
import com.dashuai.android.treasuremap.Constant;
import com.dashuai.android.treasuremap.R;
import com.dashuai.android.treasuremap.db.DatabaseHelper;
import com.dashuai.android.treasuremap.db.StockDao;
import com.dashuai.android.treasuremap.db.HistoryStockDao;
import com.dashuai.android.treasuremap.entity.Stock;
import com.dashuai.android.treasuremap.util.DialogUtil;
import com.dashuai.android.treasuremap.util.JsonUtil;
import com.dashuai.android.treasuremap.util.RequestUtil;
import com.dashuai.android.treasuremap.util.RequestUtil.Reply;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChooseStockActivity extends Activity implements Reply,
        OnClickListener {

    private EditText editText;
    private ImageButton button;
    private DialogUtil dialogUtil;
    private RequestUtil requestUtil;
    private List<Stock> data;
    private List<Stock> searchData;
    private ListView listView;
    private MyAdapter adapter;
    private TextView historyClean;
    private View layoutHistoryTitle;
    private StockDao stockDao;
    private HistoryStockDao stockHistoryDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (CPQApplication.VERSION == Constant.PHONE) {
            setContentView(R.layout.activity_search_stock);
        } else {
            setContentView(R.layout.activity_search_stock_tv);
        }
        CPQApplication.initActionBar(this, true, "股票更换");
        init();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_btn:
                editText.setText("");
                break;
            case R.id.history_clean:
                stockHistoryDao.cleanTable();
                loadHistory();
                break;
            default:
                break;
        }
    }

    private void init() {
        stockDao = new StockDao(CPQApplication.getDB());
        stockHistoryDao = new HistoryStockDao(CPQApplication.getDB());
        dialogUtil = new DialogUtil(this);
        requestUtil = new RequestUtil(this, this);
        editText = (EditText) findViewById(R.id.search_edit);
        listView = (ListView) findViewById(R.id.search_listview);
        historyClean = (TextView) findViewById(R.id.history_clean);
        layoutHistoryTitle = findViewById(R.id.layout_history_title);
        button = (ImageButton) findViewById(R.id.search_btn);
        button.setOnClickListener(this);
        historyClean.setOnClickListener(this);
        data = new ArrayList<Stock>();
        // searchData = Stock.getAllSearchText(CPQApplication.getDB());
        searchData = new ArrayList<Stock>();
        adapter = new MyAdapter(data);
        listView.setAdapter(adapter);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() <= 0) {
                    loadHistory();
                    return;
                }
                data.clear();
                for (int i = 0; i < searchData.size(); i++) {
                    if (searchData.get(i).getSearchText()
                            .contains(s.toString().toLowerCase(Locale.ENGLISH))) {
                        data.add(searchData.get(i));
                    }
                }
                adapter.notifyDataSetChangedByHistory(false);
            }
        });
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Stock stock = (Stock) arg0.getItemAtPosition(arg2);
                returnBack(stock.getCode(), stock.getName());
            }
        });
        loadHistory();
        loadSreachData();
    }

    private void returnBack(String code, String name) {
        Intent intent = new Intent();
        intent.putExtra("code", code);
        intent.putExtra("name", name);
        setResult(Constant.CHOOSE_STOCK, intent);
        finish();
    }

    private void loadSreachData() {
        requestUtil.postRequest(Constant.URL_IP + Constant.GET_GPDM, 1);
    }

    private void loadHistory() {
        data.clear();
        data.addAll(stockHistoryDao.querySortById());
        adapter.notifyDataSetChangedByHistory(data.size() > 0 ? true : false);
    }

    private class MyAdapter extends BaseAdapter {

        private List<Stock> data;

        public MyAdapter(List<Stock> data) {
            this.data = data;
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
            TextView code, name, add;
        }

        public void notifyDataSetChangedByHistory(boolean isHistory) {
            layoutHistoryTitle.setVisibility(isHistory ? View.VISIBLE
                    : View.GONE);
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (null == convertView) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(ChooseStockActivity.this)
                        .inflate(R.layout.layout_search_list_item, parent,
                                false);
                viewHolder.code = (TextView) convertView
                        .findViewById(R.id.search_list_item_code);
                viewHolder.name = (TextView) convertView
                        .findViewById(R.id.search_list_item_name);
                viewHolder.add = (TextView) convertView
                        .findViewById(R.id.search_list_item_add);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final Stock stock = getItem(position);
            viewHolder.code.setText(stock.getCode());
            viewHolder.name.setText(stock.getName());
            viewHolder.add.setVisibility(View.GONE);

            return convertView;
        }
    }

    private void search(String code) {
        if (code.trim().length() <= 0) {
            dialogUtil.setErrorMessage("股票代码不能为空");
            return;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("codes", code.trim());
        requestUtil.postRequest(Constant.URL_IP + Constant.NOW_BY_ONECODE, map,
                0);
    }

    @Override
    public void onSuccess(JSONObject response, int what) {
        if (what == 1) {
            searchData.addAll(JsonUtil.getStocks(response));
        } else {
            Stock stock = JsonUtil.getStock(JsonUtil.getJsonObject(response,
                    "retRes"));
            stockHistoryDao.add(stock);
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putParcelable("stock", stock);
            intent.putExtras(bundle);
            CPQApplication.go2details(intent, ChooseStockActivity.this);
        }
    }

    @Override
    public void onErrorResponse(String error, int what) {
        dialogUtil.setErrorMessage(error);
    }

    @Override
    public void onFailed(String error, int what) {
        dialogUtil.setErrorMessage(error);
    }

}
