package com.sweet;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.spark.SparkManager;

/**
 * file download
 */
public class OffLineFileTransferDown {

	private File file;
	private String localFileName;
	private String serverFileName;
	private String localUrl;
	private String transStatus;
	private long position;
	private long fsize;
	private String fromUser;
	private String toUser;

	private static Map<String, OffLineFileTransferDown> downloadingMap = new HashMap<String, OffLineFileTransferDown>();

	public OffLineFileTransferDown() {
	}

	public OffLineFileTransferDown(String localUrl, String localFileName, String serverFileName,
			String fromUser, String toUser) {
		this.localUrl = localUrl;
		this.localFileName = localFileName;
		this.serverFileName = serverFileName;
		this.fromUser = fromUser;
		this.toUser = toUser;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getLocalFileName() {
		return localFileName;
	}

	public void setLocalFileName(String localFileName) {
		this.localFileName = localFileName;
	}

	public String getServerFileName() {
		return serverFileName;
	}

	public void setServerFileName(String serverFileName) {
		this.serverFileName = serverFileName;
	}

	public String getLocalUrl() {
		return localUrl;
	}

	public void setLocalUrl(String localUrl) {
		this.localUrl = localUrl;
	}

	public String getTransStatus() {
		return transStatus;
	}

	public void setTransStatus(String transStatus) {
		this.transStatus = transStatus;
	}

	public long getPosition() {
		return position;
	}

	public void setPosition(long position) {
		this.position = position;
	}

	public long getFsize() {
		return fsize;
	}

	public void setFsize(long fsize) {
		this.fsize = fsize;
	}

	public String getFromUser() {
		return fromUser;
	}

	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}

	public String getToUser() {
		return toUser;
	}

	public void setToUser(String toUser) {
		this.toUser = toUser;
	}

	/**
	 * 首次下载
	 *
	 */
	public void downFile() {
		this.file = new File(this.getLocalUrl() + "\\" + this.getLocalFileName());
		if (!this.file.getParentFile().exists()) {
			new File(this.getLocalUrl()).mkdirs();
		}
		DownFileTransferIQ dfIQ = new DownFileTransferIQ();
		dfIQ.setFileName(this.getServerFileName());
		dfIQ.setPosition(0L);

		this.setTransStatus(OffLineFileTransferComm.ING);
		this.setPosition(0L);
//		SparkManager.getConnection().sendPacket(dfIQ);
        //TODO 有修改
		SparkManager.getConnection().sendIqRequestAsync(dfIQ);//.sendPacket(dfIQ);
	}

	public void writeFile(String data) {
		FileTools.writeFile(data, this.getFile());
		downFileNext();
	}

	/**
	 * 后续下载
	 *
	 */
	public void downFileNext() {

		DownFileTransferIQ dfIQ = new DownFileTransferIQ();
		dfIQ.setFileName(this.getServerFileName());
		dfIQ.setPosition(getPosition() + OffLineFileTransferComm.lenOfOneTime);

		this.setPosition(dfIQ.getPosition());
		//TODO 有修改
//		SparkManager.getConnection().sendPacket(dfIQ);
		SparkManager.getConnection().sendIqRequestAsync(dfIQ);
	}

	public boolean finished() {
		return getTransStatus() != null && getTransStatus().equals(OffLineFileTransferComm.SUCCESS);
	}

	public boolean canceled() {
		return getTransStatus() != null && getTransStatus().equals(OffLineFileTransferComm.CANCEL);
	}

	public boolean downloading() {
		return getTransStatus() != null && getTransStatus().equals(OffLineFileTransferComm.ING);
	}

	public static void put(String key, OffLineFileTransferDown value) {
		downloadingMap.put(key, value);
	}

	public static OffLineFileTransferDown get(String key) {
		return downloadingMap.get(key);
	}

	public static void remove(String key) {
		downloadingMap.remove(key);
	}

	public static int getOffLineMapSize() {
		return downloadingMap.size();
	}
}
