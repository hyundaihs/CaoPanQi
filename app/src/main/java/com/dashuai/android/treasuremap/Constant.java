package com.dashuai.android.treasuremap;

import android.os.Environment;

import java.util.ArrayList;
import java.util.List;

public class Constant {
    public static final String SDCARD = Environment
            .getExternalStorageDirectory().getPath();
    public static final String APP_ROOT = SDCARD + "/caopanqi/";
    public static final String IMAGE_PATH = APP_ROOT + "image/";
    public static final String LOG_PATH = APP_ROOT + "log/";
    public static final String APK_PATH = APP_ROOT + "apk/CaoPanQi.apk";
    public static final String VIDEO_PATH = APP_ROOT + "video/spal.mp4";

    private static List<String> bzStatus = new ArrayList<>();

    public static String getStatus(int index) {
        if (index >= bzStatus.size()) {
            return "";
        }
        return bzStatus.get(index);
    }

    public static void addStatus(String status) {
        bzStatus.add(status);
    }

    public static void clearStatus() {
        bzStatus.clear();
    }

    public static void addAllStatus(List<String> status) {
        bzStatus.addAll(status);
    }

    public static List<String> getBzStatus() {
        return bzStatus;
    }

    public static int getBzStatusSize() {
        return bzStatus.size();
    }

//    public static String[] bzStatus = {"中性", "布局", "拉升", "建仓", "减仓", "关注",
//            "吸筹", "倒T型", "高开", "超买", "低买"};
    /**
     * 操盘器:1，原油:2，操盘器TV版:3，至尊版:4，操盘器-模拟器:5，教师版:6,创业版:7，次新版：8，财富版：9
     */
    public static final int[] ID_KEY = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    // public static final String[] ID_NAME = { "", "操盘器", "原油操盘器", "操盘器TV版",
    // "操盘器至尊版", "操盘器—模拟器", "操盘器教师版", "操盘器创业版","操盘器次新版" };

    public static final int NORMAL = 1;// 普通版
    public static final int CRUDE = 2;// 原油
    public static final int ZZ = 4;// 至尊版
    public static final int EMULATOR = 5;// 模拟器
    public static final int TEACHER = 6;// 教师
    public static final int CY = 7;
    public static final int CX = 8;

    public static final int PHONE = 0;// 手机客户端
    public static final int TV = 1;// 电视端

    public static final int NEUTRAL = 0;// 中性
    public static final int LAYOUT = 1;// 布局
    public static final int PULL_UP = 2;// 拉升
    public static final int OPEN_POSITION = 3;// 建仓
    public static final int UNDER_WEIGHT = 4;// 减仓

    /**
     * 操作类型
     */
    public static final int CHOOSE_STOCK = 0;
    public static final int BUY = 1;
    public static final int SELL = 2;

    /**
     * 自选广播标记
     */
    public static final String ACTION_PROTFOLIO = "com.byread.dynamic.protfolio";
    /**
     * 详情广播标记
     */
    public static final String ACTION_STOCK_DETAILS = "com.byread.dynamic.stock.details";
    /**
     * 网络异常广播标记
     */
    public static final String ACTION_NET_UNCONNECT = "com.byread.dynamic.netunconnect";
    /**
     * 股指广播标记
     */
    public static final String ACTION_IF = "com.byread.dynamic.if";
    /**
     * 加载广播标记
     */
    public static final String ACTION_LOADING = "com.byread.dynamic.loading";
    /**
     * 关注广播标记
     */
    public static final String ACTION_HONG_BAO = "com.byread.dynamic.loading";
    /**
     * 预警广播标记
     */
    public static final String ACTION_WARN = "com.byread.dynamic.warn";
    /**
     * 分类列表标记
     */
    public static final String ACTION_BZLIST = "com.byread.dynamic.beizhu";

    /**
     * 备注更多列表
     */
    public static final String ACTION_MORE_BZ = "com.byread.dynamic.more.beizhu";
    /**
     * 仿真标记
     */
    public static final String ACTION_FANGZHEN = "com.byread.dynamic.fangzhen";

	/* 新版本接口 */

    /**
     * 基本URL
     * apicpq.hyk001.com
     */
    public static final String URL_IP = "http://api.goodcpq.com/gp/app.php/Index/";
//    public static final String URL_IP = "http://apicpq.hyk001.com/gp/app.php/Index/";
    /**
     * 检查设备
     */
    public static final String CHECK_REG = "checkReg";
    /**
     * 注册设备
     */
    public static final String REG = "reg";
    /**
     * 获取预测和历史信息(原油)
     */
    public static final String GET_DAYS_DATA = "getDaysData";
    /**
     * 获取实时数据和操盘指令(原油)
     */
    public static final String GET_NOW_DATA = "getNowData";
    /**
     * 获取操盘器成功率(原油)
     */
    public static final String GET_CGL = "getCgl";

    /**
     * 根据股票代码获取实时信息(json数组)
     */
    public static final String NOW_BY_CODES = "nowBycodes";
    /**
     * 根据一个股票代码获取信息
     */
    public static final String NOW_BY_ONECODE = "nowByOnecode";
    /**
     * 历史信息及预测信息(股票)
     */
    public static final String HISTORY_BY_CODES = "historyBycodes";
    /**
     * 股指实时数据
     */
    public static final String NOW_GZ = "nowGz";
    /**
     * 股指历史数据
     */
    public static final String HISTORY_GZ = "historyGz";
    /**
     * 上证指数
     */
    public static final String SZSJ = "szsj";
    /**
     * 股池数据
     */
    public static final String GCSJ = "gcsj";
    /**
     * 检查软件版本
     */
    public static final String CHECK_V = "checkv";
    /**
     * 获取所有股票拼音数据
     */
    public static final String GET_GPDM = "getGpdm";
    /**
     * 获取所有视频教程
     */
    public static final String VIDEOS = "videos";
    /**
     * 备注分类列表
     */
    public static final String BZ_LISTS = "bzLists";
    /**
     * 根据备注获取实时信息
     */
    public static final String NOW_BY_BEIZHU = "nowBybeizhu";
    /**
     * 获取关注日志记录
     */
    public static final String HONG_BAO_LISTS = "hongbaolists";
    /**
     * 获取短信验证码
     */
    public static final String VERF = "verf";
    /**
     * 检查是否可以查看股池
     */
    public static final String CHECK_GC = "checkgc";
    /**
     * 检查剩余时间
     */
    public static final String CHECK_TIME = "checkTime";
    /**
     * 添加仿真
     */
    public static final String ADD_FANGZHEN = "addfj";
    /**
     * 获取仿真列表
     */
    public static final String GET_FANGZHEN_LIST = "fjlists";
    /**
     * 组合日志
     */
    public static final String FJLOG = "fjlog";
    /**
     * 历史信息周月
     */
    public static final String HISTORY_BY_CODES_MW = "historyBycodes_mw";

    /**
     * 获取备注列表信息
     */
    public static final String BZINDEX = "bzindex";
}
