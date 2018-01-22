package com.dashuai.android.treasuremap.entity;


public class StockLog {
    public static final String TAB_NAME = "stock_log";// 股票日志
    private int id;
	private String time;
	private String message;
	private String code;
	private String name;
	private long mTime;// 时间戳

	public StockLog() {
		super();
	}

	public StockLog(String time, String message) {
		super();
		this.time = time;
		this.message = message;
	}

    public StockLog(int id, String time, String message, String code, String name, long mTime) {
        this.id = id;
        this.time = time;
        this.message = message;
        this.code = code;
        this.name = name;
        this.mTime = mTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getmTime() {
		return mTime;
	}

	public void setmTime(long mTime) {
		this.mTime = mTime;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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

    @Override
    public String toString() {
        return "StockLog{" +
                "id=" + id +
                ", time='" + time + '\'' +
                ", message='" + message + '\'' +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", mTime=" + mTime +
                '}';
    }
}
