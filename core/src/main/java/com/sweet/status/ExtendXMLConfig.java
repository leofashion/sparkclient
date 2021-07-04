package com.sweet.status;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jivesoftware.spark.util.log.Log;

import com.thoughtworks.xstream.XStream;

/**
 * 本地xml配置文件封装类 liuh 2014-4-16上午10:50:42
 */
public class ExtendXMLConfig {
	private static File extendXMLConfig = new File("D:/aaa/", "ExtendXMLConfig.xml");
	private static XStream xstream = new XStream();

	private ExtendXMLConfig() {

	}

	static {
		xstream.alias("links", List.class);
		xstream.alias("link", ExtendXMLConfigItem.class);
	}

	/**
	 * 获取初始化信息封装到list中
	 * 
	 * @return
	 */
	public static List<ExtendXMLConfigItem> load() {
		List<ExtendXMLConfigItem> list = null;
		if (extendXMLConfig.exists()) {
			try {
				list = (List<ExtendXMLConfigItem>) xstream.fromXML(new FileReader(extendXMLConfig));
			} catch (Exception e) {
				xstream.alias("list", List.class);
				xstream.alias("org.jivesoftware.sparkimpl.plugin.iangel.status.ExtendXMLConfigItem",
						ExtendXMLConfigItem.class);
				try {
					list = (List<ExtendXMLConfigItem>) xstream
							.fromXML(new FileReader(extendXMLConfig));
				} catch (Exception e1) {
					Log.error(e1);
				}
			}
		}

		if (list == null) {
			list = new ArrayList<ExtendXMLConfigItem>();
		}

		Collections.sort(list, new Comparator<ExtendXMLConfigItem>() {
			public int compare(final ExtendXMLConfigItem a, final ExtendXMLConfigItem b) {
				return (String.valueOf(a.getSn()).compareToIgnoreCase(String.valueOf(b.getSn())));
			}
		});

		return list;
	}

	/**
	 * 保存配置文件
	 * 
	 * @param list
	 */
	public static void save(List<ExtendXMLConfigItem> list) {
		xstream.alias("links", List.class);
		xstream.alias("link", ExtendXMLConfigItem.class);

		try {
			xstream.toXML(list, new FileWriter(extendXMLConfig));
		} catch (IOException e) {
			Log.error("Could not save custom messages.", e);
		}
	}

	/*
	 * public static void main(String[] args){ List<ExtendXMLConfigItem> list = new ArrayList<ExtendXMLConfigItem>(); for(int i = 0; i < 3; i++){
	 * ExtendXMLConfigItem exci = new ExtendXMLConfigItem(); exci.setEnable(false); exci.setIcon("iconicon"); exci.setName("namename"); exci.setSn(10);
	 * exci.setUrl("urlurlurl"); list.add(exci); } save(list);
	 * 
	 * List<ExtendXMLConfigItem> nList = load(); for(int i = 0; i < nList.size(); i++){ ExtendXMLConfigItem e = nList.get(i); System.out.println(e.getName()); }
	 * }
	 */
}
