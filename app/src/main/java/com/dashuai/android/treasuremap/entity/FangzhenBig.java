package com.dashuai.android.treasuremap.entity;

import com.dashuai.android.treasuremap.R;

import java.util.List;

/**
 * Created by kevin on 17/4/15.
 */
public class FangzhenBig {
    public static final String[] RESULTS = {"监测中", "成功", "失败"};
    public static final int[] RESULT_COLORS = {android.R.color.white, R.color.orange, R.color.green};
    private int stockId;
    private String codes;//(股票代码)
    private String title;//(股票名称)
    private int beizhu;//拉升等状态
    private double base_low;//(最低点)
    private int result_status;//0，未结束，1，成功，2，失败
    private List<FangzhenSmall> lists;//（数据列表，下标为0的是启动点）

    public FangzhenBig() {
        super();
    }

    public FangzhenBig(int stockId, String codes, String title, int status,double base_low, int result_status, List<FangzhenSmall> lists) {
        this.stockId = stockId;
        this.codes = codes;
        this.title = title;
        this.result_status = result_status;
        this.base_low = base_low;
        this.beizhu = status;
        this.lists = lists;
    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public String getCodes() {
        return codes;
    }

    public void setCodes(String codes) {
        this.codes = codes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getBase_low() {
        return base_low;
    }

    public void setBase_low(double base_low) {
        this.base_low = base_low;
    }

    public List<FangzhenSmall> getLists() {
        return lists;
    }

    public void setLists(List<FangzhenSmall> lists) {
        this.lists = lists;
    }

    public int getResult_status() {
        return result_status;
    }

    public void setResult_status(int result_status) {
        this.result_status = result_status;
    }

    public int getBeizhu() {
        return beizhu;
    }

    public void setBeizhu(int beizhu) {
        this.beizhu = beizhu;
    }

    @Override
    public String toString() {
        return "FangzhenBig{" +
                "stockId=" + stockId +
                ", codes='" + codes + '\'' +
                ", title='" + title + '\'' +
                ", beizhu=" + beizhu +
                ", base_low=" + base_low +
                ", result_status=" + result_status +
                ", lists=" + lists +
                '}';
    }
}
