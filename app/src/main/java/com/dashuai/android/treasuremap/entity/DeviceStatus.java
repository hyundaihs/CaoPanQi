package com.dashuai.android.treasuremap.entity;

public class DeviceStatus {
	private int type;// 类型（1：有效，2：被禁用，3：已过期，4：未注册）
	private int days;// 有效时间（天）

	public DeviceStatus() {
		super();
	}

	public DeviceStatus(int type, int days) {
		super();
		this.type = type;
		this.days = days;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	@Override
	public String toString() {
		return "DeviceStatus [type=" + type + ", days=" + days + "]";
	}

}
