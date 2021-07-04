package com.sweet;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

public class FileTransferIQProvider extends IQProvider<FileTransferIQ> {

	//@Override
    @Deprecated
	public IQ parseIQ(XmlPullParser xp) throws Exception {
		FileTransferIQ result = new FileTransferIQ();
		while (true) {
			int n = xp.next();
			if (n == XmlPullParser.START_TAG) {
				if ("success".equals(xp.getName())) {
					result.setFileName(xp.getAttributeValue(0));
					result.setStatus(xp.nextText());

				}
			} else if (n == XmlPullParser.END_TAG) {
				if (FileTransferIQ.IQ_ELEMENT.equals(xp.getName())) {
					break;
				}
			}
		}
		return result;
	}

    @Override
    public FileTransferIQ parse(XmlPullParser xp, int var2) throws Exception{
        FileTransferIQ result = new FileTransferIQ();
        while (true) {
            int n = xp.next();
            if (n == XmlPullParser.START_TAG) {
                if ("success".equals(xp.getName())) {
                    result.setFileName(xp.getAttributeValue(0));
                    result.setStatus(xp.nextText());

                }
            } else if (n == XmlPullParser.END_TAG) {
                if (FileTransferIQ.IQ_ELEMENT.equals(xp.getName())) {
                    break;
                }
            }
        }
        return result;
    }
}
