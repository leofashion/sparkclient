package com.sweet.iangelgroup;

import com.sweet.DownFileTransferIQ;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.spark.util.log.Log;
import org.xmlpull.v1.XmlPullParser;

public class GroupLogNumIQ extends IQ {

	public static final String IQ_ELEMENT = "iangel";
	public static final String IQ_NAMESPACE = "ica:grouplognum";

    public GroupLogNumIQ() {
        super("grouplognum",IQ_NAMESPACE);
    }
	private String groupId = null;
	private String groupName = null;
	private String jid = null;

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

//	@Override
//	public String getChildElementXML() {
//		StringBuffer sb = new StringBuffer();
//		sb.append("<").append(IQ_ELEMENT).append(" xmlns=\"").append(IQ_NAMESPACE).append("\">");
//		sb.append("<grouplognum name=\"" + groupName + "\" jid=\"" + getJid() + "\" />");
//		sb.append("</").append(IQ_ELEMENT).append(">");
//		return sb.toString();
//	}

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder( IQChildElementXmlStringBuilder buf )
    {
        if(buf.length()!=0) {
            buf.rightAngleBracket();
        }

        XmlStringBuilder fb = buf.element("grouplognum","");
        fb.attribute("name",groupName);
        fb.attribute("jid",""+getJid());

        buf.element(IQ_ELEMENT,fb).attribute("xmlns",IQ_NAMESPACE);
        return buf;
    }

	public static class Provider extends IQProvider<GroupLogNumIQ> {

//		@Override
//		public IQ parseIQ(XmlPullParser xp) throws Exception {
//
//			GroupLogNumIQ result = new GroupLogNumIQ();
//
//			while (true) {
//				int n = xp.next();
//				if (n == XmlPullParser.START_TAG) {
//					if ("result".equals(xp.getName())) {
//						result.setResult(xp.getAttributeValue(0));
//					}
//				} else if (n == XmlPullParser.END_TAG) {
//					if (GroupLogNumIQ.IQ_ELEMENT.equals(xp.getName())) {
//						break;
//					}
//				}
//			}
//
//			return result;
//		}

        @Override
        public GroupLogNumIQ parse(XmlPullParser xp, int var2) throws Exception{

            GroupLogNumIQ result = new GroupLogNumIQ();

            while (true) {
                int n = xp.next();
                if (n == XmlPullParser.START_TAG) {
                    if ("result".equals(xp.getName())) {
                        result.setResult(xp.getAttributeValue(0));
                    }
                } else if (n == XmlPullParser.END_TAG) {
                    if (GroupLogNumIQ.IQ_ELEMENT.equals(xp.getName())) {
                        break;
                    }
                }
            }
            return result;
        }
	}
}
