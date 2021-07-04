package com.sweet.tab;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.Workspace;
import org.jivesoftware.spark.component.tabbedPane.SparkTab;
import org.jivesoftware.spark.component.tabbedPane.SparkTabbedPane;
import org.jivesoftware.spark.component.tabbedPane.SparkTabbedPaneListener;
import org.jivesoftware.spark.util.log.Log;
import org.jxmpp.jid.impl.JidCreate;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * liuh 2014-1-9上午11:04:09
 */
public class IAngelContactsTab {

	private JPanel jpanel;

	public void addTabToSpark() {
		// Get Workspace UI from SparkManager
		Workspace workspace = SparkManager.getWorkspace();
		// Retrieve the Tabbed Pane from the WorkspaceUI.
		final SparkTabbedPane tabbedPane = workspace.getWorkspacePane();
		// Add own Tab.

		jpanel = new JPanel();
		jpanel.setSize(tabbedPane.getSize());
		jpanel.setBackground((Color) UIManager.get("ContactItem.background"));

		jpanel.setLayout(new BorderLayout());
		jpanel.add(initRecentContacts(), BorderLayout.WEST);
		JScrollPane jscrollPane = new JScrollPane(jpanel);
		tabbedPane.addTab(Res.getString("recent.contacts"), SparkRes.getImageIcon(SparkRes.TAB_CHAT_DARK), jscrollPane,
				Res.getString("recent.contacts"));
		tabbedPane.addSparkTabbedPaneListener(new SparkTabbedPaneListener() {

			@Override
			public void tabSelected(SparkTab tab, Component component, int index) {
				if (index == 2) {// 最近联系人tab
					jpanel.removeAll();
					jpanel.add(initRecentContacts());
				}
			}

			@Override
			public void tabRemoved(SparkTab tab, Component component, int index) {
			}

			@Override
			public void tabAdded(SparkTab tab, Component component, int index) {
			}

			@Override
			public boolean canTabClose(SparkTab tab, Component component) {
				return false;
			}

			@Override
			public void allTabsRemoved() {

			}
		});
	}

	/**
	 * 初始化最近联系人列表以树的形式展开
	 *
	 * @return
	 */
	private JTree initRecentContacts() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("联系人列表");
		try {
			File file = new File(SparkManager.getUserDirectory(), "transcripts/conversations.xml");
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(file);
			NodeList list = doc.getElementsByTagName("user");
			for (int i = 0; i < list.getLength(); i++) {
				Node node = list.item(i);
				String jid = node.getTextContent();
				if (jid.startsWith("admin")) {
					continue;
				}
				//TODO getUserNicknameFromJID方法原传值是字符串，现修改，不知道是否正确
				String name = SparkManager.getUserManager().getUserNicknameFromJID(
                    JidCreate.bareFrom(jid));
				if (name.length() > 12) {// 过滤掉人员删除，或离职(本地的联系人保存文件任然存在)
					continue;
				}
				root.add(new DefaultMutableTreeNode(new RecentContacts(name, jid)));
			}
		} catch (FileNotFoundException fnfe) {
			Log.error("最近联系人文件不存在!");
		} catch (Exception e) {
			Log.error("recent contacts file load fail!", e);
		}
		final JTree jtree = new JTree(root);
		jtree.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				if (me.getClickCount() == 2) {// 鼠标双击
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) jtree
							.getLastSelectedPathComponent();
					if (node.isLeaf()) {// 没有子节点
						RecentContacts rc = (RecentContacts) node.getUserObject();
						SparkManager.getChatManager().activateChat(rc.getJid(), rc.getUname());
					}
				}
			}
		});
		return jtree;
	}

}
