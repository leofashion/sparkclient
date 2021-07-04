package com.sweet.test;

import org.jivesoftware.smack.packet.IQ;
public class SampleIQ extends IQ
{
    String condition;
    String value;
    protected SampleIQ(String childElementName, String childElementNamespace,
        String condition, String value)
    {
        super(childElementName, childElementNamespace);
        this.condition = condition;
        this.value = value;
    }
    /**
     * 最关键的实现类
     */
    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(
        IQChildElementXmlStringBuilder xml)
    {
        xml.attribute("condition", condition);
        xml.rightAngleBracket();
        xml.element("extraElement", "value");
        return xml;
    }
    public static void main(String[] args)
    {
        IQ iq = new SampleIQ("query", "emcc.jiyq", "what", "elemeng");
        System.out.println(iq.toString());
    }
}
