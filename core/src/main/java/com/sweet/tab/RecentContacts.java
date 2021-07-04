package com.sweet.tab;

/**
 * 最近联系人tab用到 liuh 2014-1-9下午1:35:16
 */
public class RecentContacts {
	private String uname;// 汉字名
	private String jid;// 登录号@服务地址

	public RecentContacts(String uname, String jid) {
		this.uname = uname;
		this.jid = jid;
	}

	public RecentContacts() {
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getJid() {
		return jid;
	}

	public void setJid(String jid) {
		this.jid = jid;
	}

	/**
	 * 重写toString()方法用于让节点显示汉字人名
	 */
	public String toString() {
		return this.uname;
	}

}
