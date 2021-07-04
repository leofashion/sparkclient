package com.sweet.attendcode;

import org.jivesoftware.Spark;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.util.log.Log;

import com.sweet.util.HttpClientTools;

/**
 * liuh 2014-3-26上午11:30:16
 */
public class IMAttendCode {

	private String imServerName;
	private String port;
	private String userName;
	private String passWord;
	HttpClientTools hct;

	public String getImServerName() {
		return imServerName;
	}

	public void setImServerName(String imServerName) {
		this.imServerName = imServerName;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public HttpClientTools getHct() {
		return hct;
	}

	public void setHct(HttpClientTools hct) {
		this.hct = hct;
	}

	public IMAttendCode() {
		imServerName = Spark.getServerName();
		if (imServerName == null)
			imServerName = "localhost";
		port = Spark.getSreverPort();
		if (port == null)
			port = "8080";
		userName = SparkManager.getSessionManager().getUsername();
		passWord = SparkManager.getSessionManager().getPassword();

		hct = new HttpClientTools(imServerName, Integer.valueOf(port));
	}

	/**
	 * 获取菜单显示值
	 * 
	 * @return
	 */
	public String getMenuText() {
		String ret = "";

		try {
			hct.postLogin("/ProjManager/SIMAttendCode.jsp?userName=" + userName, userName,
					passWord);
			String serverRet = hct
					.postServer("/ProjManager/SIMAttendCode.jsp?userName=" + userName);
			if (serverRet.startsWith("AMStart")) {
				ret = serverRet;
			} else if (serverRet.startsWith("AMEnd")) {
				ret = serverRet;
			} else if (serverRet.startsWith("PMStart")) {
				ret = serverRet;
			} else if (serverRet.startsWith("PMEnd")) {
				ret = serverRet;
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return ret;
	}

	// public static void main(String[] args) throws Exception {
	// try {
	// HttpClientTools hct = new HttpClientTools("localhost", 8084);
	// hct.postLogin("/ProjManager/SIMAttendCode.jsp?userName=zhangq",
	// "zhangq", "admin");
	// String serverRet = hct
	// .postServer("/ProjManager/SIMAttendCode.jsp?userName=zhangq");
	// System.out.println(serverRet);
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

}
