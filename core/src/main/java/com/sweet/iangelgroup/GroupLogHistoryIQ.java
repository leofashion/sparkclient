package com.sweet.iangelgroup;

import com.sweet.DownFileTransferIQ;
import java.util.ArrayList;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.spark.util.log.Log;
import org.xmlpull.v1.XmlPullParser;

public class GroupLogHistoryIQ extends IQ {

	public static final String IQ_ELEMENT = "iangel";
	public static final String IQ_NAMESPACE = "ica:grouploghistory";

	private String groupId = null;
	private String groupName = null;
	private String jid = null;
	private String time = null;
	private String action = null;

    public GroupLogHistoryIQ() {
        super("grouploghistory",IQ_NAMESPACE);
    }
	private ArrayList<GroupMessage> messages = new ArrayList<GroupMessage>();

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getJid() {
		return jid;
	}

	public void setJid(String jid) {
		this.jid = jid;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public ArrayList<GroupMessage> getMessages() {
		return messages;
	}

	public void addMessage(GroupMessage msg) {
		messages.add(msg);
	}

//	@Override
//	public String getChildElementXML() {
//		StringBuffer sb = new StringBuffer();
//		sb.append("<").append(IQ_ELEMENT).append(" xmlns=\"").append(IQ_NAMESPACE).append("\">");
//		sb.append("<grouploghistory name=\"" + groupName + "\" jid=\"" + getJid() + "\" action=\""
//				+ getAction() + "\" time=\"" + getTime() + "\" />");
//		sb.append("</").append(IQ_ELEMENT).append(">");
//		return sb.toString();
//	}

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder( IQChildElementXmlStringBuilder buf )
    {
        if(buf.length()!=0) {
            buf.rightAngleBracket();
        }

        XmlStringBuilder fb = buf.element("grouploghistory","");
        fb.attribute("name",groupName);
        fb.attribute("jid",""+getJid());
        fb.attribute("action",""+getAction());
        fb.attribute("time",""+getTime());

        buf.element(IQ_ELEMENT,fb).attribute("xmlns",IQ_NAMESPACE);
//        buf.element("size", Long.toString( size ));
//        buf.element("filename", fileName);
        return buf;
    }

	public static class Provider extends IQProvider<GroupLogHistoryIQ> {

//		@Override
//		public IQ parse(XmlPullParser xp) throws Exception {
//
//			GroupLogHistoryIQ result = new GroupLogHistoryIQ();
//
//			while (true) {
//				int n = xp.next();
//				if (n == XmlPullParser.START_TAG) {
//					if ("msg".equals(xp.getName())) {
//						GroupMessage msg = new GroupMessage();
//						int size = xp.getAttributeCount();
//						for (int i = 0; i < size; i++) {
//							String name = xp.getAttributeName(i);
//							String value = xp.getAttributeValue(i);
//							if (name.equals("sender")) {
//								msg.setSender(value);
//							} else if (name.equals("logTime")) {
//								msg.setLogTime(value);
//							} else if (name.equals("body")) {
//								msg.setBody(value);
//							} else if (name.equals("subject")) {
//								msg.setSubject(value);
//							}
//						}
//						result.addMessage(msg);
//					}
//				} else if (n == XmlPullParser.END_TAG) {
//					if (GroupLogHistoryIQ.IQ_ELEMENT.equals(xp.getName())) {
//						break;
//					}
//				}
//			}
//
//			return result;
//		}
        @Override
        public GroupLogHistoryIQ parse(XmlPullParser xp, int var2) throws Exception{

            GroupLogHistoryIQ result = new GroupLogHistoryIQ();

            while (true) {
                int n = xp.next();
                if (n == XmlPullParser.START_TAG) {
                    if ("msg".equals(xp.getName())) {
                        GroupMessage msg = new GroupMessage();
                        int size = xp.getAttributeCount();
                        for (int i = 0; i < size; i++) {
                            String name = xp.getAttributeName(i);
                            String value = xp.getAttributeValue(i);
                            if (name.equals("sender")) {
                                msg.setSender(value);
                            } else if (name.equals("logTime")) {
                                msg.setLogTime(value);
                            } else if (name.equals("body")) {
                                msg.setBody(value);
                            } else if (name.equals("subject")) {
                                msg.setSubject(value);
                            }
                        }
                        result.addMessage(msg);
                    }
                } else if (n == XmlPullParser.END_TAG) {
                    if (GroupLogHistoryIQ.IQ_ELEMENT.equals(xp.getName())) {
                        break;
                    }
                }
            }
            return result;
        }
	}
}
