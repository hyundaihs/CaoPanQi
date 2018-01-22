package com.dashuai.android.treasuremap.entity;

import java.util.List;

public class SzData {
	private String sz; // Array（上证数据）
	private String day; // 日期（2016-03-16）
	private String cw; // 仓位
	private String fx; // 方向
	private String y1; // 压力位1
	private String y2; // 压力位2
	private String z1; // 支撑位1
	private String z2; // 支撑位2
	private String beizhu; // 备注
	private List<String> days; // Array(日期列表)

	public SzData() {
		super();
	}

	public SzData(String sz, String day, String cw, String fx, String y1,
			String y2, String z1, String z2, String beizhu, List<String> days) {
		super();
		this.sz = sz;
		this.day = day;
		this.cw = cw;
		this.fx = fx;
		this.y1 = y1;
		this.y2 = y2;
		this.z1 = z1;
		this.z2 = z2;
		this.beizhu = beizhu;
		this.days = days;
	}

	public String getSz() {
		return sz;
	}

	public void setSz(String sz) {
		this.sz = sz;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getCw() {
		return cw;
	}

	public void setCw(String cw) {
		this.cw = cw;
	}

	public String getFx() {
		return fx;
	}

	public void setFx(String fx) {
		this.fx = fx;
	}

	public String getY1() {
		return y1;
	}

	public void setY1(String y1) {
		this.y1 = y1;
	}

	public String getY2() {
		return y2;
	}

	public void setY2(String y2) {
		this.y2 = y2;
	}

	public String getZ1() {
		return z1;
	}

	public void setZ1(String z1) {
		this.z1 = z1;
	}

	public String getZ2() {
		return z2;
	}

	public void setZ2(String z2) {
		this.z2 = z2;
	}

	public String getBeizhu() {
		return beizhu;
	}

	public void setBeizhu(String beizhu) {
		this.beizhu = beizhu;
	}

	public List<String> getDays() {
		return days;
	}

	public void setDays(List<String> days) {
		this.days = days;
	}

	@Override
	public String toString() {
		return "SzData [sz=" + sz + ", day=" + day + ", cw=" + cw + ", fx="
				+ fx + ", y1=" + y1 + ", y2=" + y2 + ", z1=" + z1 + ", z2="
				+ z2 + ", beizhu=" + beizhu + ", days=" + days + "]";
	}

}
