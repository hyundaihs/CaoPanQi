package com.dashuai.android.treasuremap.entity;


public class Record {
    public static final String TAB_NAME = "record";// 模拟器日志
	private int id;
	private String date;
	private int handle;// 买入1，卖出2
	private double price;// 成交价
	private int volume;// 成交量
	private double turnover;// 成交额
	private String reason;// 交易理由
    private String fangzhenId;//仿真ID

	public Record() {
		super();
	}

    public Record(int id, String date, int handle, double price, int volume, double turnover, String reason, String fangzhenId) {
        this.id = id;
        this.date = date;
        this.handle = handle;
        this.price = price;
        this.volume = volume;
        this.turnover = turnover;
        this.reason = reason;
        this.fangzhenId = fangzhenId;
    }

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getHandle() {
		return handle;
	}

	public void setHandle(int handle) {
		this.handle = handle;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public double getTurnover() {
		return turnover;
	}

	public void setTurnover(double turnover) {
		this.turnover = turnover;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

    public String getFangzhenId() {
        return fangzhenId;
    }

    public void setFangzhenId(String fangzhenId) {
        this.fangzhenId = fangzhenId;
    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", handle=" + handle +
                ", price=" + price +
                ", volume=" + volume +
                ", turnover=" + turnover +
                ", reason='" + reason + '\'' +
                ", fangzhenId='" + fangzhenId + '\'' +
                '}';
    }
}
