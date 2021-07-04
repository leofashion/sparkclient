package com.sweet.iangelgroup;

import com.sweet.DownFileTransferIQ;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.spark.util.log.Log;
import org.xmlpull.v1.XmlPullParser;

public class GroupLogIQ extends IQ {

	public static final String IQ_ELEMENT = "iangel";
	public static final String IQ_NAMESPACE = "ica:grouplog";
    public GroupLogIQ() {
        super("grouplog",IQ_NAMESPACE);
    }
	private String groupId = null;
	private String groupName = null;
	private String jid = null;
	private String action = null;

	private String result = null;

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

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

//	@Override
//	public String getChildElementXML() {
//		StringBuffer sb = new StringBuffer();
//		sb.append("<").append(IQ_ELEMENT).append(" xmlns=\"").append(IQ_NAMESPACE).append("\">");
//		sb.append("<grouplog name=\"" + groupName + "\" jid=\"" + getJid() + "\" action=\""
//				+ getAction() + "\" />");
//		sb.append("</").append(IQ_ELEMENT).append(">");
//		return sb.toString();
//	}
    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder( IQChildElementXmlStringBuilder buf )
    {
        if(buf.length()!=0) {
            buf.rightAngleBracket();
        }

        XmlStringBuilder fb = buf.element("grouplog","");
        fb.attribute("name",groupName);
        fb.attribute("jid",""+getJid() );
        fb.attribute("action",""+getAction());

        buf.element(IQ_ELEMENT,fb).attribute("xmlns",IQ_NAMESPACE);
        return buf;
    }

	public static class Provider extends IQProvider<GroupLogIQ> {

//		@Override
//		public IQ parseIQ(XmlPullParser xp) throws Exception {
//
//			GroupLogIQ result = new GroupLogIQ();
//
//			while (true) {
//				int n = xp.next();
//				if (n == XmlPullParser.START_TAG) {
//					if ("result".equals(xp.getName())) {
//						result.setResult(xp.getAttributeValue(0));
//					}
//				} else if (n == XmlPullParser.END_TAG) {
//					if (GroupLogIQ.IQ_ELEMENT.equals(xp.getName())) {
//						break;
//					}
//				}
//			}
//
//			return result;
//		}

        @Override
        public GroupLogIQ parse(XmlPullParser xp, int var2) throws Exception{

            GroupLogIQ result = new GroupLogIQ();

            while (true) {
                int n = xp.next();
                if (n == XmlPullParser.START_TAG) {
                    if ("result".equals(xp.getName())) {
                        result.setResult(xp.getAttributeValue(0));
                    }
                } else if (n == XmlPullParser.END_TAG) {
                    if (GroupLogIQ.IQ_ELEMENT.equals(xp.getName())) {
                        break;
                    }
                }
            }
            return result;
        }
	}
}
