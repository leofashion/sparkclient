package com.sweet.util;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPopupMenu;

import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.ContextMenuListener;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.sparkimpl.profile.VCardManager;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

/**
 * 人员列表――――右键增加“查看资料”菜单 liuh 2014-5-21下午4:43:49
 */
public class VieUserInfo implements Plugin {

	@Override
	public void initialize() {
		final ContactList contactList = SparkManager.getWorkspace().getContactList();
		contactList.addContextMenuListener(new ContextMenuListener() {

			@Override
			public void poppingUp(Object component, JPopupMenu popup) {
				if (!(component instanceof ContactItem)) {
					return;
				}

				ContactItem contactItem = (ContactItem) component;
				if (contactItem.getPresence() == null) {
					return;
				}
				Action vieUserInfo = new AbstractAction() {

					/**
					 *
					 */
					private static final long serialVersionUID = -8052837330857187407L;

					@Override
					public void actionPerformed(ActionEvent e) {
					    //TODO 修改
                        try {
                            viewUserInfo();
                        } catch (XmppStringprepException xmppStringprepException) {
                            xmppStringprepException.printStackTrace();
                        }
                    }
				};
				vieUserInfo.putValue(Action.NAME, Res.getString("menuitem.view.userinfo"));
				popup.add(vieUserInfo);
			}

			@Override
			public void poppingDown(JPopupMenu popup) {
			}

			@Override
			public boolean handleDefaultAction(MouseEvent e) {
				return false;
			}
		});

	}

	@Override
	public void shutdown() {

	}

	@Override
	public boolean canShutDown() {
		return false;
	}

	@Override
	public void uninstall() {
	}

	private void viewUserInfo() throws XmppStringprepException {
		final ContactList contactList = SparkManager.getWorkspace().getContactList();
		Collection<ContactItem> selectedUsers = contactList.getSelectedUsers();
		if (selectedUsers.size() == 1) {
			ContactItem item = (ContactItem) selectedUsers.toArray()[0];
			Presence presence = item.getPresence();
			//TODO 修改
			final BareJid jid = JidCreate.bareFrom(presence.getFrom());
			SwingWorker worker = new SwingWorker() {
				@Override
                public Object construct() {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e1) {
						// Nothing to do
					}
					return jid;
				}

				@Override
                public void finished() {
					VCardManager vcard = SparkManager.getVCardManager();
					vcard.viewProfile(jid, SparkManager.getChatManager().getChatContainer());
				}
			};
			worker.start();
		}
	}
}
