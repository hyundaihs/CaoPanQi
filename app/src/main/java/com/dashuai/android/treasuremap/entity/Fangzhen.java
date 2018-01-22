package com.dashuai.android.treasuremap.entity;

/**
 * Created by kevin on 17/4/15.
 */
public class Fangzhen {
    public static final String TAB_NAME = "fangzhen";//仿真

    private int id;
    private int stockId;
    private long date;

    public Fangzhen() {
        super();
    }

    public Fangzhen(int stockId, long date) {
        this.stockId = stockId;
        this.date = date;
    }

    public Fangzhen(int id, int stockId, long date) {
        this.id = id;
        this.stockId = stockId;
        this.date = date;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    @Override
    public String toString() {
        return "Fangzhen{" +
                "id=" + id +
                ", stockId=" + stockId +
                ", date=" + date +
                '}';
    }
}
