package com.dashuai.android.treasuremap.entity;

/**
 * Created by kevin on 17/4/15.
 */
public class FangzhenSmall {
    private String day;
    private double stlow;
    private int status;
    private double low_1;
    private double low_2;
    private int result_status;
    private double low;
    private double high;

    public FangzhenSmall() {
        super();
    }

    public FangzhenSmall(String day, double stlow, int status, double low_1, double low_2) {
        this.day = day;
        this.stlow = stlow;
        this.status = status;
        this.low_1 = low_1;
        this.low_2 = low_2;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public int getResult_status() {
        return result_status;
    }

    public void setResult_status(int result_status) {
        this.result_status = result_status;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public double getStlow() {
        return stlow;
    }

    public void setStlow(double stlow) {
        this.stlow = stlow;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getLow_1() {
        return low_1;
    }

    public void setLow_1(double low_1) {
        this.low_1 = low_1;
    }

    public double getLow_2() {
        return low_2;
    }

    public void setLow_2(double low_2) {
        this.low_2 = low_2;
    }

    @Override
    public String toString() {
        return "FangzhenSmall{" +
                "day='" + day + '\'' +
                ", stlow=" + stlow +
                ", status=" + status +
                ", low_1=" + low_1 +
                ", low_2=" + low_2 +
                '}';
    }
}
