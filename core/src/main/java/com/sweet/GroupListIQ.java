package com.sweet;

import java.util.ArrayList;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.xmlpull.v1.XmlPullParser;

public class GroupListIQ extends IQ {

	public static final String IQ_ELEMENT = "iangel";
	public static final String IQ_NAMESPACE = "ica:grouplist";

	private ArrayList<String> groups = new ArrayList<String>();
    public GroupListIQ() {
        super("grouplist",IQ_NAMESPACE);
    }
	public ArrayList<String> getGroups() {
		return groups;
	}

	public void setGroups(ArrayList<String> groups) {
		this.groups = groups;
	}

	public void addGroup(String group) {
		groups.add(group);
	}

//	@Override
    @Deprecated
	public String getChildElementXMLsweet() {
		StringBuffer sb = new StringBuffer();
		sb.append("<").append(IQ_ELEMENT).append(" xmlns=\"").append(IQ_NAMESPACE).append("\">");
		sb.append("</").append(IQ_ELEMENT).append(">");
		// <grouplist xmlns="iq:grouplist"><gname>1</gname><gname>2</gname></iangel>
		return sb.toString();
	}

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder( IQChildElementXmlStringBuilder buf )
    {
        if(buf.length()!=0) {
            buf.rightAngleBracket();
        }
        buf.element(IQ_ELEMENT,"").attribute("xmlns",IQ_NAMESPACE);
        return buf;
    }

	public static class Provider extends IQProvider<GroupListIQ> {

		@Override
		public GroupListIQ parse(XmlPullParser xp, int var2) throws Exception {

			GroupListIQ result = new GroupListIQ();

			while (true) {
				int n = xp.next();
				if (n == XmlPullParser.START_TAG) {
					if ("gname".equals(xp.getName())) {
						result.addGroup(xp.nextText());
					}
				} else if (n == XmlPullParser.END_TAG) {
					if (GroupListIQ.IQ_ELEMENT.equals(xp.getName())) {
						break;
					}
				}
			}

			return result;
		}
	}

}
