/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sweet.settings;

import java.io.*;
import java.util.ArrayList;
import org.jivesoftware.Spark;
import org.jivesoftware.spark.util.log.Log;

/**
 *
 * @author chen
 */
public class PhraseManager {

	private static ArrayList<String> msgs;

	private PhraseManager() {
	}

	/**
	 * Returns the LocalPreferences for this user.
	 *
	 * @return the LocalPreferences for this user.
	 */
	public static ArrayList<String> getPhrases() {
		if (msgs != null) {
			return msgs;
		}

		if (msgs == null) {
			// Do Initial Load from FileSystem.
			getSettingsFile();
			msgs = load();
		}

		return msgs;
	}

	/**
	 * Persists the settings to the local file system.
	 */
	public static void saveSettings() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(getSettingsFile()));
			for (String msg : msgs) {
				bw.write(msg);
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
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
		File file = new File(Spark.getSparkUserHome() + "\\phrase.properties");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException ex) {
				Log.error(ex);
			}
		}
		return file;
	}

	private static ArrayList<String> load() {
		ArrayList<String> pt = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(getSettingsFile()));
			String msg = br.readLine();
			while (msg != null) {
				pt.add(msg);
				msg = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			Log.error(e);
		}
		if (pt.isEmpty()) {
			pt.add("请到我办公室来!");
			pt.add("请稍等,一会就过去!");
			pt.add("请到会议室开会!");
		}
		return pt;
	}
}
