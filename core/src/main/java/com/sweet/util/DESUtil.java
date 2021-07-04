package com.sweet.util;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class DESUtil {

	private static String secretKey = "ugfpV1dMC5jyJtqwVAfTpHkxqJ0+E0ae";

	private static Cipher ecipher;
	private static Cipher dcipher;

	static {
		try {
			KeyGenerator _generator = KeyGenerator.getInstance("DES");
			_generator.init(new SecureRandom(secretKey.getBytes()));
			SecretKey key = _generator.generateKey();
			_generator = null;

			ecipher = Cipher.getInstance("DES");
			dcipher = Cipher.getInstance("DES");
			ecipher.init(Cipher.ENCRYPT_MODE, key);
			dcipher.init(Cipher.DECRYPT_MODE, key);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String encryptStr(String strMing) {
		byte[] byteMi = null;
		byte[] byteMing = null;
		String strMi = "";
		BASE64Encoder base64en = new BASE64Encoder();
		try {
			byteMing = strMing.getBytes("UTF8");
			byteMi = this.encryptByte(byteMing);
			strMi = base64en.encode(byteMi);
			strMi = strMi.replaceAll("[+]", "[").replaceAll("[/]", "]");
		} catch (Exception e) {
			throw new RuntimeException("加密字符串错误:" + e);
		} finally {
			base64en = null;
			byteMing = null;
			byteMi = null;
		}
		return strMi;
	}

	public String decryptStr(String strMi) {
		BASE64Decoder base64De = new BASE64Decoder();
		byte[] byteMing = null;
		byte[] byteMi = null;
		String strMing = "";
		try {
			strMi = strMi.replaceAll("[\\[]", "+").replaceAll("[\\]]", "/");
			byteMi = base64De.decodeBuffer(strMi);
			byteMing = this.decryptByte(byteMi);
			strMing = new String(byteMing, "UTF8");
		} catch (Exception e) {
			throw new RuntimeException("解密字符串错误:" + e);
		} finally {
			base64De = null;
			byteMing = null;
			byteMi = null;
		}
		return strMing;
	}

	private byte[] encryptByte(byte[] byteS) {
		byte[] byteFina = null;
		try {
			byteFina = ecipher.doFinal(byteS);
		} catch (Exception e) {
			throw new RuntimeException("加密字节错误:" + e);
		}
		return byteFina;
	}

	private byte[] decryptByte(byte[] byteD) {
		byte[] byteFina = null;
		try {
			byteFina = dcipher.doFinal(byteD);
		} catch (Exception e) {
			throw new RuntimeException("解密字节错误:" + e);
		}
		return byteFina;
	}

	public static void main(String[] args) throws Exception {
		DESUtil des = new DESUtil();
		// String str1 = "admin:admin";
		String str1 = "xialw:ttt";
		String str2 = des.encryptStr(str1);
		String deStr = des.decryptStr(str2);
		System.out.println(" 加密前： " + str1);
		System.out.println(" 加密后： " + str2);
		System.out.println(" 解密后： " + deStr);
	}
}