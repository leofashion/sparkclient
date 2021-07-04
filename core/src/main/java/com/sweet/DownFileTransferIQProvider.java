package com.sweet;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.spark.util.log.Log;
import org.xmlpull.v1.XmlPullParser;

/**
 * 文件下载
 *
 * @author Administrator
 *
 */
public class DownFileTransferIQProvider extends IQProvider<DownFileTransferIQ> {


    @Override
    public DownFileTransferIQ parse(XmlPullParser xp, int var2) throws Exception{

        DownFileTransferIQ result = new DownFileTransferIQ();

        while (true) {
            int n = xp.next();
            if (n == XmlPullParser.START_TAG) {
                if ("fd".equals(xp.getName())) {
                    try {
                        result.setFsize(Long.parseLong(xp.getAttributeValue(0)));
                        result.setFileName(xp.getAttributeValue(1));
                        result.setData(xp.nextText());
                    } catch (Exception e) {
                        Log.error("DownFileTransferIQProvider:" + e.getMessage());
                    }
                }
            } else if (n == XmlPullParser.END_TAG) {
                if (DownFileTransferIQ.IQ_ELEMENT.equals(xp.getName())) {
                    break;
                }
            }
        }
        return result;
    }
}
