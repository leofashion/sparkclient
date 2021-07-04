package com.sweet;

import org.jivesoftware.spark.SparkManager;

public class OffLineFileTransferComm {

	public static final int lenOfOneTime = 256 * 1024;
	public static final String SUCCESS = "SUCCESS";
	public static final String ERROR = "ERROR";
	public static final String ING = "ING";
	public static final String CANCEL = "CANCEL";

	/**
	 * delete server file
	 */
	public static void deleteServerFile(String serverFileName) {
		FileOperateIQ fIQ = new FileOperateIQ();
		fIQ.setName(serverFileName);
		fIQ.setAction("remove");
		//TODO 修改
		SparkManager.getConnection().sendIqRequestAsync(fIQ);
//		SparkManager.getConnection().sendPacket(fIQ);
	}

}
