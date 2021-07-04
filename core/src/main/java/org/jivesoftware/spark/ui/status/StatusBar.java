/**
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.spark.ui.status;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TimerTask;

import javax.swing.*;
import javax.swing.border.Border;

import com.sweet.attendcode.IMAttendCode;
import com.sweet.forward.IMForward;
import com.sweet.hotkeymanager.HotKeyUtil;
import com.sweet.util.HttpClientTools;
import org.jivesoftware.MainWindow;
import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.ConfirmDialog;
import org.jivesoftware.spark.ui.CommandPanel;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ImageCombiner;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.StringUtils;
import org.jivesoftware.spark.util.SwingTimerTask;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.spark.util.UIComponentRegistry;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.manager.Enterprise;
import org.jivesoftware.sparkimpl.plugin.privacy.PrivacyManager;
import org.jivesoftware.sparkimpl.plugin.privacy.list.SparkPrivacyList;
import org.jivesoftware.sparkimpl.profile.VCardEditor;
import org.jivesoftware.sparkimpl.profile.VCardListener;
import org.jivesoftware.sparkimpl.profile.VCardManager;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

//TODO: I need to remove the presence logic from this class.
public class StatusBar extends JPanel implements VCardListener {
	private static final long serialVersionUID = -4322806442034868526L;

	private List<StatusItem> statusList = new ArrayList<>();

	private JLabel imageLabel = new JLabel();
	private JLabel descriptiveLabel = new JLabel();
	private JLabel nicknameLabel = new JLabel();


    private MyJbutton signbutton = new MyJbutton(SparkRes.getImageIcon(SparkRes.SIGN_HELLO));// 签到、签退
    private MyJbutton loginOAButton = new MyJbutton(SparkRes.getImageIcon(SparkRes.LOGINOA_BUTTON));// 进入办公系统
    private MyJbutton workDailyButton = new MyJbutton(
        SparkRes.getImageIcon(SparkRes.WORKDAILY_BUTTON));// 我的日志
    private MyJbutton sendMailButton = new MyJbutton(
        SparkRes.getImageIcon(SparkRes.SENDMAIL_BUTTON));// 发送邮件
    private MyJbutton myProButton = new MyJbutton(SparkRes.getImageIcon(SparkRes.MYPRO_BUTTON));// 我的项目
    private MyJbutton phoneButton = new MyJbutton(SparkRes.getImageIcon(SparkRes.PHONE_BUTTON));// 发送手机短信
    private MyJbutton bookListButton = new MyJbutton(
        SparkRes.getImageIcon(SparkRes.BOOKLIST_BUTTON));// 通讯录

    private IMForward imforward;

    private StatusPanel statusPanel = new StatusPanel();
    private CommandPanel commandPanel ;
	private Image backgroundImage;
	private Runnable changePresenceRunnable;

	private Presence currentPresence;

	public StatusBar() {
		this(true);
	}

	public StatusBar(boolean doLayout) {

		commandPanel = UIComponentRegistry.createCommandPanel();

		if (doLayout) {
			setLayout(new GridBagLayout());

			backgroundImage = Default.getImageIcon(Default.TOP_BOTTOM_BACKGROUND_IMAGE).getImage();



			ImageIcon brandedImage = Default.getImageIcon(Default.BRANDED_IMAGE);
			if (brandedImage != null && brandedImage.getIconWidth() > 1) {
				final JLabel brandedLabel = new JLabel(brandedImage);
				add(brandedLabel, new GridBagConstraints(3, 0, 1, 3, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
			}


			add(imageLabel, new GridBagConstraints(0, 0, 1, 3, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(2, 8, 2, 2), 0, 0));

			add(nicknameLabel, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2, 12, 0, 0), 0, 0));
			add(statusPanel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 7, 0, 0), 0, 0));
			add(commandPanel, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));
			nicknameLabel.setToolTipText(SparkManager.getConnection().getUser().toString());
			nicknameLabel.setFont(new Font("宋体", Font.PLAIN, 12));

            Insets insets = new Insets(1, 10, 0, 0);
            add(myProButton, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));
            add(sendMailButton, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));
            add(bookListButton, new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));

            add(workDailyButton, new GridBagConstraints(5, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));
            add(phoneButton, new GridBagConstraints(6, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));

            add(signbutton, new GridBagConstraints(7, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));
            add(loginOAButton, new GridBagConstraints(8, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));

            signbutton.setToolTipText(Res.getString("AMStart"));
            loginOAButton.setToolTipText(Res.getString("menuitem.login.oa"));
            loginOAButton.setVisible(false);

            workDailyButton.setToolTipText(Res.getString("menuitem.oa.workDiary"));
            sendMailButton.setToolTipText(Res.getString("menuitem.oa.sendMail"));
            myProButton.setToolTipText(Res.getString("menuitem.oa.myProButton"));
            phoneButton.setToolTipText(Res.getString("menuitem.oa.phoneButton"));
            bookListButton.setToolTipText(Res.getString("menuitem.oa.booklist"));

            setStatus(Res.getString("status.online"));
		}

		buildStatusItemList();

		// SPARK-1521, if we log in as invisible, default status should be "Invisible"
		currentPresence = getPresenceOnStart();

		updatePresence();        

		//setBorder(BorderFactory.createLineBorder(new Color(197, 213, 230), 1));

		SparkManager.getSessionManager().addPresenceListener( presence -> {
			presence.setStatus(StringUtils.modifyWildcards(presence.getStatus()));
			changeAvailability(presence);

			// SPARK-1524:
			// after reconnected if we had the 'invisible' presence
			// we should re-send it
			if (PresenceManager.isInvisible(currentPresence)) {
				TimerTask task = new SwingTimerTask() {
					@Override
					public void doRun() {
						PrivacyManager.getInstance().goToInvisible();
					}
				};
				TaskEngine.getInstance().schedule(task, 500);
			}
		} );

		final TimerTask task = new SwingTimerTask() {
			@Override
			public void doRun() {
				SparkManager.getVCardManager().addVCardListener(SparkManager.getWorkspace().getStatusBar());
			}
		};

		TaskEngine.getInstance().schedule(task, 3000);
		changePresenceRunnable = () -> updatePresence();


	}

	public void setAvatar(Icon icon) {
		if ( icon == null )
		{
			imageLabel.setIcon( null );
		}
		else
		{
			Image image = ImageCombiner.iconToImage( icon );
			if ( icon.getIconHeight() > 64 || icon.getIconWidth() > 64 )
			{
				imageLabel.setIcon( new ImageIcon( image.getScaledInstance( -1, 64, Image.SCALE_SMOOTH ) ) );
			}
			else
			{
				imageLabel.setIcon( icon );
			}
		}
		imageLabel.setBorder(null);
		revalidate();
		allowProfileEditing();
	}

	public CommandPanel getCommandPanel()
	{
		return commandPanel;
	}

	public void setNickname(String nickname) {
		nicknameLabel.setText(nickname);
	}

	/**
	 * Sets the current status text in the Status Manager.
	 *
	 * @param status the status to set.
	 */
	public void setStatus(String status) {
		statusPanel.setStatus(status);
	}

	protected void updatePresence() {
		setStatus(currentPresence.getStatus());
		final Icon icon = PresenceManager.getIconFromPresence(currentPresence);
		if (icon != null) {
			statusPanel.setIcon(icon);
		}
	}

	public void showPopup(MouseEvent e) {
		final JPopupMenu popup = new JPopupMenu();

		List<CustomStatusItem> custom = CustomMessages.load();
		if (custom == null) {
			custom = new ArrayList<>();
		}

		// Sort Custom Messages
		Collections.sort( custom, ( a, b ) -> ( a.getStatus().compareToIgnoreCase( b.getStatus() ) ) );

		// Build menu from StatusList
		for (final StatusItem statusItem : statusList) {
			final Action statusAction = new AbstractAction() {
				private static final long serialVersionUID = -192865863435381702L;

				@Override
				public void actionPerformed(ActionEvent actionEvent) {
					final String text = statusItem.getText();
					final StatusItem si = getStatusItem(text);
					if (si == null) {
						// Custom status
						Log.error("Unable to find status item for status - " + text);
						return;
					}

					SwingWorker worker = new SwingWorker() {
						@Override
						public Object construct() {
							return changePresence(si.getPresence());
						}

						@Override
						public void finished() {
							setStatus((String) getValue());
						}
					};
					worker.start();
				}
			};

			statusAction.putValue(Action.NAME, statusItem.getText());
			statusAction.putValue(Action.SMALL_ICON, statusItem.getIcon());

			// Has Children
			boolean hasChildren = false;
			for (Object aCustom : custom) {
				final CustomStatusItem cItem = (CustomStatusItem) aCustom;
				String type = cItem.getType();
				if (type.equals(statusItem.getText())) {
					hasChildren = true;
				}
			}

			if (!hasChildren) {
				// Add as Menu Item
				popup.add(statusAction);
			}
			else {

				final JMenu mainStatusItem = new JMenu(statusAction);


				popup.add(mainStatusItem);

				// Add Custom Messages
				for (Object aCustom : custom) {
					final CustomStatusItem customItem = (CustomStatusItem) aCustom;
					String type = customItem.getType();
					final String customStatus = customItem.getStatus();
					if (type.equals(statusItem.getText())) {
						// Add Child Menu
						Action action = new AbstractAction() {
							private static final long serialVersionUID = -1264239704492879742L;

							@Override
							public void actionPerformed(ActionEvent actionEvent) {
								final String text = mainStatusItem.getText();
								final StatusItem si = getStatusItem(text);
								if (si == null) {
									// Custom status
									Log.error("Unable to find status item for status - " + text);
									return;
								}

								SwingWorker worker = new SwingWorker() {
									@Override
									public Object construct() {
										Presence presence = PresenceManager.copy(si.getPresence());
										presence.setStatus(customStatus);
										presence.setPriority(customItem.getPriority());
										return changePresence(presence);
									}

									@Override
									public void finished() {
										setStatus((String) getValue());
									}
								};
								worker.start();
							}
						};
						action.putValue(Action.NAME, customItem.getStatus());
						action.putValue(Action.SMALL_ICON, statusItem.getIcon());
						mainStatusItem.add(action);
					}
				}

				// If menu has children, allow it to still be clickable.
				mainStatusItem.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent mouseEvent) {
						statusAction.actionPerformed(null);
						popup.setVisible(false);
					}
				});
			}
		}


		//SPARK-1521. Add privacy menu if Privacy Manager is active and have any visible lists
		final PrivacyManager pmanager = PrivacyManager.getInstance();
		if (pmanager.isPrivacyActive() && pmanager.getPrivacyLists().size() > 0) {

			JMenu privMenu = new JMenu(Res.getString("privacy.status.menu.entry"));
			privMenu.setIcon(SparkRes.getImageIcon("PRIVACY_ICON_SMALL"));

			for (SparkPrivacyList plist : pmanager.getPrivacyLists()) {
				JMenuItem it = new JMenuItem(plist.getListName());
				privMenu.add(it);
				if (plist.isActive()) {
					it.setIcon(SparkRes.getImageIcon("PRIVACY_LIGHTNING"));
				} else {
					it.setIcon(null);
				}
				final SparkPrivacyList finalList = plist;
				it.addActionListener( e1 -> PrivacyManager.getInstance().setListAsActive(finalList.getListName()) );
			}

			if (pmanager.hasActiveList()) {
				JMenuItem remMenu = new JMenuItem(Res.getString("privacy.menuitem.deactivate.current.list", pmanager.getActiveList().getListName()),
						SparkRes.getImageIcon("PRIVACY_DEACTIVATE_LIST"));
				remMenu.addActionListener( e1 -> pmanager.declineActiveList() );
				privMenu.addSeparator();
				privMenu.add(remMenu);
			}

			popup.add(privMenu);
		}

		// Add change message
		final JMenuItem changeStatusMenu = new JMenuItem(Res.getString("menuitem.set.status.message"), SparkRes.getImageIcon(SparkRes.BLANK_IMAGE));
		popup.addSeparator();


		popup.add(changeStatusMenu);
		changeStatusMenu.addActionListener( e1 -> CustomMessages.addCustomMessage() );


		Action editMessagesAction = new AbstractAction() {
			private static final long serialVersionUID = 7148051050075679995L;

			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				CustomMessages.editCustomMessages();
			}
		};

		editMessagesAction.putValue(Action.NAME, Res.getString("menuitem.edit.status.message"));
		popup.add(editMessagesAction);

		final JPanel panel = getStatusPanel();
		popup.show(panel, 0, panel.getHeight());
	}

	protected JPanel getStatusPanel() {
		return statusPanel;
	}

	public void changeAvailability(final Presence presence) {
		// SPARK-1524: if we were reconnected because of the error
		// then we get presence with the mode == null. 
		if (presence.getMode() == null)
			return;

		if ((presence.getMode() == currentPresence.getMode()) && (presence.getType() == currentPresence.getType()) && (presence.getStatus().equals(currentPresence.getStatus()))) {
			ExtensionElement pe = presence.getExtension("x", "vcard-temp:x:update");
			if (pe != null) {
				// Update VCard
				loadVCard();
			}
			return;
		}
		currentPresence = presence;

		SwingUtilities.invokeLater(changePresenceRunnable);
	}

	/**
	 * Populates the current Dnd List.
	 */
	private void buildStatusItemList() {
		for (Presence presence : PresenceManager.getPresences()) {
			Icon icon = PresenceManager.getIconFromPresence(presence);
			StatusItem item = new StatusItem(presence, icon);
			statusList.add(item);
		}

		final Icon availableIcon = PresenceManager.getIconFromPresence(new Presence(Presence.Type.available));

		// Set default presence icon (Avaialble)
		statusPanel.setIcon(availableIcon);
	}


	public Collection<StatusItem> getStatusList() {
		return statusList;
	}

	public Collection<CustomStatusItem> getCustomStatusList()
	{
		List<CustomStatusItem> custom = CustomMessages.load();
		if (custom == null)
			custom = new ArrayList<>();

		// Sort Custom Messages
		Collections.sort( custom, ( a, b ) -> ( a.getStatus().compareToIgnoreCase( b.getStatus() ) ) );

		return custom;
	}

	public Presence getPresence() {
		return currentPresence;
	}

	public StatusItem getStatusItem(String label) {
		for (StatusItem aStatusList : statusList) {
			if (aStatusList.getText().equals(label)) {
				return aStatusList;
			}
		}
		return null;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (backgroundImage != null) {
			double scaleX = getWidth() / (double)backgroundImage.getWidth(null);
			double scaleY = getHeight() / (double)backgroundImage.getHeight(null);
			AffineTransform xform = AffineTransform.getScaleInstance(scaleX, scaleY);
			((Graphics2D)g).drawImage(backgroundImage, xform, this);
		}
	}


	public void loadVCard() {
		final Runnable loadVCard = () -> {
			VCard vcard = SparkManager.getVCardManager().getVCard();
			updateVCardInformation(vcard);
            imforward = new IMForward();
            if (imforward.getSignVal().equalsIgnoreCase("true")) {
                initSignButton();
            }
            initOtherAllButton();
		};

		TaskEngine.getInstance().submit(loadVCard);
	}

	protected void updateVCardInformation(final VCard vCard) {
		SwingUtilities.invokeLater( () -> {
			if (vCard.getError() == null) {
				String firstName = vCard.getFirstName();
				String lastName = vCard.getLastName();
				String nickname = vCard.getNickName();
				if (ModelUtil.hasLength(firstName) && ModelUtil.hasLength(lastName)) {
					setNickname(firstName + " " + lastName);
				}
				else if (ModelUtil.hasLength(firstName)) {
					setNickname(firstName);
				}
				else if (ModelUtil.hasLength(nickname)) {
					setNickname(nickname);
				}
				else {
					nickname = SparkManager.getSessionManager().getUsername();
					setNickname(nickname);
				}
			}
			else {
				String nickname = SparkManager.getSessionManager().getUsername();
				setNickname(nickname);
				return;
			}


			byte[] avatarBytes = null;
			try {
				avatarBytes = vCard.getAvatar();
			}
			catch (Exception e) {
				Log.error("Cannot retrieve avatar bytes.", e);
			}


			if (avatarBytes != null && avatarBytes.length > 0) {
				try {
					ImageIcon avatarIcon = new ImageIcon(avatarBytes);
					avatarIcon = VCardManager.scale(avatarIcon);
					setAvatar(avatarIcon);
					imageLabel.invalidate();
					imageLabel.validate();
					imageLabel.repaint();
				}
				catch (Exception e) {
					// no issue
				}
			}
			else {
				imageLabel.setIcon(null);
				imageLabel.setBorder(null);
				imageLabel.invalidate();
				imageLabel.validate();
				imageLabel.repaint();
			}
		} );

	}

	public static Presence copyPresence(Presence presence) {
		return new Presence(presence.getType(), presence.getStatus(), presence.getPriority(), presence.getMode());
	}

	/**
	 * Return the nickname Component used to display the users profile name.
	 *
	 * @return the label.
	 */
	public JLabel getNicknameLabel() {
		return nicknameLabel;
	}

	public void setStatusPanelEnabled(Boolean enabled) {
	    getStatusPanel().setEnabled(enabled);
    }

	private class StatusPanel extends JPanel {
		private static final long serialVersionUID = -5086334443225239032L;
		private JLabel iconLabel;
		private JLabel statusLabel;

		public StatusPanel() {
			super();

			setOpaque(false);

			iconLabel = new JLabel();
			statusLabel = new JLabel();

			setLayout(new GridBagLayout());

			// Remove padding from icon label
			iconLabel.setIconTextGap(0);

			add(iconLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			add(statusLabel, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 2, 0, 0), 0, 0));

			statusLabel.setFont(new Font("宋体", Font.PLAIN, 12));

			// See if we should disable ability to change presence status
			if (!Default.getBoolean(Default.DISABLE_PRESENCE_STATUS_CHANGE) && Enterprise.containsFeature(Enterprise.PRESENCE_STATUS_FEATURE)) statusLabel.setIcon(SparkRes.getImageIcon(SparkRes.DOWN_ARROW_IMAGE));

			statusLabel.setHorizontalTextPosition(JLabel.LEFT);

			setOpaque(false);

			final Border border = BorderFactory.createEmptyBorder(2, 2, 2, 2);
			setBorder(border);

			// See if we should disable ability to change presence status
			if (!Default.getBoolean(Default.DISABLE_PRESENCE_STATUS_CHANGE) && Enterprise.containsFeature(Enterprise.PRESENCE_STATUS_FEATURE)) {
				statusLabel.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseReleased(MouseEvent e) {
						if (!isEnabled()) return;
					    showPopup(e);
					}

					@Override
					public void mouseEntered(MouseEvent e) {
                        if (!isEnabled()) return;
						setCursor(GraphicUtils.HAND_CURSOR);
						setBorder(BorderFactory.createBevelBorder(0));
					}

					@Override
					public void mouseExited(MouseEvent e) {
						setCursor(GraphicUtils.DEFAULT_CURSOR);
						setBorder(border);
					}

					@Override
					public void mousePressed(MouseEvent e) {
                        if (!isEnabled()) return;
						setBorder(BorderFactory.createBevelBorder(1));
					}

				});
			}            

		}

		public void setStatus(String status) {
			int length = status.length();
			String visualStatus = status;
			if (length > 30) {
				visualStatus = status.substring(0, 27) + "...";
			}

			statusLabel.setText(visualStatus);
			statusLabel.setToolTipText(status);
		}

		public void setIcon(Icon icon) {
			iconLabel.setIcon(icon);
		}
	}

	public void setBackgroundImage(Image image) {
		this.backgroundImage = image;
	}

	public void setDescriptiveText(String text) {
		descriptiveLabel.setText(text);
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension dim = super.getPreferredSize();
		dim.width = 0;
		return dim;
	}


	@Override
	public void vcardChanged(VCard vcard) {
		updateVCardInformation(vcard);
	}

	protected Runnable getChangePresenceRunnable() {
		return changePresenceRunnable;
	}

	protected Presence getCurrentPresence() {
		return currentPresence;
	}

	private String changePresence(Presence presence) {
		// SPARK-1521. Other clients can see "Invisible" status while we are disappearing.
		// So we send "Offline" instead of "Invisible" for them.
		boolean isNewPresenceInvisible = PresenceManager
				.isInvisible(presence);
		if (isNewPresenceInvisible && !PrivacyManager.getInstance().isPrivacyActive()) {
			JOptionPane.showMessageDialog(null, Res.getString("dialog.invisible.privacy.lists.not.supported"));
		}

		Presence copyPresence = copyPresence(presence);
		if (isNewPresenceInvisible) {
			copyPresence.setStatus(null);
		}
		if (PresenceManager.areEqual(getCurrentPresence(), copyPresence)) {
			return presence.getStatus();
		}

		// ask user to confirm that all group chat rooms will be closed if
		// he/she goes to invisible.
		if (isNewPresenceInvisible
				&& SparkManager.getChatManager().getChatContainer()
				.hasGroupChatRooms()) {
			int reply = JOptionPane
					.showConfirmDialog(
							null,
							Res.getString("dialog.confirm.close.all.conferences.if.invisible.msg"),
							Res.getString("dialog.confirm.to.reveal.visibility.title"),
							JOptionPane.YES_NO_OPTION);
			if (reply == JOptionPane.NO_OPTION) {
				return getCurrentPresence().getStatus();
			}
		}

		// If we go visible then we should send "Available" first.
		if (!isNewPresenceInvisible
				&& PresenceManager.isInvisible(getCurrentPresence()))
			PrivacyManager.getInstance().goToVisible();

		// Then set the current status.
		SparkManager.getSessionManager().changePresence(copyPresence);

		// If we go invisible we should activate the "globally invisible list"
		// and send "Available" after "Unavailable" presence.
		if (isNewPresenceInvisible) {
			SparkManager.getChatManager().getChatContainer()
			.closeAllGroupChatRooms();
			PrivacyManager.getInstance().goToInvisible();
		}

		return presence.getStatus();
	}

	protected Presence getPresenceOnStart() {
		return SettingsManager.getLocalPreferences().isLoginAsInvisible() 
				? PresenceManager.getUnavailablePresence() 
						: PresenceManager.getAvailablePresence();
	}

	public void allowProfileEditing() {
		// Allow profile editing ONLY if both client-side and server-side settings permit it
		if (Default.getBoolean(Default.DISABLE_EDIT_PROFILE) || !Enterprise.containsFeature(Enterprise.VCARD_FEATURE)) return;

		// Go ahead and show the profile when clicking on the Avatar image
		imageLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				if (mouseEvent.getClickCount() == 1) {
					VCardManager vcardManager = SparkManager.getVCardManager();
					VCardEditor editor = new VCardEditor();
					editor.editProfile(vcardManager.getVCard(), SparkManager.getWorkspace());
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				imageLabel.setCursor(GraphicUtils.HAND_CURSOR);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				imageLabel.setCursor(GraphicUtils.DEFAULT_CURSOR);
			}
		});
	}

    /**
     * 初始化其他按钮，增加触发事件
     */
    private void initOtherAllButton() {
        if (null == imforward)
            imforward = new IMForward();

        if (!imforward.getDiaryVal().equalsIgnoreCase("false")) {// 日志
            workDailyButton.setVisible(true);
            workDailyButton.addActionListener(new AbstractAction() {
                /**
                 *
                 */
                private static final long serialVersionUID = -2865142938423122616L;

                public void actionPerformed(ActionEvent e) {
                    try {
                        HotKeyUtil.openURL(HotKeyUtil.DIARY);
                    } catch (Exception e1) {
                        Log.error("Error launching browser:", e1);
                    }
                }
            });
        }
        if (!imforward.getMailVal().equalsIgnoreCase("false")) {
            sendMailButton.setVisible(true);
            sendMailButton.addActionListener(new AbstractAction() {
                /**
                 *
                 */
                private static final long serialVersionUID = 7289645937731264131L;

                public void actionPerformed(ActionEvent e) {
                    try {
                        HotKeyUtil.openURL(HotKeyUtil.SEND_MAIL);
                    } catch (Exception e1) {
                        Log.error("Error launching browser:", e1);
                    }
                }
            });
        }
        if (!imforward.getMailVal().equalsIgnoreCase("false")) {
            myProButton.setVisible(true);
            myProButton.addActionListener(new AbstractAction() {
                /**
                 *
                 */
                private static final long serialVersionUID = 7148051050075679995L;

                public void actionPerformed(ActionEvent e) {
                    try {
                        HotKeyUtil.openURL(HotKeyUtil.MYPRO);
                    } catch (Exception e1) {
                        Log.error("Error launching browser:", e1);
                    }
                }
            });
        }

        if (!imforward.getPhoneVal().equalsIgnoreCase("false")) {
            phoneButton.setVisible(true);
            phoneButton.addActionListener(new AbstractAction() {
                /**
                 *
                 */
                private static final long serialVersionUID = 7095894237237396656L;

                public void actionPerformed(ActionEvent e) {
                    try {
                        HotKeyUtil.openURL(HotKeyUtil.SEND_PHONE_MSG);
                    } catch (Exception e1) {
                        Log.error("Error launching browser:", e1);
                    }
                }
            });
        }
        if (!imforward.getBookListVal().equalsIgnoreCase("false")) {
            bookListButton.setVisible(true);
            bookListButton.addActionListener(new AbstractAction() {
                /**
                 *
                 */
                private static final long serialVersionUID = 5633113455957222691L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        HotKeyUtil.openURL(HotKeyUtil.BOOK_LIST);
                    } catch (Exception e1) {
                        Log.error("Error launching browser:", e1);
                    }
                }
            });
        }
    }


    private void initSignButton() {
        signbutton.setVisible(true);
        final IMAttendCode imac = new IMAttendCode();
        final HttpClientTools hct = imac.getHct();
        String attendCode = imac.getMenuText();
        String tmpTip = attendCode;
        try {
            if ((attendCode.startsWith("AMEnd") || attendCode.startsWith("PMEnd"))
                && attendCode.indexOf(";") == -1) {// 上午已签退或者下午已签退，此时图片显示应为“工作中”
                signbutton.setIcon(SparkRes.getImageIcon(SparkRes.SIGN_GOODBYE));
                tmpTip = attendCode.substring(0, attendCode.indexOf(";") + 1);
            } else if (attendCode.equals("AMStart") || attendCode.equals("PMStart")) {// 上午下午自动签到
                hct.postLogin(
                    "/ProjManager/SIMAttendCode.jsp?userName=" + imac.getUserName()
                        + "&attendCode=" + attendCode,
                    imac.getUserName(), hct.getServerPort() + "");
                String serverRet = hct.postServer("/ProjManager/SIMAttendCode.jsp?userName="
                    + imac.getUserName() + "&attendCode=" + attendCode);
                signbutton.setIcon(SparkRes.getImageIcon(SparkRes.SIGN_WORKING));
                tmpTip = serverRet;

            } else {
                signbutton.setIcon(SparkRes.getImageIcon(SparkRes.SIGN_WORKING));
            }
            if (tmpTip.indexOf(";") != -1) {
                tmpTip = tmpTip.substring(0, tmpTip.indexOf(";"));
            }
            if (tmpTip.length() > 0) {
                signbutton.setToolTipText(Res.getString(tmpTip));
            }

            signbutton.addActionListener(new AbstractAction() {
                /**
                 *
                 */
                private static final long serialVersionUID = 8559139283962665504L;

                public void actionPerformed(ActionEvent e) {
                    try {
                        IMAttendCode imac2 = new IMAttendCode();
                        HttpClientTools hct2 = imac2.getHct();
                        String attendcode2 = imac2.getMenuText();
                        if (attendcode2.indexOf(";") != -1) {
                            attendcode2 = attendcode2.substring(0, attendcode2.indexOf(";"));
                        }
                        hct2.postLogin(
                            "/ProjManager/SIMAttendCode.jsp?userName=" + imac2.getUserName()
                                + "&attendCode=" + attendcode2,
                            imac2.getUserName(), hct2.getServerPort() + "");
                        String tmptip = hct2.postServer("/ProjManager/SIMAttendCode.jsp?userName="
                            + imac2.getUserName() + "&attendCode=" + attendcode2);
                        signbutton.setIcon(SparkRes.getImageIcon(SparkRes.SIGN_GOODBYE));
                        if (tmptip.indexOf(";") != -1) {
                            tmptip = tmptip.substring(0, tmptip.indexOf(";"));
                        }

                        if (tmptip.startsWith("AMEnd") || tmptip.startsWith("PMEnd")) {
                            final ConfirmDialog confirm = new ConfirmDialog();
                            confirm.showConfirmDialog(SparkManager.getMainWindow(), "信息提示",
                                Res.getString(tmptip) + "成功！是否退出小信使？", Res.getString("yes"),
                                Res.getString("no"), null);
                            confirm.setConfirmListener(new ConfirmDialog.ConfirmListener() {
                                public void yesOption() {
                                    MainWindow.getInstance().shutdown();
                                }

                                public void noOption() {
                                }
                            });
                        } else {
                            JOptionPane.showMessageDialog(SparkManager.getMainWindow(),
                                Res.getString(tmptip) + "成功！");
                        }
                        signbutton.setToolTipText(Res.getString(tmptip));

                    } catch (Exception e1) {
                        Log.error(e1);
                    }
                }
            });
        } catch (Exception e) {
            Log.error(e);
        }
    }


    private class MyJbutton extends JButton {
        private static final long serialVersionUID = 1L;

        public MyJbutton(ImageIcon imcon) {
            super(imcon);
            // setSize(15, 15);
            setMargin(new Insets(0, 0, 0, 0));// 下左右上
            setIconTextGap(0);// 将标签中显示的文本和图标之间的间隔量设置为0
            setBorderPainted(false);// 不打印边框
            // setBorder(null);//除去边框
            setText(null);// 除去按钮的默认名称
            setFocusPainted(false);// 除去焦点的框
            setContentAreaFilled(false);// 除去默认的背景填充

            setVisible(false);

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                    setBorderPainted(true);
                }

                public void mouseExited(MouseEvent e) {
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    setBorderPainted(false);
                }
            });

        }

    }


}
