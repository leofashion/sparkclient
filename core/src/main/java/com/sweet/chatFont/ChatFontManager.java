package com.sweet.chatFont;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ChatContainer;
import org.jivesoftware.spark.ui.ChatInputEditor;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomNotFoundException;
import org.jivesoftware.spark.util.log.Log;

/**
 * liuh 2014-5-27下午12:32:54
 */
public class ChatFontManager extends JPanel implements ItemListener, ActionListener {
	private static final long serialVersionUID = -1131475313703188425L;

	private JComboBox jFontBox;
	private JComboBox jSizeBox;
	private MyJbutton jButton;
	private Color color;
	private String font = "宋体";
	private int size = 14;
	private static final Integer SIZE_ARR[] = { 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22 };
	private static ChatFontManager chatFontManager;
	private FontSetting fontSet;

	private ChatFontManager() {
		setLayout(new FlowLayout());
		String[] fontArr = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getAvailableFontFamilyNames();
		Arrays.sort(fontArr);
		Collections.reverse(Arrays.asList(fontArr));
		jFontBox = new JComboBox(Arrays.copyOfRange(fontArr, 0, 20));
		jSizeBox = new JComboBox(SIZE_ARR);
		jButton = new MyJbutton(SparkRes.getImageIcon(SparkRes.PICK_COLOR_BTN));// 签到、签退

		add(jFontBox);
		add(jSizeBox);
		add(jButton);

		fontSet = FontSetting.getInstance();
		jFontBox.setSelectedItem(fontSet.getFontName());
		jSizeBox.setSelectedItem(fontSet.getFontSize());

		jFontBox.addItemListener(this);
		jSizeBox.addItemListener(this);
		jButton.addActionListener(this);

	}

	public static ChatFontManager getInstance() {
		if (chatFontManager == null)
			chatFontManager = new ChatFontManager();
		return chatFontManager;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		font = (String) jFontBox.getSelectedItem();
		size = (Integer) jSizeBox.getSelectedItem();
		formateText(font, size, color);
		fontSet.SaveAll(font, size, color);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Color color = JColorChooser.showDialog(getActiveChatRoom(), "选择颜色", Color.BLACK);
		this.color = color;
		formateText(font, size, color);
		fontSet.SaveAll(font, size, color);
	}

	public void formateText(String fontName, int size, Color color, ChatRoom chatRoom) {
		this.color = color;
		if (chatRoom == null) {
			chatRoom = getActiveChatRoom();
		}
		if (chatRoom != null) {
			ChatInputEditor editor = chatRoom.getChatInputEditor();
			StyledDocument doc = editor.getStyledDocument();
			Style s = doc.addStyle("style1", null);
			StyleConstants.setFontSize(s, size);
			StyleConstants.setFontFamily(s, fontName);
			doc.setCharacterAttributes(0, doc.getLength(), doc.getStyle("style1"), false);
			//TODO  有修改
            editor.setFont(new Font(fontName, Font.PLAIN, size));
//			editor.setFont(fontName);
//            editor.setFontSize(size);
			editor.setForeground(color);

//			editor.setFontFamily(fontName);
//			editor.setFSize(size);
//			editor.setFontColor(color.getRed() + ";" + color.getGreen() + ";" + color.getBlue());
		}
	}

	public void formateText(String fontName, int size, Color color) {
		formateText(fontName, size, color, null);
	}

	/**
	 * 获取当前chatroom
	 *
	 * @return
	 */
	private static ChatRoom getActiveChatRoom() {
		ChatManager chatManager = SparkManager.getChatManager();
		ChatContainer chatContainer = chatManager.getChatContainer();
		if (chatContainer.getTabCount() > 0) {
			try {
				return chatContainer.getActiveChatRoom();
			} catch (ChatRoomNotFoundException e) {
				Log.error(e);
			}
		}
		return null;
	}

	private class MyJbutton extends JButton {
		private static final long serialVersionUID = 1L;

		public MyJbutton(ImageIcon imcon) {
			super(imcon);
			setMargin(new Insets(0, 0, 0, 0));// 下左右上
			setIconTextGap(0);// 将标签中显示的文本和图标之间的间隔量设置为0
			setBorderPainted(false);// 不打印边框
			setText(null);// 除去按钮的默认名称
			setFocusPainted(false);// 除去焦点的框
			setContentAreaFilled(false);// 除去默认的背景填充
			setToolTipText(Res.getString("tip.color"));

			addMouseListener(new MouseAdapter() {
				@Override
                public void mouseEntered(MouseEvent e) {
					setCursor(new Cursor(Cursor.HAND_CURSOR));
					setBorderPainted(true);
				}

				@Override
                public void mouseExited(MouseEvent e) {
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					setBorderPainted(false);
				}
			});
		}

	}

}
