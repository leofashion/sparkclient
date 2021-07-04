//package com.sweet.iangelgroup;
//
//import java.awt.Color;
//import java.awt.Component;
//import java.awt.Cursor;
//import java.awt.FlowLayout;
//import java.awt.GridBagConstraints;
//import java.awt.GridBagLayout;
//import java.awt.Insets;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.MouseEvent;
//import java.awt.event.MouseListener;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//
//import javax.swing.ImageIcon;
//import javax.swing.JLabel;
//import javax.swing.JOptionPane;
//import javax.swing.JPanel;
//import javax.swing.JScrollPane;
//import javax.swing.UIManager;
//
//import org.jivesoftware.resource.Res;
//import org.jivesoftware.resource.SparkRes;
//import org.jivesoftware.smack.XMPPException;
//import org.jivesoftware.smack.util.StringUtils;
//import org.jivesoftware.smackx.Form;
//import org.jivesoftware.smackx.muc.HostedRoom;
//import org.jivesoftware.smackx.muc.MultiUserChat;
//import org.jivesoftware.spark.SparkManager;
//import org.jivesoftware.spark.Workspace;
//import org.jivesoftware.spark.component.RolloverButton;
//import org.jivesoftware.spark.component.VerticalFlowLayout;
//import org.jivesoftware.spark.component.tabbedPane.SparkTab;
//import org.jivesoftware.spark.component.tabbedPane.SparkTabbedPane;
//import org.jivesoftware.spark.component.tabbedPane.SparkTabbedPaneListener;
//import org.jivesoftware.spark.ui.ChatRoomNotFoundException;
//import org.jivesoftware.spark.ui.rooms.GroupChatRoom;
//import org.jivesoftware.spark.util.SwingWorker;
//import org.jivesoftware.spark.util.UIComponentRegistry;
//import org.jivesoftware.spark.util.log.Log;
//import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
//import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
//
///*
// * 群列表
// * @author chenhy
// */
//public class IAngelGroupServices {
//
//	private JPanel jpanel;
//	private JPanel toolbarPanel;
//	private JPanel listPanel;
//	private JScrollPane jscrollPane;
//	private final JLabel progressBar = new JLabel();
//
//	public void addTabToSpark() {
//
//		Workspace workspace = SparkManager.getWorkspace();
//		final SparkTabbedPane tabbedPane = workspace.getWorkspacePane();
//
//		jpanel = new JPanel();
//		jpanel.setSize(tabbedPane.getSize());
//		jpanel.setLayout(new VerticalFlowLayout());
//		jpanel.setBackground((Color) UIManager.get("ContactItem.background"));
//
//		initToolbarPanel();
//		jpanel.add(toolbarPanel);
//
//		initListPanel();
//		jpanel.add(listPanel);
//
//		jscrollPane = new JScrollPane(jpanel);
//		jscrollPane.getVerticalScrollBar().setBlockIncrement(200);
//		jscrollPane.getVerticalScrollBar().setUnitIncrement(20);
//
//		ImageIcon tabIcon = SparkRes.getImageIcon(SparkRes.TAB_MYGROUPS_DARK);
//		tabbedPane.addTab(null, tabIcon, jscrollPane, "群");
//		tabbedPane.addSparkTabbedPaneListener(new SparkTabbedPaneListener() {
//
//			@Override
//			public void tabSelected(SparkTab tab, Component component, int index) {
//				if (index == 3) {// 群
//					initRoomList();
//				}
//			}
//
//			@Override
//			public void tabRemoved(SparkTab tab, Component component, int index) {
//			}
//
//			@Override
//			public void tabAdded(SparkTab tab, Component component, int index) {
//			}
//
//			@Override
//			public boolean canTabClose(SparkTab tab, Component component) {
//				return false;
//			}
//
//			@Override
//			public void allTabsRemoved() {
//
//			}
//		});
//
//	}
//
//	private void initToolbarPanel() {
//		toolbarPanel = new JPanel();
//		toolbarPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
//		toolbarPanel.setBackground((Color) UIManager.get("ContactItem.background"));
//
//		RolloverButton createButton = new RolloverButton("创建");
//		createButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				createRoom();
//			}
//		});
//
//		ImageIcon icon = new ImageIcon(
//				getClass().getClassLoader().getResource("images/ajax-loader.gif"));
//		progressBar.setIcon(icon);
//		progressBar.setVisible(false);
//
//		toolbarPanel.add(progressBar);
//		toolbarPanel.add(createButton);
//	}
//
//	public void displayProgressBar() {
//		progressBar.setVisible(true);
//	}
//
//	public void hiddenProgressBar() {
//		progressBar.setVisible(false);
//	}
//
//	private void initListPanel() {
//		listPanel = new JPanel();
//		listPanel.setBackground(Color.white);
//		listPanel.setLayout(new GridBagLayout());
//
//		initRoomList();
//	}
//
//	public static boolean initing = false;
//
//	public void initRoomList() {
//		if (initing) {
//			return;
//		}
//		initing = true;
//		displayProgressBar();
//		listPanel.removeAll();
//		SwingWorker worker = new SwingWorker() {
//			Collection<HostedRoom> rooms;
//
//			public Object construct() {
//				try {
//					rooms = getRoomList(GroupHelper.group());
//				} catch (Exception e) {
//					Log.error("Unable to retrieve list of rooms.", e);
//				}
//
//				return "OK";
//			}
//
//			public void finished() {
//				try {
//					if (rooms != null) {
//						for (HostedRoom room : rooms) {
//
//							String roomName = room.getName();
//							String roomJID = room.getJid();
//
//							GroupUserIQ gu = GroupHelper.getGroupUsers(roomName);
//
//							String curJid = SparkManager.getSessionManager().getJID();
//							curJid = StringUtils.parseBareAddress(curJid);
//							if (!gu.has(curJid)) {
//								continue;
//							}
//
//							String owner = "";
//							if (gu.getOwners().size() > 0) {
//								owner = gu.getOwners().get(0);
//							}
//							owner = SparkManager.getUserManager().getNickname(owner);
//							addRoomToTable(roomJID, roomName, owner, 0);
//						}
//					}
//				} catch (Exception e) {
//					Log.error("Error setting up GroupChatTable", e);
//				}
//				hiddenProgressBar();
//				initing = false;
//			}
//		};
//
//		worker.start();
//	}
//
//	private static Collection<HostedRoom> getRoomList(String serviceName) throws Exception {
//		return MultiUserChat.getHostedRooms(SparkManager.getConnection(), serviceName);
//	};
//
//	private void createRoom() {
//		GroupCreationDialog mucRoomDialog = new GroupCreationDialog();
//		final MultiUserChat groupChat = mucRoomDialog.createGroupChat(SparkManager.getMainWindow(),
//				GroupHelper.group());
//		LocalPreferences pref = SettingsManager.getLocalPreferences();
//
//		if (null != groupChat) {
//
//			// Join Room
//			try {
//				GroupChatRoom room = UIComponentRegistry.createGroupChatRoom(groupChat);
//
//				groupChat.create(pref.getNickname());
//
//				// Send Form
//				Form form = groupChat.getConfigurationForm().createAnswerForm();
//				if (mucRoomDialog.isPasswordProtected()) {
//					String password = mucRoomDialog.getPassword();
//					room.setPassword(password);
//					form.setAnswer("muc#roomconfig_passwordprotectedroom", true);
//					form.setAnswer("muc#roomconfig_roomsecret", password);
//				}
//				form.setAnswer("muc#roomconfig_roomname", mucRoomDialog.getRoomName());
//				String subject = mucRoomDialog.getRoomTopic();
//
//				if (subject != null && subject.length() > 0) {
//					form.setAnswer("muc#roomconfig_roomdesc", subject);
//				}
//
//				form.setAnswer("muc#roomconfig_persistentroom", true);
//
//				List<String> owners = new ArrayList<String>();
//				owners.add(SparkManager.getSessionManager().getBareAddress());
//				form.setAnswer("muc#roomconfig_roomowners", owners);
//
//				form.setAnswer("muc#roomconfig_membersonly", true);
//				form.setAnswer("muc#roomconfig_allowinvites", true);
//				form.setAnswer("muc#roomconfig_enablelogging", true);
//
//				// new DataFormDialog(groupChat, form);
//				groupChat.sendConfigurationForm(form);
//
//				if (subject != null && subject.length() > 0) {
//					groupChat.changeSubject(subject);
//				}
//
//				String roomTitle = mucRoomDialog.getRoomName();
//				String jid = SparkManager.getSessionManager().getBareAddress();
//				GroupHelper.setLoginTime(roomTitle, jid);
//
//				addRoomToTable(groupChat.getRoom(), StringUtils.parseName(groupChat.getRoom()),
//						SparkManager.getUserManager().getNickname(), 0);
//
//			} catch (XMPPException e1) {
//				Log.error("Error creating new room.", e1);
//				JOptionPane.showMessageDialog(jpanel, Res.getString("message.room.creation.error"),
//						Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
//			}
//		}
//	}
//
//	private void addRoomToTable(String jid, String roomName, String owner, int unreadnumber) {
//
//		Insets insets = new Insets(5, 5, 5, 5);
//		int row = listPanel.getComponentCount() / 4;
//
//		JLabel img = new JLabel(SparkRes.getImageIcon(SparkRes.GROUP_ICON));
//		listPanel.add(img, new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
//				GridBagConstraints.NONE, insets, 0, 0));
//
//		JLabel size = new JLabel(owner);
//		listPanel.add(size, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
//				GridBagConstraints.NONE, insets, 0, 0));
//
//		final JLabel name = new JLabel(roomName);
//		name.addMouseListener(new MouseListener() {
//			@Override
//			public void mouseClicked(MouseEvent e) {
//				String roomName = ((JLabel) e.getSource()).getText();
//				final String roomJID = roomName + "@" + GroupHelper.group();
//				try {
//					SparkManager.getChatManager().getChatContainer().getChatRoom(roomJID);
//				} catch (ChatRoomNotFoundException e1) {
//					GroupUtils.joinConferenceOnSeperateThread(roomName, roomJID, null);
//				}
//			}
//
//			@Override
//			public void mouseEntered(MouseEvent e) {
//				((JLabel) e.getSource()).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//			}
//
//			@Override
//			public void mouseExited(MouseEvent e) {
//				((JLabel) e.getSource()).setCursor(Cursor.getDefaultCursor());
//			}
//
//			@Override
//			public void mousePressed(MouseEvent e) {
//
//			}
//
//			@Override
//			public void mouseReleased(MouseEvent e) {
//
//			}
//		});
//		listPanel.add(name, new GridBagConstraints(2, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
//				GridBagConstraints.NONE, insets, 0, 0));
//
//		int num = 0;
//		num = GroupHelper.getUnReadNum(roomName, SparkManager.getSessionManager().getBareAddress());
//		if (num == 0) {
//			JLabel numLabel = new JLabel("");
//			listPanel.add(numLabel, new GridBagConstraints(3, row, 1, 1, 1.0, 0.0,
//					GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
//		} else {
//			JLabel numLabel = new JLabel(" " + num + " ");
//			numLabel.setOpaque(true);
//			numLabel.setBackground(Color.RED);
//			numLabel.setForeground(Color.WHITE);
//			numLabel.addMouseListener(new MouseListener() {
//
//				@Override
//				public void mouseReleased(MouseEvent e) {
//
//				}
//
//				@Override
//				public void mousePressed(MouseEvent e) {
//
//				}
//
//				@Override
//				public void mouseExited(MouseEvent e) {
//					((JLabel) e.getSource()).setCursor(Cursor.getDefaultCursor());
//				}
//
//				@Override
//				public void mouseEntered(MouseEvent e) {
//					((JLabel) e.getSource())
//							.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//				}
//
//				@Override
//				public void mouseClicked(MouseEvent e) {
//					String roomName = name.getText();
//					final String roomJID = roomName + "@" + GroupHelper.group();
//					try {
//						SparkManager.getChatManager().getChatContainer().getChatRoom(roomJID);
//					} catch (ChatRoomNotFoundException e1) {
//						GroupUtils.joinConferenceOnSeperateThread(roomName, roomJID, null);
//					}
//
//				}
//			});
//			listPanel.add(numLabel, new GridBagConstraints(3, row, 1, 1, 1.0, 0.0,
//					GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
//		}
//
//	}
//}
