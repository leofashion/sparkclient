package com.sweet.chatFont;

import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomListener;

/**
 * 聊天设置字体插件 liuh 2014-5-27上午9:43:18
 */
public class ChatFontPlugin implements Plugin, ChatRoomListener {

	private ChatManager chatManager;

	@Override
	public void initialize() {

		chatManager = SparkManager.getChatManager();
		addChatRoomListener();
	}

	private void addChatRoomListener() {
		chatManager.addChatRoomListener(this);
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

	/* ChatRoomListener 接口中方法开始 */
	@Override
	public void chatRoomOpened(ChatRoom room) {
		ChatFontManager fontManager = ChatFontManager.getInstance();
		room.addEditorComponent(fontManager);
		FontSetting fontSet = FontSetting.getInstance();
		fontManager.formateText(fontSet.getFontName(), fontSet.getFontSize(),
				fontSet.getFontColor(), room);
	}

	@Override
	public void chatRoomLeft(ChatRoom room) {

	}

	@Override
	public void chatRoomClosed(ChatRoom room) {

	}

	@Override
	public void chatRoomActivated(ChatRoom room) {
		ChatFontManager fontManager = ChatFontManager.getInstance();
		room.addEditorComponent(fontManager);
		FontSetting fontSet = FontSetting.getInstance();
		fontManager.formateText(fontSet.getFontName(), fontSet.getFontSize(),
				fontSet.getFontColor(), room);

	}

	@Override
	public void userHasJoined(ChatRoom room, String userid) {

	}

	@Override
	public void userHasLeft(ChatRoom room, String userid) {

	}
	/* ChatRoomListener 接口中方法结束 */

}
