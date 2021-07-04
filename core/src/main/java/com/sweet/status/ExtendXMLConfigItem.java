package com.sweet.status;

/**
 * 本地xml配置文件封装类 liuh 2014-4-16上午10:50:42
 */
public class ExtendXMLConfigItem {
	private String name;// 名称字符串
	private String url;// 连接地址
	private String icon;// 链接图标
	private int sn;// 显示顺序
	private boolean enable;// 是否启动

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getSn() {
		return sn;
	}

	public void setSn(int sn) {
		this.sn = sn;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

}
