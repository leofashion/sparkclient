package com.sweet;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.util.XmlStringBuilder;

/**
 * offline download
 */
public class DownFileTransferIQ extends IQ {

	public static final String IQ_ELEMENT = "iangel";
	public static final String IQ_NAMESPACE = "ica:filetransferdown";

	private String fileName = "";
	private long fsize;
	private String data = "";

	private long position = 0L;
	private int size = OffLineFileTransferComm.lenOfOneTime;

    public DownFileTransferIQ() {
        super("downfile",IQ_NAMESPACE);
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

	public long getPosition() {
		return position;
	}

	public void setPosition(long position) {
		this.position = position;
	}

	public long getFsize() {
		return fsize;
	}

	public void setFsize(long fsize) {
		this.fsize = fsize;
	}

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder( IQChildElementXmlStringBuilder buf )
    {
//        if(buf.length()!=0) {
            buf.rightAngleBracket();
//        }

        buf.append("<").append(IQ_ELEMENT).append(" xmlns=\"").append(IQ_NAMESPACE).append("\">");
        buf.append("<fd name=\"" + fileName + "\" position=\"" + position + "\" size=\"" + size
            + "\" />");
        buf.append("</").append(IQ_ELEMENT).append(">");
        /*
            <iangel xmlns="ica:filetransferdown" >
                <fd name="fileName" position="" size="" />
            </iangel>

		 * 调用 ： <fu name=””>数据</fu> 返回 : <success>ok或 fail</success>
		 *
		 * 下载 ： <fd name=”” position = “” size=””/> 返回 ： <fd fsize="" >数据</fd>
		 */
        return buf;
    }
}
