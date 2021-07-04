package com.sweet;

import java.util.List;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.util.XmlStringBuilder;

/**
 * liuh 2014-2-17下午4:26:24
 */
public class FileTransferIQ extends IQ {

	public static final String IQ_ELEMENT = "iangel";
	public static final String IQ_NAMESPACE = "ica:filetransferup";

	private String fileName = "";
	private String data = "";
	private String status = null;

    public FileTransferIQ() {
        super("filetransferup",IQ_NAMESPACE);
    }

    public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder( IQChildElementXmlStringBuilder buf )
    {
        if(buf.length()!=0) {
            buf.rightAngleBracket();
        }

        XmlStringBuilder fb = buf.element("fu",data);
        fb.attribute("name",""+fileName);

        buf.element(IQ_ELEMENT,fb).attribute("xmlns",IQ_NAMESPACE);
//        buf.element("size", Long.toString( size ));
//        buf.element("filename", fileName);
        return buf;
    }

	public String getChildElementXMLSweet() {
		StringBuffer sb = new StringBuffer();
		sb.append("<").append(IQ_ELEMENT).append(" xmlns=\"").append(IQ_NAMESPACE).append("\">");
		sb.append("<fu name=\"" + fileName + "\">" + data + "</fu>");
		sb.append("</").append(IQ_ELEMENT).append(">");
		System.out.println(sb.toString());
		return sb.toString();

		/*
		 * 调用 ： <fu name=””>数据</fu> 返回 : <success name="">ok或 fail</success>
		 *
		 * 下载 ： <fd name=”” position = “” size=””/> 返回 ： <fd fsize="" name="">数据</fd>
		 */
	}





}
