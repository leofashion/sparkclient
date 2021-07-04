package com.sweet;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

public class UserListIQ extends IQ {

	public static final String IQ_ELEMENT = "iangel";
	public static final String IQ_NAMESPACE = "ica:userlist";

    public UserListIQ() {
        super("downfile",IQ_NAMESPACE);
    }
	private Hashtable<String, ArrayList<String>> groups = new Hashtable<String, ArrayList<String>>();

	public ArrayList<String> getUsers(String gname) {
		return groups.get(gname);
	}

	public void setUsers(String gname, ArrayList<String> users) {
		this.groups.put(gname, users);
	}

	public void addUser(String gname, String user) {
		if (gname == null)
			return;
		ArrayList<String> users = groups.get(gname);
		if (users == null) {
			users = new ArrayList<String>();
		}
		users.add(user);
		groups.put(gname, users);
	}

//	@Override
//	public String getChildElementXML() {
//		StringBuffer sb = new StringBuffer();
//		sb.append("<").append(IQ_ELEMENT).append(" xmlns=\"").append(IQ_NAMESPACE).append("\">");
//		sb.append("</").append(IQ_ELEMENT).append(">");
//		// <grouplist xmlns="iq:grouplist"><gname>1</gname><gname>2</gname></iangel>
//		return sb.toString();
//	}
    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder( IQChildElementXmlStringBuilder buf )
    {
        if(buf.length()!=0) {
            buf.rightAngleBracket();
        }


        buf.element(IQ_ELEMENT,"").attribute("xmlns",IQ_NAMESPACE);
//        buf.element("size", Long.toString( size ));
//        buf.element("filename", fileName);
        return buf;
    }

//	@Override
//	public String toString() {
//		StringBuffer sb = new StringBuffer();
//		Enumeration<String> key = groups.keys();
//		while (key.hasMoreElements()) {
//			String gname = (String) key.nextElement();
//			ArrayList<String> users = groups.get(gname);
//			for (String user : users) {
//				sb.append(user + ":" + gname + "\n");
//			}
//		}
//		return sb.toString();
//	}

	public static class Provider extends IQProvider<UserListIQ> {

//		@Override
//		public IQ parseIQ(XmlPullParser xp) throws Exception {
//
//			UserListIQ result = new UserListIQ();
//
//			while (true) {
//				int n = xp.next();
//				if (n == XmlPullParser.START_TAG) {
//					if ("uname".equals(xp.getName())) {
//						int ac = xp.getAttributeCount();
//						String gname = null;
//						for (int i = 0; i < ac; i++) {
//							if (xp.getAttributeName(i).equals("gname")) {
//								gname = xp.getAttributeValue(i);
//								break;
//							}
//						}
//						String uname = xp.nextText();
//						result.addUser(gname, uname);
//					}
//				} else if (n == XmlPullParser.END_TAG) {
//					if (UserListIQ.IQ_ELEMENT.equals(xp.getName())) {
//						break;
//					}
//				}
//			}
//
//			return result;
//		}

        @Override
        public UserListIQ parse(XmlPullParser xp, int var2) throws Exception{

            UserListIQ result = new UserListIQ();

            while (true) {
                int n = xp.next();
                if (n == XmlPullParser.START_TAG) {
                    if ("uname".equals(xp.getName())) {
                        int ac = xp.getAttributeCount();
                        String gname = null;
                        for (int i = 0; i < ac; i++) {
                            if (xp.getAttributeName(i).equals("gname")) {
                                gname = xp.getAttributeValue(i);
                                break;
                            }
                        }
                        String uname = xp.nextText();
                        result.addUser(gname, uname);
                    }
                } else if (n == XmlPullParser.END_TAG) {
                    if (UserListIQ.IQ_ELEMENT.equals(xp.getName())) {
                        break;
                    }
                }
            }

            return result;
        }
	}

}
