package com.sweet.util;

/**
 * 字符串工具包
 * 
 * @author chenhy
 *
 */
public class StringUtils {

	public static boolean isEmpty(String string) {
		return string == null || string.length() == 0;
	}
	 public static String ToSBC(String input)
	    {
	        //半角转全角：
	        char[] c=input.toCharArray();
	        for (int i = 0; i < c.length; i++)
	        {
	            if (c[i]==32)
	            {
	                c[i]=(char)12288;
	                continue;
	            }
	            if (c[i]<127)
	                c[i]=(char)(c[i]+65248);
	        }
	        return new String(c);
	    }
}
