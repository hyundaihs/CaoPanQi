package com.dashuai.android.treasuremap.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dashuai.android.treasuremap.CPQApplication;
import com.dashuai.android.treasuremap.Constant;
import com.dashuai.android.treasuremap.R;
import com.dashuai.android.treasuremap.db.DatabaseHelper;
import com.dashuai.android.treasuremap.db.StockLogDao;
import com.dashuai.android.treasuremap.fragment.EWMFragment;
import com.dashuai.android.treasuremap.fragment.FangZhenFragment;
import com.dashuai.android.treasuremap.fragment.IF_TV_Fragment;
import com.dashuai.android.treasuremap.fragment.MarketFragment;
import com.dashuai.android.treasuremap.fragment.MarketTVFragment;
import com.dashuai.android.treasuremap.fragment.MineFragment;
import com.dashuai.android.treasuremap.fragment.ProtfolioFragment;
import com.dashuai.android.treasuremap.fragment.ProtfolioTVFragment;
import com.dashuai.android.treasuremap.fragment.StocksPoolFragment;
import com.dashuai.android.treasuremap.fragment.WarnLogFragment;
import com.dashuai.android.treasuremap.service.CurrentDataService;
import com.dashuai.android.treasuremap.util.AppVersionUtil;
import com.dashuai.android.treasuremap.util.DialogUtil;
import com.dashuai.android.treasuremap.widget.MyCheckButton;
import com.dashuai.android.treasuremap.widget.MyCheckButton.OnClickTouch;

import org.w3c.dom.Text;

public class HomepageActivity extends FragmentActivity implements OnClickTouch {

    private MyCheckButton portfolio, stockPool, me, market, record, ewm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        AppVersionUtil util = new AppVersionUtil(this, Constant.APK_PATH,
                Constant.URL_IP + Constant.CHECK_V);
        util.checkVersion(false);
        Intent bindIntent = new Intent(this, CurrentDataService.class);
        startService(bindIntent);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        portfolio.setVisibility(CPQApplication.isLogined() ? View.VISIBLE
                : View.GONE);
        market.setVisibility(CPQApplication.isLogined() ? View.VISIBLE
                : View.GONE);
        stockPool.setVisibility(CPQApplication.isLogined() ? View.VISIBLE
                : View.GONE);
    }

    private void initView() {
        portfolio = (MyCheckButton) findViewById(R.id.homepage_radio1);
        market = (MyCheckButton) findViewById(R.id.homepage_radio2);
        me = (MyCheckButton) findViewById(R.id.homepage_radio3);
        stockPool = (MyCheckButton) findViewById(R.id.homepage_radio4);
        record = (MyCheckButton) findViewById(R.id.homepage_radio5);
        ewm = (MyCheckButton) findViewById(R.id.homepage_radio6);
        portfolio.setText("自选", "Portfolio");
        if (CPQApplication.CLIENT == Constant.ZZ
                || CPQApplication.CLIENT == Constant.CY
                || CPQApplication.CLIENT == Constant.CX) {
            stockPool.setText("股池", "S-Pool");
            market.setText("行情", "Market");
        } else {
            market.setText("股池", "S-Pool");
            stockPool.setText("股指", "Stock_IF");
        }
        me.setText("操盘器", "CPQ");
        record.setText("日志", "Record");
        if (CPQApplication.VERSION == Constant.PHONE) {
            ewm.setText("二维码", "EWM");
        } else {
            ewm.setText("仿真", "Simulation");
        }
        portfolio.setOnClickTouch(this);
        stockPool.setOnClickTouch(this);
        me.setOnClickTouch(this);
        market.setOnClickTouch(this);
        record.setOnClickTouch(this);
        ewm.setOnClickTouch(this);
        if (CPQApplication.isLogined()) {
            portfolio.setChecked(true);
        } else {
            me.setChecked(true);
        }
    }

    private long exitTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (portfolio.isChecked()) {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    Toast.makeText(getApplicationContext(), "再按一次退出程序",
                            Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                } else {
                    finish();
                }
                return true;
            } else {
                if (CPQApplication.VERSION == Constant.TV) {
                    loadProtfolioTV();
                    hideElse(portfolio);
                }
                portfolio.setChecked(true);
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void hideElse(View view) {
        LinearLayout linearLayout = (LinearLayout) portfolio.getParent();
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            MyCheckButton child = (MyCheckButton) linearLayout.getChildAt(i);
            if (child.getId() != view.getId()) {
                child.setChecked(false);
            }
        }
    }

    private ProtfolioFragment protfolioFragment;
    private ProtfolioTVFragment protfolioTVFragment;
    private StocksPoolFragment stocksPoolFragment;
    private MarketFragment marketFragment;
    private MarketTVFragment marketTVFragment;
    private IF_TV_Fragment if_TV_Fragment;
    private WarnLogFragment warnLogFragment;
    private MineFragment mineFragment;
    private EWMFragment ewmFragment;
    private FangZhenFragment fangZhenFragment;

    /**
     * 加载自选
     */
    private void loadProtfolio() {
        if (!CPQApplication.isLogined()) {
            return;
        }
        CPQApplication.initActionBar(this, false, "我的自选");
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (null == protfolioFragment) {
            protfolioFragment = new ProtfolioFragment();
        }
        transaction.replace(R.id.content, protfolioFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * 加载TV自选
     */
    private void loadProtfolioTV() {
        if (!CPQApplication.isLogined()) {
            return;
        }
        CPQApplication.initActionBar(this, false, "我的自选", "添加",
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(HomepageActivity.this,
                                SearchStockActivity.class));
                    }
                });
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (null == protfolioTVFragment) {
            protfolioTVFragment = new ProtfolioTVFragment();
        }
        transaction.replace(R.id.content, protfolioTVFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * 加载股池
     */
    private void loadStocksPoolFragment() {
        if (!CPQApplication.isLogined()) {
            return;
        }
        CPQApplication.initActionBar(this, false, "股池");
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (null == stocksPoolFragment) {
            stocksPoolFragment = new StocksPoolFragment();
        }
        transaction.replace(R.id.content, stocksPoolFragment);
        transaction.commit();
    }

    /**
     * 加载行情
     */
    private void loadMarketFragment() {
        if (!CPQApplication.isLogined()) {
            return;
        }
        CPQApplication.initActionBar(this, false, "行情", "排序", new OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView) v;
                if (null != marketFragment) {
                    if (textView.getText().equals("排序")) {
                        marketFragment.edit();
                    } else {
                        marketFragment.finish();
                    }
                }
                textView.setText(textView.getText().equals("排序") ? "完成" : "排序");
            }
        });
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (null == marketFragment) {
            marketFragment = new MarketFragment();
        }
        transaction.replace(R.id.content, marketFragment);
        transaction.commit();
    }

    /**
     * 加载TV版行情
     */
    private void loadMarketTVFragment() {
        if (!CPQApplication.isLogined()) {
            return;
        }
        CPQApplication.initActionBar(this, false, "行情", "排序", new OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView) v;
                if (null != marketTVFragment) {
                    if (textView.getText().equals("排序")) {
                        marketTVFragment.edit();
                    } else {
                        marketTVFragment.finish();
                    }
                }
                textView.setText(textView.getText().equals("排序") ? "完成" : "排序");
            }
        });
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (null == marketTVFragment) {
            marketTVFragment = new MarketTVFragment();
        }
        transaction.replace(R.id.content, marketTVFragment);
        transaction.commit();
    }

    /**
     * 加载TV股指
     */
    private void loadIFTVFragment() {
        if (!CPQApplication.isLogined()) {
            return;
        }
        CPQApplication.initActionBar(this, false, "股指");
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (null == if_TV_Fragment) {
            if_TV_Fragment = new IF_TV_Fragment();
        }
        transaction.replace(R.id.content, if_TV_Fragment);
        transaction.commit();
    }

    /**
     * 加载日志
     */
    private void loadWarnLogFragment() {
        CPQApplication.initActionBar(this, false, "日志", "清空",
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        new DialogUtil(HomepageActivity.this).setMessage("提示", "确定要清空日志吗？", "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                StockLogDao stockLogDao = new StockLogDao(
                                        CPQApplication.getDB());
                                stockLogDao.cleanTable();
                                if (null != warnLogFragment) {
                                    warnLogFragment.refresh(50);
                                }
                            }
                        }, "取消", null);
                    }
                });
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (null == warnLogFragment) {
            warnLogFragment = new WarnLogFragment();
        } else {
            warnLogFragment.refresh(50);
        }
        transaction.replace(R.id.content, warnLogFragment);
        transaction.commit();
    }

    /**
     * 加载我的
     */
    private void loadMineFragment() {
        CPQApplication.initActionBar(this, false, "操盘器");
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (null == mineFragment) {
            mineFragment = new MineFragment();
        }
        transaction.replace(R.id.content, mineFragment);
        transaction.commit();
    }

    /**
     * 加载二维码
     */
    private void loadEWMFragment() {
        CPQApplication.initActionBar(this, false, "二维码");
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (null == ewmFragment) {
            ewmFragment = new EWMFragment();
        }
        transaction.replace(R.id.content, ewmFragment);
        transaction.commit();
    }

    /**
     * 仿真
     */
    private void loadFangzhenFragment() {
        CPQApplication.initActionBar(this, false, "仿真", "添加", new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomepageActivity.this,
                        SearchStockActivity.class);
                intent.putExtra("model", 1);
                startActivity(intent);
            }
        });
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (null == fangZhenFragment) {
            fangZhenFragment = new FangZhenFragment();
        }
        transaction.replace(R.id.content, fangZhenFragment);
        transaction.commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.homepage_radio1:
                if (CPQApplication.VERSION == Constant.PHONE) {
                    loadProtfolio();
                } else {
                    loadProtfolioTV();
                }

                break;
            case R.id.homepage_radio2:
//			if (CPQApplication.CLIENT == Constant.ZZ
//					|| CPQApplication.CLIENT == Constant.CY
//					|| CPQApplication.CLIENT == Constant.CX) {
                if (CPQApplication.VERSION == Constant.PHONE) {
                    loadMarketFragment();
                } else {
                    loadMarketTVFragment();
                }
//			} else {
//				loadStocksPoolFragment();
//			}
                break;
            case R.id.homepage_radio3:
                loadMineFragment();
                break;
            case R.id.homepage_radio4:
                loadStocksPoolFragment();
//			if (CPQApplication.CLIENT == Constant.ZZ
//					|| CPQApplication.CLIENT == Constant.CY
//					|| CPQApplication.CLIENT == Constant.CX) {
//				loadStocksPoolFragment();
//			} else {
//				if (CPQApplication.VERSION == Constant.PHONE) {
//					loadIFFragment();
//				} else {
//					loadIFTVFragment();
//				}
//			}
                break;
            case R.id.homepage_radio5:
                loadWarnLogFragment();
                break;
            case R.id.homepage_radio6:
                if (CPQApplication.VERSION == Constant.PHONE) {
                    loadEWMFragment();
                } else {
                    loadFangzhenFragment();
                }
                break;
            default:
                if (CPQApplication.VERSION == Constant.PHONE) {
                    loadProtfolio();
                } else {
                    loadProtfolioTV();
                }
                break;
        }
    }
}
