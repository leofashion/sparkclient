/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sweet.util;

import java.awt.Font;

/**
 *
 * @author chenhy
 */
public class FontUtil {

	private static String defaultFontName = "宋体";
	private static int defaultFontSize = 12;

	public static String getDefaultFontName() {
		return defaultFontName;
	}

	public static void setDefaultFontName(String defaultFontName) {
		FontUtil.defaultFontName = defaultFontName;
	}

	public static int getDefaultFontSize() {
		return defaultFontSize;
	}

	public static void setDefaultFontSize(int defaultFontSize) {
		FontUtil.defaultFontSize = defaultFontSize;
	}

	public static Font getDefaultFont() {
		return new Font(getDefaultFontName(), Font.PLAIN, getDefaultFontSize());
	}

}
