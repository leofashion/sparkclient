//package com.sweet.iangelgroup;
//
//import org.jivesoftware.Spark;
//import org.jivesoftware.smack.chat.Chat;
//import org.jivesoftware.smack.ConnectionConfiguration;
//import org.jivesoftware.smack.MessageListener;
//import org.jivesoftware.smack.XMPPConnection;
//import org.jivesoftware.smack.XMPPException;
//import org.jivesoftware.smack.packet.Message;
//
//public class MessageHelper {
//
//	private static XMPPConnection conn = null;
//
//	private static void initConnection() {
//		try {
//			String serverName = Spark.getServerName();
//			ConnectionConfiguration cconfig = new ConnectionConfiguration(serverName, 5222);
//			XMPPConnection con = new XMPPConnection(cconfig);
//			con.connect();
//			con.login("admin", "admin");
//
//			conn = con;
//		} catch (XMPPException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public synchronized static void sendMessage(String target, String msg) {
//		if (conn == null) {
//			initConnection();
//		}
//		if (conn != null) {
//			Chat chat = conn.getChatManager().createChat(target, new MessageListener() {
//				public void processMessage(Chat chat, Message message) {
//					System.out.println("I'm sending: " + message.getBody());
//				}
//			});
//			try {
//				chat.sendMessage(msg);
//			} catch (Exception e) {
//			}
//		}
//	}
//
//	public static void close() {
//		if (conn != null) {
//			conn.disconnect();
//		}
//		conn = null;
//	}
//}
