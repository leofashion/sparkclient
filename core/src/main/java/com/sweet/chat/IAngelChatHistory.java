package com.sweet.chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.TimerTask;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.jdesktop.swingx.calendar.DateUtils;
import org.jivesoftware.Spark;
import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.BackgroundPanel;
import org.jivesoftware.spark.ui.ContactGroup;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.ui.VCardPanel;
import org.jivesoftware.spark.util.BrowserLauncher;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.filetransfer.transfer.Downloads;
import org.jivesoftware.sparkimpl.plugin.transcripts.ChatTranscript;
import org.jivesoftware.sparkimpl.plugin.transcripts.ChatTranscripts;
import org.jivesoftware.sparkimpl.plugin.transcripts.HistoryMessage;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

/**
 * 查看聊天记录 liuh 2013-12-24下午3:48:19
 */
public class IAngelChatHistory implements TreeSelectionListener {
	private final String timeFormat = "HH:mm:ss";
	private String dateFormat = ((SimpleDateFormat) SimpleDateFormat
			.getDateInstance(SimpleDateFormat.FULL)).toPattern();
	private SimpleDateFormat notificationDateFormatter;
	private SimpleDateFormat messageDateFormatter;
	private JTree jtree;
	private JPanel rightPanel;
	private DefaultMutableTreeNode root = new DefaultMutableTreeNode(
			Res.getString("system.messages"));// 根节点，点击时显示系统消息

	public IAngelChatHistory() {
		notificationDateFormatter = new SimpleDateFormat(dateFormat);
		messageDateFormatter = new SimpleDateFormat(timeFormat);
	}

	public void showFrame() throws XmppStringprepException {
		createNodes();
		jtree = new JTree(root);

		jtree.addTreeSelectionListener(this);

		JScrollPane treeView = new JScrollPane(jtree);
		rightPanel = new JPanel();

		JScrollPane rightView = new JScrollPane(rightPanel);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

		splitPane.setLeftComponent(treeView);
		splitPane.setRightComponent(rightView);

		JFrame frame = new JFrame(Res.getString("title.chatHistory"));//
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);// 设置窗体关闭方式
		frame.setSize(800, 500);

		frame.setIconImage(SparkManager.getApplicationImage().getImage());

		Dimension displaySize = Toolkit.getDefaultToolkit().getScreenSize();// 获得显示器大小对象
		Dimension frameSize = frame.getSize();// 获得窗口大小对象
		if (frameSize.width > displaySize.width)
			frameSize.width = displaySize.width;// 窗口的宽度不能大于显示器的宽度
		if (frameSize.height > displaySize.height)
			frameSize.height = displaySize.height;// 窗口的高度不能大于显示器的高度
		frame.setLocation((displaySize.width - frameSize.width) / 2,
				(displaySize.height - frameSize.height) / 2);// 设置窗口居中显示器显示

		frame.add(splitPane);
		frame.setVisible(true);
		splitPane.setDividerLocation(0.2);

		//TODO  有修改
//		showHistory("admin@" + SparkManager.getSessionManager().getServerAddress());
		showHistory(JidCreate.bareFrom("admin@" + SparkManager.getSessionManager().getServerAddress()));
	}

	@Override
    public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) jtree.getLastSelectedPathComponent();
		if (node == null) {
            return;
        }
		if (node.isRoot()) {// 点击根目录显示系统消息
		    //TODO 有修改
            try {
                showHistory(JidCreate.bareFrom("admin@" + SparkManager.getSessionManager().getServerAddress()));
            } catch (XmppStringprepException xmppStringprepException) {
                xmppStringprepException.printStackTrace();
            }
//			showHistory("admin@" + SparkManager.getSessionManager().getServerAddress());
		}
		Object nodeInfo = node.getUserObject();
		rightPanel.removeAll();
		if (node.isLeaf()) {// 没有子节点
			ContactItem ci = (ContactItem) nodeInfo;
			showHistory(ci.getJid());
		} else {
			return;
		}
		rightPanel.revalidate();
	}

	// 初始化信息人信息树
	private void createNodes() {
		ContactList contactList = SparkManager.getWorkspace().getContactList();

		for (ContactGroup group : contactList.getContactGroups()) {
			String groupName = group.getGroupName();

			if (Res.getString("group.offline").equals(groupName)) {
				continue;
			}
			DefaultMutableTreeNode dmtorg = initDeptNode(groupName);
			final List<ContactItem> offlineContacts = new ArrayList<ContactItem>(
					group.getOfflineContacts());
			Collections.sort(offlineContacts, ContactList.ContactItemComparator);
			for (ContactItem item : offlineContacts) {
				dmtorg.insert(new DefaultMutableTreeNode(item), 0);
			}
			for (ContactItem item : group.getContactItems()) {
				dmtorg.insert(new DefaultMutableTreeNode(item), 0);
			}
		}
	}

	private DefaultMutableTreeNode initDeptNode(String deptName) {// 公司领导：：资产财务部
		String[] pnames = deptName.split("::");
		DefaultMutableTreeNode rootp = root;
		for (String pname : pnames) {
			DefaultMutableTreeNode node = getChildNode(rootp, pname);
			if (node == null) {
				node = new DefaultMutableTreeNode(pname);
				rootp.add(node);
			}
			rootp = node;
		}
		return rootp;
	}

	private DefaultMutableTreeNode getChildNode(DefaultMutableTreeNode pNode, String name) {
		DefaultMutableTreeNode result = null;
		if (pNode.getChildCount() > 0) {
			Enumeration<DefaultMutableTreeNode> en = pNode.children();
			while (en.hasMoreElements()) {
				DefaultMutableTreeNode childNode = en.nextElement();
				Object linkValue = childNode.getUserObject();
				if (linkValue instanceof ContactItem) {
					continue;
				}
				if (name.equals(linkValue.toString())) {
					result = childNode;
					break;
				}
			}
		}
		return result;
	}

	public void visitAllNodes(TreeNode node) {
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				visitAllNodes(n);
			}
		}
	}
    public String unxmlFromstr(String string) {
        string = string.replaceAll("<", "&lt;");
        string = string.replaceAll(">", "&gt;");
        return string;
    }
	public void showHistory(final BareJid jid) {
		SwingWorker transcriptLoader = new SwingWorker() {
			@Override
            public Object construct() {
//				String bareJID = StringUtils.parseBareAddress(jid);
				return ChatTranscripts.getChatTranscript(jid);
			}

			@Override
            public void finished() {
				final JPanel mainPanel = new BackgroundPanel();

				mainPanel.setLayout(new BorderLayout());
				// add search text input
				final JPanel topPanel = new BackgroundPanel();
				topPanel.setLayout(new GridBagLayout());

				final VCardPanel vacardPanel = new VCardPanel(jid);
				final JTextField searchField = new JTextField(25);
				searchField.setText(Res.getString("message.search.for.history"));
				searchField.setToolTipText(Res.getString("message.search.for.history"));
				searchField.setForeground((Color) UIManager.get("TextField.lightforeground"));

				topPanel.add(vacardPanel,
						new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST,
								GridBagConstraints.NONE, new Insets(1, 5, 1, 1), 0, 0));
				topPanel.add(searchField,
						new GridBagConstraints(1, 0, GridBagConstraints.REMAINDER, 1, 1.0, 1.0,
								GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
								new Insets(1, 1, 6, 1), 0, 0));

				mainPanel.add(topPanel, BorderLayout.NORTH);
				// final JFrame frame = new JFrame(Res.getString("title.history.for", SparkManager.getUserManager().getUserNicknameFromJID(jid)));

				final JEditorPane window = new JEditorPane();
				window.setEditorKit(new HTMLEditorKit());
				window.addHyperlinkListener(new HyperlinkListener() {
					@Override
                    public void hyperlinkUpdate(HyperlinkEvent e) {
						if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
							String link = e.getDescription();
							if (link.startsWith("http://")) {
								try {
									BrowserLauncher.openURL(link);
								} catch (Exception browserException) {
									Log.error("Error launching browser:", browserException);
								}
							} else {
								File file = new File(e.getDescription());
								if (!file.exists()) {
									JOptionPane.showMessageDialog(null, "文件不存在或者已被转移!", "提示",
											JOptionPane.ERROR_MESSAGE);
									return;
								}
								if (!Desktop.isDesktopSupported())
									return;
								Desktop dt = Desktop.getDesktop();
								try {
									dt.open(file);
								} catch (IOException ex) {
								}
							}
						}
					}
				});

				window.setBackground(Color.white);
				final JScrollPane pane = new JScrollPane(window);
				pane.getVerticalScrollBar().setBlockIncrement(200);
				pane.getVerticalScrollBar().setUnitIncrement(20);

				mainPanel.add(pane, BorderLayout.CENTER);
				rightPanel.setLayout(new BorderLayout());
				rightPanel.add(mainPanel, BorderLayout.CENTER);
				rightPanel.setSize(600, 400);

				// frame.setIconImage(SparkRes.getImageIcon(SparkRes.HISTORY_16x16).getImage());
				// frame.getContentPane().setLayout(new BorderLayout());
				//
				// frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
				//
				// frame.pack();
				// frame.setSize(600, 400);
				window.setCaretPosition(0);
				window.requestFocus();
				// GraphicUtils.centerWindowOnScreen(frame);
				// frame.setVisible(true);

				window.setEditable(false);

				final StringBuilder builder = new StringBuilder();
				builder.append("<html><body><table cellpadding=0 cellspacing=0>");

				final TimerTask transcriptTask = new TimerTask() {
					public void run() {
						ChatTranscript transcript = (ChatTranscript) get();

						// reduce the size of our transcript to the last 5000Messages
						// This will prevent JavaOutOfHeap Errors
						ArrayList<HistoryMessage> toobig = (ArrayList<HistoryMessage>) transcript
								.getMessage(null);

						// Get the Maximum size from settingsfile
                        //TODO 有修改
//						int maxsize = SettingsManager.getLocalPreferences().getMaximumHistory();
						int maxsize = SettingsManager.getLocalPreferences().getMaxCurrentHistorySize();
						if (toobig.size() > maxsize) {
							transcript = new ChatTranscript();

							for (int i = toobig.size() - 1; i >= toobig.size() - maxsize; --i) {
								transcript.addHistoryMessage(toobig.get(i));
							}
						}

						final List<HistoryMessage> list = transcript
								.getMessage(Res.getString("message.search.for.history").equals(
										searchField.getText()) ? null : searchField.getText());

						final String personalNickname = SparkManager.getUserManager().getNickname();
						Date lastPost = null;
						String lastPerson = null;
						boolean initialized = false;
						for (HistoryMessage message : list) {
							String color = "blue";

							//TODO 修改
							Jid from = message.getFrom();
                            String nickname = null;
                            try {
                                nickname = SparkManager.getUserManager()
                                        .getUserNicknameFromJID(JidCreate.bareFrom(message.getFrom()));
                            } catch (XmppStringprepException e) {
                                e.printStackTrace();
                            }
//									.getUserNicknameFromJID(message.getFrom());
							String body = org.jivesoftware.spark.util.StringUtils
									.escapeHTMLTags(message.getBody());
							if (nickname.equals(message.getFrom())) {
							    //TODO 修改
								Jid otherJID = message.getFrom();
                                Jid myJID = SparkManager.getSessionManager().getUserBareAddress();
//								String otherJID = StringUtils.parseBareAddress(message.getFrom());
//								String myJID = SparkManager.getSessionManager().getBareAddress();

								if (otherJID.equals(myJID)) {
									nickname = personalNickname;
								} else {
									nickname = myJID.toString();
//									nickname = StringUtils.parseName(nickname);
								}
							}

							//TODO 修改
							if (!from
									.equals(SparkManager.getSessionManager().getUserBareAddress())) {
								color = "red";
							}
//							if (!StringUtils.parseBareAddress(from)
//									.equals(SparkManager.getSessionManager().getBareAddress())) {
//								color = "red";
//							}

							long lastPostTime = lastPost != null ? lastPost.getTime() : 0;

							int diff = 0;
							if (DateUtils.getDaysDiff(lastPostTime,
									message.getDate().getTime()) != 0) {
								diff = DateUtils.getDaysDiff(lastPostTime,
										message.getDate().getTime());
							} else {
								diff = DateUtils.getDayOfWeek(lastPostTime)
										- DateUtils.getDayOfWeek(message.getDate().getTime());
							}

							if (diff != 0) {
								if (initialized) {
									builder.append("<tr><td><br></td></tr>");
								}
								builder.append("<tr><td colspan=2><font size=4 color=gray><b><u>")
										.append(notificationDateFormatter.format(message.getDate()))
										.append("</u></b></font></td></tr>");
								lastPerson = null;
								initialized = true;
							}

							String value = "[" + messageDateFormatter.format(message.getDate())
									+ "]&nbsp;&nbsp;  ";

							boolean newInsertions = lastPerson == null
									|| !lastPerson.equals(nickname);
							if (newInsertions) {
								builder.append("<tr valign=top><td colspan=2 nowrap>");
								builder.append("<font size=4 color='").append(color)
										.append("'><b>");
								builder.append(nickname);
								builder.append("</b></font>");
								builder.append("</td></tr>");
							}

							builder.append("<tr valign=top><td align=left nowrap>");
							builder.append(value);
							builder.append("</td><td align=left>");
							if (body.startsWith("_FILE_{")) {
								builder.append("发送文件:");
								String strFile = body.substring(7, body.length() - 1);
								File file = new File(strFile);
								builder.append(
										"<a href=\"" + strFile + "\">" + file.getName() + "</a>");
							} else {
								String imsg = message.getBody();
								if (imsg.startsWith("<imsg ") && imsg.endsWith("</imsg>")) {
									org.dom4j.Document xmldoc;
									try {
										xmldoc = DocumentHelper.parseText(imsg);
										org.dom4j.Element root = xmldoc.getRootElement();
										String type = root.attributeValue("type");
										String linkid = root.attributeValue("linkid");
										String msg = org.jivesoftware.spark.util.StringUtils
												.escapeHTMLTags(root.getTextTrim());

										StringBuffer sb = new StringBuffer();
										sb.append(Spark.getOAServerURL());
										sb.append("&type=" + type);
										sb.append("&linkid=" + linkid);
										String link = sb.toString();

										builder.append(msg + " ");
										builder.append("<a href=\"" + link + "\">进入查看</a>");
									} catch (Exception e) {
										Log.error("插入链接时:", e);
									}
								} else if (imsg.indexOf("<cancelFile ") != -1) {
									try {
										org.dom4j.Document xmldoc = DocumentHelper.parseText(imsg);
										org.dom4j.Element root = xmldoc.getRootElement();
										String fname = root.attributeValue("fileName");
										String type = root.attributeValue("type");
										if (type.equals("cancel")) {// 取消
											builder.append(Res.getString("cancelTrasfer.tip") + "\""
													+ fname + "\"");
										} else {// 转离线发送
											builder.append(Res.getString("cancelTransfer.off.tip")
													+ "\"" + fname + "\"");
										}
									} catch (Exception e) {
										Log.error(e.getMessage());
									}
								} else if (imsg.indexOf("<offlinefile ") != -1) {
									try {
										int ofsp1 = imsg.indexOf("<offlinefile ");
										int ofsp2 = 0;
										if (ofsp1 >= 0) {
											ofsp2 = imsg.indexOf("</offlinefile>", ofsp1);
										}
										if (ofsp1 >= 0 && ofsp2 > 0) {//
											imsg = imsg.substring(ofsp1, ofsp2 + 14);
										}
										org.dom4j.Document xmldoc = DocumentHelper.parseText(imsg);
										org.dom4j.Element root = xmldoc.getRootElement();
										String fname = root.attributeValue("fname");
										String filePath = Downloads.getDownloadDirectory() + "\\"
												+ fname.substring(fname.lastIndexOf("-") + 1);
										builder.append(Res.getString("message.offlineFile.tip")
												+ "<a href=\"" + filePath + "\">" + filePath
												+ "</a>");
									} catch (Exception e) {
										Log.error(e.getMessage());
									}
								} else if (imsg.indexOf("<fontFormate ") != -1) {
									org.dom4j.Document xmldoc;
									try {
										xmldoc = DocumentHelper.parseText(imsg);
										org.dom4j.Element root = xmldoc.getRootElement();
										String name = root.attributeValue("name");
										int size = Integer.valueOf(root.attributeValue("size"));
										String fcolor = root.attributeValue("color");
										fcolor = fcolor.replaceAll(";", ",");
										if (root.elementText("imsg") != null) {
											int sp1 = imsg.indexOf("<imsg ");
											if (sp1 != -1) {
												int sp2 = imsg.indexOf("</imsg>");
												String tmpstr = imsg.substring(sp1, sp2 + 7);
												xmldoc = DocumentHelper.parseText(tmpstr);
												org.dom4j.Element root2 = xmldoc.getRootElement();
												String type = root2.attributeValue("type");
												String linkid = root2.attributeValue("linkid");
												String msg = org.jivesoftware.spark.util.StringUtils
														.escapeHTMLTags(root2.getTextTrim());

												StringBuffer sb = new StringBuffer();
												sb.append(Spark.getOAServerURL());
												sb.append("&type=" + type);
												sb.append("&linkid=" + linkid);
												String link = sb.toString();
												builder.append(msg + " ");
												builder.append("<a href=\"" + link + "\">进入查看</a>");
											}

										} else if (imsg.indexOf("_FILE_{") != -1) {
											builder.append("发送文件:");
											int sp1 = imsg.indexOf("_FILE_");
											int sp2 = imsg.lastIndexOf("</fontFormate>");
											String strFile = imsg.substring(sp1 + 7, sp2 - 1);
											File file = new File(strFile);
											builder.append("<a href=\"" + strFile + "\">"
													+ file.getName() + "</a>");
										} else {
											builder.append("<tr><td style=\"font-size:" + size
													+ ";font-family:" + name + ";color:rgb("
													+ fcolor + ");\" >");
											builder.append(root.getTextTrim());
											builder.append("</span></td></tr>");
										}
									} catch (DocumentException e) {
										String tmpxml = imsg.substring(0, imsg.indexOf(">") + 1);
										try {
											xmldoc = DocumentHelper
													.parseText(tmpxml + "</fontFormate>");
											org.dom4j.Element root = xmldoc.getRootElement();
											String name = root.attributeValue("name");
											int size = Integer.valueOf(root.attributeValue("size"));
											String fcolor = root.attributeValue("color");
											fcolor = fcolor.replaceAll(";", ",");
											builder.append("<tr><td style=\"font-size:" + size
													+ ";font-family:" + name + ";color:rgb("
													+ fcolor + ");\" >");
											String str = imsg.substring(imsg.indexOf(">") + 1,
													imsg.indexOf("</fontFormate>"));

											//TODO 修改
											builder.append(unxmlFromstr(str));
//											builder.append(org.jivesoftware.spark.util.StringUtils
//													.unxmlFromstr(str));
											builder.append("</span></td></tr>");
										} catch (DocumentException e1) {
										}
									}
								} else {
									builder.append(body);
								}
							}

							builder.append("</td></tr>");

							lastPost = message.getDate();
							lastPerson = nickname;
						}
						builder.append("</table></body></html>");

						// Handle no history
						if (transcript.getMessages().size() == 0) {
							builder.append("<b>").append(Res.getString("message.no.history.found"))
									.append("</b>");
						}

						window.setText(builder.toString());
						builder.replace(0, builder.length(), "");
						int lengthOfChat = window.getDocument().getLength();
						window.setCaretPosition(lengthOfChat);
					}
				};



				searchField.addKeyListener(new KeyListener() {
					@Override
					public void keyTyped(KeyEvent e) {
					}

					@Override
					public void keyReleased(KeyEvent e) {
						if (e.getKeyChar() == KeyEvent.VK_ENTER) {
							TaskEngine.getInstance().schedule(transcriptTask, 10);
							searchField.requestFocus();
						}
					}

					@Override
					public void keyPressed(KeyEvent e) {

					}
				});
				searchField.addFocusListener(new FocusListener() {
					@Override
                    public void focusGained(FocusEvent e) {
						searchField.setText("");
						searchField.setForeground((Color) UIManager.get("TextField.foreground"));
					}

					@Override
                    public void focusLost(FocusEvent e) {
						searchField
								.setForeground((Color) UIManager.get("TextField.lightforeground"));
						searchField.setText(Res.getString("message.search.for.history"));
					}
				});

				TaskEngine.getInstance().schedule(transcriptTask, 10);

				// frame.addWindowListener(new WindowAdapter() {
				// public void windowClosing(WindowEvent e) {
				// window.setText("");
				// }
				// @Override
				// public void windowClosed(WindowEvent e) {
				// frame.removeWindowListener(this);
				// frame.dispose();
				// transcriptTask.cancel();
				// topPanel.remove(vacardPanel);
				// }
				// });
			}
		};

		transcriptLoader.start();
	}

}
