package com.sweet.util;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * liuh 2014-3-10下午10:49:54
 */
public class HttpClientTools {
	private String serverIP;
	private int serverPort;
	private HttpClient client;

	public String getServerIP() {
		return serverIP;
	}

	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public HttpClient getClient() {
		return client;
	}

	public void setClient(HttpClient client) {
		this.client = client;
	}

	public HttpClientTools(String serverIP, int serverPort) {
		this.serverIP = serverIP;
		this.serverPort = serverPort;
		client = new HttpClient();
		client.getHostConfiguration().setHost(serverIP, serverPort, "http");
	}

	/**
	 * 首次发送请求
	 * 
	 * @param url
	 * @throws HttpException
	 * @throws IOException
	 */
	private void getMethod(String url) throws HttpException, IOException {
		GetMethod get = new GetMethod(url);
		client.executeMethod(get);
		System.out.println("Get: " + get.getStatusLine());
		get.releaseConnection();
	}

	/**
	 * 登录
	 * 
	 * @param userName
	 * @param pwd
	 * @throws HttpException
	 * @throws IOException
	 */
	public void postLogin(String url, String userName, String pwd)
			throws HttpException, IOException {
		getMethod(url);
		PostMethod post = new PostMethod("/ProjManager/j_security_check");
		NameValuePair userid = new NameValuePair("j_username", userName);
		NameValuePair password = new NameValuePair("j_password", pwd);
		post.setRequestBody(new NameValuePair[] { userid, password });
		client.executeMethod(post);
		System.out.println("Post: " + post.getStatusLine());
		post.releaseConnection();
	}

	/**
	 * 获取后台返回值 检测是否有新版本需要更新
	 * 
	 * @param url
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public String postServer(String url) throws HttpException, IOException {
		String ret = "";
		GetMethod redirect = new GetMethod(url);
		client.executeMethod(redirect);
		ret = redirect.getResponseBodyAsString().trim();
		redirect.releaseConnection();
		return ret;
	}

	/*
	 * public static void main(String[] args){ HttpClientTools hct = new HttpClientTools("192.168.123.200", 8080); try { String url =
	 * "/ProjManager/getIMForwardValue.jsp?akey=sign"; hct.postLogin(url,"s170", "s170"); String aa = hct.postServer(url); System.out.println("-----aa----" +
	 * aa); } catch (HttpException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); } }
	 */

}
