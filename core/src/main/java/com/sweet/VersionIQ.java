//package com.sweet;
//
//import org.jivesoftware.smack.packet.IQ;
//import org.jivesoftware.smack.util.XmlStringBuilder;
//
//public class VersionIQ extends IQ {
//
//
//    private String name;
//    private String version;
//    private String os;
//
//
//    public String getName() {
//        return this.name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getVersion() {
//        return this.version;
//    }
//
//    public void setVersion(String version) {
//        this.version = version;
//    }
//
//    public String getOs() {
//        return this.os;
//    }
//
//    public void setOs(String os) {
//        this.os = os;
//    }
//
//    public VersionIQ() {
//        super("version","version");
//
//    }
//
//    @Override
//    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder iqChildElementXmlStringBuilder) {
//
//        StringBuilder buf = new StringBuilder();
//        buf.append("<query xmlns=\"jabber:iq:version\">");
//        if (this.name != null) {
//            buf.append("<name>").append(this.name).append("</name>");
//        }
//
//        if (this.version != null) {
//            buf.append("<version>").append(this.version).append("</version>");
//        }
//
//        if (this.os != null) {
//            buf.append("<os>").append(this.os).append("</os>");
//        }
//
//        buf.append("</query>");
//
//        iqChildElementXmlStringBuilder.append(new XmlStringBuilder(buf.toString()));
//
//        return iqChildElementXmlStringBuilder;
//
//    }
//}
