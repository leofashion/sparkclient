package com.sweet;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * liuh 2014-2-17下午5:43:03
 */
public class FileTools {

	public static String readFile(File file, long position, int length) {
		byte[] ret = null;
		try {
			if (file == null) {
				return null;
			}
			FileInputStream in = new FileInputStream(file);
			if (position > 0) {
				in.skip(position);
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream(length);
			byte[] b = new byte[length];
			int n;
			if ((n = in.read(b)) != -1) {
				out.write(b, 0, n);
				ret = out.toByteArray();
			} else {
				ret = out.toByteArray();
			}
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytesToHexString(ret);
	}

	public synchronized static File writeFile(String data, File outputFile) {
		BufferedOutputStream stream = null;
		try {
			FileOutputStream fstream = new FileOutputStream(outputFile, true);
			stream = new BufferedOutputStream(fstream);
			stream.write(hexStringToBytes(data));
			stream.close();
			stream = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return outputFile;
	}

	public static byte[] getBytesFromFile(File file) {
		byte[] ret = null;
		try {
			if (file == null) {
				return null;
			}
			FileInputStream in = new FileInputStream(file);
			ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
			byte[] b = new byte[4096];
			int n;
			while ((n = in.read(b)) != -1) {
				out.write(b, 0, n);
			}
			in.close();
			out.close();
			ret = out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * Convert byte[] to hex
	 * 
	 * @param src
	 * @return
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	private static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		// hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	private static byte charToByte(char c) {
		return (byte) "0123456789abcdef".indexOf(c);
	}

	public static double getFileSizeMB(File file) {
		DecimalFormat df = new DecimalFormat("0.00");
		return Double.valueOf(df.format(Double.valueOf((file.length() / 1024)) / 1024));
	}

	public static double getFileSizeMB(long flen) {
		DecimalFormat df = new DecimalFormat("0.00");
		return Double.valueOf(df.format(Double.valueOf((flen / 1024)) / 1024));
	}

}
