package com.sweet.util;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.jivesoftware.MainWindow;
import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.util.log.Log;

/**
 * 下载最新小信使文件 liuh 2014-3-11上午10:42:35
 */
public final class DownLoadFile extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2420872084077202541L;
	final JProgressBar progressBar = new JProgressBar();
	final JPanel jpanel = new JPanel();
	final Button button = new Button(Res.getString("cancel"));
	private Thread threadA;

	public DownLoadFile(int width, int height) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(width, height);
		setVisible(true);
		setIconImage(SparkManager.getApplicationImage().getImage());
		setTitle("下载");
		setResizable(false);
		Color bgColor = new Color(240, 243, 253);
		this.getContentPane().setBackground(bgColor);

		jpanel.setBackground(bgColor);
		jpanel.add(button, BorderLayout.CENTER);
		getContentPane().add(progressBar, BorderLayout.NORTH);
		getContentPane().add(jpanel, BorderLayout.EAST);
		progressBar.setStringPainted(true);

		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				threadA.interrupt();
				setVisible(false);
			}
		});
	}

	public void download(final String serverAddress, final String localFolder) {

		threadA = new Thread(new Runnable() {
			public void run() {
				try {
					URL url = new URL(serverAddress);
					URLConnection urlConn = url.openConnection();
					urlConn.connect();
					InputStream in = urlConn.getInputStream();
					long flen = urlConn.getContentLength();
					String filePath = url.getFile();
					String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
					String localFilePath = localFolder + fileName;
					FileOutputStream out = new FileOutputStream(localFilePath);
					byte[] b = new byte[1024];
					int len;
					long tempLen = 0;
					while (true) {
						threadA.sleep(100);
						while ((len = in.read(b)) != -1) {
							tempLen += len;
							out.write(b, 0, len);
							long p = tempLen * 100 / flen;
							progressBar.setValue(Math.round(p));
						}
						if (tempLen >= flen) {
							out.close();
							in.close();
							threadA.sleep(1000);
							setVisible(false);
							startRun(localFilePath);
							break;
						}
					}
				} catch (Exception e) {
					Log.error(e.getMessage());
				}
			}
		});
		threadA.start();
	}

	/**
	 * 启动安装程序
	 * 
	 * @param localFilePath
	 */
	private void startRun(String localFilePath) {
		try {
			localFilePath = formateDOSstr(localFilePath);
			Runtime.getRuntime().exec("cmd /c " + localFilePath);
			MainWindow.getInstance().shutdown();
		} catch (IOException e) {
			Log.error(e.getMessage());
		}
	}

	private String formateDOSstr(String str) {
		if (str.indexOf("^") != -1) {
			str = str.replaceAll("^", "^^");
		}
		if (str.indexOf("&") != -1) {
			str = str.replaceAll("&", "^&");
		}
		if (str.indexOf("(") != -1) {
			str = str.replaceAll("(", "^(");
		}
		if (str.indexOf(")") != -1) {
			str = str.replaceAll(")", "^)");
		}
		if (str.indexOf(" ") != -1) {
			str = str.replaceAll(" ", "^ ");
		}
		return str;
	}

	// public static void main(String[] args){
	// DownLoadFile dlf = new DownLoadFile(300,100);
	// String sourceURL = "http://localhost:8084/download/SweetIM-Setup.exe";
	// String localFolder = "d:/aaa/";
	// dlf.download(sourceURL, localFolder);
	//
	// }

}
