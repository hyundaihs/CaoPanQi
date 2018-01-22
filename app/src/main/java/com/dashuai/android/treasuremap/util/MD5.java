/**
 * MD5.java [v 1.0.0]
 * classes : com.android.kkclient.utils.MD5
 * 蔡雨峰 Create at 2013-12-24 下午1:48:11
 */
package com.dashuai.android.treasuremap.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * com.android.kkclient.utils.MD5
 * 
 * @author 蔡雨峰 <br/>
 * 
 *         Create at 2013-12-24 下午1:48:11
 */
public class MD5 {
	public static String getMD5Str(String str) {
		MessageDigest messageDigest = null;

		try {
			messageDigest = MessageDigest.getInstance("MD5");

			messageDigest.reset();

			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			System.out.println("NoSuchAlgorithmException caught!");
			System.exit(-1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		byte[] byteArray = messageDigest.digest();

		StringBuffer md5StrBuff = new StringBuffer();

		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}
		// 16位加密，从第9位到25位
		return md5StrBuff.toString();
	}

	/**
	 * 
	 * @param pass
	 * @return
	 */
	public static String encryption(String pass) {
		return MD5.getMD5Str("nimdaae" + MD5.getMD5Str(pass));
	}
}
