//package com.sweet.iangelgroup;
//
//import org.jivesoftware.smack.packet.IQ;
//import org.jivesoftware.smack.provider.IQProvider;
//import org.xmlpull.v1.XmlPullParser;
//
//public class GroupUserRemoveIQ extends IQ {
//
//	public static final String IQ_ELEMENT = "iangel";
//	public static final String IQ_NAMESPACE = "ica:groupuserremove";
//
//	String groupId = null;
//	String groupName = null;
//	String jid = null;
//	boolean success = false;
//
//	public String getGroupId() {
//		return groupId;
//	}
//
//	public void setGroupId(String groupId) {
//		this.groupId = groupId;
//	}
//
//	public String getGroupName() {
//		return groupName;
//	}
//
//	public void setGroupName(String groupName) {
//		this.groupName = groupName;
//	}
//
//	public String getJid() {
//		return jid;
//	}
//
//	public void setJid(String jid) {
//		this.jid = jid;
//	}
//
//	public boolean isSuccess() {
//		return success;
//	}
//
//	public void setSuccess(boolean success) {
//		this.success = success;
//	}
//
//	@Override
//	public String getChildElementXML() {
//		StringBuffer sb = new StringBuffer();
//		sb.append("<").append(IQ_ELEMENT).append(" xmlns=\"").append(IQ_NAMESPACE).append("\">");
//		sb.append("<group name=\"" + groupName + "\" jid=\"" + jid + "\" />");
//		sb.append("</").append(IQ_ELEMENT).append(">");
//		return sb.toString();
//	}
//
//	public static class Provider implements IQProvider {
//
//		@Override
//		public IQ parseIQ(XmlPullParser xp) throws Exception {
//
//			GroupUserRemoveIQ result = new GroupUserRemoveIQ();
//
//			while (true) {
//				int n = xp.next();
//				if (n == XmlPullParser.START_TAG) {
//					if ("result".equals(xp.getName())) {
//						result.setSuccess(Boolean.parseBoolean(xp.getText()));
//					}
//				} else if (n == XmlPullParser.END_TAG) {
//					if (GroupUserRemoveIQ.IQ_ELEMENT.equals(xp.getName())) {
//						break;
//					}
//				}
//			}
//
//			return result;
//		}
//	}
//
//}
