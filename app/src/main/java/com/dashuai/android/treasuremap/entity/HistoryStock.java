package com.dashuai.android.treasuremap.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class HistoryStock implements Parcelable {
    //public static final String TAB_NAME = "stock_history";// 股票历史数据
    private int id;
    private String day;// => 日期(2016-02-25)
    private double open;// 10.350
    private double high;// 最高(10.490)
    private double low;// 最低(10.300)
    private double close;// 收盘(10.340)
    private double dk;// 多空(31.4875)
    private double y1;// 压1(32.295)
    private double y2;// 压2(32.3162)
    private double y3;// 压3(32.3375)
    private double z1;// 支1(31.445)
    private double z2;// 支2(31.0413)
    private double z3;// 支3(30.6375)
    private double zf;// 振幅(1.38334)
    private double jj;// 均价
    private int heightstatus;// 0普通，1红色，2橘红加粗
    private int lowstatus;// 0普通，1绿色，2蓝色加粗
    private boolean ishighseries;// 是否高位连续3天
    private boolean islowseries;// 是否低位连续5天
    private int beizhu;// 备注(0:中性,1:布局,2:拉升,3:建仓,4:减仓)
    private boolean isTk;// 是否跳空

    public HistoryStock() {
        super();
    }

    public HistoryStock(int id, String day, double open, double high, double low,
                        double close, double dk, double y1, double y2, double y3,
                        double z1, double z2, double z3, double zf, double jj,
                        int heightstatus, int lowstatus, boolean ishighseries,
                        boolean islowseries, int beizhu) {
        super();
        this.id = id;
        this.day = day;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.dk = dk;
        this.y1 = y1;
        this.y2 = y2;
        this.y3 = y3;
        this.z1 = z1;
        this.z2 = z2;
        this.z3 = z3;
        this.zf = zf;
        this.jj = jj;
        this.heightstatus = heightstatus;
        this.lowstatus = lowstatus;
        this.ishighseries = ishighseries;
        this.islowseries = islowseries;
        this.beizhu = beizhu;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isTk() {
        return isTk;
    }

    public void setTk(boolean isTk) {
        this.isTk = isTk;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
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

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getDk() {
        return dk;
    }

    public void setDk(double dk) {
        this.dk = dk;
    }

    public double getY1() {
        return y1;
    }

    public void setY1(double y1) {
        this.y1 = y1;
    }

    public double getY2() {
        return y2;
    }

    public void setY2(double y2) {
        this.y2 = y2;
    }

    public double getY3() {
        return y3;
    }

    public void setY3(double y3) {
        this.y3 = y3;
    }

    public double getZ1() {
        return z1;
    }

    public void setZ1(double z1) {
        this.z1 = z1;
    }

    public double getZ2() {
        return z2;
    }

    public void setZ2(double z2) {
        this.z2 = z2;
    }

    public double getZ3() {
        return z3;
    }

    public void setZ3(double z3) {
        this.z3 = z3;
    }

    public double getZf() {
        return zf;
    }

    public void setZf(double zf) {
        this.zf = zf;
    }

    public double getJj() {
        return jj;
    }

    public void setJj(double jj) {
        this.jj = jj;
    }

    public int getHeightstatus() {
        return heightstatus;
    }

    public void setHeightstatus(int heightstatus) {
        this.heightstatus = heightstatus;
    }

    public int getLowstatus() {
        return lowstatus;
    }

    public void setLowstatus(int lowstatus) {
        this.lowstatus = lowstatus;
    }

    public boolean isIshighseries() {
        return ishighseries;
    }

    public void setIshighseries(boolean ishighseries) {
        this.ishighseries = ishighseries;
    }

    public boolean isIslowseries() {
        return islowseries;
    }

    public void setIslowseries(boolean islowseries) {
        this.islowseries = islowseries;
    }

    public int getBeizhu() {
        return beizhu;
    }

    public void setBeizhu(int beizhu) {
        this.beizhu = beizhu;
    }

    @Override
    public String toString() {
        return "HistoryStock [id=" + id + ",day=" + day + ", open=" + open + ", high=" + high
                + ", low=" + low + ", close=" + close + ", dk=" + dk + ", y1="
                + y1 + ", y2=" + y2 + ", y3=" + y3 + ", z1=" + z1 + ", z2="
                + z2 + ", z3=" + z3 + ", zf=" + zf + ", jj=" + jj
                + ", heightstatus=" + heightstatus + ", lowstatus=" + lowstatus
                + ", ishighseries=" + ishighseries + ", islowseries="
                + islowseries + ", beizhu=" + beizhu + "]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.day);
        dest.writeDouble(this.open);
        dest.writeDouble(this.high);
        dest.writeDouble(this.low);
        dest.writeDouble(this.close);
        dest.writeDouble(this.dk);
        dest.writeDouble(this.y1);
        dest.writeDouble(this.y2);
        dest.writeDouble(this.y3);
        dest.writeDouble(this.z1);
        dest.writeDouble(this.z2);
        dest.writeDouble(this.z3);
        dest.writeDouble(this.zf);
        dest.writeDouble(this.jj);
        dest.writeInt(this.heightstatus);
        dest.writeInt(this.lowstatus);
        dest.writeByte(this.ishighseries ? (byte) 1 : (byte) 0);
        dest.writeByte(this.islowseries ? (byte) 1 : (byte) 0);
        dest.writeInt(this.beizhu);
        dest.writeByte(this.isTk ? (byte) 1 : (byte) 0);
    }

    protected HistoryStock(Parcel in) {
        this.id = in.readInt();
        this.day = in.readString();
        this.open = in.readDouble();
        this.high = in.readDouble();
        this.low = in.readDouble();
        this.close = in.readDouble();
        this.dk = in.readDouble();
        this.y1 = in.readDouble();
        this.y2 = in.readDouble();
        this.y3 = in.readDouble();
        this.z1 = in.readDouble();
        this.z2 = in.readDouble();
        this.z3 = in.readDouble();
        this.zf = in.readDouble();
        this.jj = in.readDouble();
        this.heightstatus = in.readInt();
        this.lowstatus = in.readInt();
        this.ishighseries = in.readByte() != 0;
        this.islowseries = in.readByte() != 0;
        this.beizhu = in.readInt();
        this.isTk = in.readByte() != 0;
    }

    public static final Parcelable.Creator<HistoryStock> CREATOR = new Parcelable.Creator<HistoryStock>() {
        @Override
        public HistoryStock createFromParcel(Parcel source) {
            return new HistoryStock(source);
        }

        @Override
        public HistoryStock[] newArray(int size) {
            return new HistoryStock[size];
        }
    };
}
