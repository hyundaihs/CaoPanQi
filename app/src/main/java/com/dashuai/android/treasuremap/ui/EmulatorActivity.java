package com.dashuai.android.treasuremap.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.dashuai.android.treasuremap.CPQApplication;
import com.dashuai.android.treasuremap.Constant;
import com.dashuai.android.treasuremap.R;
import com.dashuai.android.treasuremap.adapter.DataAdapter;
import com.dashuai.android.treasuremap.adapter.RecordAdapter;
import com.dashuai.android.treasuremap.db.RecordDao;
import com.dashuai.android.treasuremap.db.ScoreDao;
import com.dashuai.android.treasuremap.entity.Capital;
import com.dashuai.android.treasuremap.entity.HistoryStock;
import com.dashuai.android.treasuremap.entity.Record;
import com.dashuai.android.treasuremap.entity.Score;
import com.dashuai.android.treasuremap.entity.Stock;
import com.dashuai.android.treasuremap.util.CalendarUtil;
import com.dashuai.android.treasuremap.util.DialogUtil;
import com.dashuai.android.treasuremap.util.JsonUtil;
import com.dashuai.android.treasuremap.util.RequestUtil;
import com.dashuai.android.treasuremap.util.RequestUtil.Reply;
import com.dashuai.android.treasuremap.util.SPUtil;
import com.dashuai.android.treasuremap.widget.DoubleDatePickerDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmulatorActivity extends Activity implements Reply,
        OnClickListener {

    private Capital capital;
    private List<HistoryStock> stocks = new ArrayList<HistoryStock>();
    private Stock stock = new Stock();
    private SPUtil spUtil;

    private TextView initialCap, asset, balance, accountPl, plscale, cbPrice;
    private TextView empty;
    private Button auto, buy, sell, step, restart, score, choose;
    private ListView dataList;
    private ListView recordList;
    private DataAdapter dataAdapter;
    private RecordAdapter recordAdapter;

    private RequestUtil requestUtil;
    private DialogUtil dialogUtil;

    private HistoryStock selected;
    private int select;
    private static final int SPACE_TIME = 3000;// 毫秒
    private double chengben;
    private ScoreDao scoreDao;
    private CalendarUtil startTime, endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emulator);
        scoreDao = new ScoreDao(CPQApplication.getDB());
        startTime = new CalendarUtil();
        endTime = new CalendarUtil();

        initView();
        initData();
    }

    private void initView() {
        dataList = (ListView) findViewById(R.id.dataList);
        recordList = (ListView) findViewById(R.id.recordList);
        // date = (TextView) findViewById(R.id.date);
        choose = (Button) findViewById(R.id.choose);
        initialCap = (TextView) findViewById(R.id.initial_cap);
        asset = (TextView) findViewById(R.id.asset);
        balance = (TextView) findViewById(R.id.balance);
        accountPl = (TextView) findViewById(R.id.accountPl);
        plscale = (TextView) findViewById(R.id.plscale);
        cbPrice = (TextView) findViewById(R.id.chengben);
        score = (Button) findViewById(R.id.scores);
        auto = (Button) findViewById(R.id.auto);
        buy = (Button) findViewById(R.id.buy);
        sell = (Button) findViewById(R.id.sell);
        step = (Button) findViewById(R.id.step);
        restart = (Button) findViewById(R.id.restart);
        empty = (TextView) findViewById(R.id.stock_details_item_empty);
        empty.setText("均价");
        auto.setOnClickListener(this);
        buy.setOnClickListener(this);
        sell.setOnClickListener(this);
        step.setOnClickListener(this);
        restart.setOnClickListener(this);
        score.setOnClickListener(this);
        choose.setOnClickListener(this);
        dataAdapter = new DataAdapter(this, stocks);
        dataList.setAdapter(dataAdapter);
        dataList.setSelection(select);
        dataList.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    int first = dataList.getFirstVisiblePosition();
                    if (select > first) {
                        select = first;
                        selected = dataAdapter.getItem(select);
                        dataAdapter.setSelect(select);
                        dataAdapter.notifyDataSetChanged();
                        dataList.setSelection(select);
                        countCapital();
                    } else {
                        dataList.setSelection(first);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });
        recordAdapter = new RecordAdapter(this, CPQApplication.records);
        recordList.setAdapter(recordAdapter);
        recordList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Record record = (Record) parent.getItemAtPosition(position);
                dialogUtil.setErrorMessage((null == record.getReason() || ""
                        .equals(record.getReason())) ? "未填写交易理由" : record
                        .getReason());
            }
        });
    }

    @Override
    protected void onDestroy() {
        CPQApplication.records.clear();
        super.onDestroy();
    }

    //	private void startAuto() {
//		mHandler.sendEmptyMessageDelayed(0, SPACE_TIME);
//		isAutoing = true;
//		auto.setText("暂停");
//	}
//
//	private void stopAuto() {
//		mHandler.removeMessages(0);
//		isAutoing = false;
//		auto.setText("自动");
//	}

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.choose:
                intent.setClass(EmulatorActivity.this, ChooseStockActivity.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.scores:
                if (CPQApplication.records.size() <= 0) {
                    dialogUtil.setErrorMessage("您还没有开始考试！");
                    return;
                }
                String msg = "";
                if (capital.getPlscale() * 100 < 5) {
                    msg = "不及格，请继续努力";
                } else if (capital.getPlscale() * 100 < 10) {
                    msg = "60分，很幸运，再加油";
                } else if (capital.getPlscale() * 100 < 20) {
                    msg = "70分，不错，再接再厉";
                } else if (capital.getPlscale() * 100 < 30) {
                    msg = "80分，太好了，你已经基本掌握";
                } else if (capital.getPlscale() * 100 < 40) {
                    msg = "85分，很棒哦，越来越炉火纯青了";
                } else if (capital.getPlscale() * 100 < 50) {
                    msg = "90分，太棒了，你已经是专家了";
                } else if (capital.getPlscale() * 100 < 70) {
                    msg = "95分，太精彩了，你是我偶像";
                } else {
                    msg = "100分，完美，你简直就是股神";
                }
                final Score score = new Score();
                CalendarUtil calendarUtil = new CalendarUtil();
                score.setDate(calendarUtil.format(CalendarUtil.STANDARD));
                score.setFangzhenId(calendarUtil.format(CalendarUtil.YYYYMMDDHHMMSS));
                score.setScore(msg);
                score.setCode(stock.getCode());
                score.setName(stock.getName());
                score.setTotalAssets(capital.getAsset());
                if (dataAdapter.getLevel() == DataAdapter.Level.MIDLL) {
                    score.setLevel(1);
                } else if (dataAdapter.getLevel() == DataAdapter.Level.HIGH) {
                    score.setLevel(2);
                } else {
                    score.setLevel(0);
                }
                if (CPQApplication.records.size() > 0) {
                    score.setStartDate(CPQApplication.records.get(CPQApplication.records.size() - 1).getDate());
                    score.setEndDate(CPQApplication.records.get(0).getDate());
                }
                dialogUtil.setMessage("提示：", msg, "保存",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                save(score);
                            }
                        }, "不保存", null);
                break;
            case R.id.restart:
                reStart();
                break;
            case R.id.auto:
                dialogUtil.setThreeBtnMessage("提示", "请选择等级", "高级", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        auto.setText("高级");
                        dataAdapter.setLevel(DataAdapter.Level.HIGH);
                    }
                }, "中级", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        auto.setText("中级");
                        dataAdapter.setLevel(DataAdapter.Level.MIDLL);
                    }
                }, "初级", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        auto.setText("初级");
                        dataAdapter.setLevel(DataAdapter.Level.BASIC);
                    }
                });
                reStart();
                break;
            case R.id.buy:
                intent.setClass(this, BuyActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("stock", selected);
                intent.putExtras(bundle);
                intent.putExtra("balance", capital.getBalance());
                intent.putExtra("stock_num", capital.getStockNum());
                startActivityForResult(intent, 0);
                break;
            case R.id.sell:
                intent.setClass(this, SellActivity.class);
                Bundle bundle2 = new Bundle();
                bundle2.putParcelable("stock", selected);
                intent.putExtras(bundle2);
                intent.putExtra("balance", capital.getBalance());
                intent.putExtra("stock_num", capital.getStockNum());
                startActivityForResult(intent, 0);
                break;
            case R.id.step:
                selectNext();
                break;

            default:
                break;
        }
    }

    private void selectNext() {
        select--;
        if (select < 0) {
            dialogUtil.setMessage("提示", "已经到底了，是否重新开始？", "重新开始",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            reStart();
                        }
                    }, "取消", null);
            return;
        }
        selected = dataAdapter.getItem(select);
        dataAdapter.setSelect(select);
        dataAdapter.notifyDataSetChanged();
        dataList.setSelection(select);
        countCapital();
    }

    /**
     * 重新开始
     */
    private void reStart() {
        initData();
        CPQApplication.records.clear();
        recordAdapter.notifyDataSetChanged();
    }

    private void fillView() {
        CPQApplication.initActionBar(this, true,
                stock.getName() + "(" + stock.getCode() + ")", "起始时间", new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // 最后一个false表示不显示日期，如果要显示日期，最后参数可以是true或者不用输入
                        new DoubleDatePickerDialog(EmulatorActivity.this, 0, new DoubleDatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear,
                                                  int startDayOfMonth, DatePicker endDatePicker, int endYear, int endMonthOfYear,
                                                  int endDayOfMonth) {
                                startTime.set(startYear, startMonthOfYear, startDayOfMonth);
                                endTime.set(endYear, endMonthOfYear, endDayOfMonth);
//                                String textString = String.format("开始时间：%d-%d-%d\n结束时间：%d-%d-%d\n", startYear,
//                                        startMonthOfYear + 1, startDayOfMonth, endYear, endMonthOfYear + 1, endDayOfMonth);
                                loadData();
                            }
                        }, startTime, endTime, true).show();
                    }
                });
    }

    private void initData() {
        spUtil = new SPUtil(this);
        requestUtil = new RequestUtil(this, this);
        dialogUtil = new DialogUtil(this);
        stock.setCode(spUtil.getCode());
        stock.setName(spUtil.getName());
        initCapital();
        loadData();
    }

    private void initCapital() {
        capital = new Capital();
        capital.setInitialCap(spUtil.getInitialCap());
        capital.setBalance(capital.getInitialCap());
        refreshViews();
    }

    private void countCapital() {
        double buys = 0, sells = 0;
        if (CPQApplication.records.size() <= 0) {
            return;
        }
        for (int i = 0; i < CPQApplication.records.size(); i++) {
            Record record = CPQApplication.records.get(i);
            if (record.getHandle() == Constant.BUY) {
                buys += record.getTurnover();
            } else {
                sells += record.getTurnover();
            }
        }
        chengben = ((buys - sells) == 0 || capital.getStockNum() == 0 ? 0
                : (buys - sells) / capital.getStockNum());
        capital.setAsset(capital.getStockNum() * selected.getHigh() * 0.99
                + capital.getBalance());
        capital.setAccountPl((selected.getHigh() * 0.99 - chengben)
                * capital.getStockNum());
        double pl = capital.getAccountPl() == 0 ? 0
                : (capital.getAccountPl() / (selected.getHigh() * 0.99
                * capital.getStockNum() - capital.getAccountPl()));
        capital.setPlscale(pl);
        refreshViews();
    }

    private void refreshViews() {
        cbPrice.setText(CPQApplication.round(chengben, 2));
        asset.setText(CPQApplication.round(capital.getAsset()));
        balance.setText(CPQApplication.round(capital.getBalance()));
        if (capital.getAccountPl() > 0) {
            accountPl.setTextColor(getResources().getColor(R.color.orange));
            plscale.setTextColor(getResources().getColor(R.color.orange));
        } else if (capital.getAccountPl() < 0) {
            accountPl.setTextColor(getResources().getColor(R.color.green));
            plscale.setTextColor(getResources().getColor(R.color.green));
        } else {
            accountPl.setTextColor(Color.WHITE);
            plscale.setTextColor(Color.WHITE);
        }
        accountPl.setText(CPQApplication.round(capital.getAccountPl()));
        plscale.setText(CPQApplication.round(capital.getPlscale() * 100) + "%");
    }

    private void loadData() {
        fillView();
        // dialogUtil.setProgress();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("codes", stock.getCode());
        if (startTime != null && !startTime.equals("") && endTime != null && !endTime.equals("")) {
            map.put("s_date", startTime.format(CalendarUtil.YYYY_MM_DD));
            map.put("e_date", endTime.format(CalendarUtil.YYYY_MM_DD));
        }
        map.put("pagesize", -1);
        requestUtil.postRequest(Constant.URL_IP + Constant.HISTORY_BY_CODES,
                map, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null == data) {
            return;
        }
        switch (resultCode) {
            case Constant.CHOOSE_STOCK:
                stock.setCode(data.getStringExtra("code"));
                stock.setName(data.getStringExtra("name"));
                spUtil.setStock(stock.getCode(), stock.getName());
                CPQApplication.records.clear();
                loadData();
                break;
            case Constant.BUY:
                capital.setStockNum(data.getIntExtra("stock_num", 0));
                capital.setBalance(data.getDoubleExtra("balance", 0));
                countCapital();
                break;
            case Constant.SELL:
                capital.setStockNum(data.getIntExtra("stock_num", 0));
                capital.setBalance(data.getDoubleExtra("balance", 0));
                countCapital();
                break;

            default:
                break;
        }
        recordAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSuccess(JSONObject response, int what) {
        // dialogUtil.dismiss();
        stocks.clear();
        stocks.addAll(JsonUtil.getHistoryStocks(response));
        dataAdapter.notifyDataSetChanged();
        if (dataAdapter.getCount() <= 0) {
            new DialogUtil(this).setErrorMessage("请选择合适的时间段");
            return;
        }
        select = dataAdapter.getCount();
        selectNext();
    }

    @Override
    public void onErrorResponse(String error, int what) {
        dialogUtil.setErrorMessage(error);
    }

    @Override
    public void onFailed(String error, int what) {
        dialogUtil.setErrorMessage(error);
    }

    private void save(Score score) {
        scoreDao.add(score);
        RecordDao recordDao = new RecordDao(CPQApplication.getDB());
        for (Record record : CPQApplication.records) {
            record.setFangzhenId(score.getFangzhenId());
            recordDao.add(record);
        }
        reStart();
    }
}
