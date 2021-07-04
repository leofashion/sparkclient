package com.sweet.hotkeymanager;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.preference.Preference;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;

import com.melloware.jintellitype.JIntellitype;
import com.sweet.forward.IMForward;
import com.thoughtworks.xstream.XStream;

/**
 * 快捷键设置配置界面; 将配置信息写入本地文件 liuh 2014-5-4下午2:00:40
 */
public class HotKeyManagerPreference implements Preference {

	private XStream xstream;
	private HotKeyManagerPreferences preferences;
	private HotKeyManagerPanel hotkeyPanel;
	public static String NAMESPACE = "Hotkeys";

	public HotKeyManagerPreference() {
	}

	@Override
	public String getTitle() {
		return Res.getString("title.hotkey.preferences");
	}

	@Override
	public Icon getIcon() {
//		return SparkRes.getImageIcon(SparkRes.HOTKEY_PREFERENCES_IMAGE);
		return SparkRes.getImageIcon(SparkRes.PREFERENCES_IMAGE);
	}

	@Override
	public String getTooltip() {
		return Res.getString("title.hotkey");
	}

	@Override
	public String getListName() {
		return Res.getString("title.hotkey");
	}

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public JComponent getGUI() {
		if (hotkeyPanel == null) {
			try {
				EventQueue.invokeAndWait(new Runnable() {
					public void run() {
						hotkeyPanel = new HotKeyManagerPanel();
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return hotkeyPanel;
	}

	public void loadFromFile() {

		if (preferences != null) {
			return;
		}
		initPreferences();
		try {
			initJIntellitype();
		} catch (Exception e) {
			Log.error("hot key regist faild2!!");
		}
	}

	@Override
	public void load() {
		if (hotkeyPanel == null) {
			hotkeyPanel = new HotKeyManagerPanel();
		}
		SwingWorker worker = new SwingWorker() {
			@Override
            public Object construct() {
				loadFromFile();
				return preferences;
			}

			@Override
            public void finished() {
				// Set default settings
				hotkeyPanel.setCommingmessageBox(preferences.getIscommingmessage());
				hotkeyPanel.setCommingmessageText(preferences.getCommingmessage());
				hotkeyPanel.getCommingmessageJTextField()
						.setEnabled(preferences.getIscommingmessage());
				hotkeyPanel.setTextBgcolor(hotkeyPanel.getCommingmessageJTextField(),
						preferences.getIscommingmessage());

				hotkeyPanel.setOpenmainwindBox(preferences.getIsopenmainwind());
				hotkeyPanel.setOpenmainwindText(preferences.getOpenmainwind());
				hotkeyPanel.getOpenmainwindJTextField().setEnabled(preferences.getIsopenmainwind());
				hotkeyPanel.setTextBgcolor(hotkeyPanel.getOpenmainwindJTextField(),
						preferences.getIsopenmainwind());

				hotkeyPanel.setLoginOABox(preferences.getIsloginOA());
				hotkeyPanel.setLoginOAText(preferences.getLoginOA());
				hotkeyPanel.getLoginOAJTextField().setEnabled(preferences.getIsloginOA());
				hotkeyPanel.setTextBgcolor(hotkeyPanel.getLoginOAJTextField(),
						preferences.getIsloginOA());

				hotkeyPanel.setLoginOutBox(preferences.getIsloginOut());
				hotkeyPanel.setLoginOutText(preferences.getLoginOut());
				hotkeyPanel.getLoginOutJTextField().setEnabled(preferences.getIsloginOut());
				hotkeyPanel.setTextBgcolor(hotkeyPanel.getLoginOutJTextField(),
						preferences.getIsloginOut());

				hotkeyPanel.setSendMailBox(preferences.getIsendMail());
				hotkeyPanel.setSendMailText(preferences.getSendMail());
				hotkeyPanel.getSendMailJTextField().setEnabled(preferences.getIsendMail());
				hotkeyPanel.setTextBgcolor(hotkeyPanel.getSendMailJTextField(),
						preferences.getIsendMail());

				hotkeyPanel.setSendPhoneMsgBox(preferences.getIssendPhoneMsg());
				hotkeyPanel.setSendPhoneMsgText(preferences.getSendPhoneMsg());
				hotkeyPanel.getSendPhoneMsgJTextField().setEnabled(preferences.getIssendPhoneMsg());
				hotkeyPanel.setTextBgcolor(hotkeyPanel.getSendPhoneMsgJTextField(),
						preferences.getIssendPhoneMsg());

				hotkeyPanel.setScreenCaptureBox(preferences.getIsscreenCapture());
				hotkeyPanel.setScreenCaptureText(preferences.getScreenCapture());
				hotkeyPanel.getScreenCaptureJTextField()
						.setEnabled(preferences.getIsscreenCapture());
				hotkeyPanel.setTextBgcolor(hotkeyPanel.getScreenCaptureJTextField(),
						preferences.getIsscreenCapture());
				initJtextBackgroundColor();
				initSpecialCheckBox();
			}

		};
		worker.start();
	}

	/**
	 * 没配置 手机短信、邮件..checkbox设成只读
	 */
	private void initSpecialCheckBox() {
		IMForward imforward = new IMForward();
		if (imforward.getMailVal().equalsIgnoreCase("false")) {
			hotkeyPanel.getSendMailBoxObj().setEnabled(false);
			hotkeyPanel.getSendMailJTextField().setEnabled(false);
			hotkeyPanel.setTextBgcolor(hotkeyPanel.getSendMailJTextField(), false);
		} else {
			hotkeyPanel.getSendMailBoxObj().setEnabled(true);
		}
		if (imforward.getPhoneVal().equalsIgnoreCase("false")) {
			hotkeyPanel.getSendPhoneMsgBoxObj().setEnabled(false);
			hotkeyPanel.getSendPhoneMsgJTextField().setEnabled(false);
			hotkeyPanel.setTextBgcolor(hotkeyPanel.getSendPhoneMsgJTextField(), false);
		} else {
			hotkeyPanel.getSendPhoneMsgBoxObj().setEnabled(true);
		}
	}

	private void initJtextBackgroundColor() {// 根据是否启用设置文本框背景色
		if (preferences.getIscommingmessage()) {
			hotkeyPanel.getCommingmessageJTextField().setBackground(getWhite());
		} else {
			hotkeyPanel.getCommingmessageJTextField().setBackground(getGray());
		}
	}

	private Color getWhite() {
		return new Color(255, 255, 255);
	}

	private Color getGray() {
		return new Color(212, 208, 200);
	}

	@Override
	public void commit() {
		preferences.setIscommingmessage(hotkeyPanel.getCommingmessageBox());
		preferences.setCommingmessage(hotkeyPanel.getCommingmessageText());

		preferences.setIsopenmainwind(hotkeyPanel.getOpenmainwindBox());
		preferences.setOpenmainwind(hotkeyPanel.getOpenmainwindText());

		preferences.setIsloginOA(hotkeyPanel.getLoginOABox());
		preferences.setLoginOA(hotkeyPanel.getLoginOAText());

		preferences.setIsloginOut(hotkeyPanel.getLoginOutBox());
		preferences.setLoginOut(hotkeyPanel.getLoginOutText());

		preferences.setIsendMail(hotkeyPanel.getSendMailBox());
		preferences.setSendMail(hotkeyPanel.getSendMailText());

		preferences.setIssendPhoneMsg(hotkeyPanel.getSendPhoneMsgBox());
		preferences.setSendPhoneMsg(hotkeyPanel.getSendPhoneMsgText());

		preferences.setIsscreenCapture(hotkeyPanel.getScreenCaptureBox());
		preferences.setScreenCapture(hotkeyPanel.getScreenCaptureText());

		saveHotKeyFile();
		try {
			initJIntellitype();
		} catch (Exception e) {
			Log.error("hot key regist faild!");
		}
	}

	@Override
	public boolean isDataValid() {
		return true;
	}

	@Override
	public String getErrorMessage() {
		return null;
	}

	@Override
	public Object getData() {
		return null;
	}

	@Override
	public void shutdown() {

	}

	private XStream getXStream() {
		if (xstream == null) {
			xstream = new XStream();
			xstream.alias("hotkeys", HotKeyManagerPreferences.class);
		}
		return xstream;
	}

	private File getHotKeySettingsFile() {
		File file = new File(SparkManager.getUserDirectory(), "hotkeysetting");//
		if (!file.exists()) {
			file.mkdirs();
		}
		return new File(file, "hotkey-setting.xml");
	}

	private void saveHotKeyFile() {
		try {
			FileWriter writer = new FileWriter(getHotKeySettingsFile());
			getXStream().toXML(preferences, writer);
		} catch (Exception e) {
			Log.error("Error saving hotkey settings.", e);
		}
	}

	public HotKeyManagerPreferences getPreferences() {
		initPreferences();
		return preferences;
	}

	private void initPreferences() {
		if (!getHotKeySettingsFile().exists()) {
			preferences = new HotKeyManagerPreferences();
			saveHotKeyFile();
		} else {
			File settingsFile = getHotKeySettingsFile();
			try {
				FileReader reader = new FileReader(settingsFile);
				preferences = (HotKeyManagerPreferences) getXStream().fromXML(reader);
			} catch (Exception e) {
				Log.error("Error loading HotKeyManager Preferences.", e);
				preferences = new HotKeyManagerPreferences();
			}
		}
	}

	private class HotKeyManagerPanel extends JPanel
			implements ActionListener, KeyListener, FocusListener {
		private static final long serialVersionUID = 1L;

		private final JCheckBox commingmessageBox = new JCheckBox(); // 接收消息
		private final JTextField commingmessageText = new MyJTextField(30);

		private final JCheckBox openmainwindBox = new JCheckBox(); // 打开关闭主窗口
		private final JTextField openmainwindText = new MyJTextField(30);

		private final JCheckBox loginOABox = new JCheckBox(); // 打开信息系统
		private final JTextField loginOAText = new MyJTextField(30);

		private final JCheckBox loginOutBox = new JCheckBox(); // 退出小信使
		private final JTextField loginOutText = new MyJTextField(30);

		private final JCheckBox sendMailBox = new JCheckBox(); // 发送邮件
		private final JTextField sendMailText = new MyJTextField(30);

		private final JCheckBox sendPhoneMsgBox = new JCheckBox(); // 发送手机短信
		private final JTextField sendPhoneMsgText = new MyJTextField(30);

		private final JCheckBox screenCaptureBox = new JCheckBox(); // 截屏
		private final JTextField screenCaptureText = new MyJTextField(30);

		public HotKeyManagerPanel() {
			setLayout(new GridBagLayout());
			setBorder(BorderFactory.createTitledBorder(Res.getString("title.hotkey.preferences")));
			ResourceUtils.resButton(commingmessageBox,
					Res.getString("checkbox.hotkey.commingmessage"));
			ResourceUtils.resButton(openmainwindBox, Res.getString("checkbox.hotkey.openmainwind"));
			ResourceUtils.resButton(loginOABox, Res.getString("checkbox.hotkey.loginOA"));
			ResourceUtils.resButton(loginOutBox, Res.getString("checkbox.hotkey.loginOut"));
			ResourceUtils.resButton(sendMailBox, Res.getString("checkbox.hotkey.sendMail"));
			ResourceUtils.resButton(sendPhoneMsgBox, Res.getString("checkbox.hotkey.sendPhoneMsg"));
			ResourceUtils.resButton(screenCaptureBox,
					Res.getString("checkbox.hotkey.screenCapture"));

			add(commingmessageBox,
					new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
							GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
			add(commingmessageText,
					new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST,
							GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

			add(openmainwindBox,
					new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
							GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
			add(openmainwindText,
					new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST,
							GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

			add(loginOABox,
					new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
							GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
			add(loginOAText,
					new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST,
							GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

			add(loginOutBox,
					new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
							GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
			add(loginOutText,
					new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST,
							GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

			add(sendMailBox,
					new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
							GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
			add(sendMailText,
					new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST,
							GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

			add(sendPhoneMsgBox,
					new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
							GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
			add(sendPhoneMsgText,
					new GridBagConstraints(1, 5, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST,
							GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

			add(screenCaptureBox,
					new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
							GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
			add(screenCaptureText,
					new GridBagConstraints(1, 6, 1, 1, 0.0, 1.0, GridBagConstraints.NORTHWEST,
							GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

			initJTextListener();
		}

		private void initJTextListener() {
			/* regist ActionListener */
			commingmessageBox.addActionListener(this);
			openmainwindBox.addActionListener(this);
			loginOABox.addActionListener(this);
			loginOutBox.addActionListener(this);
			sendMailBox.addActionListener(this);
			sendPhoneMsgBox.addActionListener(this);
			screenCaptureBox.addActionListener(this);

			/* regist KeyListener */
			commingmessageText.addKeyListener(this);
			openmainwindText.addKeyListener(this);
			loginOAText.addKeyListener(this);
			loginOutText.addKeyListener(this);
			sendMailText.addKeyListener(this);
			sendPhoneMsgText.addKeyListener(this);
			screenCaptureText.addKeyListener(this);

			/* regist FocusListener */
			commingmessageText.addFocusListener(this);
			openmainwindText.addFocusListener(this);
			loginOAText.addFocusListener(this);
			loginOutText.addFocusListener(this);
			sendMailText.addFocusListener(this);
			sendPhoneMsgText.addFocusListener(this);
			screenCaptureText.addFocusListener(this);
		}

		public boolean getCommingmessageBox() {
			return commingmessageBox.isSelected();
		}

		public void setCommingmessageBox(boolean b) {
			commingmessageBox.setSelected(b);
		}

		public String getCommingmessageText() {
			return commingmessageText.getText();
		}

		public JTextField getCommingmessageJTextField() {
			return commingmessageText;
		}

		public void setCommingmessageText(String t) {
			commingmessageText.setText(t);
		}

		public boolean getOpenmainwindBox() {
			return openmainwindBox.isSelected();
		}

		public void setOpenmainwindBox(boolean b) {
			openmainwindBox.setSelected(b);
		}

		public String getOpenmainwindText() {
			return openmainwindText.getText();
		}

		public JTextField getOpenmainwindJTextField() {
			return openmainwindText;
		}

		public void setOpenmainwindText(String t) {
			openmainwindText.setText(t);
		}

		public boolean getLoginOABox() {
			return loginOABox.isSelected();
		}

		public void setLoginOABox(boolean b) {
			loginOABox.setSelected(b);
		}

		public String getLoginOAText() {
			return loginOAText.getText();
		}

		public JTextField getLoginOAJTextField() {
			return loginOAText;
		}

		public void setLoginOAText(String t) {
			loginOAText.setText(t);
		}

		public boolean getLoginOutBox() {
			return loginOutBox.isSelected();
		}

		public void setLoginOutBox(boolean b) {
			loginOutBox.setSelected(b);
		}

		public String getLoginOutText() {
			return loginOutText.getText();
		}

		public JTextField getLoginOutJTextField() {
			return loginOutText;
		}

		public void setLoginOutText(String t) {
			loginOutText.setText(t);
		}

		public boolean getSendMailBox() {
			return sendMailBox.isSelected();
		}

		public JCheckBox getSendMailBoxObj() {
			return sendMailBox;
		}

		public void setSendMailBox(boolean b) {
			sendMailBox.setSelected(b);
		}

		public String getSendMailText() {
			return sendMailText.getText();
		}

		public JTextField getSendMailJTextField() {
			return sendMailText;
		}

		public void setSendMailText(String t) {
			sendMailText.setText(t);
		}

		public boolean getSendPhoneMsgBox() {
			return sendPhoneMsgBox.isSelected();
		}

		public JCheckBox getSendPhoneMsgBoxObj() {
			return sendPhoneMsgBox;
		}

		public void setSendPhoneMsgBox(boolean b) {
			sendPhoneMsgBox.setSelected(b);
		}

		public String getSendPhoneMsgText() {
			return sendPhoneMsgText.getText();
		}

		public JTextField getSendPhoneMsgJTextField() {
			return sendPhoneMsgText;
		}

		public void setSendPhoneMsgText(String t) {
			sendPhoneMsgText.setText(t);
		}

		public boolean getScreenCaptureBox() {
			return screenCaptureBox.isSelected();
		}

		public void setScreenCaptureBox(boolean b) {
			screenCaptureBox.setSelected(b);
		}

		public String getScreenCaptureText() {
			return screenCaptureText.getText();
		}

		public JTextField getScreenCaptureJTextField() {
			return screenCaptureText;
		}

		public void setScreenCaptureText(String t) {
			screenCaptureText.setText(t);
		}

		/**
		 * 设置背景色
		 *
		 * @param textField
		 * @param b
		 */
		public void setTextBgcolor(JTextField textField, boolean b) {
			if (b) {
                textField.setBackground(getWhite());
            } else {
                textField.setBackground(getGray());
            }
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			boolean tmp = false;
			if (e.getSource() == commingmessageBox) {
				tmp = commingmessageBox.isSelected();
				commingmessageText.setEnabled(tmp);
				if (tmp) {
					commingmessageText.setBackground(getWhite());
				} else {
					commingmessageText.setBackground(getGray());
				}
			} else if (e.getSource() == openmainwindBox) {
				tmp = openmainwindBox.isSelected();
				openmainwindText.setEnabled(tmp);
				if (tmp) {
					openmainwindText.setBackground(getWhite());
				} else {
					openmainwindText.setBackground(getGray());
				}
			} else if (e.getSource() == loginOABox) {
				tmp = loginOABox.isSelected();
				loginOAText.setEnabled(tmp);
				if (tmp) {
					loginOAText.setBackground(getWhite());
				} else {
					loginOAText.setBackground(getGray());
				}
			} else if (e.getSource() == loginOutBox) {
				tmp = loginOutBox.isSelected();
				loginOutText.setEnabled(tmp);
				if (tmp) {
					loginOutText.setBackground(getWhite());
				} else {
					loginOutText.setBackground(getGray());
				}
			} else if (e.getSource() == sendMailBox) {
				tmp = sendMailBox.isSelected();
				sendMailText.setEnabled(tmp);
				if (tmp) {
					sendMailText.setBackground(getWhite());
				} else {
					sendMailText.setBackground(getGray());
				}
			} else if (e.getSource() == sendPhoneMsgBox) {
				tmp = sendPhoneMsgBox.isSelected();
				sendPhoneMsgText.setEnabled(tmp);
				if (tmp) {
					sendPhoneMsgText.setBackground(getWhite());
				} else {
					sendPhoneMsgText.setBackground(getGray());
				}
			} else if (e.getSource() == screenCaptureBox) {
				tmp = screenCaptureBox.isSelected();
				screenCaptureText.setEnabled(tmp);
				if (tmp) {
					screenCaptureText.setBackground(getWhite());
				} else {
					screenCaptureText.setBackground(getGray());
				}
			}
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyPressed(KeyEvent e) {
			String strASC = HotKeyUtil.getPressKeyStr(e);
			String keyText = "";// ;
			int keyCode = e.getKeyCode();
			if (HotKeyUtil.keyMap.containsKey(keyCode)) {
				keyText = HotKeyUtil.keyMap.get(keyCode);
			} else if (keyCode != KeyEvent.VK_SHIFT && keyCode != KeyEvent.VK_CONTROL
					&& keyCode != KeyEvent.VK_ALT) {
				strASC = "";
				keyText = Res.getString("hotkey.jtext.nonetext");
			}
			MyJTextField mtext = null;
			if (e.getSource() instanceof MyJTextField) {
				mtext = (MyJTextField) e.getSource();
				mtext.setText("");
				mtext.setText(strASC + keyText);
			}
		}

		/**
		 * 控制必须包含 Ctrl、Shift、Alt三个按键中的任意一个或多个
		 */
		@Override
		public void keyReleased(KeyEvent e) {
			MyJTextField mtext = null;
			if (e.getSource() instanceof MyJTextField) {
				mtext = (MyJTextField) e.getSource();
				String strText = mtext.getText().trim();
				if (strText.indexOf("+") == -1 || strText.split("[+]").length == 1) {
					mtext.setText(Res.getString("hotkey.jtext.nonetext"));// 无
				}
			}
		}

		@Override
		public void focusGained(FocusEvent e) {
			if (e != null && (e.getSource() instanceof JTextField)) {
				JTextField jtf = (JTextField) e.getSource();
				jtf.getCaret().setVisible(true);
				jtf.setCaretPosition(jtf.getText().length());
			}
		}

		@Override
		public void focusLost(FocusEvent e) {

		}

	}

	/**
	 * 注册热键
	 */
	private void initJIntellitype() throws Exception {
		if (preferences != null) {
			if (preferences.getIscommingmessage()) {
				String msg = preferences.getCommingmessage();
				if (msg.indexOf("+") != -1) {
					String[] arr = msg.split("[+]");
					registerKeyCode(1, arr);
				}
			} else {
				clearRegister(1);
			}

			if (preferences.getIsopenmainwind()) {
				String msg = preferences.getOpenmainwind();
				if (msg.indexOf("+") != -1) {
					String[] arr = msg.split("[+]");
					registerKeyCode(2, arr);
				}
			} else {
				clearRegister(2);
			}

			if (preferences.getIsloginOA()) {
				String msg = preferences.getLoginOA();
				if (msg.indexOf("+") != -1) {
					String[] arr = msg.split("[+]");
					registerKeyCode(3, arr);
				}
			} else {
				clearRegister(3);
			}

			if (preferences.getIsloginOut()) {
				String msg = preferences.getLoginOut();
				if (msg.indexOf("+") != -1) {
					String[] arr = msg.split("[+]");
					registerKeyCode(4, arr);
				}
			} else {
				clearRegister(4);
			}

			if (preferences.getIsendMail()) {
				String msg = preferences.getSendMail();
				if (msg.indexOf("+") != -1) {
					String[] arr = msg.split("[+]");
					registerKeyCode(5, arr);
				}
			} else {
				clearRegister(5);
			}

			if (preferences.getIssendPhoneMsg()) {
				String msg = preferences.getSendPhoneMsg();
				if (msg.indexOf("+") != -1) {
					String[] arr = msg.split("[+]");
					registerKeyCode(6, arr);
				}
			} else {
				clearRegister(6);
			}

			if (preferences.getIsscreenCapture()) {
				String msg = preferences.getScreenCapture();
				if (msg.indexOf("+") != -1) {
					String[] arr = msg.split("[+]");
					registerKeyCode(7, arr);
				}
			} else {
				clearRegister(7);
			}
		}
	}

	/**
	 * shift ctrl alt 按键之和
	 *
	 * @param arr
	 * @return
	 */
	private int getModifierCode(String[] arr) throws IndexOutOfBoundsException {
		int num = 0;
		for (int i = 0; i < arr.length - 1; i++) {
			num += HotKeyUtil.getKode(arr[i].trim());
		}
		return num;
	}

	/**
	 * 注册热键
	 *
	 * @param id
	 * @param arr
	 */
	private void registerKeyCode(int id, String[] arr) throws Exception {
		int mcode = getModifierCode(arr);
		int keyCode = arr[arr.length - 1].trim().toCharArray()[0];
		JIntellitype.getInstance().registerSwingHotKey(id, mcode, keyCode);
	}

	/**
	 * 清除注册热键
	 *
	 * @param id
	 */
	private void clearRegister(int id) throws Exception {
		JIntellitype.getInstance().unregisterHotKey(id);
	}

}
