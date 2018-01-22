package com.dashuai.android.treasuremap.entity;


public class Capital {
	private int id;

	private double initialCap;// 初始资金
	private double asset;// 当前资产
	private double balance;// 余额
	private double accountPl;// 账户盈亏
	private double plscale;// 盈亏比例
	private int stockNum;// 持股数量

	public Capital() {
		super();
	}

	public Capital(int id, double initialCap, double asset, double balance,
			double accountPl, double plscale) {
		super();
		this.id = id;
		this.initialCap = initialCap;
		this.asset = asset;
		this.balance = balance;
		this.accountPl = accountPl;
		this.plscale = plscale;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStockNum() {
		return stockNum;
	}

	public void setStockNum(int stockNum) {
		this.stockNum = stockNum;
	}

	public double getInitialCap() {
		return initialCap;
	}

	public void setInitialCap(double initialCap) {
		this.initialCap = initialCap;
	}

	public double getAsset() {
		return asset;
	}

	public void setAsset(double asset) {
		this.asset = asset;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public double getAccountPl() {
		return accountPl;
	}

	public void setAccountPl(double accountPl) {
		this.accountPl = accountPl;
	}

	public double getPlscale() {
		return plscale;
	}

	public void setPlscale(double plscale) {
		this.plscale = plscale;
	}

	@Override
	public String toString() {
		return "Capital [id=" + id + ", initialCap=" + initialCap + ", asset="
				+ asset + ", balance=" + balance + ", accountPl=" + accountPl
				+ ", plscale=" + plscale + ", stockNum=" + stockNum + "]";
	}

}
