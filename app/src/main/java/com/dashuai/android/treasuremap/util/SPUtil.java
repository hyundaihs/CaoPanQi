package com.dashuai.android.treasuremap.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SPUtil {
	private SharedPreferences sp;
	private Editor editor;

	public SPUtil(Context context) {
		if (null == sp) {
			sp = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		}
		editor = sp.edit();
		editor.commit();
	}

	/**
	 * 是否是第一次打开
	 * 
	 * @param key
	 * @return
	 */
	public boolean isFirst(String key) {
		boolean flag = sp.getBoolean(key, true);
		if (flag) {
			editor.putBoolean(key, false);
			editor.commit();
		}
		return flag;
	}

	public boolean isVip() {
		boolean flag = sp.getBoolean("isVip", false);
		return flag;
	}

	public void setVip(boolean isVip) {
		editor.putBoolean("isVip", isVip);
		editor.commit();
	}

	public String getStockPoolPassword() {
		return sp.getString("stock_pool_password", null);
	}

	public void setStockPoolPassword(String stockPoolPassword) {
		editor.putString("stock_pool_password", stockPoolPassword);
		editor.commit();
	}

	public void setWarn(String warnNum, boolean isWarned) {
		editor.putBoolean(warnNum, isWarned);
		editor.commit();
	}

	public boolean isWarn(String warnNum) {
		return sp.getBoolean(warnNum, false);
	}

	public String getAuthCode() {
		return sp.getString("authcode", null);
	}

	public void setWarnText(String key, double value) {
		editor.putString(key + "warn", String.valueOf(value));
		editor.commit();
	}

	public double getWarnText(String key) {
		return Double.parseDouble(sp.getString(key + "warn", "0"));
	}

	public void setAllWarn(boolean isWarn) {
		editor.putBoolean("all_warn", isWarn);
		editor.commit();
	}

	public boolean isAllWarn() {
		return sp.getBoolean("all_warn", true);
	}

	public void setLastHongbaoTime(long time) {
		editor.putLong("last_time", time);
		editor.commit();
	}

	public long getLastHongbaoTime() {
		return sp.getLong("last_time", 0) / 1000;
	}

    public void setLastFangzhenTime(long time) {
        editor.putLong("last_fz_time", time);
        editor.commit();
    }

    public long getLastFangzhenTime() {
        return sp.getLong("last_fz_time", 0) / 1000;
    }

	public void setBzWarn(String key, boolean isWarn) {
		editor.putBoolean(key, isWarn);
		editor.commit();
	}

	public boolean isBzWarn(String key) {
		return sp.getBoolean(key, false);
	}

	/**
	 * 是否所有的分类预警都没有开启
	 * 
	 * @return
	 */
	public boolean isNotAllBzWarn() {
		for (int i = 0; i < 6; i++) {
			if (sp.getBoolean("beizhu_" + i, false)) {
				return false;
			}
		}
		return true;
	}

	public void setStock(String code, String name) {
		editor.putString("e_code", code);
		editor.putString("e_name", name);
		editor.commit();
	}

	public String getCode() {
		return sp.getString("e_code", "sz000001");

	}

	public String getName() {
		return sp.getString("e_name", "平安银行");
	}

	public void setInitialCap(int initialCap) {
		editor.putInt("initialCap", initialCap);
		editor.commit();
	}

	public int getInitialCap() {
		return sp.getInt("initialCap", 1000000);
	}

	public boolean getBzListStatus(int key) {
		return sp.getBoolean(String.valueOf(key), true);
	}

	public void setBzListStatus(int key, boolean status) {
		editor.putBoolean(String.valueOf(key), status);
		editor.commit();
	}
}
