package com.dashuai.android.treasuremap.entity;

public class StockPool {
	private String days; // 建仓日期（2016-03-16）
	private String codes; // 股票代码
	private String title; // 股票名称
	private String yqmb; // 预期目标
	private String jgqj; // 价格区间
	private String cw; // 仓位比例
	private String fx; // 目前方向
	private String beizhu; // 备注

	public StockPool() {
		super();
	}

	public StockPool(String days, String codes, String title, String yqmb,
			String jgqj, String cw, String fx, String beizhu) {
		super();
		this.days = days;
		this.codes = codes;
		this.title = title;
		this.yqmb = yqmb;
		this.jgqj = jgqj;
		this.cw = cw;
		this.fx = fx;
		this.beizhu = beizhu;
	}

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
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

	public String getYqmb() {
		return yqmb;
	}

	public void setYqmb(String yqmb) {
		this.yqmb = yqmb;
	}

	public String getJgqj() {
		return jgqj;
	}

	public void setJgqj(String jgqj) {
		this.jgqj = jgqj;
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

	public String getBeizhu() {
		return beizhu;
	}

	public void setBeizhu(String beizhu) {
		this.beizhu = beizhu;
	}

	@Override
	public String toString() {
		return "StockPool [days=" + days + ", codes=" + codes + ", title="
				+ title + ", yqmb=" + yqmb + ", jgqj=" + jgqj + ", cw=" + cw
				+ ", fx=" + fx + ", beizhu=" + beizhu + "]";
	}

}
