package com.sweet;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.spark.util.log.Log;
import org.xmlpull.v1.XmlPullParser;

public class ServerPortIQ extends IQ {

	public static final String IQ_ELEMENT = "iangel";
	public static final String IQ_NAMESPACE = "ica:serverport";

    public ServerPortIQ() {
        super("serverport",IQ_NAMESPACE);
    }
	private String port;

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder( IQChildElementXmlStringBuilder buf )
    {
        if(buf.length()!=0) {
            buf.rightAngleBracket();
        }

        buf.append("<").append(IQ_ELEMENT).append(" xmlns=\"").append(IQ_NAMESPACE).append("\">");
        buf.append("</").append(IQ_ELEMENT).append(">");

        return buf;
    }

	public static class Provider extends IQProvider<ServerPortIQ> {

        @Override
        public ServerPortIQ parse(XmlPullParser xp, int var2) throws Exception{

            ServerPortIQ result = new ServerPortIQ();

            while (true) {
                int n = xp.next();
                if (n == XmlPullParser.START_TAG) {
                    if ("port".equals(xp.getName())) {
                        result.setPort(xp.nextText());
                    }
                } else if (n == XmlPullParser.END_TAG) {
                    if (ServerPortIQ.IQ_ELEMENT.equals(xp.getName())) {
                        break;
                    }
                }
            }
            return result;
        }
	}
}
