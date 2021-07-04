//package com.sweet.util;
//
//import java.util.Iterator;
//
//import org.jivesoftware.smack.Connection;
//import org.jivesoftware.smack.XMPPException;
//import org.jivesoftware.smackx.Form;
//import org.jivesoftware.smackx.FormField;
//import org.jivesoftware.smackx.ServiceDiscoveryManager;
//import org.jivesoftware.smackx.packet.DiscoverInfo;
//
///**
// * 获取群的信息的辅助类。
// *
// * @author chenhy
// *
// */
//public class RoomInfoAgent {
//
//	private String room;
//	private String description = "";
//
//	private String subject = "";
//
//	private int occupantsCount = -1;
//	private boolean membersOnly;
//	private boolean moderated;
//	private boolean nonanonymous;
//	private boolean passwordProtected;
//	private boolean persistent;
//
//	public static RoomInfoAgent getRoomInfo(Connection connection, String room)
//			throws XMPPException {
//
//		DiscoverInfo info = ServiceDiscoveryManager.getInstanceFor(connection).discoverInfo(room);
//		return new RoomInfoAgent(info);
//	}
//
//	private RoomInfoAgent(DiscoverInfo info) {
//		this.room = info.getFrom();
//
//		this.membersOnly = info.containsFeature("muc_membersonly");
//		this.moderated = info.containsFeature("muc_moderated");
//		this.nonanonymous = info.containsFeature("muc_nonanonymous");
//		this.passwordProtected = info.containsFeature("muc_passwordprotected");
//		this.persistent = info.containsFeature("muc_persistent");
//
//		Form form = Form.getFormFrom(info);
//
//		if (form != null) {
//
//			FormField descField = form.getField("muc#roominfo_description");
//			this.description = ((descField == null) || (!descField.getValues().hasNext()) ? ""
//					: (String) descField.getValues().next());
//
//			FormField subjField = form.getField("muc#roominfo_subject");
//
//			this.subject = ((subjField == null) || (!subjField.getValues().hasNext()) ? ""
//					: (String) subjField.getValues().next());
//
//			FormField occCountField = form.getField("muc#roominfo_occupants");
//			if (occCountField != null) {
//				Iterator<String> values = occCountField.getValues();
//				String tempCount = null;
//				if (values.hasNext()) {
//					tempCount = values.next();
//				}
//				if (values.hasNext()) {
//					this.subject = tempCount;
//					tempCount = values.next();
//				} else {
//					this.occupantsCount = Integer.parseInt(tempCount);
//				}
//			} else {
//				this.occupantsCount = -1;
//			}
//		}
//	}
//
//	public String getRoom() {
//		return this.room;
//	}
//
//	public String getDescription() {
//		return this.description;
//	}
//
//	public String getSubject() {
//		return this.subject;
//	}
//
//	public int getOccupantsCount() {
//		return this.occupantsCount;
//	}
//
//	public boolean isMembersOnly() {
//		return this.membersOnly;
//	}
//
//	public boolean isModerated() {
//		return this.moderated;
//	}
//
//	public boolean isNonanonymous() {
//		return this.nonanonymous;
//	}
//
//	public boolean isPasswordProtected() {
//		return this.passwordProtected;
//	}
//
//	public boolean isPersistent() {
//		return this.persistent;
//	}
//}
