package com.sweet.iangelgroup;

import com.sweet.DownFileTransferIQ;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.spark.util.log.Log;
import org.xmlpull.v1.XmlPullParser;

public class GroupFileAddIQ extends IQ {

	public static final String IQ_ELEMENT = "iangel";
	public static final String IQ_NAMESPACE = "ica:groupfileadd";

    public GroupFileAddIQ() {
        super("groupfileadd",IQ_NAMESPACE);
    }
	private String groupId = null;
	private String groupName = null;
	private GroupFile file;

	private boolean success = false;

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

	public GroupFile getFile() {
		return file;
	}

	public void setFile(GroupFile file) {
		this.file = file;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

//	@Override
//	public String getChildElementXML() {
//		StringBuffer sb = new StringBuffer();
//		sb.append("<").append(IQ_ELEMENT).append(" xmlns=\"").append(IQ_NAMESPACE).append("\">");
//		sb.append("<group name=\"" + groupName + "\" ");
//		sb.append(" fname=\"" + file.getFileName() + "\"");
//		sb.append(" fsize=\"" + file.getFileSize() + "\"");
//		sb.append(" />");
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
        fb.attribute("fname",""+file.getFileName());
        fb.attribute("fsize",""+file.getFileSize());

        buf.element(IQ_ELEMENT,fb).attribute("xmlns",IQ_NAMESPACE);
        return buf;
    }

	public static class Provider extends IQProvider<GroupFileAddIQ> {

//		@Override
//		public IQ parseIQ(XmlPullParser xp) throws Exception {
//
//			GroupFileAddIQ result = new GroupFileAddIQ();
//
//			while (true) {
//				int n = xp.next();
//				if (n == XmlPullParser.START_TAG) {
//					if ("result".equals(xp.getName())) {
//						result.setSuccess(Boolean.parseBoolean(xp.getText()));
//					}
//				} else if (n == XmlPullParser.END_TAG) {
//					if (GroupFileAddIQ.IQ_ELEMENT.equals(xp.getName())) {
//						break;
//					}
//				}
//			}
//
//			return result;
//		}

        @Override
        public GroupFileAddIQ parse(XmlPullParser xp, int var2) throws Exception{

            GroupFileAddIQ result = new GroupFileAddIQ();

            while (true) {
                int n = xp.next();
                if (n == XmlPullParser.START_TAG) {
                    if ("result".equals(xp.getName())) {
                        result.setSuccess(Boolean.parseBoolean(xp.getText()));
                    }
                } else if (n == XmlPullParser.END_TAG) {
                    if (GroupFileAddIQ.IQ_ELEMENT.equals(xp.getName())) {
                        break;
                    }
                }
            }
            return result;
        }
	}

}
