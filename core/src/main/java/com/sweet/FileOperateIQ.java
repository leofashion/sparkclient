package com.sweet;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.util.XmlStringBuilder;

/**
 * liuh 2014-3-4下午3:47:43
 */
public class FileOperateIQ extends IQ {
	public static final String IQ_ELEMENT = "iangel";
	public static final String IQ_NAMESPACE = "ica:fileoperate";

	private String action;
	private String name;

	private String actValue;

    public FileOperateIQ() {
        super("fileoperate",IQ_NAMESPACE);
    }
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getActValue() {
		return actValue;
	}

	public void setActValue(String actValue) {
		this.actValue = actValue;
	}


	/**
	 * ica:fileoperate <fo action="getSize|remove|...." name=""/> <fo name="">size|true|false</fo>
	 */

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder( IQChildElementXmlStringBuilder buf )
    {
//        if(buf.length()!=0) {
            buf.rightAngleBracket();
//        }

        buf.append("<").append(IQ_ELEMENT).append(" xmlns=\"").append(IQ_NAMESPACE).append("\">");
        buf.append("<fo action=\"" + action + "\"  name=\"" + name + "\" />");
        buf.append("</").append(IQ_ELEMENT).append(">");
        return buf;
    }

}
