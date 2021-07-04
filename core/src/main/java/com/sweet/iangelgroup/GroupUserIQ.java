package com.sweet.iangelgroup;

import com.sweet.DownFileTransferIQ;
import java.util.ArrayList;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.spark.util.log.Log;
import org.xmlpull.v1.XmlPullParser;

public class GroupUserIQ extends IQ {

	public static final String IQ_ELEMENT = "iangel";
	public static final String IQ_NAMESPACE = "ica:groupuser";

	String groupId = null;
	String groupName = null;

	private ArrayList<String> admins = new ArrayList<String>();
	private ArrayList<String> owners = new ArrayList<String>();
	private ArrayList<String> members = new ArrayList<String>();
    public GroupUserIQ() {
        super("groupuser",IQ_NAMESPACE);
    }
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

	public ArrayList<String> getAdmins() {
		return admins;
	}

	public void addAdmins(String user) {
		admins.add(user);
	}

	public void setAdmins(ArrayList<String> admins) {
		this.admins = admins;
	}

	public ArrayList<String> getOwners() {
		return owners;
	}

	public void addOwners(String user) {
		owners.add(user);
	}

	public void setOwners(ArrayList<String> owners) {
		this.owners = owners;
	}

	public ArrayList<String> getMembers() {
		return members;
	}

	public void addMembers(String user) {
		members.add(user);
	}

	public void setMembers(ArrayList<String> members) {
		this.members = members;
	}

	public boolean has(String jid) {
		return admins.contains(jid) || owners.contains(jid) || members.contains(jid);
	}

	public boolean isManager(String jid) {
		return admins.contains(jid) || owners.contains(jid);
	}

//	@Override
//	public String getChildElementXML() {
//		StringBuffer sb = new StringBuffer();
//		sb.append("<").append(IQ_ELEMENT).append(" xmlns=\"").append(IQ_NAMESPACE).append("\">");
//		sb.append("<group name=\"" + groupName + "\" />");
//		sb.append("</").append(IQ_ELEMENT).append(">");
//		return sb.toString();
//	}

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder( IQChildElementXmlStringBuilder buf )
    {
        if(buf.length()!=0) {
            buf.rightAngleBracket();
        }

        XmlStringBuilder fb = buf.element("group","");
        fb.attribute("name",groupName);

        buf.element(IQ_ELEMENT,fb).attribute("xmlns",IQ_NAMESPACE);
//        buf.element("size", Long.toString( size ));
//        buf.element("filename", fileName);
        return buf;
    }

	public static class Provider extends IQProvider<GroupUserIQ> {

//		@Override
//		public IQ parseIQ(XmlPullParser xp) throws Exception {
//
//			GroupUserIQ result = new GroupUserIQ();
//
//			while (true) {
//				int n = xp.next();
//				if (n == XmlPullParser.START_TAG) {
//					if ("user".equals(xp.getName())) {
//						String type = xp.getAttributeValue(0);
//						if (type.equals("member")) {
//							result.addMembers(xp.nextText());
//						} else if (type.equals("admin")) {
//							result.addAdmins(xp.nextText());
//						} else if (type.equals("owner")) {
//							result.addOwners(xp.nextText());
//						}
//					}
//				} else if (n == XmlPullParser.END_TAG) {
//					if (GroupUserIQ.IQ_ELEMENT.equals(xp.getName())) {
//						break;
//					}
//				}
//			}
//
//			return result;
//		}

        @Override
        public GroupUserIQ parse(XmlPullParser xp, int var2) throws Exception{

            GroupUserIQ result = new GroupUserIQ();

            while (true) {
                int n = xp.next();
                if (n == XmlPullParser.START_TAG) {
                    if ("user".equals(xp.getName())) {
                        String type = xp.getAttributeValue(0);
                        if (type.equals("member")) {
                            result.addMembers(xp.nextText());
                        } else if (type.equals("admin")) {
                            result.addAdmins(xp.nextText());
                        } else if (type.equals("owner")) {
                            result.addOwners(xp.nextText());
                        }
                    }
                } else if (n == XmlPullParser.END_TAG) {
                    if (GroupUserIQ.IQ_ELEMENT.equals(xp.getName())) {
                        break;
                    }
                }
            }
            return result;
        }
	}

}
