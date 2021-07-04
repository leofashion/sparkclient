package com.sweet.iangelgroup;

import com.sweet.DownFileTransferIQ;
import java.util.ArrayList;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.spark.util.log.Log;
import org.xmlpull.v1.XmlPullParser;

public class GroupFileIQ extends IQ {

	public static final String IQ_ELEMENT = "iangel";
	public static final String IQ_NAMESPACE = "ica:groupfile";
    public GroupFileIQ() {
        super("groupfile",IQ_NAMESPACE);
    }
	private String groupId = null;
	private String groupName = null;

	private ArrayList<GroupFile> files = new ArrayList<GroupFile>();

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

	public ArrayList<GroupFile> getFiles() {
		return files;
	}

	public void setFiles(ArrayList<GroupFile> files) {
		this.files = files;
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
        return buf;
    }

	public static class Provider extends IQProvider<GroupFileIQ> {

//		@Override
//		public IQ parseIQ(XmlPullParser xp) throws Exception {
//
//			GroupFileIQ result = new GroupFileIQ();
//			ArrayList<GroupFile> files = new ArrayList<GroupFile>();
//
//			while (true) {
//				int n = xp.next();
//				if (n == XmlPullParser.START_TAG) {
//					if ("file".equals(xp.getName())) {
//						String name = xp.getAttributeValue(0);
//						String size = xp.getAttributeValue(1);
//						String jid = xp.getAttributeValue(2);
//						GroupFile file = new GroupFile();
//						file.setFileName(name);
//						file.setJid(jid);
//						file.setFileSize(Long.parseLong(size));
//						files.add(file);
//					}
//				} else if (n == XmlPullParser.END_TAG) {
//					if (GroupFileIQ.IQ_ELEMENT.equals(xp.getName())) {
//						break;
//					}
//				}
//			}
//			result.setFiles(files);
//			return result;
//		}

        @Override
        public GroupFileIQ parse(XmlPullParser xp, int var2) throws Exception{

            GroupFileIQ result = new GroupFileIQ();
            ArrayList<GroupFile> files = new ArrayList<GroupFile>();

            while (true) {
                int n = xp.next();
                if (n == XmlPullParser.START_TAG) {
                    if ("file".equals(xp.getName())) {
                        String name = xp.getAttributeValue(0);
                        String size = xp.getAttributeValue(1);
                        String jid = xp.getAttributeValue(2);
                        GroupFile file = new GroupFile();
                        file.setFileName(name);
                        file.setJid(jid);
                        file.setFileSize(Long.parseLong(size));
                        files.add(file);
                    }
                } else if (n == XmlPullParser.END_TAG) {
                    if (GroupFileIQ.IQ_ELEMENT.equals(xp.getName())) {
                        break;
                    }
                }
            }
            result.setFiles(files);
            return result;
        }
	}

}
