package com.sweet.hotkeymanager;

import static java.awt.event.KeyEvent.VK_0;
import static java.awt.event.KeyEvent.VK_9;
import static java.awt.event.KeyEvent.VK_A;
import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_F1;
import static java.awt.event.KeyEvent.VK_F12;
import static java.awt.event.KeyEvent.VK_PAGE_UP;
import static java.awt.event.KeyEvent.VK_Z;
import static java.awt.event.KeyEvent.getKeyText;

import java.awt.Event;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import org.jivesoftware.Spark;
import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.ConfirmDialog;
import org.jivesoftware.spark.util.BrowserLauncher;
import org.jivesoftware.spark.util.log.Log;

import com.melloware.jintellitype.JIntellitype;
import com.sweet.attendcode.IMAttendCode;
import com.sweet.forward.IMForward;
import com.sweet.util.HttpClientTools;

/**
 * 快捷键设置工具类 liuh 2014-5-7下午6:16:50
 */
public class HotKeyUtil {
	public static HashMap<Integer, String> keyMap = new HashMap<Integer, String>();
	public static final String SEND_MAIL = "sendMail";// 发送邮件
	public static final String DIARY = "diary";// 日志
	public static final String MYPRO = "mypro";// 我的项目
	public static final String SEND_PHONE_MSG = "sendPhoneMsg";// 发送手机短信
	public static final String BOOK_LIST = "booklist";

	public static String getPressKeyStr(KeyEvent e) {
		StringBuilder sb = new StringBuilder();
		if (e.isControlDown()) {
			sb.append("Ctrl + ");
		}
		if (e.isAltDown()) {
			sb.append("Alt + ");
		}
		if (e.isShiftDown()) {
			sb.append("Shift + ");
		}
		return sb.toString();
	}

	/**
	 * 退出小信使
	 */
	public static void loginOut() {
		try {
			IMForward imforward = new IMForward();
			if (imforward.getSignVal().equalsIgnoreCase("true")) {
				final ConfirmDialog confirm = new ConfirmDialog();
				confirm.showConfirmDialog(SparkManager.getMainWindow(), "信息提示",
						"您将退出小信使，是否签退？<br>点击“是”退出小信使并签退，点击“否”仅退出小信使！", Res.getString("yes"),
						Res.getString("no"), null);
				confirm.setConfirmListener(new ConfirmDialog.ConfirmListener() {
					public void yesOption() {
						try {
							IMAttendCode imac2 = new IMAttendCode();
							HttpClientTools hct2 = imac2.getHct();
							String attendcode2 = imac2.getMenuText();
							if (attendcode2.indexOf(";") != -1) {
								attendcode2 = attendcode2.substring(0, attendcode2.indexOf(";"));
							}
							hct2.postLogin(
									"/ProjManager/SIMAttendCode.jsp?userName=" + imac2.getUserName()
											+ "&attendCode=" + attendcode2,
									imac2.getUserName(), hct2.getServerPort() + "");
							hct2.postServer("/ProjManager/SIMAttendCode.jsp?userName="
									+ imac2.getUserName() + "&attendCode=" + attendcode2);
							SparkManager.getMainWindow().shutdown();
						} catch (Exception e) {
							Log.error(e);
						}
					}

					public void noOption() {
						SparkManager.getMainWindow().shutdown();
					}
				});
			} else {
				SparkManager.getMainWindow().shutdown();
			}
		} catch (Exception e) {
			Log.error(e);
			SparkManager.getMainWindow().shutdown();
		}
	}

	/**
	 * 登陆办公系统
	 */
	public static void loginOA() {
		try {
			BrowserLauncher.openURL(Spark.getOAServerURL());
		} catch (Exception e) {
			Log.error("hot loginoa failed" + e);
		}
	}

	/**
	 * 发送邮件
	 */
	public static void sendMail() {
		IMForward imforward = new IMForward();
		if (!imforward.getMailVal().equalsIgnoreCase("false")) {
			try {
				openURL(SEND_MAIL);
			} catch (Exception e) {
				Log.error(e);
			}
		}
	}

	/**
	 * 发送手机短信
	 */
	public static void sendPhoneMsg() {
		IMForward imforward = new IMForward();
		if (!imforward.getPhoneVal().equalsIgnoreCase("false")) {
			try {
				openURL(SEND_PHONE_MSG);
			} catch (Exception e) {
				Log.error(e);
			}
		}
	}

	/**
	 * 跳转到指定页面
	 *
	 * @param type
	 * @throws Exception
	 */
	public static void openURL(String type) throws Exception {
		BrowserLauncher.openURL(Spark.getOAServerURL() + "&type=" + type);
	}

	/**
	 * 清空注册快捷键
	 */

	public static void clearRegHotKey() {
		JIntellitype.getInstance().cleanUp();
	}

	static {
		for (int i = VK_A; i <= VK_Z; i++) {
			keyMap.put(i, getKeyText(i));
		}
		for (int i = VK_0; i <= VK_9; i++) {
			keyMap.put(i, getKeyText(i));
		}
		for (int i = VK_F1; i <= VK_F12; i++) {
			keyMap.put(i, getKeyText(i));
		}
		for (int i = VK_PAGE_UP; i <= VK_DOWN; i++) {// 上下左右，home\end\pgup\pgdn
			keyMap.put(i, getKeyText(i));
		}
		/*
		 * keyMap.put(VK_BACK_SLASH, "\\"); keyMap.put(VK_CLOSE_BRACKET, "]"); keyMap.put(VK_COLON, ":"); keyMap.put(VK_COMMA, ","); keyMap.put(VK_EQUALS, "=");
		 * keyMap.put(VK_MINUS, "-"); keyMap.put(VK_OPEN_BRACKET, "["); keyMap.put(VK_PERIOD, "."); keyMap.put(VK_SEMICOLON, ";"); keyMap.put(VK_SLASH, "/");
		 * keyMap.put(VK_QUOTE, "'");
		 */
	}

	/**
	 * 获取按键 code
	 *
	 * @param keyText
	 * @return
	 */
	public static int getKode(String keyText) {
		if (keyText.equalsIgnoreCase("shift")) {
			return Event.SHIFT_MASK;
		}
		if (keyText.equalsIgnoreCase("alt")) {
			return Event.ALT_MASK;
		}
		if (keyText.equalsIgnoreCase("ctrl")) {
			return Event.CTRL_MASK;
		}
		return -1;
	}

}
