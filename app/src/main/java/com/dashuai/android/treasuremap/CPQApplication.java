package com.dashuai.android.treasuremap;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.dashuai.android.treasuremap.db.BZListStatusDao;
import com.dashuai.android.treasuremap.db.DBManager;
import com.dashuai.android.treasuremap.db.FangzhenDao;
import com.dashuai.android.treasuremap.db.StockDao;
import com.dashuai.android.treasuremap.entity.BZListStatus;
import com.dashuai.android.treasuremap.entity.BzList;
import com.dashuai.android.treasuremap.entity.Fangzhen;
import com.dashuai.android.treasuremap.entity.FangzhenBig;
import com.dashuai.android.treasuremap.entity.Record;
import com.dashuai.android.treasuremap.entity.Stock;
import com.dashuai.android.treasuremap.ui.StockDetailsActivity;
import com.dashuai.android.treasuremap.ui.StockDetailsTVActivity;
import com.dashuai.android.treasuremap.util.CalendarUtil;
import com.dashuai.android.treasuremap.util.CrashHandler;
import com.dashuai.android.treasuremap.util.FileUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;


public class CPQApplication extends Application {

    public static List<Stock> protfolios;// 自选
    public static List<Stock> beizhu;// 备注预警
    public static List<Stock> moreBZ;// 更多备注列表
    public static Stock stockDetails;// 详情
    public static Stock gzStock;
    public static List<BzList> bzList;
    public static boolean isIfOpen = false;// 是否开启股指的实时数据
    public static boolean isDetailsOpen = false;// 是否开启详情实时数据获取
    public static List<Record> records = new ArrayList<Record>();
    public static List<Stock> detailsResource = new ArrayList<Stock>();
    public static List<FangzhenBig> fangzhenBigs = new ArrayList<FangzhenBig>();
    /**
     * 用户身份
     */
    public static int CLIENT = Constant.ZZ;
    public static int VERSION = Constant.PHONE;
    private static DBManager dbManager;
    private static List<Fangzhen> fangzhens;
    private static long lastHongbaoTime;// 上次获取红包日志的时间戳
    private static long lastFangzhenTime;//上次获取的组合日志的时间
    private static int bzcode = -1;// 实时获取的备注列表的代码
    private static boolean isLogined = false;

    public static Stock getDetailsStock(int index) {
        return detailsResource.get(index);
    }

    public static void setDetailsResource(List<Stock> list) {
        detailsResource.clear();
        detailsResource.addAll(list);
    }

    public static int getDetailsResourceCount() {
        return detailsResource.size();
    }

    // public static boolean IS_TEST = true;// 是否试用

    /**
     * public static final int CRUDE = 2;// 原油 public static final int ZZ = 4;//
     * 至尊版 public static final int EMULATOR = 5;// 模拟器 public static final int
     * TEACHER = 6;// 教师
     *
     * @return
     */
    public static int getID_KEY() {
        return VERSION == Constant.PHONE ? Constant.ID_KEY[4] : Constant.ID_KEY[7];
//        switch (CLIENT) {
//            case Constant.NORMAL:
//                if (VERSION == Constant.PHONE) {
//                    return Constant.ID_KEY[1];
//                } else {
//                    return Constant.ID_KEY[3];
//                }
//            case Constant.CRUDE:
//                return Constant.ID_KEY[2];
//            case Constant.ZZ:
//                return VERSION == Constant.PHONE ? Constant.ID_KEY[4] : Constant.ID_KEY[9];
//            case Constant.EMULATOR:
//                return Constant.ID_KEY[5];
//            case Constant.TEACHER:
//                return Constant.ID_KEY[6];
//            case Constant.CY:
//                return Constant.ID_KEY[7];
//            case Constant.CX:
//                return Constant.ID_KEY[8];
//            default:
//                return Constant.ID_KEY[1];
//        }

    }

    public static String round(Double v) {
        return String.valueOf(round(v, 0));
    }

    public static boolean isLogined() {
        return isLogined;
    }

    public static void setLogined(boolean isLogined) {
        CPQApplication.isLogined = isLogined;
    }

    public static String round(Double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b = null == v ? new BigDecimal("0.0") : new BigDecimal(
                Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).toString();
    }

    public static int getBzcode() {
        return bzcode;
    }

    public static void setBzcode(int bzcode) {
        CPQApplication.bzcode = bzcode;
    }

    public static List<Stock> getMoreBZ() {
        return moreBZ;
    }

    public static void setMoreBZ(List<Stock> moreBZ) {
        CPQApplication.moreBZ = moreBZ;
    }

    public static void go2details(Intent intent, Context context) {
        if (CPQApplication.VERSION == Constant.PHONE) {
            intent.setClass(context, StockDetailsActivity.class);
        } else {
            intent.setClass(context, StockDetailsTVActivity.class);
        }
        context.startActivity(intent);
    }

    public static String half_up(double value) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        df.setRoundingMode(RoundingMode.HALF_UP);
        String string = df.format(value);
        if (string.length() > 6) {
            df = new DecimalFormat("#,##0.0");
            string = df.format(value);
        }
        return string;
    }

    public static long getLastHongbaoTime() {
        return lastHongbaoTime / 1000;
    }

    public static void setLastHongbaoTime(long lastHongbaoTime) {
        CPQApplication.lastHongbaoTime = lastHongbaoTime;
    }

    public static long getLastFangzhenTime() {
        return lastFangzhenTime;
    }

    public static void setLastFangzhenTime(long lastFangzhenTime) {
        CPQApplication.lastFangzhenTime = lastFangzhenTime;
    }

    public static DBManager getDbManager() {
        return dbManager;
    }

    public static SQLiteDatabase getDB() {
        return dbManager.getDb();
    }

    public static List<Stock> getStocks() {
        if (null == protfolios) {
            protfolios = new StockDao(getDB()).querySortById();
        }
        return protfolios;
    }

    public static List<Fangzhen> getFangzhens() {
        if (null == fangzhens) {
            fangzhens = new FangzhenDao(getDB()).querySortByID();
        }
        return fangzhens;
    }

    public static void setFangzhens() {
        if (null != fangzhens) {
            List<Fangzhen> list = new FangzhenDao(getDB()).querySortByID();
            fangzhens.clear();
            fangzhens.addAll(list);
        } else {
            fangzhens = new FangzhenDao(getDB()).querySortByID();
        }
    }

    public static void setStocks() {
        if (null != protfolios) {
            List<Stock> list = new StockDao(getDB()).querySortById();
            for (int i = 0; i < list.size(); i++) {
                Stock stockNew = list.get(i);
                for (Stock stockOld : protfolios) {
                    if (stockNew.getCode().equals(stockOld.getCode())) {
                        addStockInfo(stockOld, stockNew);
                    }
                }
            }
            protfolios.clear();
            protfolios.addAll(list);
        } else {
            protfolios = new StockDao(getDB()).querySortById();
        }
    }

    private static void addStockInfo(Stock from, Stock to) {
        to.setCode(from.getCode());
        to.setName(from.getName());
        to.setDq(from.getDq());
        to.setHigh(from.getHigh());
        to.setLow(from.getLow());
        to.setZde(from.getZde());
        to.setZdf(from.getZdf());
        to.setZf(from.getZf());
        to.setKp(from.getKp());
        to.setZrsp(from.getZrsp());
        to.setBeizhu(from.getBeizhu());
        to.setDk(from.getDk());
        to.setP1(from.getP1());
        to.setP2(from.getP2());
        to.setP3(from.getP3());
        to.setS1(from.getS1());
        to.setS2(from.getS2());
        to.setS3(from.getS3());
        to.setYcday(from.getYcday());
    }

    /**
     * 获取版本名称
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        PackageManager manager = context.getPackageManager();
        String version = "";
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),
                    0);
            version = info.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * 获取版本号
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        PackageManager manager = context.getPackageManager();
        int version = 0;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),
                    0);
            version = info.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * @param context
     * @param hasBack has backup button
     * @param title
     */
    public static void initActionBar(Activity context, boolean hasBack,
                                     String title) {
        initActionBar(context, hasBack, title, null, null, false, null);
    }

    public static void hideActionBar(Activity context) {
        ActionBar mActionbar = context.getActionBar();
        mActionbar.hide();
    }

    public static void initActionBar(Activity context, boolean hasBack,
                                     String title, boolean isChecked,
                                     OnCheckedChangeListener onCheckedChangeListener) {
        initActionBar(context, hasBack, title, null, null, isChecked,
                onCheckedChangeListener);
    }

    public static void initActionBar(final Activity context, boolean hasBack,
                                     String title, String right, OnClickListener onClickListener) {
        initActionBar(context, hasBack, title, right, onClickListener, false,
                null);
    }

    public static void initActionBar(final Activity context, boolean hasBack,
                                     String title, String right, OnClickListener onClickListener,
                                     boolean isChecked, OnCheckedChangeListener onCheckedChangeListener) {
        ActionBar mActionbar = context.getActionBar();
        mActionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionbar.setDisplayShowCustomEnabled(true);
        mActionbar.setCustomView(R.layout.layout_actionbar);
        TextView titleText = (TextView) mActionbar.getCustomView()
                .findViewById(R.id.title);
        TextView rightText = (TextView) mActionbar.getCustomView()
                .findViewById(R.id.text);
        ImageView back = (ImageView) mActionbar.getCustomView().findViewById(
                R.id.back);
        Switch titleSwitch = (Switch) mActionbar.getCustomView().findViewById(
                R.id.title_switch);
        back.setVisibility(hasBack ? View.VISIBLE : View.GONE);
        rightText.setVisibility(null == right ? View.GONE : View.VISIBLE);
        rightText.setText(null == right ? "" : right);
        titleText.setText(null == title ? "" : title);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                context.finish();
            }
        });
        if (null != onClickListener) {
            rightText.setOnClickListener(onClickListener);
        }
        titleSwitch.setVisibility(null == onCheckedChangeListener ? View.GONE
                : View.VISIBLE);
        titleSwitch.setChecked(isChecked);
        if (null != onCheckedChangeListener) {
            titleSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
        }
    }

    /**
     * 返回股指代码
     *
     * @return
     */
    @SuppressWarnings("deprecation")
    public static String getIF_stuff() {
        CalendarUtil calendarUtil = new CalendarUtil();
        if (calendarUtil.getDayOfWeek() > 3) {
            calendarUtil.setMonth(calendarUtil.getMonth() + 1);
        } else if (calendarUtil.getWeekOfMonth() == 3) {
            if (calendarUtil.getDayOfWeek() > 5) {
                calendarUtil.setMonth(calendarUtil.getMonth() + 1);
            } else if (calendarUtil.getDayOfWeek() == 5) {
                if (calendarUtil.getHourOfDay() > 14) {
                    calendarUtil.setMonth(calendarUtil.getMonth() + 1);
                }
            }
        }
        return calendarUtil.format(CalendarUtil.YYYY_MM);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        VERSION = BuildConfig.IS_PHONE ? Constant.PHONE : Constant.TV;
        // 初始化数据库
        dbManager = DBManager.getInstance(this).openDatabase();
        // 初始化相关文件路径
        if (FileUtil.initPath(Constant.IMAGE_PATH)) {
            Log.i("ZSApplication", "图片路径初始化成功");
        }
        if (FileUtil.initPath(Constant.LOG_PATH)) {
            Log.i("ZSApplication", "日志路径初始化成功");
        }
        if (FileUtil.initPath(Constant.APK_PATH)) {
            Log.i("ZSApplication", "APK路径初始化成功");
        }
        if (FileUtil.initPath(Constant.VIDEO_PATH)) {
            Log.i("ZSApplication", "视频路径初始化成功");
        }
        // 实例化日志
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext(), Constant.LOG_PATH);

        Constant.clearStatus();
        BZListStatusDao bzListStatusDao = new BZListStatusDao(CPQApplication.getDB());
        List<BZListStatus> list = bzListStatusDao.querys(new BZListStatus());
        for (int i = 0; i < list.size(); i++) {
            Constant.addStatus(list.get(i).getName());
        }
    }
}
