package com.dashuai.android.treasuremap.util;

import android.content.Context;
import android.text.Editable;

public class TextUtil {
	/**
	 * 判断输入框是否为空或者只有空格
	 * 
	 * @return
	 */
	public static boolean isEmpty(Editable editable) {
		return null == editable || editable.length() <= 0
				|| editable.toString().trim().length() <= 0;
	}

	/**
	 * 判断字符串是否为空或者只有空格
	 * 
	 * @return
	 */
	public static boolean isEmpty(String text) {
		return null == text || text.length() <= 0 || text.trim().length() <= 0;
	}

	public static String getStringById(Context context, int id) {
		return context.getResources().getString(id);
	}
}
