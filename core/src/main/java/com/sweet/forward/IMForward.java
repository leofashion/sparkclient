package com.sweet.forward;

import java.io.IOException;

import org.apache.commons.httpclient.HttpException;
import org.jivesoftware.Spark;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.util.log.Log;

import com.sweet.util.HttpClientTools;

/**
 * 获取信天使 config\IMForward.properties 配置文件中value liuh 2014-4-11下午3:37:03
 */
public class IMForward {
	private static final String KEY_DIARY = "diary";// 上报日志
	private static final String KEY_MAIL = "sendMail";// 发送邮件
	private static final String KEY_SIGN = "sign";// 签到签退
	private static final String KEY_PRO = "mypro";// 我的项目
	private static final String KEY_PHONE = "sendPhoneMsg";// 发送手机短信
	private static final String KEY_BOOKLIST = "booklist";

	private HttpClientTools httpclient = null;
	private String userName;
	private String password;

	public IMForward() {
		String imServerName = Spark.getServerName();
		if (imServerName == null)
			imServerName = "localhost";
		String port = Spark.getSreverPort();
		if (port == null)
			port = "8080";
		this.userName = SparkManager.getSessionManager().getUsername();
		this.password = SparkManager.getSessionManager().getPassword();
		httpclient = new HttpClientTools(imServerName, Integer.valueOf(port));
	}

	/**
	 * 签到签退
	 * 
	 * @return false:不显示签到图标 true:显示签到图标
	 */
	public String getSignVal() {
		return getIMForwardValue(KEY_SIGN);
	}

	/**
	 * 上报日志
	 * 
	 * @return 1、false(不显示图标);2、跳转页面
	 */
	public String getDiaryVal() {
		return getIMForwardValue(KEY_DIARY);
	}

	/**
	 * 发送邮件
	 * 
	 * @return 1、false(不显示图标);2、跳转页面
	 */
	public String getMailVal() {
		return getIMForwardValue(KEY_MAIL);
	}

	/**
	 * 我的项目
	 * 
	 * @return 1、false(不显示图标);2、跳转页面
	 */
	public String getMyProVal() {
		return getIMForwardValue(KEY_PRO);
	}

	/**
	 * 发送手机短信
	 * 
	 * @return 1、false(不显示图标);2、跳转页面
	 */
	public String getPhoneVal() {
		return getIMForwardValue(KEY_PHONE);
	}

	/**
	 * 通讯录
	 * 
	 * @return 1、false(不显示查看通讯录菜单)2、调准页面
	 */
	public String getBookListVal() {
		return getIMForwardValue(KEY_BOOKLIST);
	}

	/**
	 * 获取配置文件值
	 * 
	 * @param key
	 * @return value
	 */
	private String getIMForwardValue(String key) {
		String ret = null;
		try {
			httpclient.postLogin("/ProjManager/getIMForwardValue.jsp?akey=" + key, userName,
					password);
			ret = httpclient.postServer("/ProjManager/getIMForwardValue.jsp?akey=" + key);
			if (ret.indexOf("html") != -1) {// 报错
				ret = "false";
			}
		} catch (HttpException e) {
			Log.error(e);
		} catch (IOException e) {
			Log.error(e);
		}
		return ret;
	}

}
