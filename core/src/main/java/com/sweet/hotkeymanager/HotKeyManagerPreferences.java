package com.sweet.hotkeymanager;

/**
 * hotkey-setting.xml 配置文件元素封装类 liuh 2014-5-4下午2:02:53
 */
public class HotKeyManagerPreferences {
	private String commingmessage; // 接收消息
	private String openmainwind; // 打开关闭主窗口
	private String loginOA; // 打开信息系统
	private String loginOut; // 退出小信使
	private String sendMail; // 发送邮件
	private String sendPhoneMsg; // 发送手机短信
	private String screenCapture; // 截屏
	private boolean iscommingmessage; // 是否启用“接收消息”快捷键
	private boolean isopenmainwind; // 是否启用“打开关闭主窗口”快捷键
	private boolean isloginOA; // 是否启用“打开信息系统”快捷键
	private boolean isloginOut; // 是否启用“退出小信使”快捷键
	private boolean isendMail; // 是否启用“发送邮件” 快捷键
	private boolean issendPhoneMsg; // 是否启用“发送手机短信”快捷键
	private boolean isscreenCapture; // 是否启用“截屏”快捷键

	public HotKeyManagerPreferences() {
		commingmessage = "Ctrl + Alt + Z";
		openmainwind = "Ctrl + Alt + W";
		loginOA = "Ctrl + Alt + S";
		loginOut = "Ctrl + Alt + X";
		sendMail = "Ctrl + Alt + M";
		sendPhoneMsg = "Ctrl + Alt + P";
		screenCapture = "Ctrl + Alt + A";
		iscommingmessage = true;
		isopenmainwind = true;
		isloginOA = true;
		isloginOut = true;
		isendMail = true;
		issendPhoneMsg = true;
		isscreenCapture = true;
	}

	public String getCommingmessage() {
		return commingmessage;
	}

	public void setCommingmessage(String commingmessage) {
		this.commingmessage = commingmessage;
	}

	public String getOpenmainwind() {
		return openmainwind;
	}

	public void setOpenmainwind(String openmainwind) {
		this.openmainwind = openmainwind;
	}

	public String getLoginOA() {
		return loginOA;
	}

	public void setLoginOA(String loginOA) {
		this.loginOA = loginOA;
	}

	public String getLoginOut() {
		return loginOut;
	}

	public void setLoginOut(String loginOut) {
		this.loginOut = loginOut;
	}

	public String getSendMail() {
		return sendMail;
	}

	public void setSendMail(String sendMail) {
		this.sendMail = sendMail;
	}

	public String getSendPhoneMsg() {
		return sendPhoneMsg;
	}

	public void setSendPhoneMsg(String sendPhoneMsg) {
		this.sendPhoneMsg = sendPhoneMsg;
	}

	public String getScreenCapture() {
		return screenCapture;
	}

	public void setScreenCapture(String screenCapture) {
		this.screenCapture = screenCapture;
	}

	public boolean getIscommingmessage() {
		return iscommingmessage;
	}

	public void setIscommingmessage(boolean iscommingmessage) {
		this.iscommingmessage = iscommingmessage;
	}

	public boolean getIsopenmainwind() {
		return isopenmainwind;
	}

	public void setIsopenmainwind(boolean isopenmainwind) {
		this.isopenmainwind = isopenmainwind;
	}

	public boolean getIsloginOA() {
		return isloginOA;
	}

	public void setIsloginOA(boolean isloginOA) {
		this.isloginOA = isloginOA;
	}

	public boolean getIsloginOut() {
		return isloginOut;
	}

	public void setIsloginOut(boolean isloginOut) {
		this.isloginOut = isloginOut;
	}

	public boolean getIsendMail() {
		return isendMail;
	}

	public void setIsendMail(boolean isendMail) {
		this.isendMail = isendMail;
	}

	public boolean getIssendPhoneMsg() {
		return issendPhoneMsg;
	}

	public void setIssendPhoneMsg(boolean issendPhoneMsg) {
		this.issendPhoneMsg = issendPhoneMsg;
	}

	public boolean getIsscreenCapture() {
		return isscreenCapture;
	}

	public void setIsscreenCapture(boolean isscreenCapture) {
		this.isscreenCapture = isscreenCapture;
	}

}
