///**
// * $RCSfile: ,v $
// * $Revision: $
// * $Date: $
// *
// * Copyright (C) 2004-2011 Jive Software. All rights reserved.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.sweet.iangelgroup;
//
//import java.awt.BorderLayout;
//import java.awt.GridBagConstraints;
//import java.awt.GridBagLayout;
//import java.awt.Insets;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.beans.PropertyChangeEvent;
//import java.beans.PropertyChangeListener;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//
//import javax.swing.AbstractAction;
//import javax.swing.Action;
//import javax.swing.DefaultListModel;
//import javax.swing.JButton;
//import javax.swing.JComboBox;
//import javax.swing.JComponent;
//import javax.swing.JDialog;
//import javax.swing.JFrame;
//import javax.swing.JLabel;
//import javax.swing.JList;
//import javax.swing.JOptionPane;
//import javax.swing.JPanel;
//import javax.swing.JPopupMenu;
//import javax.swing.JScrollPane;
//import javax.swing.JTextField;
//
//import org.jivesoftware.resource.Res;
//import org.jivesoftware.resource.SparkRes;
//import org.jivesoftware.smack.XMPPException;
//import org.jivesoftware.smack.util.StringUtils;
//import org.jivesoftware.smackx.bookmark.BookmarkedConference;
//import org.jivesoftware.spark.ChatManager;
//import org.jivesoftware.spark.ChatNotFoundException;
//import org.jivesoftware.spark.SparkManager;
//import org.jivesoftware.spark.component.TitlePanel;
//import org.jivesoftware.spark.ui.ChatRoom;
//import org.jivesoftware.spark.ui.RosterPickList;
//import org.jivesoftware.spark.ui.rooms.GroupChatRoom;
//import org.jivesoftware.spark.util.ModelUtil;
//import org.jivesoftware.spark.util.ResourceUtils;
//import org.jivesoftware.spark.util.SwingWorker;
//import org.jivesoftware.spark.util.log.Log;
//import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
//
//final class GroupInvitationDialog extends JPanel {
//	private static final long serialVersionUID = -8588678602429200581L;
//	private JLabel roomsLabel = new JLabel();
//	private JComponent roomsField = new JTextField();
//	private JTextField textRoomsField;
//	private JComboBox comboRoomsField;
//
//	private JLabel messageLabel = new JLabel();
//	private JTextField messageField = new JTextField();
//
//	private JLabel inviteLabel = new JLabel();
//
//	private DefaultListModel invitedUsers = new DefaultListModel();
//	private JList invitedUserList = new JList(invitedUsers);
//
//	private GroupUserIQ hasUsers = null;
//
//	private JDialog dlg;
//
//	private GridBagLayout gridBagLayout1 = new GridBagLayout();
//
//	public GroupInvitationDialog(boolean adhoc) {
//		if (adhoc) {
//			roomsField = new JTextField();
//			textRoomsField = (JTextField) roomsField;
//		} else {
//			roomsField = new JComboBox();
//			comboRoomsField = (JComboBox) roomsField;
//			comboRoomsField.setEditable(true);
//			comboRoomsField.addActionListener(new ActionListener() {
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					// get selected bookmark and persist it:
//					BookmarkedConference bookmarkedConf = null;
//					Object bookmarkedConfItem = comboRoomsField.getSelectedItem();
//					if (bookmarkedConfItem instanceof GroupItem) {
//						bookmarkedConf = ((GroupItem) bookmarkedConfItem).getBookmarkedConf();
//						SettingsManager.getLocalPreferences()
//								.setDefaultBookmarkedConf(bookmarkedConf.getJid());
//						SettingsManager.saveSettings();
//					}
//				}
//			});
//		}
//		setLayout(gridBagLayout1);
//
//		add(roomsLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
//				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
//		add(roomsField, new GridBagConstraints(1, 0, 3, 1, 1.0, 0.0, GridBagConstraints.WEST,
//				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
//
//		add(messageLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
//				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
//		add(messageField, new GridBagConstraints(1, 1, 3, 1, 1.0, 0.0, GridBagConstraints.WEST,
//				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
//
//		JButton browseButton = new JButton();
//		ResourceUtils.resButton(browseButton, Res.getString("button.roster"));
//
//		add(browseButton, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
//				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
//
//		browseButton.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent actionEvent) {
//				RosterPickList browser = new RosterPickList();
//				Collection<String> col = browser.showRoster(dlg);
//
//				String ignore = "";
//				for (String aCol : col) {
//					String jid = aCol;
//					String text = SparkManager.getUserManager().getNickname(jid);
//					MyListItem li = new MyListItem(jid, text);
//					if (hasUsers.has(jid)) {
//						ignore += text + ",";
//						continue;
//					}
//
//					if (!invitedUsers.contains(li)) {
//						invitedUsers.addElement(li);
//					}
//				}
//				if (ignore.length() > 0) {
//					JOptionPane.showMessageDialog(dlg,
//							"群中已有人员:" + ignore.substring(0, ignore.length() - 1), "忽略提示",
//							JOptionPane.INFORMATION_MESSAGE);
//				}
//			}
//		});
//
//		add(inviteLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
//				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
//		add(new JScrollPane(invitedUserList), new GridBagConstraints(1, 3, 3, 1, 1.0, 1.0,
//				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
//
//		// Add Resource Utils
//		ResourceUtils.resLabel(messageLabel, messageField, "邀请消息:");
//		ResourceUtils.resLabel(roomsLabel, roomsField, "群名称:");
//		inviteLabel.setText(Res.getString("label.invited.users"));
//
//		// Add Listener to list
//		invitedUserList.addMouseListener(new MouseAdapter() {
//			public void mouseReleased(MouseEvent mouseEvent) {
//				if (mouseEvent.isPopupTrigger()) {
//					showPopup(mouseEvent);
//				}
//			}
//
//			public void mousePressed(MouseEvent mouseEvent) {
//				if (mouseEvent.isPopupTrigger()) {
//					showPopup(mouseEvent);
//				}
//			}
//		});
//	}
//
//	private void showPopup(MouseEvent e) {
//		final JPopupMenu popup = new JPopupMenu();
//		final int index = invitedUserList.locationToIndex(e.getPoint());
//
//		Action removeAction = new AbstractAction() {
//			private static final long serialVersionUID = 7837533277115442942L;
//
//			public void actionPerformed(ActionEvent e) {
//				invitedUsers.remove(index);
//			}
//		};
//
//		removeAction.putValue(Action.NAME, Res.getString("menuitem.remove"));
//		removeAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_DELETE));
//
//		popup.add(removeAction);
//
//		popup.show(invitedUserList, e.getX(), e.getY());
//
//	}
//
//	private void fillRoomsUI(Collection<BookmarkedConference> rooms, String adHocRoomName) {
//		if (textRoomsField != null) {
//			if (adHocRoomName.indexOf("@") > 0) {
//				adHocRoomName = adHocRoomName.substring(0, adHocRoomName.indexOf("@"));
//			}
//
//			hasUsers = GroupHelper.getGroupUsers(adHocRoomName);
//
//			textRoomsField.setEnabled(false);
//			textRoomsField.setText(adHocRoomName);
//			messageField.setText("邀请您加入(" + adHocRoomName + ")群.");
//		}
//		if (comboRoomsField != null) {
//			// comboRoomsField.setSelectedIndex(-1);
//			GroupItem bookmarkedConf = null;
//			final String bookmarkedConfJid = SettingsManager.getLocalPreferences()
//					.getDefaultBookmarkedConf();
//
//			for (BookmarkedConference room : rooms) {
//				final GroupItem ci = new GroupItem(room);
//				if (bookmarkedConfJid != null
//						&& bookmarkedConfJid.equalsIgnoreCase(ci.getBookmarkedConf().getJid())) {
//					bookmarkedConf = ci;
//				}
//				comboRoomsField.addItem(ci);
//			}
//			if (bookmarkedConf != null) {
//				comboRoomsField.setSelectedItem(bookmarkedConf);
//			}
//		}
//	}
//
//	private String getSelectedRoomName() {
//		String roomTitle = null;
//
//		if (textRoomsField != null) {
//			roomTitle = textRoomsField.getText();
//		}
//		if (comboRoomsField != null) {
//			roomTitle = comboRoomsField.getSelectedItem().toString();
//		}
//
//		if (roomTitle.indexOf("@") < 0) {
//			roomTitle += "@" + GroupHelper.group();
//		}
//
//		return roomTitle;
//	}
//
//	private BookmarkedConference getSelectedBookmarkedConference() {
//		BookmarkedConference bookmarkedConf = null;
//		if (comboRoomsField != null) {
//			Object bookmarkedConfItem = comboRoomsField.getSelectedItem();
//			if (bookmarkedConfItem instanceof GroupItem) {
//				bookmarkedConf = ((GroupItem) bookmarkedConfItem).getBookmarkedConf();
//			}
//		}
//		return bookmarkedConf;
//	}
//
//	public void inviteUsersToRoom(final String serviceName, Collection<BookmarkedConference> rooms,
//			String adHocRoomName, Collection<String> jids) {
//		fillRoomsUI(rooms, adHocRoomName);
//
//		JFrame parent = SparkManager.getChatManager().getChatContainer().getChatFrame();
//		if (parent == null || !parent.isVisible()) {
//			parent = SparkManager.getMainWindow();
//		}
//
//		// Add jids to user list
//		if (jids != null) {
//			for (Object jid : jids) {
//				invitedUsers.addElement(jid);
//			}
//		}
//
//		final JOptionPane pane;
//
//		TitlePanel titlePanel;
//
//		// Create the title panel for this dialog
//		titlePanel = new TitlePanel("群邀请", "邀请人员加入到群", SparkRes.getImageIcon(SparkRes.BLANK_IMAGE),
//				true);
//
//		// Construct main panel w/ layout.
//		final JPanel mainPanel = new JPanel();
//		mainPanel.setLayout(new BorderLayout());
//		mainPanel.add(titlePanel, BorderLayout.NORTH);
//
//		// The user should only be able to close this dialog.
//		Object[] options = { Res.getString("invite"), Res.getString("cancel"), "删除" };
//		pane = new JOptionPane(this, JOptionPane.PLAIN_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION,
//				null, options, options[0]);
//
//		mainPanel.add(pane, BorderLayout.CENTER);
//
//		final JOptionPane p = new JOptionPane();
//
//		dlg = p.createDialog(parent, "群");
//		dlg.setModal(false);
//
//		dlg.pack();
//		dlg.setSize(500, 450);
//		dlg.setResizable(true);
//		dlg.setContentPane(mainPanel);
//		dlg.setLocationRelativeTo(parent);
//
//		PropertyChangeListener changeListener = new PropertyChangeListener() {
//			public void propertyChange(PropertyChangeEvent e) {
//				String value = (String) pane.getValue();
//				if (Res.getString("cancel").equals(value)) {
//					pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
//					dlg.dispose();
//				} else if ("删除".equals(value)) {
//					Object[] values = invitedUserList.getSelectedValues();
//					int size = values == null ? 0 : values.length;
//					if (size == 0) {
//						JOptionPane.showMessageDialog(dlg, "请先选择要取消的邀请人员!",
//								Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
//						pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
//						return;
//					}
//
//					for (int i = size - 1; i > -1; i--) {
//						invitedUsers.removeElement(values[i]);
//					}
//					pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
//				} else if (Res.getString("invite").equals(value)) {
//					final String roomTitle = getSelectedRoomName();
//					final BookmarkedConference selectedBookmarkedConf = getSelectedBookmarkedConference();
//					int size = invitedUserList.getModel().getSize();
//
//					if (size == 0) {
//						JOptionPane.showMessageDialog(dlg, "请先选择邀请的人员!",
//								Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
//						pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
//						return;
//					}
//
//					if (!ModelUtil.hasLength(roomTitle)) {
//						JOptionPane.showMessageDialog(dlg,
//								Res.getString("message.no.room.to.join.error"),
//								Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
//						pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
//						return;
//					}
//					String roomName = "";
//
//					// Add all rooms the user is in to list.
//					ChatManager chatManager = SparkManager.getChatManager();
//					for (ChatRoom chatRoom : chatManager.getChatContainer().getChatRooms()) {
//						if (chatRoom instanceof GroupChatRoom) {
//							GroupChatRoom groupRoom = (GroupChatRoom) chatRoom;
//							if (groupRoom.getRoomname().equals(roomTitle)) {
//								roomName = groupRoom.getMultiUserChat().getRoom();
//								break;
//							}
//						}
//					}
//					String message = messageField.getText();
//					final String messageText = message != null ? message
//							: Res.getString("message.please.join.in.conference");
//
//					if (invitedUsers.getSize() > 0) {
//						invitedUserList.setSelectionInterval(0, invitedUsers.getSize() - 1);
//					}
//
//					GroupChatRoom chatRoom;
//					try {
//						chatRoom = SparkManager.getChatManager().getGroupChat(roomName);
//					} catch (ChatNotFoundException e1) {
//						dlg.setVisible(false);
//						final List<String> jidList = new ArrayList<String>();
//						Object[] jids = invitedUserList.getSelectedValues();
//						for (Object jid : jids) {
//							jidList.add(((MyListItem) jid).getValue());
//						}
//
//						SwingWorker worker = new SwingWorker() {
//							public Object construct() {
//								try {
//									Thread.sleep(15);
//								} catch (InterruptedException e2) {
//									Log.error(e2);
//								}
//								return "ok";
//							}
//
//							public void finished() {
//								try {
//									if (selectedBookmarkedConf == null) {
//										GroupUtils.createPrivateConference(serviceName, messageText,
//												roomTitle, jidList);
//									} else {
//										GroupUtils.joinConferenceOnSeperateThread(
//												selectedBookmarkedConf.getName(),
//												selectedBookmarkedConf.getJid(),
//												selectedBookmarkedConf.getPassword(), messageText,
//												jidList);
//									}
//								} catch (XMPPException e2) {
//									JOptionPane.showMessageDialog(pane, GroupUtils.getReason(e2),
//											Res.getString("title.error"),
//											JOptionPane.ERROR_MESSAGE);
//								}
//							}
//						};
//
//						worker.start();
//						pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
//						return;
//					}
//
//					pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
//					dlg.dispose();
//
//					Object[] values = invitedUserList.getSelectedValues();
//
//					for (Object object : values) {
//						String jid = ((MyListItem) object).getValue();
//						chatRoom.getMultiUserChat().invite(jid, message != null ? message
//								: Res.getString("message.please.join.in.conference"));
//						String nickname = SparkManager.getUserManager().getUserNicknameFromJID(jid);
//						GroupHelper.setLoginTime(StringUtils.parseName(roomTitle), jid);
//						chatRoom.getTranscriptWindow().insertNotificationMessage("邀请 " + nickname,
//								ChatManager.NOTIFICATION_COLOR);
//					}
//				}
//			}
//		};
//
//		pane.addPropertyChangeListener(changeListener);
//
//		dlg.setVisible(true);
//		dlg.toFront();
//		dlg.requestFocus();
//	}
//
//	class MyListItem {
//		private String text;
//		private String value;
//
//		public MyListItem(String value, String text) {
//			this.text = text;
//			this.value = value;
//		}
//
//		public String getValue() {
//			return value;
//		}
//
//		@Override
//		public String toString() {
//			return text;
//		}
//
//		@Override
//		public boolean equals(Object obj) {
//			if (obj instanceof MyListItem) {
//				MyListItem target = (MyListItem) obj;
//				return value.equals(target.getValue());
//			} else {
//				return value.equals(obj.toString());
//			}
//		}
//	}
//}
