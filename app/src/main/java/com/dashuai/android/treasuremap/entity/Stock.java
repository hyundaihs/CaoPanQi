package com.dashuai.android.treasuremap.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

/**
 * 股票
 *
 * @author kevin
 */
public class Stock implements Parcelable, Cloneable {
    public static final String TAB_NAME = "stock";// 股票
    public static final String HISTORY_TAB_NAME = "stock_history";// 股票历史搜索数据
    private int id;// id
    private String code;// 股票代码
    private String name;// 股票名称
    private double dq;// 当前价格
    private double high;// 最高
    private double low;// 最低
    private double zde;// 涨跌额
    private double zdf;// 涨跌幅
    private double zf;// 振幅
    private double kp;// 开盘
    private double zrsp;// 昨日收盘
    private int beizhu, beizhu_w, beizhu_m;// 备注(0:中性,1:布局,2:拉升,3:建仓,4:减仓)
    private double risePrice;
    private double fallPrice;
    private double riseIncrease;
    private int is_sf;// 振幅标示
    private double fallIncrease;
    private int historyWarn;
    private double dk, p1, p2, p3, s1, s2, s3;
    private double dk_w, p1_w, p2_w, p3_w, s1_w, s2_w, s3_w;
    private double dk_m, p1_m, p2_m, p3_m, s1_m, s2_m, s3_m;
    private double jj;// 均价
    private String ycday;// 预测日期
    private String searchText;
    private boolean isDT;// 是否倒T
    private boolean isXC;// 是否吸筹
    private boolean isGZ;// 是否关注
    private boolean isHZ;// 是否今天开盘比昨天最高高
    /**
     * 高位，低位，收盘，顺序，0无序，1从小到大，2从大到小
     */
    private int cl4, cl5, cl6;

    public Stock() {
        super();
    }

    public Stock(int id, String code, String name, double dq, double high, double low, double zde,
                 double zdf, double zf, double kp, double zrsp, int beizhu, int beizhu_w, int beizhu_m, double risePrice,
                 double fallPrice, double riseIncrease, int is_sf, double fallIncrease,
                 int historyWarn, double dk, double p1, double p2, double p3, double s1, double s2,
                 double s3, double dk_w, double p1_w, double p2_w, double p3_w, double s1_w,
                 double s2_w, double s3_w, double dk_m, double p1_m, double p2_m, double p3_m,
                 double s1_m, double s2_m, double s3_m, double jj, String ycday, String searchText,
                 boolean isDT, boolean isXC, boolean isGZ, int cl4, int cl5, int cl6, boolean isHZ) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.dq = dq;
        this.high = high;
        this.low = low;
        this.zde = zde;
        this.zdf = zdf;
        this.zf = zf;
        this.kp = kp;
        this.zrsp = zrsp;
        this.beizhu = beizhu;
        this.beizhu_w = beizhu_w;
        this.beizhu_m = beizhu_m;
        this.risePrice = risePrice;
        this.fallPrice = fallPrice;
        this.riseIncrease = riseIncrease;
        this.is_sf = is_sf;
        this.fallIncrease = fallIncrease;
        this.historyWarn = historyWarn;
        this.dk = dk;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.s1 = s1;
        this.s2 = s2;
        this.s3 = s3;
        this.dk_w = dk_w;
        this.p1_w = p1_w;
        this.p2_w = p2_w;
        this.p3_w = p3_w;
        this.s1_w = s1_w;
        this.s2_w = s2_w;
        this.s3_w = s3_w;
        this.dk_m = dk_m;
        this.p1_m = p1_m;
        this.p2_m = p2_m;
        this.p3_m = p3_m;
        this.s1_m = s1_m;
        this.s2_m = s2_m;
        this.s3_m = s3_m;
        this.jj = jj;
        this.ycday = ycday;
        this.searchText = searchText;
        this.isDT = isDT;
        this.isXC = isXC;
        this.isGZ = isGZ;
        this.isHZ = isHZ;
        this.cl4 = cl4;
        this.cl5 = cl5;
        this.cl6 = cl6;
    }


    public double getDk_w() {
        return dk_w;
    }

    public void setDk_w(double dk_w) {
        this.dk_w = dk_w;
    }

    public double getP1_w() {
        return p1_w;
    }

    public void setP1_w(double p1_w) {
        this.p1_w = p1_w;
    }

    public double getP2_w() {
        return p2_w;
    }

    public void setP2_w(double p2_w) {
        this.p2_w = p2_w;
    }

    public double getP3_w() {
        return p3_w;
    }

    public void setP3_w(double p3_w) {
        this.p3_w = p3_w;
    }

    public double getS1_w() {
        return s1_w;
    }

    public void setS1_w(double s1_w) {
        this.s1_w = s1_w;
    }

    public double getS2_w() {
        return s2_w;
    }

    public void setS2_w(double s2_w) {
        this.s2_w = s2_w;
    }

    public double getS3_w() {
        return s3_w;
    }

    public void setS3_w(double s3_w) {
        this.s3_w = s3_w;
    }

    public double getDk_m() {
        return dk_m;
    }

    public void setDk_m(double dk_m) {
        this.dk_m = dk_m;
    }

    public double getP1_m() {
        return p1_m;
    }

    public void setP1_m(double p1_m) {
        this.p1_m = p1_m;
    }

    public double getP2_m() {
        return p2_m;
    }

    public void setP2_m(double p2_m) {
        this.p2_m = p2_m;
    }

    public double getP3_m() {
        return p3_m;
    }

    public void setP3_m(double p3_m) {
        this.p3_m = p3_m;
    }

    public double getS1_m() {
        return s1_m;
    }

    public void setS1_m(double s1_m) {
        this.s1_m = s1_m;
    }

    public double getS2_m() {
        return s2_m;
    }

    public void setS2_m(double s2_m) {
        this.s2_m = s2_m;
    }

    public double getS3_m() {
        return s3_m;
    }

    public void setS3_m(double s3_m) {
        this.s3_m = s3_m;
    }

    public int getCl4() {
        return cl4;
    }

    public void setCl4(int cl4) {
        this.cl4 = cl4;
    }

    public int getCl5() {
        return cl5;
    }

    public void setCl5(int cl5) {
        this.cl5 = cl5;
    }

    public int getCl6() {
        return cl6;
    }

    public void setCl6(int cl6) {
        this.cl6 = cl6;
    }

    public boolean isDT() {
        return isDT;
    }

    public void setDT(boolean isDT) {
        this.isDT = isDT;
    }

    public boolean isXC() {
        return isXC;
    }

    public void setXC(boolean isXC) {
        this.isXC = isXC;
    }

    public boolean isGZ() {
        return isGZ;
    }

    public void setGZ(boolean isGZ) {
        this.isGZ = isGZ;
    }

    public int getIs_sf() {
        return is_sf;
    }

    public void setIs_sf(int is_sf) {
        this.is_sf = is_sf;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDq() {
        return dq;
    }

    public void setDq(double dq) {
        this.dq = dq;
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

    public double getZde() {
        return zde;
    }

    public void setZde(double zde) {
        this.zde = zde;
    }

    public double getZdf() {
        return zdf;
    }

    public void setZdf(double zdf) {
        this.zdf = zdf;
    }

    public double getZf() {
        return zf;
    }

    public void setZf(double zf) {
        this.zf = zf;
    }

    public double getKp() {
        return kp;
    }

    public void setKp(double kp) {
        this.kp = kp;
    }

    public double getZrsp() {
        return zrsp;
    }

    public void setZrsp(double zrsp) {
        this.zrsp = zrsp;
    }

    public int getBeizhu() {
        return beizhu;
    }

    public void setBeizhu(int beizhu) {
        this.beizhu = beizhu;
    }

    public double getRisePrice() {
        return risePrice;
    }

    public void setRisePrice(double risePrice) {
        this.risePrice = risePrice;
    }

    public double getFallPrice() {
        return fallPrice;
    }

    public void setFallPrice(double fallPrice) {
        this.fallPrice = fallPrice;
    }

    public double getRiseIncrease() {
        return riseIncrease;
    }

    public void setRiseIncrease(double riseIncrease) {
        this.riseIncrease = riseIncrease;
    }

    public double getFallIncrease() {
        return fallIncrease;
    }

    public void setFallIncrease(double fallIncrease) {
        this.fallIncrease = fallIncrease;
    }

    public int getHistoryWarn() {
        return historyWarn;
    }

    public void setHistoryWarn(int historyWarn) {
        this.historyWarn = historyWarn;
    }

    public double getDk() {
        return dk;
    }

    public void setDk(double dk) {
        this.dk = dk;
    }

    public double getP1() {
        return p1;
    }

    public void setP1(double p1) {
        this.p1 = p1;
    }

    public double getP2() {
        return p2;
    }

    public void setP2(double p2) {
        this.p2 = p2;
    }

    public double getP3() {
        return p3;
    }

    public void setP3(double p3) {
        this.p3 = p3;
    }

    public double getS1() {
        return s1;
    }

    public void setS1(double s1) {
        this.s1 = s1;
    }

    public double getS2() {
        return s2;
    }

    public void setS2(double s2) {
        this.s2 = s2;
    }

    public double getS3() {
        return s3;
    }

    public void setS3(double s3) {
        this.s3 = s3;
    }

    public double getJj() {
        return jj;
    }

    public void setJj(double jj) {
        this.jj = jj;
    }

    public String getYcday() {
        return ycday;
    }

    public void setYcday(String ycday) {
        this.ycday = ycday;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public int getBeizhu_w() {
        return beizhu_w;
    }

    public void setBeizhu_w(int beizhu_w) {
        this.beizhu_w = beizhu_w;
    }

    public int getBeizhu_m() {
        return beizhu_m;
    }

    public void setBeizhu_m(int beizhu_m) {
        this.beizhu_m = beizhu_m;
    }

    public boolean isHZ() {
        return isHZ;
    }

    public void setHZ(boolean isHZ) {
        this.isHZ = isHZ;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", dq=" + dq +
                ", high=" + high +
                ", low=" + low +
                ", zde=" + zde +
                ", zdf=" + zdf +
                ", zf=" + zf +
                ", kp=" + kp +
                ", zrsp=" + zrsp +
                ", beizhu=" + beizhu +
                ", beizhu_w=" + beizhu_w +
                ", beizhu_m=" + beizhu_m +
                ", risePrice=" + risePrice +
                ", fallPrice=" + fallPrice +
                ", riseIncrease=" + riseIncrease +
                ", is_sf=" + is_sf +
                ", fallIncrease=" + fallIncrease +
                ", historyWarn=" + historyWarn +
                ", dk=" + dk +
                ", p1=" + p1 +
                ", p2=" + p2 +
                ", p3=" + p3 +
                ", s1=" + s1 +
                ", s2=" + s2 +
                ", s3=" + s3 +
                ", dk_w=" + dk_w +
                ", p1_w=" + p1_w +
                ", p2_w=" + p2_w +
                ", p3_w=" + p3_w +
                ", s1_w=" + s1_w +
                ", s2_w=" + s2_w +
                ", s3_w=" + s3_w +
                ", dk_m=" + dk_m +
                ", p1_m=" + p1_m +
                ", p2_m=" + p2_m +
                ", p3_m=" + p3_m +
                ", s1_m=" + s1_m +
                ", s2_m=" + s2_m +
                ", s3_m=" + s3_m +
                ", jj=" + jj +
                ", ycday='" + ycday + '\'' +
                ", searchText='" + searchText + '\'' +
                ", isDT=" + isDT +
                ", isXC=" + isXC +
                ", isGZ=" + isGZ +
                ", isHZ=" + isHZ +
                ", cl4=" + cl4 +
                ", cl5=" + cl5 +
                ", cl6=" + cl6 +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.code);
        dest.writeString(this.name);
        dest.writeDouble(this.dq);
        dest.writeDouble(this.high);
        dest.writeDouble(this.low);
        dest.writeDouble(this.zde);
        dest.writeDouble(this.zdf);
        dest.writeDouble(this.zf);
        dest.writeDouble(this.kp);
        dest.writeDouble(this.zrsp);
        dest.writeInt(this.beizhu);
        dest.writeInt(this.beizhu_w);
        dest.writeInt(this.beizhu_m);
        dest.writeDouble(this.risePrice);
        dest.writeDouble(this.fallPrice);
        dest.writeDouble(this.riseIncrease);
        dest.writeInt(this.is_sf);
        dest.writeDouble(this.fallIncrease);
        dest.writeInt(this.historyWarn);
        dest.writeDouble(this.dk);
        dest.writeDouble(this.p1);
        dest.writeDouble(this.p2);
        dest.writeDouble(this.p3);
        dest.writeDouble(this.s1);
        dest.writeDouble(this.s2);
        dest.writeDouble(this.s3);
        dest.writeDouble(this.jj);
        dest.writeString(this.ycday);
        dest.writeString(this.searchText);
        dest.writeByte(this.isDT ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isXC ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isGZ ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isHZ ? (byte) 1 : (byte) 0);
    }

    protected Stock(Parcel in) {
        this.id = in.readInt();
        this.code = in.readString();
        this.name = in.readString();
        this.dq = in.readDouble();
        this.high = in.readDouble();
        this.low = in.readDouble();
        this.zde = in.readDouble();
        this.zdf = in.readDouble();
        this.zf = in.readDouble();
        this.kp = in.readDouble();
        this.zrsp = in.readDouble();
        this.beizhu = in.readInt();
        this.beizhu_w = in.readInt();
        this.beizhu_m = in.readInt();
        this.risePrice = in.readDouble();
        this.fallPrice = in.readDouble();
        this.riseIncrease = in.readDouble();
        this.is_sf = in.readInt();
        this.fallIncrease = in.readDouble();
        this.historyWarn = in.readInt();
        this.dk = in.readDouble();
        this.p1 = in.readDouble();
        this.p2 = in.readDouble();
        this.p3 = in.readDouble();
        this.s1 = in.readDouble();
        this.s2 = in.readDouble();
        this.s3 = in.readDouble();
        this.jj = in.readDouble();
        this.ycday = in.readString();
        this.searchText = in.readString();
        this.isDT = in.readByte() != 0;
        this.isXC = in.readByte() != 0;
        this.isGZ = in.readByte() != 0;
        this.isHZ = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Stock> CREATOR = new Parcelable.Creator<Stock>() {
        @Override
        public Stock createFromParcel(Parcel source) {
            return new Stock(source);
        }

        @Override
        public Stock[] newArray(int size) {
            return new Stock[size];
        }
    };

    @Override
    public Object clone() {
        Object object = null;
        try {
            object = super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return object;
    }
}
