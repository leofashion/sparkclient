//package com.sweet.iangelgroup;
//
//import org.jivesoftware.resource.SparkRes;
//import org.jivesoftware.resource.Res;
//import org.jivesoftware.smack.XMPPException;
//import org.jivesoftware.smackx.muc.MultiUserChat;
//import org.jivesoftware.spark.SparkManager;
//import org.jivesoftware.spark.component.TitlePanel;
//import org.jivesoftware.spark.util.ModelUtil;
//import org.jivesoftware.spark.util.ResourceUtils;
//import org.jivesoftware.spark.util.log.Log;
//
//import com.sweet.util.RoomInfoAgent;
//
//import javax.swing.JDialog;
//import javax.swing.JLabel;
//import javax.swing.JOptionPane;
//import javax.swing.JPanel;
//import javax.swing.JTextField;
//
//import java.awt.BorderLayout;
//import java.awt.Component;
//import java.awt.GridBagConstraints;
//import java.awt.GridBagLayout;
//import java.awt.Insets;
//import java.beans.PropertyChangeEvent;
//import java.beans.PropertyChangeListener;
//
//public class GroupCreationDialog extends JPanel {
//
//	private static final long serialVersionUID = 5128378645865107839L;
//
//	private JLabel nameLabel = new JLabel();
//	private JTextField nameField = new JTextField();
//
//	private JLabel subjectLabel = new JLabel();
//	private JTextField subjectField = new JTextField();
//
//	private GridBagLayout gridBagLayout1 = new GridBagLayout();
//	private MultiUserChat groupChat = null;
//
//	public GroupCreationDialog() {
//		try {
//			jbInit();
//		} catch (Exception e) {
//			Log.error(e);
//		}
//	}
//
//	private void jbInit() throws Exception {
//		this.setLayout(gridBagLayout1);
//		this.add(nameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
//				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
//		this.add(nameField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
//				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
//
//		this.add(subjectLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
//				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
//		this.add(subjectField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
//				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
//
//		ResourceUtils.resLabel(nameLabel, nameField, Res.getString("sweet.group.name"));
//		ResourceUtils.resLabel(subjectLabel, subjectField, Res.getString("sweet.group.subject"));
//	}
//
//	public MultiUserChat createGroupChat(Component parent, final String serviceName) {
//		final JOptionPane pane;
//		final JDialog dlg;
//
//		TitlePanel titlePanel;
//
//		// Create the title panel for this dialog
//		titlePanel = new TitlePanel(Res.getString("sweet.group.formtitle"), "",
//				SparkRes.getImageIcon(SparkRes.BLANK_24x24), true);
//
//		// Construct main panel w/ layout.
//		final JPanel mainPanel = new JPanel();
//		mainPanel.setLayout(new BorderLayout());
//		mainPanel.add(titlePanel, BorderLayout.NORTH);
//
//		// The user should only be able to close this dialog.
//		Object[] options = { Res.getString("sweet.group.save"),
//				Res.getString("sweet.group.close") };
//		pane = new JOptionPane(this, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null,
//				options, options[0]);
//
//		mainPanel.add(pane, BorderLayout.CENTER);
//
//		JOptionPane p = new JOptionPane();
//		dlg = p.createDialog(parent, Res.getString("sweet.group.title"));
//		dlg.pack();
//		dlg.setSize(400, 250);
//		dlg.setContentPane(mainPanel);
//		dlg.setLocationRelativeTo(parent);
//
//		PropertyChangeListener changeListener = new PropertyChangeListener() {
//			public void propertyChange(PropertyChangeEvent e) {
//				Object o = pane.getValue();
//				if (o instanceof Integer) {
//					dlg.setVisible(false);
//					return;
//				}
//
//				String value = (String) pane.getValue();
//				if (Res.getString("sweet.group.close").equals(value)) {
//					dlg.setVisible(false);
//				} else if (Res.getString("sweet.group.save").equals(value)) {
//					boolean isValid = validatePanel();
//					if (isValid) {
//						String room = nameField.getText().replaceAll(" ", "_") + "@" + serviceName;
//						try {
//							RoomInfoAgent.getRoomInfo(SparkManager.getConnection(), room);
//							JOptionPane.showMessageDialog(dlg, "该群名已被占用.", "群名称存在",
//									JOptionPane.ERROR_MESSAGE);
//							nameField.requestFocus();
//							return;
//						} catch (XMPPException e1) {
//							// Nothing to do
//						}
//
//						groupChat = createGroupChat(nameField.getText(), serviceName);
//						if (groupChat == null) {
//							showError("Could not join chat " + nameField.getText());
//							pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
//
//						} else {
//							pane.removePropertyChangeListener(this);
//							dlg.setVisible(false);
//						}
//					} else {
//						pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
//					}
//				}
//			}
//		};
//
//		pane.addPropertyChangeListener(changeListener);
//		nameField.requestFocusInWindow();
//
//		dlg.setVisible(true);
//		dlg.toFront();
//		dlg.requestFocus();
//
//		return groupChat;
//	}
//
//	private boolean validatePanel() {
//		String roomName = nameField.getText();
//
//		// Check for valid information
//		if (!ModelUtil.hasLength(roomName)) {
//			showError(Res.getString("message.specify.name.error"));
//			nameField.requestFocus();
//			return false;
//		}
//
//		if (roomName != null && roomName.length() > 16) {
//			showError("群名称长度不得超过16!");
//			nameField.requestFocus();
//			return false;
//		}
//
//		boolean check = roomName.matches("^[A-Za-z0-9\u4e00-\u9fa5]+$");
//		if (!check) {
//			showError("群名称只能由字母数字和汉字组成!");
//			nameField.requestFocus();
//			return false;
//		}
//
//		String roomTopic = subjectField.getText();
//		if (roomTopic != null && roomTopic.length() > 100) {
//			showError("群主题长度不得超过100!");
//			subjectField.requestFocus();
//			return false;
//		}
//
//		return true;
//	}
//
//	private MultiUserChat createGroupChat(String roomName, String serviceName) {
//		String room = roomName.replaceAll(" ", "_") + "@" + serviceName;
//
//		// Create a group chat with valid information
//		return new MultiUserChat(SparkManager.getConnection(), room.toLowerCase());
//	}
//
//	private void showError(String errorMessage) {
//		JOptionPane.showMessageDialog(this, errorMessage, Res.getString("title.error"),
//				JOptionPane.ERROR_MESSAGE);
//	}
//
//	public boolean isPrivate() {
//		return false;
//	}
//
//	public boolean isPermanent() {
//		return true;
//	}
//
//	public boolean isPasswordProtected() {
//		return false;
//	}
//
//	public String getPassword() {
//		return null;
//	}
//
//	public String getRoomName() {
//		return nameField.getText();
//	}
//
//	public String getRoomTopic() {
//		return subjectField.getText();
//	}
//
//}
