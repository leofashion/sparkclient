package com.sweet.chatFont;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.util.log.Log;

/**
 * liuh 2014-5-28下午4:25:50
 */
public class FontSetting {

	private static Properties props;
	private static FontSetting fontSet;

	private FontSetting() {
		props = getPropInstance();
	}

	public static FontSetting getInstance() {
		if (fontSet != null) {
			return fontSet;
		}
		fontSet = new FontSetting();
		return fontSet;
	}

	private static Properties getPropInstance() {
		if (props != null) {
			return props;
		}
		if (props == null) {
			getSettingsFile();
			props = load();
		}

		return props;
	}

	private static File getSettingsFile() {
		File file = new File(SparkManager.getUserDirectory() + "\\fontsetting.properties");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException ex) {
				Log.error(ex);
			}
		}
		return file;
	}

	private static Properties load() {
		Properties pt = new Properties();
		try {
			pt.load(new FileInputStream(getSettingsFile()));
		} catch (IOException e) {
			Log.error(e);
		}
		return pt;
	}

	public static void saveSettings() {
		try {
			props.store(new FileOutputStream(getSettingsFile()), "font seetiong");
		} catch (Exception e) {
			Log.error("Error saving settings.", e);
		}
	}

	/**
	 * 字体名称
	 * 
	 * @param name
	 */
	public void setFontName(String name) {
		props.setProperty("fontName", name);
		saveSettings();
	}

	public String getFontName() {
		return props.getProperty("fontName", "宋体");
	}

	/**
	 * 字体大小
	 * 
	 * @param size
	 */
	public void setFontSize(int size) {
		props.setProperty("fontSize", String.valueOf(size));
		saveSettings();
	}

	public int getFontSize() {
		return Integer.valueOf(props.getProperty("fontSize", "14"));
	}

	/**
	 * 字体颜色
	 * 
	 * @param rgb
	 */
	public void setFontColor(String rgb) {
		props.setProperty("fontColor", rgb);
		saveSettings();
	}

	public Color getFontColor() {
		String[] strArr = props.getProperty("fontColor", "0;0;0").split(";");
		return new Color(Integer.valueOf(strArr[0]), Integer.valueOf(strArr[1]),
				Integer.valueOf(strArr[2]));
	}

	public String getFontColorStr() {
		return props.getProperty("fontColor", "0;0;0");
	}

	public void SaveAll(String font, int size, Color color) {
		props.setProperty("fontName", font);
		props.setProperty("fontSize", String.valueOf(size));
		props.setProperty("fontColor",
				color.getRed() + ";" + color.getGreen() + ";" + color.getBlue());
		saveSettings();
	}

}
