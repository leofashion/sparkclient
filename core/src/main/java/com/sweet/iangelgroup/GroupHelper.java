//package com.sweet.iangelgroup;
//
//import java.util.ArrayList;
//
//import org.jivesoftware.smack.PacketCollector;
//import org.jivesoftware.smack.XMPPConnection;
//import org.jivesoftware.smack.filter.PacketIDFilter;
//import org.jivesoftware.smack.provider.ProviderManager;
//import org.jivesoftware.spark.SparkManager;
//
//public class GroupHelper {
//
//	private final static String GROUP = "group";
//	private final static String GROUP_TEMP = "discussion";
//
//	public static String group() {
//		return GROUP + "." + SparkManager.getConnection().getServiceName();
//	}
//
//	public static String groupTemp() {
//		return GROUP_TEMP + "." + SparkManager.getConnection().getServiceName();
//	}
//
//	public static GroupUserIQ getGroupUsers(String roomTitle) {
//
//		ProviderManager.getInstance().addIQProvider(GroupUserIQ.IQ_ELEMENT,
//				GroupUserIQ.IQ_NAMESPACE, new GroupUserIQ.Provider());
//
//		XMPPConnection connect = SparkManager.getConnection();
//
//		GroupUserIQ request = new GroupUserIQ();
//		request.setGroupName(roomTitle);
//		request.setTo("admin" + "@" + connect.getServiceName());
//
//		PacketCollector pc = connect
//				.createPacketCollector(new PacketIDFilter(request.getPacketID()));
//
//		connect.sendPacket(request);
//
//		GroupUserIQ result = (GroupUserIQ) pc.nextResult();
//		return result;
//	}
//
//	public static boolean isGroupChat(String roomId) {
//		return roomId != null && (roomId.indexOf(GROUP) > 0 || roomId.indexOf(GROUP_TEMP) > 0);
//	}
//
//	public static boolean deleteGroupUser(String roomTitle, String jid) {
//		ProviderManager.getInstance().addIQProvider(GroupUserRemoveIQ.IQ_ELEMENT,
//				GroupUserRemoveIQ.IQ_NAMESPACE, new GroupUserRemoveIQ.Provider());
//
//		XMPPConnection connect = SparkManager.getConnection();
//
//		GroupUserRemoveIQ request = new GroupUserRemoveIQ();
//		request.setGroupName(roomTitle);
//		request.setJid(jid);
//		request.setTo("admin" + "@" + connect.getServiceName());
//
//		PacketCollector pc = connect
//				.createPacketCollector(new PacketIDFilter(request.getPacketID()));
//
//		connect.sendPacket(request);
//
//		GroupUserRemoveIQ result = (GroupUserRemoveIQ) pc.nextResult();
//		return result.isSuccess();
//	}
//
//	public static ArrayList<GroupFile> getGroupFiles(String roomTitle) {
//		ProviderManager.getInstance().addIQProvider(GroupFileIQ.IQ_ELEMENT,
//				GroupFileIQ.IQ_NAMESPACE, new GroupFileIQ.Provider());
//
//		XMPPConnection connect = SparkManager.getConnection();
//
//		GroupFileIQ request = new GroupFileIQ();
//		request.setGroupName(roomTitle);
//		request.setTo("admin" + "@" + connect.getServiceName());
//
//		PacketCollector pc = connect
//				.createPacketCollector(new PacketIDFilter(request.getPacketID()));
//
//		connect.sendPacket(request);
//
//		GroupFileIQ result = (GroupFileIQ) pc.nextResult();
//		return result.getFiles();
//	}
//
//	public static boolean addGroupFile(String roomTitle, GroupFile f) {
//		ProviderManager.getInstance().addIQProvider(GroupFileAddIQ.IQ_ELEMENT,
//				GroupFileAddIQ.IQ_NAMESPACE, new GroupFileAddIQ.Provider());
//
//		XMPPConnection connect = SparkManager.getConnection();
//
//		GroupFileAddIQ request = new GroupFileAddIQ();
//		request.setGroupName(roomTitle);
//		request.setFile(f);
//		request.setTo("admin" + "@" + connect.getServiceName());
//
//		PacketCollector pc = connect
//				.createPacketCollector(new PacketIDFilter(request.getPacketID()));
//
//		connect.sendPacket(request);
//
//		GroupFileAddIQ result = (GroupFileAddIQ) pc.nextResult();
//		return result.isSuccess();
//	}
//
//	public static boolean deleteGroupFile(String roomTitle, GroupFile f) {
//		ProviderManager.getInstance().addIQProvider(GroupFileDeleteIQ.IQ_ELEMENT,
//				GroupFileDeleteIQ.IQ_NAMESPACE, new GroupFileDeleteIQ.Provider());
//
//		XMPPConnection connect = SparkManager.getConnection();
//
//		GroupFileDeleteIQ request = new GroupFileDeleteIQ();
//		request.setGroupName(roomTitle);
//		request.setFile(f);
//		request.setTo("admin" + "@" + connect.getServiceName());
//
//		PacketCollector pc = connect
//				.createPacketCollector(new PacketIDFilter(request.getPacketID()));
//
//		connect.sendPacket(request);
//
//		GroupFileDeleteIQ result = (GroupFileDeleteIQ) pc.nextResult();
//		return result.isSuccess();
//	}
//
//	public static boolean setLogoutTime(String roomTitle, String jid) {
//		ProviderManager.getInstance().addIQProvider(GroupLogIQ.IQ_ELEMENT, GroupLogIQ.IQ_NAMESPACE,
//				new GroupLogIQ.Provider());
//
//		XMPPConnection connect = SparkManager.getConnection();
//
//		GroupLogIQ request = new GroupLogIQ();
//		request.setGroupName(roomTitle);
//		request.setJid(jid);
//		request.setAction("logout");
//		request.setTo("admin" + "@" + connect.getServiceName());
//
//		PacketCollector pc = connect
//				.createPacketCollector(new PacketIDFilter(request.getPacketID()));
//
//		connect.sendPacket(request);
//
//		GroupLogIQ result = (GroupLogIQ) pc.nextResult();
//		return result.getResult() != null && result.getResult().equals("0");
//	}
//
//	public static boolean setLoginTime(String roomTitle, String jid) {
//		ProviderManager.getInstance().addIQProvider(GroupLogIQ.IQ_ELEMENT, GroupLogIQ.IQ_NAMESPACE,
//				new GroupLogIQ.Provider());
//
//		XMPPConnection connect = SparkManager.getConnection();
//
//		GroupLogIQ request = new GroupLogIQ();
//		request.setGroupName(roomTitle);
//		request.setJid(jid);
//		request.setAction("login");
//		request.setTo("admin" + "@" + connect.getServiceName());
//
//		PacketCollector pc = connect
//				.createPacketCollector(new PacketIDFilter(request.getPacketID()));
//
//		connect.sendPacket(request);
//
//		GroupLogIQ result = (GroupLogIQ) pc.nextResult();
//		return result.getResult() != null && result.getResult().equals("0");
//	}
//
//	public static int getUnReadNum(String roomTitle, String jid) {
//		ProviderManager.getInstance().addIQProvider(GroupLogNumIQ.IQ_ELEMENT,
//				GroupLogNumIQ.IQ_NAMESPACE, new GroupLogNumIQ.Provider());
//
//		XMPPConnection connect = SparkManager.getConnection();
//
//		GroupLogNumIQ request = new GroupLogNumIQ();
//		request.setGroupName(roomTitle);
//		request.setJid(jid);
//		request.setTo("admin" + "@" + connect.getServiceName());
//
//		PacketCollector pc = connect
//				.createPacketCollector(new PacketIDFilter(request.getPacketID()));
//
//		connect.sendPacket(request);
//
//		GroupLogNumIQ result = (GroupLogNumIQ) pc.nextResult();
//
//		return result.getResult() != null ? Integer.parseInt(result.getResult()) : 0;
//	}
//
//	public static ArrayList<GroupMessage> getUnReadMessage(String roomTitle, String jid) {
//		ProviderManager.getInstance().addIQProvider(GroupLogHistoryIQ.IQ_ELEMENT,
//				GroupLogHistoryIQ.IQ_NAMESPACE, new GroupLogHistoryIQ.Provider());
//
//		XMPPConnection connect = SparkManager.getConnection();
//
//		GroupLogHistoryIQ request = new GroupLogHistoryIQ();
//		request.setGroupName(roomTitle);
//		request.setJid(jid);
//		request.setAction("unread");
//		request.setTo("admin" + "@" + connect.getServiceName());
//
//		PacketCollector pc = connect
//				.createPacketCollector(new PacketIDFilter(request.getPacketID()));
//
//		connect.sendPacket(request);
//
//		GroupLogHistoryIQ result = (GroupLogHistoryIQ) pc.nextResult();
//
//		return result.getMessages();
//	}
//
//	public static ArrayList<GroupMessage> getHistoryMessage(String roomTitle, String jid,
//			String strTime) {
//
//		ProviderManager.getInstance().addIQProvider(GroupLogHistoryIQ.IQ_ELEMENT,
//				GroupLogHistoryIQ.IQ_NAMESPACE, new GroupLogHistoryIQ.Provider());
//
//		XMPPConnection connect = SparkManager.getConnection();
//
//		GroupLogHistoryIQ request = new GroupLogHistoryIQ();
//		request.setGroupName(roomTitle);
//		request.setJid(jid);
//		request.setAction("history");
//		request.setTime(strTime);
//		request.setTo("admin" + "@" + connect.getServiceName());
//
//		PacketCollector pc = connect
//				.createPacketCollector(new PacketIDFilter(request.getPacketID()));
//
//		connect.sendPacket(request);
//
//		GroupLogHistoryIQ result = (GroupLogHistoryIQ) pc.nextResult();
//
//		return result.getMessages();
//	}
//
//}
