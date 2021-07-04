/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sweet.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.jivesoftware.Spark;
import org.jivesoftware.spark.util.log.Log;

/**
 *
 * @author chen
 */
public class MsgSendHotkeyManager {

	private static Properties props;

	private MsgSendHotkeyManager() {
	}

	/**
	 * Returns the LocalPreferences for this user.
	 *
	 * @return the LocalPreferences for this user.
	 */
	public static Properties getHotkey() {
		if (props != null) {
			return props;
		}

		if (props == null) {
			// Do Initial Load from FileSystem.
			getSettingsFile();
			props = load();
		}

		return props;
	}

	/**
	 * Persists the settings to the local file system.
	 */
	public static void saveSettings() {

		try {
			props.store(new FileOutputStream(getSettingsFile()), "Hotkey Settings");
		} catch (Exception e) {
			Log.error("Error saving settings.", e);
		}
	}

	/**
	 * Return true if the settings file exists.
	 *
	 * @return true if the settings file exists.('phrase.properties')
	 */
	public static boolean exists() {
		return getSettingsFile().exists();
	}

	/**
	 * Returns the settings file.
	 *
	 * @return the settings file.
	 */
	public static File getSettingsFile() {
		File file = new File(Spark.getSparkUserHome() + "\\hotkey.properties");
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

	public static boolean isEnter() {
		Properties pt = getHotkey();
		String strEnter = pt.getProperty("isEnter", "true");
		return Boolean.valueOf(strEnter);
	}

	public static void setEnter(boolean entered) {
		Properties pt = getHotkey();
		pt.setProperty("isEnter", String.valueOf(entered));
		saveSettings();
	}
}
