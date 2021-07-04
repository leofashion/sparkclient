package com.sweet.chat;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.TitlePanel;
import org.jivesoftware.spark.util.ResourceUtils;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import com.sweet.settings.PhraseManager;

public class CustomPhraseDialog extends JPanel {

	private static final long serialVersionUID = -6701044884693048469L;

	private JLabel inviteLabel = new JLabel();

	private DefaultListModel invitedUsers = new DefaultListModel();
	private JList invitedUserList = new JList(invitedUsers);

	private JDialog dlg;

	private GridBagLayout gridBagLayout1 = new GridBagLayout();

	public CustomPhraseDialog() {

		setLayout(gridBagLayout1);

		JLabel jidLabel = new JLabel();
		final JTextField jidField = new JTextField();
		JButton addJIDButton = new JButton();
		ResourceUtils.resButton(addJIDButton, "添加");
		ResourceUtils.resLabel(jidLabel, jidField, "新的常用语");

		add(jidLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		add(jidField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		add(addJIDButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

		addJIDButton.addActionListener(new ActionListener() {
			@Override
            public void actionPerformed(ActionEvent actionEvent) {
				String jid = jidField.getText();
				if (jid == null || jid.isEmpty()) {
					JOptionPane.showMessageDialog(dlg, "待添加的常用语不能为空!", "系统提示",
							JOptionPane.ERROR_MESSAGE);
					jidField.setText("");
					jidField.requestFocus();
				} else {
					if (!invitedUsers.contains(jid)) {
						invitedUsers.addElement(jid);
					}
					jidField.setText("");
				}
			}
		});

		ArrayList<String> initMessages = PhraseManager.getPhrases();
		for (String str : initMessages) {
			invitedUsers.addElement(str);
		}

		add(inviteLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		add(new JScrollPane(invitedUserList), new GridBagConstraints(1, 1, 2, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

		// Add Resource Utils
		inviteLabel.setText("常用语");

		// Add Listener to list
		invitedUserList.addMouseListener(new MouseAdapter() {
			@Override
            public void mouseReleased(MouseEvent mouseEvent) {
				if (mouseEvent.isPopupTrigger()) {
					showPopup(mouseEvent);
				}
			}

			@Override
            public void mousePressed(MouseEvent mouseEvent) {
				if (mouseEvent.isPopupTrigger()) {
					showPopup(mouseEvent);
				}
			}
		});
	}

	private void showPopup(MouseEvent e) {
		final JPopupMenu popup = new JPopupMenu();
		final int index = invitedUserList.locationToIndex(e.getPoint());

		Action removeAction = new AbstractAction() {
			private static final long serialVersionUID = 7837533277115442942L;

			public void actionPerformed(ActionEvent e) {
				invitedUsers.remove(index);
			}
		};

		removeAction.putValue(Action.NAME, Res.getString("menuitem.remove"));
		removeAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_DELETE));

		popup.add(removeAction);

		popup.show(invitedUserList, e.getX(), e.getY());

	}

	public void editPhraseMessages() {

		JFrame parent = SparkManager.getChatManager().getChatContainer().getChatFrame();
		if (parent == null || !parent.isVisible()) {
			parent = SparkManager.getMainWindow();
		}

		final JOptionPane pane;

		TitlePanel titlePanel;

		// Create the title panel for this dialog
		titlePanel = new TitlePanel("", "设置自己的常用语", SparkRes.getImageIcon(SparkRes.BLANK_IMAGE),
				true);

		// Construct main panel w/ layout.
		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(titlePanel, BorderLayout.NORTH);

		// The user should only be able to close this dialog.
		Object[] options = { "保存", "关闭" };
		pane = new JOptionPane(this, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null,
				options, options[0]);

		mainPanel.add(pane, BorderLayout.CENTER);

		final JOptionPane p = new JOptionPane();

		dlg = p.createDialog(parent, "设置常用语");
		dlg.setModal(false);

		dlg.pack();
		dlg.setSize(500, 450);
		dlg.setResizable(true);
		dlg.setContentPane(mainPanel);
		dlg.setLocationRelativeTo(parent);

		PropertyChangeListener changeListener = new PropertyChangeListener() {
			@Override
            public void propertyChange(PropertyChangeEvent e) {
				String value = (String) pane.getValue();
				if (("关闭").equals(value)) {
					pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
					dlg.dispose();
				} else if (("保存").equals(value)) {
					ArrayList<String> phrases = PhraseManager.getPhrases();
					phrases.clear();
					for (int i = 0; i < invitedUsers.getSize(); i++) {
						phrases.add(invitedUsers.elementAt(i).toString());
					}
					PhraseManager.saveSettings();
					pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
					dlg.dispose();
				}
			}
		};

		pane.addPropertyChangeListener(changeListener);

		dlg.setVisible(true);
		dlg.toFront();
		dlg.requestFocus();
	}
}
