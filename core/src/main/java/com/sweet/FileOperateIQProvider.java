package com.sweet;

import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.ParserUtils;
import org.xmlpull.v1.XmlPullParser;

/**
 * liuh 2014-3-4下午3:57:18
 */
public class FileOperateIQProvider extends IQProvider {



	//TODO 复制源码 需修改
    @Override
    public Element parse(XmlPullParser xmlPullParser, int i) throws Exception {
        ParserUtils.assertAtStartTag(xmlPullParser);
        int initialDepth = xmlPullParser.getDepth();
        Element e = this.parse(xmlPullParser, initialDepth);
        ParserUtils.forwardToEndTagOfDepth(xmlPullParser, initialDepth);
        return e;
    }
}
