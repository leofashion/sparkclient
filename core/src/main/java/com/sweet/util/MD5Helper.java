package com.sweet.util;

import java.security.*;

/**
 *
 * @author chenhy
 *
 */
public class MD5Helper {
	private static MessageDigest md5Factory = null;
	static {
		try {
			if (md5Factory == null) {
				md5Factory = MessageDigest.getInstance("MD5");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取字符串的md5处理的hash值
	 * 
	 * @param credential
	 * @return
	 * @throws Exception
	 */
	public static String getMD5String(String credential) throws Exception {
		md5Factory.reset();
		md5Factory.update(credential.getBytes());
		byte[] bytes = md5Factory.digest();
		StringBuffer sb = new StringBuffer(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			int value = (int) (bytes[i] >> 4);
			sb.append(convertDigit(value));
			value = (int) (bytes[i] & 0x0f);
			sb.append(convertDigit(value));
		}
		return sb.toString();
	}

	private static char convertDigit(int value) {
		value &= 0x0f;
		if (value >= 10)
			return ((char) (value - 10 + 'a'));
		else
			return ((char) (value + '0'));
	}

}