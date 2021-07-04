package com.sweet.hotkeymanager;

import java.awt.Frame;

import org.jivesoftware.MainWindow;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ChatContainer;
import org.jivesoftware.spark.ui.ChatFrame;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomButton;
import org.jivesoftware.spark.ui.ChatRoomNotFoundException;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.spark.util.log.Log;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;

/**
 * liuh 2014-5-4下午2:00:56
 */
public class HotKeyManagerPlugin implements Plugin, HotkeyListener {
	HotKeyManagerPreference hotKeyManagerPreference;

	@Override
	public void initialize() {
		hotKeyManagerPreference = new HotKeyManagerPreference();
		SparkManager.getPreferenceManager().addPreference(hotKeyManagerPreference);
		// Load hotkey preferences.
		final Runnable hotKeyLoader = new Runnable() {
			@Override
            public void run() {
				hotKeyManagerPreference.loadFromFile();
			}
		};

		TaskEngine.getInstance().submit(hotKeyLoader);
		try {
			JIntellitype.getInstance().addHotKeyListener(this);
		} catch (Exception e) {
			Log.error(e);
		}
	}

	@Override
	public void shutdown() {

	}

	@Override
	public boolean canShutDown() {
		return false;
	}

	@Override
	public void uninstall() {

	}

	@Override
	public void onHotKey(int id) {
		if (id == 1) {// 接收消息
			ChatManager chatManager = SparkManager.getChatManager();
			ChatContainer chatContainer = chatManager.getChatContainer();
			if (chatContainer.getTabCount() > 0) {
				ChatFrame chatFrame = chatManager.getChatContainer().getChatFrame();
				chatFrame.bringFrameIntoFocus();
			}
		} else if (id == 2) {// 打开关闭窗口
			MainWindow mw = SparkManager.getMainWindow();
			if (mw.isMinimumSizeSet() || !mw.isVisible()) {
				mw.setState(Frame.NORMAL);
				mw.setVisible(true);
				mw.toFront();
			} else {
				mw.setVisible(false);
			}
		} else if (id == 3) {// 打开信息系统
			HotKeyUtil.loginOA();
		} else if (id == 4) {// 退出小信使
			HotKeyUtil.loginOut();
		} else if (id == 5) {// 发送邮件
			HotKeyUtil.sendMail();
		} else if (id == 6) {// 发送手机短信
			HotKeyUtil.sendPhoneMsg();
		} else if (id == 7) {// 截屏
			ChatManager chatManager = SparkManager.getChatManager();
			ChatContainer chatContainer = chatManager.getChatContainer();
			if (chatContainer.getTabCount() > 0) {
				ChatRoom chatRoom;
				try {
					chatRoom = chatContainer.getActiveChatRoom();
					if (chatRoom != null) {
                        //TODO 修改
						ChatRoomButton btn = chatRoom.getToolBar().getScreenshotButton();
						SparkManager.getTransferManager().sendScreenshot(btn, chatRoom);
					}
				} catch (ChatRoomNotFoundException e) {
					Log.error(e);
				}
			}
		}
	}
}
