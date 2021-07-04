package com.sweet.util;

/**
 * liuh 2014-6-6下午3:06:22
 */
public class IAngelUtil {
	/**
	 * 已下形式不增加字体格式
	 * 
	 * @param text
	 * @return
	 */
	public static boolean checkText(String text) {

		if ((text.startsWith("http://") || text.startsWith("ftp://") || text.startsWith("https://")
				|| text.startsWith("www.")) && text.indexOf(".") > 1) {
			return false;
		}
		if (text.startsWith("\\\\")
				|| ((text.indexOf(":/") > 0 || text.indexOf(":\\") > 0) && text.indexOf(".") > 1)) {
			return false;
		}
		if (text.indexOf("<offlinefile ") != -1) {
			return false;
		}
		return true;
	}
}
