package com.sweet;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.spark.SparkManager;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

/**
 *
 * liuh 2014-2-21 13:52:42
 */
public class OffLineFileTransferUpload {

	private File file;
	private long position;
	private String localFileName;
	private String serverFileName;
	private String transStatus;

	private boolean isGroupChat = false;

	private static Map<String, OffLineFileTransferUpload> offLineMap = new HashMap<String, OffLineFileTransferUpload>();

	public OffLineFileTransferUpload() {
	}

	public OffLineFileTransferUpload(File file, String localFileName, String serverFileName) {
		this.file = file;
		this.localFileName = localFileName;
		this.serverFileName = serverFileName;
	}

	public boolean isGroupChat() {
		return isGroupChat;
	}

	public void setGroupChat(boolean isGroupChat) {
		this.isGroupChat = isGroupChat;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public long getPosition() {
		return position;
	}

	public void setPosition(long position) {
		this.position = position;
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

	public String getTransStatus() {
		return transStatus;
	}

	public void setTransStatus(String transStatus) {
		this.transStatus = transStatus;
	}

	/**
	 * first upload
	 */
	public void sendFile() {
		String data = FileTools.readFile(this.getFile(), 0, OffLineFileTransferComm.lenOfOneTime);
		FileTransferIQ glIQ = new FileTransferIQ();
		glIQ.setFileName(this.getServerFileName());
		glIQ.setData(data);
		this.setPosition(OffLineFileTransferComm.lenOfOneTime);
		this.setTransStatus(OffLineFileTransferComm.ING);
		//TODO 修改
		SparkManager.getConnection().sendIqRequestAsync(glIQ);
//		SparkManager.getConnection().sendPacket(glIQ);
	}

	/**
	 * from posi upload
	 *
	 * @param posi
	 */
	public void sendFile(long posi) throws XmppStringprepException {
		FileTransferIQ glIQ = new FileTransferIQ();
		String data = FileTools.readFile(this.getFile(), posi,
				OffLineFileTransferComm.lenOfOneTime);
		glIQ.setFileName(this.getServerFileName());
		glIQ.setData(data);
		this.setPosition(posi + OffLineFileTransferComm.lenOfOneTime);
		this.setTransStatus(OffLineFileTransferComm.ING);
        //TODO 修改
        glIQ.setTo(JidCreate.from("admin" + "@" + SparkManager.getConnection().getStreamId()));
//		glIQ.setTo("admin" + "@" + SparkManager.getConnection().getServiceName());

//		SparkManager.getConnection().sendPacket(glIQ);
        SparkManager.getConnection().sendIqRequestAsync(glIQ);
	}

	/**
	 * sended total
	 *
	 * @return
	 */
	public long getBytesSent() {
		return this.getPosition();
	}

	public static void put(String key, OffLineFileTransferUpload value) {
		offLineMap.put(key, value);
	}

	public static OffLineFileTransferUpload get(String key) {
		return offLineMap.get(key);
	}

	public static void remove(String key) {
		offLineMap.remove(key);
	}

	public static int getOffLineMapSize() {
		return offLineMap.size();
	}

}
