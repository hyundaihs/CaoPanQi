package com.dashuai.android.treasuremap.service;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.dashuai.android.treasuremap.CPQApplication;
import com.dashuai.android.treasuremap.Constant;
import com.dashuai.android.treasuremap.R;
import com.dashuai.android.treasuremap.db.StockLogDao;
import com.dashuai.android.treasuremap.entity.BzList;
import com.dashuai.android.treasuremap.entity.Stock;
import com.dashuai.android.treasuremap.entity.StockLog;
import com.dashuai.android.treasuremap.util.CalendarUtil;
import com.dashuai.android.treasuremap.util.DialogUtil;
import com.dashuai.android.treasuremap.util.JsonUtil;
import com.dashuai.android.treasuremap.util.RequestUtil;
import com.dashuai.android.treasuremap.util.RequestUtil.Reply;
import com.dashuai.android.treasuremap.util.SPUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurrentDataService extends Service implements Reply {
    private boolean flag = true;
    private RequestUtil requestUtil;
    private SPUtil spUtil;
    private List<Stock> stockList;
    private StockLogDao stockLogDao;
    private MediaPlayer mp;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        spUtil = new SPUtil(this);
        stockLogDao = new StockLogDao(CPQApplication.getDB());
        requestUtil = new RequestUtil(this, this);
        stockList = new ArrayList<Stock>();
        thread.start();
        thread2.start();
        thread3.start();
        // initRing();
    }

    private void initRing() {
        mp = new MediaPlayer();
        try {
            mp.setDataSource(this, RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            mp.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    @Override
    public void onDestroy() {
        flag = false;
        super.onDestroy();
    }

    private Thread thread3 = new Thread(new Runnable() {
        @Override
        public void run() {
            if (CPQApplication.getDB() == null) {
                return;
            }
            while (flag) {
                Log.d("Service", "红包");
                Map<String, Object> map = new HashMap<String, Object>();
                if (CPQApplication.getLastHongbaoTime() > 0) {
                    map.put("hongbao_time",
                            CPQApplication.getLastHongbaoTime());
                } else {
                    map.put("hongbao_time", spUtil.getLastHongbaoTime());
                }
                map.put("cyb", CPQApplication.CLIENT == Constant.CY ? 1 : 0);
                requestUtil.postRequest(Constant.URL_IP
                        + Constant.HONG_BAO_LISTS, map, 2, false);
                requestUtil.postRequest(
                        Constant.URL_IP + Constant.BZ_LISTS, map, 3);
                if (CPQApplication.getBzcode() >= 0) {
                    map.put("beizhu", CPQApplication.getBzcode());
                    requestUtil.postRequest(Constant.URL_IP
                            + Constant.NOW_BY_BEIZHU, map, 4);
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });
    private Thread thread2 = new Thread(new Runnable() {
        @Override
        public void run() {
            if (CPQApplication.getDB() == null) {
                return;
            }
            while (flag) {
                if (CPQApplication.getFangzhens().size() > 0) {
                    Log.d("Service", "仿真");
                    JSONArray array = new JSONArray();
                    for (int i = 0; i < CPQApplication.getFangzhens().size(); i++) {
                        array.put(CPQApplication.getFangzhens().get(i).getStockId());
                    }
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("ids", array);
                    if (CPQApplication.getLastFangzhenTime() > 0) {
                        map.put("times",
                                CPQApplication.getLastFangzhenTime());
                    } else {
                        map.put("times", spUtil.getLastFangzhenTime());
                    }
                    requestUtil.postRequest(Constant.URL_IP
                            + Constant.GET_FANGZHEN_LIST, map, 6, false);
                    if (CPQApplication.VERSION == Constant.TV) {
                        requestUtil.postRequest(Constant.URL_IP
                                + Constant.FJLOG, map, 7, false);
                    }
                }
                if (CPQApplication.stockDetails != null
                        && CPQApplication.isDetailsOpen) {
                    Log.d("Service", "详情");
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("codes", CPQApplication.stockDetails.getCode());
                    requestUtil.postRequest(Constant.URL_IP
                            + Constant.NOW_BY_ONECODE, map, 1, false);
                }
                if (CPQApplication.isIfOpen) {
                    Log.d("Service", "GZ");
                    requestUtil.postRequest(Constant.URL_IP + Constant.NOW_GZ,
                            5, false);
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    private Thread thread = new Thread(new Runnable() {

        @Override
        public void run() {
            if (CPQApplication.getDB() == null) {
                return;
            }
            while (flag) {
                sendLoading();
                stockList.clear();
                stockList.addAll(CPQApplication.getStocks());
                if (stockList.size() > 0) {
                    Log.d("Service", "自选");
                    JSONArray array = new JSONArray();
                    for (int i = 0; i < stockList.size(); i++) {
                        array.put(stockList.get(i).getCode());
                    }
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("codes", array);
                    requestUtil.postRequest(Constant.URL_IP
                            + Constant.NOW_BY_CODES, map, 0, false);
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    private void sendLoading() {
        Intent intent = new Intent();
        intent.setAction(Constant.ACTION_LOADING);
        sendBroadcast(intent);
    }

    private void addStockInfo(Stock from, Stock to) {
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
        to.setBeizhu_w(from.getBeizhu_w());
        to.setBeizhu_m(from.getBeizhu_m());
        to.setDk(from.getDk());
        to.setP1(from.getP1());
        to.setP2(from.getP2());
        to.setP3(from.getP3());
        to.setS1(from.getS1());
        to.setS2(from.getS2());
        to.setS3(from.getS3());
        to.setDk_m(from.getDk_m());
        to.setP1_m(from.getP1_m());
        to.setP2_m(from.getP2_m());
        to.setP3_m(from.getP3_m());
        to.setS1_m(from.getS1_m());
        to.setS2_m(from.getS2_m());
        to.setS3_m(from.getS3_m());
        to.setDk_w(from.getDk_w());
        to.setP1_w(from.getP1_w());
        to.setP2_w(from.getP2_w());
        to.setP3_w(from.getP3_w());
        to.setS1_w(from.getS1_w());
        to.setS2_w(from.getS2_w());
        to.setS3_w(from.getS3_w());
        to.setYcday(from.getYcday());
        to.setIs_sf(from.getIs_sf());
        to.setDT(from.isDT());
        to.setXC(from.isXC());
        to.setGZ(from.isGZ());
        to.setHZ(from.isHZ());
        to.setCl4(from.getCl4());
        to.setCl5(from.getCl5());
        to.setCl6(from.getCl6());
    }

    @Override
    public void onSuccess(JSONObject response, int what) {
        if (what == 0) {
            List<Stock> list = JsonUtil.getStocks(response);
            for (int i = 0; i < list.size(); i++) {
                Stock stockNew = list.get(i);
                for (Stock stockOld : CPQApplication.protfolios) {
                    if (stockNew.getCode().equals(stockOld.getCode())) {
                        addStockInfo(stockNew, stockOld);
                        isWarn(stockOld);
                    }
                }
            }
            Intent intent = new Intent();
            intent.setAction(Constant.ACTION_PROTFOLIO);
            sendBroadcast(intent);
        } else if (what == 1) {
            Stock stock = JsonUtil.getStock(JsonUtil.getJsonObject(response,
                    "retRes"));
            addStockInfo(stock, CPQApplication.stockDetails);
            Intent intent = new Intent();
            intent.setAction(Constant.ACTION_STOCK_DETAILS);
            sendBroadcast(intent);
        } else if (what == 2) {
            List<StockLog> list = JsonUtil.getHongbaoList(response);
            for (int i = list.size() - 1; i >= 0; i--) {
                stockLogDao.addByCodeName(list.get(i));
                if (i == 0) {
                    CPQApplication.setLastHongbaoTime(list.get(i).getmTime());
                    spUtil.setLastHongbaoTime(list.get(i).getmTime());
                }
            }
            Intent intent = new Intent();
            intent.setAction(Constant.ACTION_HONG_BAO);
            sendBroadcast(intent);
        } else if (what == 3) {// 获取分类列表
            if (null == CPQApplication.bzList) {
                CPQApplication.bzList = JsonUtil.getBzLists(response);
            } else {
                List<BzList> list = JsonUtil.getBzLists(response);
                for (int i = 0; i < list.size(); i++) {
                    for (int j = 0; j < CPQApplication.bzList.size(); j++) {
                        if (list.get(i).getBeizhu() == CPQApplication.bzList
                                .get(j).getBeizhu()) {
                            CPQApplication.bzList.get(j).getLists().clear();
                            CPQApplication.bzList.get(j).getLists()
                                    .addAll(list.get(i).getLists());
                        }
                    }
                }
            }
            Intent intent = new Intent();
            intent.setAction(Constant.ACTION_BZLIST);
            sendBroadcast(intent);
        } else if (what == 4) {
            CPQApplication.setMoreBZ(JsonUtil.getStocks(response));
            Intent intent = new Intent();
            intent.setAction(Constant.ACTION_MORE_BZ);
            sendBroadcast(intent);
        } else if (what == 5) {
            CPQApplication.gzStock = JsonUtil.getStock(JsonUtil.getJsonObject(
                    response, "retRes"));
            Intent intent = new Intent();
            intent.setAction(Constant.ACTION_IF);
            sendBroadcast(intent);
        } else if (what == 6) {
            CPQApplication.fangzhenBigs.clear();
            CPQApplication.fangzhenBigs.addAll(JsonUtil.getFangzhenBigs(response));
            Intent intent = new Intent();
            intent.setAction(Constant.ACTION_FANGZHEN);
            sendBroadcast(intent);
        } else if (what == 7) {
            List<StockLog> list = JsonUtil.getFangzhenLogs(response);
            for (int i = list.size() - 1; i >= 0; i--) {
                stockLogDao.addByCodeName(list.get(i));
                if (i == 0) {
                    CPQApplication.setLastFangzhenTime(list.get(i).getmTime());
                    spUtil.setLastFangzhenTime(list.get(i).getmTime());
                }
            }
            Intent intent = new Intent();
            intent.setAction(Constant.ACTION_HONG_BAO);
            sendBroadcast(intent);
        }
    }

    private void isWarn(String msg, Stock stock) {
        double risePrice = stock.getRisePrice();
        double fallPrice = stock.getFallPrice();
        double riseIncrease = stock.getRiseIncrease();
        double fallIncrease = stock.getFallIncrease();
        double currPrice = stock.getDq();
        double currIncrease = stock.getZdf();
        if (risePrice > 0 && currPrice >= risePrice) {
            if (!spUtil.isWarn(stock.getCode() + "risePrice_" + risePrice)) {
                warn(stock, stock.getName() + "上涨到" + risePrice + "元！");
                spUtil.setWarn(stock.getCode() + "risePrice_" + risePrice, true);
            }
        } else {
            spUtil.setWarn(stock.getCode() + "risePrice_" + risePrice, false);
        }
        if (fallPrice > 0 && currPrice <= fallPrice) {
            if (!spUtil.isWarn(stock.getCode() + "fallPrice_" + fallPrice)) {
                warn(stock, stock.getName() + "下跌到" + fallPrice + "元！");
                spUtil.setWarn(stock.getCode() + "fallPrice_" + fallPrice, true);
            }
        } else {
            spUtil.setWarn(stock.getCode() + "fallPrice_" + fallPrice, false);
        }
        if (riseIncrease > 0 && currIncrease >= riseIncrease) {
            if (!spUtil
                    .isWarn(stock.getCode() + "riseIncrease_" + riseIncrease)) {
                warn(stock, stock.getName() + "涨幅达到" + riseIncrease + "%！");
                spUtil.setWarn(
                        stock.getCode() + "riseIncrease_" + riseIncrease, true);
            }
        } else {
            spUtil.setWarn(stock.getCode() + "riseIncrease_" + riseIncrease,
                    false);
        }
        if (fallIncrease < 0 && currIncrease <= fallIncrease) {
            if (!spUtil
                    .isWarn(stock.getCode() + "fallIncrease_" + fallIncrease)) {
                warn(stock, stock.getName() + "跌幅达到" + fallIncrease + "%！");
                spUtil.setWarn(
                        stock.getCode() + "fallIncrease_" + fallIncrease, true);
            }
        } else {
            spUtil.setWarn(stock.getCode() + "fallIncrease_" + fallIncrease,
                    false);
        }
        if (stock.getHistoryWarn() == 1) {
            String text = getWarnText(stock);
            if (null != text && !"".equals(text)) {
                warn(stock, msg + text);
            }
        }
    }

    private void isWarn(Stock stock) {
        isWarn("", stock);
    }

    private String getWarnText(Stock stock) {
        String text = "";
        double currPrice = stock.getDq();
        double lastValue = spUtil.getWarnText(stock.getCode());
        double newValue = 0;
        if (currPrice > stock.getP3()) {
            newValue = stock.getP3();
            text = "平仓3";
        } else if (currPrice > stock.getP2()) {
            newValue = stock.getP2();
            text = "平仓2";
        } else if (currPrice > stock.getP1()) {
            newValue = stock.getP1();
            text = "平仓1";
        } else if (currPrice < stock.getS3()) {
            newValue = stock.getS3();
            text = "建仓3";
        } else if (currPrice < stock.getS2()) {
            newValue = stock.getS2();
            text = "建仓2";
        } else if (currPrice < stock.getS1()) {
            newValue = stock.getS1();
            text = "建仓1";
        } else {
            newValue = currPrice;
        }
        if (newValue != currPrice) {
            if (lastValue > newValue) {
                spUtil.setWarnText(stock.getCode(), newValue);
                return stock.getName() + "下跌到" + text + " " + currPrice;
            } else if (lastValue < newValue) {
                spUtil.setWarnText(stock.getCode(), newValue);
                return stock.getName() + "上涨到" + text + " " + currPrice;
            }
        } else {
            spUtil.setWarnText(stock.getCode(), newValue);
        }
        return "";
    }

    private void warn(Stock stock, String text) {
        CalendarUtil calendarUtil = new CalendarUtil();
        int hour = calendarUtil.getHour();
        int min = calendarUtil.getMinute();
        if (calendarUtil.getDayOfWeek() == 0 || calendarUtil.getDayOfWeek() == 6 || hour < 9
                || (hour == 9 && min < 30) || (hour > 11 && hour < 13)
                || hour > 15 || (hour == 11 && min > 30)
                || (hour == 15 && min > 0)) {
            return;
        }
        if (spUtil.isAllWarn()) {
            if (CPQApplication.VERSION == Constant.TV) {
                notifiCustom(text);
            }
            notification(text);
        }
        if (null != CPQApplication.getDB()) {
            StockLog stockLog = new StockLog();
            stockLog.setMessage(text);
            stockLog.setTime(calendarUtil.format(CalendarUtil.STANDARD));
            stockLog.setCode(stock.getCode());
            stockLog.setName(stock.getName());
            long re = stockLogDao.addByCodeName(stockLog);
        }
    }

    private void notification(String text) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (null == manager) {
            return;
        }
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("操盘器提示您");
        builder.setContentText(text);
        builder.setSmallIcon(R.drawable.app_icon);
        Notification notification = builder.getNotification();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        manager.notify(R.drawable.app_icon, notification);
    }

    private void notifiCustom(String text) {
        final DialogUtil dialogUtil = new DialogUtil(this, true);
        dialogUtil.setMessage("操盘器提示您:", text, "关闭所有", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogUtil.dismiss(true);
            }
        }, "关闭", null);
    }

    // private void startAlarm() {
    // mp.start();
    // mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
    //
    // @Override
    // public void onCompletion(MediaPlayer mp) {
    // mp.release();
    // mp = null;
    // }
    // });
    // Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),
    // getSystemDefultRingtoneUri());
    // if (null != r) {
    // r.play();
    // }
    // }

    // 获取系统默认铃声的Uri
    private Uri getSystemDefultRingtoneUri() {
        return RingtoneManager.getActualDefaultRingtoneUri(this,
                RingtoneManager.TYPE_ALARM);
    }

    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    Log.i("后台", appProcess.processName);
                    return true;
                } else {
                    Log.i("前台", appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public void onErrorResponse(String error, int what) {
        if (null != error) {
            Intent intent = new Intent();
            intent.setAction(Constant.ACTION_NET_UNCONNECT);
            intent.putExtra("error", error);
            sendBroadcast(intent);
        }
    }

    @Override
    public void onFailed(String error, int what) {
        if (null != error) {
            Intent intent = new Intent();
            intent.setAction(Constant.ACTION_NET_UNCONNECT);
            intent.putExtra("error", error);
            sendBroadcast(intent);
        }
    }

}
