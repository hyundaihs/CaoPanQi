package com.dashuai.android.treasuremap.util;

import android.util.Log;

import com.dashuai.android.treasuremap.CPQApplication;
import com.dashuai.android.treasuremap.Constant;
import com.dashuai.android.treasuremap.entity.BzList;
import com.dashuai.android.treasuremap.entity.DeviceStatus;
import com.dashuai.android.treasuremap.entity.FangzhenBig;
import com.dashuai.android.treasuremap.entity.FangzhenSmall;
import com.dashuai.android.treasuremap.entity.HistoryStock;
import com.dashuai.android.treasuremap.entity.Stock;
import com.dashuai.android.treasuremap.entity.StockLog;
import com.dashuai.android.treasuremap.entity.StockPool;
import com.dashuai.android.treasuremap.entity.SzData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonUtil {
    /**
     * 判断数据获取是否成功
     *
     * @param response
     * @return
     */
    public static boolean isSuccess(JSONObject response) {
        int retInt = 0;
        try {
            retInt = response.getInt("retInt");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return retInt == 1 ? true : false;
    }

    /**
     * 获取数据获取失败错误信息
     *
     * @param response
     * @return
     */
    public static String getErrorInfo(JSONObject response) {
        String error = "";
        try {
            error = response.getString("retErr");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return error;
    }

    /**
     * 获取列表长度
     *
     * @param response
     * @return
     */
    public static int getCountFromList(JSONObject response) {
        JSONObject retRes;
        int i = 0;
        try {
            retRes = response.getJSONObject("retRes");
            i = getInt(retRes, "count");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return i;
    }

    /**
     * 获取列表更新时间
     *
     * @param response
     * @return
     */
    public static String getCreateTime(JSONObject response) {
        JSONObject retRes;
        String time = "";
        try {
            retRes = response.getJSONObject("retRes");
            time = getString(retRes, "create_time");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return time;
    }

    public static String getString(JSONObject object, String key) {
        String textString = "";
        if (!object.isNull(key)) {
            try {
                textString = object.getString(key);
            } catch (JSONException e) {
//                e.printStackTrace();
            }
        }
        return textString;

    }

    public static int getInt(JSONObject object, String key) {
        int i = 0;
        if (!object.isNull(key)) {
            try {
                i = object.getInt(key);
            } catch (JSONException e) {
//                e.printStackTrace();
            }
        }
        return i;
    }

    public static long getLong(JSONObject object, String key) {
        long i = 0;
        if (!object.isNull(key)) {
            try {
                i = object.getLong(key);
            } catch (JSONException e) {
//                e.printStackTrace();
            }
        }
        return i;
    }

    public static double getDouble(JSONObject object, String key) {
        double i = 0;
        if (!object.isNull(key)) {
            try {
                i = object.getDouble(key);
            } catch (JSONException e) {
//                e.printStackTrace();
            }
        }
        return i;
    }

    public static JSONArray getJsonArray(JSONObject object, String key) {
        JSONArray jsonArray = new JSONArray();
        if (!object.isNull(key)) {
            try {
                String jsonArrayStr = object.getString(key);
                if (null == jsonArrayStr || "".equals(jsonArray)) {
                    return jsonArray;
                }
                if (jsonArrayStr.contains("[")) {
                    jsonArray = object.getJSONArray(key);
                } else {
                    jsonArray.put(object.getJSONObject(key));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return jsonArray;
            }
        }
        return jsonArray;
    }

    public static JSONObject getJsonObject(JSONObject object, String key) {
        JSONObject result = new JSONObject();
        if (!object.isNull(key)) {
            try {
                String resultStr = object.getString(key);
                if (null == resultStr || "".equals(resultStr)) {
                    return result;
                }
                result = object.getJSONObject(key);
            } catch (JSONException e) {
                e.printStackTrace();
                return result;
            }
        }
        return result;
    }

    public static JSONObject getJSONObjectFromArray(JSONArray array, int i) {
        JSONObject object = null;
        try {
            object = array.getJSONObject(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    public static List<String> getStringListFromArray(JSONArray array) {
        List<String> list = new ArrayList<String>();
        try {
            for (int i = 0; i < array.length(); i++) {
                list.add(array.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取多个String类型的信息
     *
     * @param array
     * @return
     */
    public static List<String> getStringList(JSONArray array, String key) {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < array.length(); i++) {
            list.add(getString(getJSONObjectFromArray(array, i), key));
        }
        return list;
    }

    public static List<Stock> getStocksFromTxt(String text) {
        List<Stock> stocks = new ArrayList<Stock>();
        return stocks;
    }

    /**
     * 获取多个股票信息
     *
     * @param response
     * @return
     */
    public static List<Stock> getStocks(JSONObject response) {
        List<Stock> list = new ArrayList<Stock>();
        JSONArray array = getJsonArray(response, "retRes");
        for (int i = 0; i < array.length(); i++) {
            list.add(getStock(getJSONObjectFromArray(array, i)));
        }
        return list;
    }

    // /**
    // * 获取多个模拟器的股票信息
    // * @param response
    // * @return
    // */
    // public static List<StockEmulator> getStockEmulators(JSONObject response)
    // {
    // List<StockEmulator> list = new ArrayList<StockEmulator>();
    // JSONArray array = getJsonArray(response, "retRes");
    // for (int i = 0; i < array.length(); i++) {
    // list.add(getStockEmulator(getJSONObjectFromArray(array, i)));
    // }
    // return list;
    // }
    // /**
    // * 获取单个模拟器的股票信息
    // * @param response
    // * @return
    // */
    // public static StockEmulator getStockEmulator(JSONObject object) {
    // StockEmulator stock = new StockEmulator();
    // stock.setDay(getString(object, "day"));
    // stock.setOpen(getDouble(object, "open"));
    // stock.setHigh(getDouble(object, "high"));
    // stock.setLow(getDouble(object, "low"));
    // stock.setClose(getDouble(object, "close"));
    // stock.setDk(getDouble(object, "dk"));
    // stock.setY1(getDouble(object, "y1"));
    // stock.setY2(getDouble(object, "y2"));
    // stock.setY3(getDouble(object, "y3"));
    // stock.setZ1(getDouble(object, "z1"));
    // stock.setZ2(getDouble(object, "z2"));
    // stock.setZ3(getDouble(object, "z3"));
    // stock.setZf(getDouble(object, "zf"));
    // stock.setHeightstatus(getInt(object, "heightstatus"));
    // stock.setLowstatus(getInt(object, "lowstatus"));
    // stock.setIshighseries(getInt(object, "ishighseries") == 1 ? true
    // : false);
    // stock.setIslowseries(getInt(object, "islowseries") == 1 ? true : false);
    // stock.setBeizhu(getInt(object, "beizhu"));
    // return stock;
    // }

    /**
     * 获取股票
     *
     * @param object
     * @return
     */
    public static Stock getStock(JSONObject object) {
        Stock stock = new Stock();
        stock.setCode(getString(object, "codes"));
        stock.setName(getString(object, "title"));
        stock.setDq(getDouble(object, "dq"));
        stock.setHigh(getDouble(object, "high"));
        stock.setLow(getDouble(object, "low"));
        stock.setZde(getDouble(object, "zde"));
        stock.setZdf(getDouble(object, "zdf") * 100);
        stock.setZf(getDouble(object, "zf"));
        stock.setKp(getDouble(object, "kp"));
        stock.setZrsp(getDouble(object, "zrsp"));
        stock.setBeizhu(getInt(object, "beizhu"));
        stock.setDk(getDouble(object, "dk"));
        stock.setP1(getDouble(object, "y1"));
        stock.setP2(getDouble(object, "y2"));
        stock.setP3(getDouble(object, "y3"));
        stock.setBeizhu_w(getInt(object, "beizhu_w"));
        stock.setDk_w(getDouble(object, "dk_w"));
        stock.setP1_w(getDouble(object, "y1_w"));
        stock.setP2_w(getDouble(object, "y2_w"));
        stock.setP3_w(getDouble(object, "y3_w"));
        stock.setBeizhu_m(getInt(object, "beizhu_m"));
        stock.setDk_m(getDouble(object, "dk_m"));
        stock.setP1_m(getDouble(object, "y1_m"));
        stock.setP2_m(getDouble(object, "y2_m"));
        stock.setP3_m(getDouble(object, "y3_m"));
        stock.setIs_sf(getInt(object, "is_sf"));
        stock.setS1(getDouble(object, "z1"));
        stock.setS2(getDouble(object, "z2"));
        stock.setS3(getDouble(object, "z3"));
        stock.setS1_w(getDouble(object, "z1_w"));
        stock.setS2_w(getDouble(object, "z2_w"));
        stock.setS3_w(getDouble(object, "z3_w"));
        stock.setS1_m(getDouble(object, "z1_m"));
        stock.setS2_m(getDouble(object, "z2_m"));
        stock.setS3_m(getDouble(object, "z3_m"));
        stock.setJj(getDouble(object, "jj"));
        stock.setYcday(getString(object, "ycday"));
        stock.setSearchText(stock.getCode() + stock.getName()
                + getString(object, "pinyin"));
        stock.setGZ(getInt(object, "hongbao") == 1 ? true : false);
        stock.setXC(getInt(object, "xichou") == 1 ? true : false);
        stock.setDT(getInt(object, "cl1") == 1 ? true : false);
        stock.setHZ(getInt(object, "hz") == 1 ? true : false);
        stock.setCl4(getInt(object, "cl4"));
        stock.setCl5(getInt(object, "cl5"));
        stock.setCl6(getInt(object, "cl6"));
        return stock;
    }

    /**
     * 获取多个历史数据
     *
     * @param response
     * @return
     */
    public static ArrayList<HistoryStock> getHistoryStocks(JSONObject response) {
        ArrayList<HistoryStock> list = new ArrayList<HistoryStock>();
        JSONArray array = getJsonArray(response, "retRes");
        for (int i = 0; i < array.length(); i++) {
            list.add(getHistoryStock(getJSONObjectFromArray(array, i)));
        }
        return list;
    }

    /**
     * 获取多个周历史数据
     *
     * @param response
     * @return
     */
    public static ArrayList<HistoryStock> getWeekHistoryStocks(JSONObject response) {
        ArrayList<HistoryStock> list = new ArrayList<HistoryStock>();
        JSONObject object = getJsonObject(response, "retRes");
        JSONArray wArray = getJsonArray(object, "mw");
        for (int i = 0; i < wArray.length(); i++) {
            list.add(getHistoryStock(getJSONObjectFromArray(wArray, i)));
        }
        return list;
    }

    /**
     * 获取多个月历史数据
     *
     * @param response
     * @return
     */
    public static ArrayList<HistoryStock> getMonthHistoryStocks(JSONObject response) {
        ArrayList<HistoryStock> list = new ArrayList<HistoryStock>();
        JSONObject object = getJsonObject(response, "retRes");
        JSONArray mArray = getJsonArray(object, "mm");
        for (int i = 0; i < mArray.length(); i++) {
            list.add(getHistoryStock(getJSONObjectFromArray(mArray, i)));
        }
        return list;
    }

    /**
     * 获取历史数据
     *
     * @param object
     * @return
     */
    public static HistoryStock getHistoryStock(JSONObject object) {
        HistoryStock historyStock = new HistoryStock();
        historyStock.setDay(getString(object, "day"));
        historyStock.setOpen(getDouble(object, "open"));
        historyStock.setHigh(getDouble(object, "high"));
        historyStock.setLow(getDouble(object, "low"));
        historyStock.setClose(getDouble(object, "close"));
        historyStock.setDk(getDouble(object, "dk"));
        historyStock.setY1(getDouble(object, "y1"));
        historyStock.setY2(getDouble(object, "y2"));
        historyStock.setY3(getDouble(object, "y3"));
        historyStock.setZ1(getDouble(object, "z1"));
        historyStock.setZ2(getDouble(object, "z2"));
        historyStock.setZ3(getDouble(object, "z3"));
        historyStock.setZf(getDouble(object, "zf"));
        historyStock.setJj(getDouble(object, "jj"));
        historyStock.setTk(getInt(object, "cl2") == 1 ? true : false);
        historyStock.setHeightstatus(getInt(object, "heightstatus"));
        historyStock.setLowstatus(getInt(object, "lowstatus"));
        historyStock.setIshighseries(getInt(object, "ishighseries") == 1 ? true
                : false);
        historyStock.setIslowseries(getInt(object, "islowseries") == 1 ? true
                : false);
        historyStock.setBeizhu(getInt(object, "beizhu"));
        return historyStock;
    }

    /**
     * 获取上证信息
     *
     * @return
     */
    public static SzData getSzData(JSONObject response) {
        SzData szData = new SzData();
        JSONObject retRes = getJsonObject(response, "retRes");
        JSONObject object = getJsonObject(retRes, "sz");
        szData.setDay(getString(object, "days"));
        szData.setBeizhu(getString(object, "beizhu"));
        szData.setCw(getString(object, "cw"));
        szData.setFx(getString(object, "fx"));
        szData.setSz(getString(object, "sz"));
        szData.setY1(getString(object, "y1"));
        szData.setY2(getString(object, "y2"));
        szData.setZ1(getString(object, "z1"));
        szData.setZ2(getString(object, "z2"));
        JSONArray array = getJsonArray(retRes, "days");
        List<String> list = getStringListFromArray(array);
        szData.setDays(list);
        return szData;
    }

    /**
     * 获取一组股池数据
     *
     * @param response
     * @return
     */
    public static List<StockPool> getStockPools(JSONObject response) {
        List<StockPool> list = new ArrayList<StockPool>();
        JSONArray array = getJsonArray(response, "retRes");
        for (int i = 0; i < array.length(); i++) {
            list.add(getStockPool(getJSONObjectFromArray(array, i)));
        }
        return list;
    }

    /**
     * 获取股池信息
     *
     * @param object
     * @return
     */
    public static StockPool getStockPool(JSONObject object) {
        StockPool stockPool = new StockPool();
        stockPool.setDays(getString(object, "days"));
        stockPool.setCodes(getString(object, "codes"));
        stockPool.setTitle(getString(object, "title"));
        stockPool.setYqmb(getString(object, "yqmb"));
        stockPool.setJgqj(getString(object, "jgqj"));
        stockPool.setCw(getString(object, "cw"));
        stockPool.setFx(getString(object, "fx"));
        stockPool.setBeizhu(getString(object, "beizhu"));
        return stockPool;
    }

    /**
     * 获取所有视频信息
     *
     * @param response
     */
    public static List<HashMap<String, String>> getVideos(JSONObject response) {
        JSONArray array = getJsonArray(response, "retRes");
        List<HashMap<String, String>> videos = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < array.length(); i++) {
            HashMap<String, String> hashMap = new HashMap<String, String>();
            JSONObject object = getJSONObjectFromArray(array, i);
            hashMap.put("title", getString(object, "title"));
            hashMap.put("url", getString(object, "http_url"));
            videos.add(hashMap);
        }
        return videos;
    }

    /**
     * 获取备注分类信息
     *
     * @param response
     * @return
     */
    public static List<BzList> getBzLists(JSONObject response) {
        List<BzList> bzLists = new ArrayList<BzList>();
        JSONArray retRes = getJsonArray(response, "retRes");
        for (int i = 0; i < retRes.length(); i++) {
            BzList bzList = new BzList();
            JSONObject jsonObject = getJSONObjectFromArray(retRes, i);
            bzList.setBeizhu(getInt(jsonObject, "beizhu"));
            JSONArray array = getJsonArray(jsonObject, "lists");
            List<Stock> list = new ArrayList<Stock>();
            for (int j = 0; j < array.length(); j++) {
                JSONObject object = getJSONObjectFromArray(array, j);
                list.add(getStock(object));
            }
            bzList.setLists(list);
            bzLists.add(bzList);
        }

        return bzLists;
    }

    /**
     * 获取红包日志记录列表
     *
     * @return
     */
    public static List<StockLog> getHongbaoList(JSONObject response) {
        JSONArray array = getJsonArray(response, "retRes");
        List<StockLog> list = new ArrayList<StockLog>();
        for (int i = 0; i < array.length(); i++) {
            list.add(getHongbao(getJSONObjectFromArray(array, i)));
        }
        return list;
    }

    /**
     * 获取红包日志记录
     *
     * @return
     */
    public static StockLog getHongbao(JSONObject object) {
        StockLog stockLog = new StockLog();
        stockLog.setCode(getString(object, "codes"));
        stockLog.setName(getString(object, "title"));
        int beizhu = getInt(object, "beizhu");
        double dq = getDouble(object, "dq");
        int is_sf = getInt(object, "is_sf");
        CalendarUtil calendarUtil = new CalendarUtil();
        long mTime = getLong(object, "hongbao_time") * 1000;
        calendarUtil.setTimeInMillis(mTime);
        stockLog.setmTime(mTime);
        stockLog.setTime(calendarUtil.format(CalendarUtil.STANDARD));
        if (CPQApplication.VERSION == Constant.PHONE) {
            stockLog.setMessage(stockLog.getName() + "关注  价格：" + dq + "  "
                    + (is_sf == 1 ? "*" : "") + Constant.STATUS.get(beizhu));
        } else {
            stockLog.setMessage(stockLog.getName() + "进入关注阶段  当前价格：" + dq
                    + "  当前分类：" + (is_sf == 1 ? "*" : "")
                    + Constant.STATUS.get(beizhu));

        }
        return stockLog;
    }

    /**
     * 获取设备状态剩余时间
     *
     * @param response
     * @return
     */
    public static DeviceStatus getDeviceStatus(JSONObject response) {
        JSONObject retRes = getJsonObject(response, "retRes");
        DeviceStatus deviceStatus = new DeviceStatus();
        deviceStatus.setType(getInt(retRes, "type"));
        deviceStatus.setDays(getInt(retRes, "days"));
        return deviceStatus;
    }

    public static List<FangzhenBig> getFangzhenBigs(JSONObject response) {
        JSONArray array = getJsonArray(response, "retRes");
        List<FangzhenBig> list = new ArrayList<FangzhenBig>();
        for (int i = 0; i < array.length(); i++) {
            list.add(getFangzhenBig(getJSONObjectFromArray(array, i)));
        }
        return list;
    }

    public static FangzhenBig getFangzhenBig(JSONObject object) {
        FangzhenBig fangzhenBig = new FangzhenBig();
        fangzhenBig.setStockId(getInt(object, "id"));
        fangzhenBig.setCodes(getString(object, "codes"));
        fangzhenBig.setTitle(getString(object, "title"));
        fangzhenBig.setBase_low(getDouble(object, "base_low"));
        fangzhenBig.setResult_status(getInt(object, "status"));
        fangzhenBig.setBeizhu(getInt(object, "beizhu"));
        List<FangzhenSmall> list = new ArrayList<FangzhenSmall>();
        JSONArray array = getJsonArray(object, "lists");
        for (int i = 0; i < array.length(); i++) {
            list.add(getFangzhenSmall(getJSONObjectFromArray(array, i)));
        }
        fangzhenBig.setLists(list);
        return fangzhenBig;
    }

    public static FangzhenSmall getFangzhenSmall(JSONObject object) {
        FangzhenSmall fangzhenSmall = new FangzhenSmall();
        fangzhenSmall.setDay(getString(object, "day"));
        fangzhenSmall.setLow_1(getDouble(object, "low_1"));
        fangzhenSmall.setLow_2(getDouble(object, "low_2"));
        fangzhenSmall.setStatus(getInt(object, "status"));
        fangzhenSmall.setStlow(getDouble(object, "stlow"));
        fangzhenSmall.setLow(getDouble(object, "low"));
        fangzhenSmall.setHigh(getDouble(object, "high"));
        return fangzhenSmall;
    }

    public static List<StockLog> getFangzhenLogs(JSONObject response) {
        JSONArray array = getJsonArray(response, "retRes");
        List<StockLog> list = new ArrayList<StockLog>();
        for (int i = 0; i < array.length(); i++) {
            list.add(getFangzhenLog(getJSONObjectFromArray(array, i)));
        }
        return list;
    }

    public static StockLog getFangzhenLog(JSONObject object) {
        /**
         * [fjgpdm_id] => 128 （组合id）
         [codes] => sh600021（股票代码）
         [title] => 上海电力（股票名称）
         [status] => 1（状态 1:成功,2:失败,3:到达启动点）
         [create_time] => 2017-05-01 14:31:18（时间
         */
        StockLog stockLog = new StockLog();
        stockLog.setCode(getString(object, "codes"));
        stockLog.setName(getString(object, "title"));
        double price = getDouble(object, "db");
        CalendarUtil calendarUtil = new CalendarUtil();
        long mTime = getLong(object, "create_time") * 1000;
        calendarUtil.setTimeInMillis(mTime);
        stockLog.setmTime(mTime);
        stockLog.setTime(calendarUtil.format(CalendarUtil.STANDARD));
        switch (getInt(object, "status")) {
            case 1:
                stockLog.setMessage(stockLog.getName() + "当前价格：" + price + ",已经成功");
                break;
            case 2:
                stockLog.setMessage(stockLog.getName() + "当前价格：" + price + ",仿真失败");
                break;
            case 3:
                stockLog.setMessage(stockLog.getName() + "当前价格：" + price + ",启动点出现");
                break;
        }
        return stockLog;
    }

    public static List<String> getBzInfoList(JSONObject object) {
        List<String> list = new ArrayList<>();
        JSONArray jsonArray = getJsonArray(object, "retRes");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object1 = getJSONObjectFromArray(jsonArray, i);
            list.add(getInt(object1,"id"), getString(object1, "title"));
        }
        return list;
    }

}
