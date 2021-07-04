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
package org.jivesoftware.sparkimpl.plugin.filetransfer.transfer.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.*;

import com.sweet.FileTools;
import com.sweet.OffLineFileTransferComm;
import com.sweet.OffLineFileTransferUpload;
import com.sweet.iangelgroup.GroupFile;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.FileDragLabel;
import org.jivesoftware.spark.filetransfer.SparkTransferManager;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.ui.TranscriptWindow;
import org.jivesoftware.spark.util.ByteFormat;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.StringUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.stringprep.XmppStringprepException;

public class SendFileTransfer extends JPanel {

    private static final long serialVersionUID = -4403839897649365671L;
    private FileDragLabel imageLabel = new FileDragLabel();
    private JLabel titleLabel = new JLabel();
    private JLabel fileLabel = new JLabel();

    private TransferButton cancelButton = new TransferButton();
    private JProgressBar progressBar = new JProgressBar();
    private File fileToSend;
    private OutgoingFileTransfer transfer;

    private TransferButton retryButton = new TransferButton();
	private TransferButton offlineButton = new TransferButton();

    private FileTransferManager transferManager;
    private EntityFullJid fullJID;
    private String nickname;
    private JLabel progressLabel = new JLabel();
    private long _startTime;
    private ChatRoom chatRoom;

    public SendFileTransfer(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;

        setLayout(new GridBagLayout());

        setBackground(new Color(250, 249, 242));
        add(imageLabel, new GridBagConstraints(0, 0, 1, 3, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        add(titleLabel, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        titleLabel.setForeground(new Color(211, 174, 102));
        add(fileLabel, new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));

        cancelButton.setText(Res.getString("cancel"));
        retryButton.setText(Res.getString("retry"));
        cancelButton.setIcon(SparkRes.getImageIcon(SparkRes.SMALL_DELETE));
        retryButton.setIcon(SparkRes.getImageIcon(SparkRes.REFRESH_IMAGE));

        add(cancelButton, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
        add(retryButton, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
		add(offlineButton, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
        retryButton.setVisible(false);

        retryButton.addActionListener(e -> {
            try {
                File file = new File(transfer.getFilePath());
                transfer = transferManager.createOutgoingFileTransfer(fullJID);
                transfer.sendFile(file, "Sending");
            } catch (SmackException e1) {
                Log.error("An error occurred while creating an outgoing file transfer.", e1);
            }
            sendFile(transfer, transferManager, fullJID, nickname);
        });

		cancelButton.setForeground(new Color(73, 113, 196));
		cancelButton.setFont(new Font("宋体", Font.PLAIN, 12));
		cancelButton
				.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(73, 113, 196)));

		retryButton.setForeground(new Color(73, 113, 196));
		retryButton.setFont(new Font("宋体", Font.PLAIN, 12));
		retryButton.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(73, 113, 196)));

		offlineButton.setForeground(new Color(73, 113, 196));
		offlineButton.setFont(new Font("宋体", Font.PLAIN, 12));
		offlineButton
				.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(73, 113, 196)));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.white));
    }

    public void sendFile(final OutgoingFileTransfer transfer, FileTransferManager transferManager, final EntityFullJid jid, final String nickname) {
        this.transferManager = transferManager;
        //SPARK-1869
        FileTransferNegotiator.getInstanceFor(SparkManager.getConnection());
        FileTransferNegotiator.IBB_ONLY = SettingsManager.getLocalPreferences().isFileTransferIbbOnly();

		cancelButton.setVisible(true);
		retryButton.setVisible(false);
		offlineButton.setVisible(true);
		this.fullJID = jid;
		this.nickname = nickname;

        this.transfer = transfer;
        String fileName = transfer.getFileName();
        String filePath = transfer.getFilePath();
        long fileSize = transfer.getFileSize();
        ByteFormat format = new ByteFormat();
        String fileSizeString = format.format(fileSize);

        fileToSend = new File(transfer.getFilePath());
        imageLabel.setFile(fileToSend);

        fileLabel.setText(fileName + " (" + fileSizeString + ")");

        ContactList contactList = SparkManager.getWorkspace().getContactList();
        ContactItem contactItem = contactList.getContactItemByJID(jid);

        saveEventToHistory(Res.getString("message.file.transfer.history.request.sent", filePath, fileSizeString, nickname));
        titleLabel.setText(Res.getString("message.transfer.waiting.on.user", contactItem.getDisplayName()));

        if (isImage(fileName)) {
            try {
                URL imageURL = new File(transfer.getFilePath()).toURI().toURL();
                ImageIcon image = new ImageIcon(imageURL);
                image = GraphicUtils.scaleImageIcon(image, 64, 64);
                imageLabel.setIcon(image);
            } catch (MalformedURLException e) {
                Log.error("Could not locate image.", e);
                imageLabel.setIcon(SparkRes.getImageIcon(SparkRes.DOCUMENT_INFO_32x32));
            }
        } else {
            File file = new File(transfer.getFilePath());
            Icon icon = GraphicUtils.getIcon(file);
            imageLabel.setIcon(icon);
        }
        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                transfer.cancel();
				Message message = new Message();
				message.setThread(StringUtils.randomString(6));
				message.setBody("<cancelFile fileName=\"" + transfer.getFileName()
						+ "\" type=\"cancel\"></cancelFile>");
				message.setType(Message.Type.chat);
				message.setTo(jid);
				message.setFrom(SparkManager.getSessionManager().getJID());
                try {
                    SparkManager.getConnection().sendStanza(message);
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });

        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                cancelButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

		offlineButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseEvent) {
				String filePath = transfer.getFilePath();
				long flen = transfer.getFileSize();
				if (FileTools.getFileSizeMB(flen) > 1024) {
					JOptionPane.showMessageDialog(null,
							Res.getString("message.offlineFile.maxsize"));
				} else {
					transfer.cancel();
					sendOfflineFile(filePath, transferManager, jid, nickname);
					Message message = new Message();
					message.setThread(StringUtils.randomString(6));
					message.setBody("<cancelFile fileName=\"" + transfer.getFileName()
							+ "\" type=\"turnoffline\"></cancelFile>");
					message.setType(Message.Type.chat);
					message.setTo(jid);
					message.setFrom(SparkManager.getSessionManager().getJID());
                    try {
                        SparkManager.getConnection().sendStanza(message);
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
			}
		});

		offlineButton.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				offlineButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			public void mouseExited(MouseEvent e) {
				offlineButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});
        progressBar.setMaximum(100);
        progressBar.setVisible(false);
        progressBar.setStringPainted(true);
        add(progressBar, new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 150, 0));
        add(progressLabel, new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 150, 0));

        SwingWorker worker = new SwingWorker() {
            @Override
            public Object construct() {
                while (true) {
                    try {
                        if (transfer.getBytesSent() > 0 && _startTime == 0) {
                            _startTime = System.currentTimeMillis();
                        }

                        long startTime = System.currentTimeMillis();
                        long startByte = transfer.getBytesSent();
                        Thread.sleep(500);
                        FileTransfer.Status status = transfer.getStatus();
                        if (status == Status.complete) {
                            saveEventToHistory(Res.getString("message.file.transfer.history.send.complete", filePath, nickname));
                            break;
                        } else if (status == Status.error) {
                            saveEventToHistory(Res.getString("message.file.transfer.history.send.error", filePath, nickname));
                            break;
                        } else if (status == Status.cancelled) {
                            saveEventToHistory(Res.getString("message.file.transfer.history.send.canceled", filePath, nickname));
                            break;
                        } else if (status == Status.refused) {
                            saveEventToHistory(Res.getString("message.file.transfer.history.contact.rejected", filePath, nickname));
                            break;
                        }
                        long endTime = System.currentTimeMillis();
                        long endByte = transfer.getBytesSent();

                        long timeDiff = endTime - startTime;
                        long byteDiff = endByte - startByte;

                        updateBar(transfer, nickname, TransferUtils.calculateSpeed(byteDiff, timeDiff));
                    } catch (InterruptedException e) {
                        Log.error("Unable to sleep thread.", e);
                    }
                }
                return "";
            }

            @Override
            public void finished() {
                updateBar(transfer, nickname, "??MB/s");
            }
        };

        worker.start();

        makeClickable(imageLabel);
        makeClickable(titleLabel);
    }

	private void sendOfflineFile(final String filePath, FileTransferManager transferManager,
			final EntityFullJid jid, final String nickname) {
		File file = new File(filePath);
		String curTime = System.currentTimeMillis() + "";
		OffLineFileTransferUpload ofu = new OffLineFileTransferUpload(file, file.getName(),
				curTime + "-" + file.getName());
		OffLineFileTransferUpload.put(curTime + "-" + file.getName(), ofu);
		ofu.sendFile();
		TranscriptWindow tw = new TranscriptWindow();
		sendOfflineFile(file, ofu, nickname, tw, curTime, jid.asEntityBareJid(), curTime + "-" + file.getName());
	}

	/* 发送文件到服务器 */
	public void sendOfflineFile(final File file, final OffLineFileTransferUpload ofu,
                                final String nickName, final TranscriptWindow transcriptWindow, final String curTime,
                                final EntityBareJid jid, final String serverFileName) {
		cancelButton.setVisible(true);
		retryButton.setVisible(false);
		offlineButton.setVisible(false);

		String fileName = file.getName();
		long fileSize = file.length();
		ByteFormat format = new ByteFormat();
		String text = format.format(fileSize);

		fileToSend = new File(file.getPath());
		imageLabel.setFile(fileToSend);

		fileLabel.setText(fileName + " (" + text + ")");

		titleLabel.setText(Res.getString("message.transfer.File"));

		if (isImage(fileName)) {
			try {
				URL imageURL = new File(file.getPath()).toURI().toURL();
				ImageIcon image = new ImageIcon(imageURL);
				image = GraphicUtils.scaleImageIcon(image, 64, 64);
				imageLabel.setIcon(image);
			} catch (MalformedURLException e) {
				Log.error("Could not locate image.", e);
				imageLabel.setIcon(SparkRes.getImageIcon(SparkRes.DOCUMENT_INFO_32x32));
			}
		} else {
			Icon icon = GraphicUtils.getIcon(file);
			imageLabel.setIcon(icon);
		}

		cancelButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseEvent) {

				ofu.setTransStatus(OffLineFileTransferComm.CANCEL);
			}
		});

		cancelButton.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

			}

			public void mouseExited(MouseEvent e) {
				cancelButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});

		progressBar.setMaximum(100);
		progressBar.setVisible(true);
		progressBar.setStringPainted(true);
		add(progressBar, new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 150, 0));
		add(progressLabel, new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 150, 0));

		SwingWorker worker = new SwingWorker() {
			public Object construct() {
				while (true) {
					try {
						// transfer.getBytesSent():Returns the amount of bytes that have been sent for the file transfer
						if (ofu.getBytesSent() > 0 && _startTime == 0) {
                            _startTime = System.currentTimeMillis();
						}

						long starttime = System.currentTimeMillis();
						long startbyte = ofu.getBytesSent();
						Thread.sleep(500);
						if (ofu.getTransStatus().equals(OffLineFileTransferComm.SUCCESS)) {// 文件传输完毕进程结束
							OffLineFileTransferUpload.remove(serverFileName);

//							GroupFile groupFile = new GroupFile();
//							groupFile.setFileName(serverFileName);
//							groupFile.setFileSize(file.length());
//							String roomTitle = SparkTransferManager.getChatRoomTitle();
//							GroupHelper.addGroupFile(roomTitle, groupFile);

							Message message = new Message();
							message.setThread(StringUtils.randomString(6));
							message.setBody(Res.getString("message.offlineFile.tip")
									+ "<offlinefile fname=\"" + curTime + "-" + file.getName()
									+ "\"></offlinefile>");
							message.setType(Message.Type.chat);
							message.setTo(jid);
							message.setFrom(SparkManager.getSessionManager().getJID());
							SparkManager.getConnection().sendStanza(message);

							break;
						} else if (ofu.getTransStatus().equals(OffLineFileTransferComm.CANCEL)) {
							OffLineFileTransferUpload.remove(serverFileName);
							break;
						}
						long endtime = System.currentTimeMillis();
						long endbyte = ofu.getBytesSent();

						long timediff = endtime - starttime;
						long bytediff = endbyte - startbyte;

						updateOffLineBar(ofu, file, nickName,
								TransferUtils.calculateSpeed(bytediff, timediff), serverFileName);

					} catch (Exception e) {
						Log.error(e);
					}
				}

				return "";
			}

			public void finished() {
				updateOffLineBar(ofu, file, nickName, "??MB/s", serverFileName);
			}
		};

		worker.start();
		makeClickable(imageLabel);
		makeClickable(titleLabel);
		makeClickable(fileLabel);
	}
    private void makeClickable(final JLabel label) {
        label.setToolTipText(Res.getString("message.click.to.open"));

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openFile(fileToSend);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                label.setCursor(new Cursor(Cursor.HAND_CURSOR));

            }

            @Override
            public void mouseExited(MouseEvent e) {
                label.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    private void openFile(File downloadedFile) {
        try {
            Desktop.getDesktop().open(downloadedFile);
        } catch (IOException e) {
            Log.error("An error occurred while trying to open downloaded file: " + downloadedFile, e);
        }
    }

	private void updateBar(final OutgoingFileTransfer transfer, String nickname,
			String kBperSecond) {
		FileTransfer.Status status = transfer.getStatus();
		if (status == Status.negotiating_stream) {
			titleLabel.setText(Res.getString("message.negotiation.file.transfer", nickname));
		} else if (status == Status.error) {
			if (transfer.getException() != null) {
				Log.error("Error occured during file transfer.", transfer.getException());
			}
			progressBar.setVisible(false);
			progressLabel.setVisible(false);
			titleLabel.setText(Res.getString("message.unable.to.send.file", nickname));
			cancelButton.setVisible(false);
			retryButton.setVisible(false);
			offlineButton.setVisible(false);
			showAlert(true);
		} else if (status == Status.in_progress) {
			titleLabel.setText(Res.getString("message.sending.file.to", nickname));
			showAlert(false);
			if (!progressBar.isVisible()) {
				progressBar.setVisible(true);
				progressLabel.setVisible(true);
				offlineButton.setVisible(false);
			}

            try {
                SwingUtilities.invokeAndWait(() -> {
                    // 100 % = Filesize
                    // x %   = Currentsize
                    long p = (transfer.getBytesSent() * 100 / transfer.getFileSize());
                    progressBar.setValue(Math.round(p));
                });
            } catch (Exception e) {
                Log.error("An error occurred while trying to update the file transfer progress bar.", e);
            }

            ByteFormat format = new ByteFormat();
            String bytesSent = format.format(transfer.getBytesSent());
            String est = TransferUtils.calculateEstimate(transfer.getBytesSent(), transfer.getFileSize(), _startTime, System.currentTimeMillis());

            progressLabel.setText(Res.getString("message.transfer.progressbar.text.sent", bytesSent, kBperSecond, est));
        } else if (status == Status.complete) {
            progressBar.setVisible(false);

            if ( _startTime == 0 ) { // SPARK-2192: Sometimes, the startTime of the transfer hasn't been recorded yet when it already finished.
                _startTime = System.currentTimeMillis();
            }
            String fin = TransferUtils.convertSecondstoHHMMSS(Math.round(Math.max(0, System.currentTimeMillis() - _startTime)) / 1000);
            _startTime = 0;
            progressLabel.setText(Res.getString("label.time", fin));
            titleLabel.setText(Res.getString("message.you.have.sent", nickname));
            cancelButton.setVisible(false);
			offlineButton.setVisible(false);
            showAlert(true);
			Message msg = new Message();
			msg.setBody("_FILE_{" + transfer.getFilePath() + "}");
			msg.setTo(fullJID);
			msg.setFrom(SparkManager.getSessionManager().getJID());
			//String bid = StringUtils.parseBareAddress(fullJID);
			ChatRoom chatRoom = SparkManager.getChatManager().getChatRoom(fullJID.asEntityBareJid());
			chatRoom.addToTranscript(msg, true);
        } else if (status == Status.cancelled) {
            progressBar.setVisible(false);
            progressLabel.setVisible(false);
            titleLabel.setText(Res.getString("message.file.transfer.canceled"));
            cancelButton.setVisible(false);
			retryButton.setVisible(false);
			offlineButton.setVisible(false);
            showAlert(true);
        } else if (status == Status.refused) {
            progressBar.setVisible(false);
            progressLabel.setVisible(false);
            titleLabel.setText(Res.getString("message.file.transfer.rejected", nickname));
            cancelButton.setVisible(false);
			retryButton.setVisible(false);
			offlineButton.setVisible(false);
            showAlert(true);
        }
    }

	private void updateOffLineBar(final OffLineFileTransferUpload ofu, File file, String nickname,
			String kBperSecond, String serverFileName) {
		String status = ofu.getTransStatus();
            if (status.equals(OffLineFileTransferComm.ERROR)) {//
			// JOptionPane.showMessageDialog(null, "status==error");
		} else if (status.equals(OffLineFileTransferComm.ING)) {// 传输中
			titleLabel.setText(Res.getString("message.sending.offLinefile.to", nickname));
			showAlert(false);
			if (!progressBar.isVisible()) {
				progressBar.setVisible(true);
				progressLabel.setVisible(true);
				offlineButton.setVisible(false);
			}
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						// 100 % = Filesize
						// x % = Currentsize
						long p = (ofu.getBytesSent() * 100 / ofu.getFile().length());
						progressBar.setValue(Math.round(p));
					}
				});
			} catch (Exception e) {
				Log.error(e);
			}

			ByteFormat format = new ByteFormat();
			String bytesSent = format.format(ofu.getBytesSent());
			String est = TransferUtils.calculateEstimate(ofu.getBytesSent(), ofu.getFile().length(),
                _startTime, System.currentTimeMillis());

			progressLabel.setText(Res.getString("message.transfer.progressbar.text.sent", bytesSent,
					kBperSecond, est));
			cancelButton.setVisible(true);

		} else if (status.equals(OffLineFileTransferComm.SUCCESS)) {// 传输完成
			OffLineFileTransferUpload.remove(serverFileName);
			progressBar.setVisible(false);
			String fin = TransferUtils.convertSecondstoHHMMSS(
					Math.round(System.currentTimeMillis() - _startTime) / 1000);
			progressLabel.setText(Res.getString("label.time", fin));
			titleLabel.setText(Res.getString("message.you.have.sent", nickname));
			cancelButton.setVisible(false);
			offlineButton.setVisible(false);
			showAlert(true);
		} else if (status.equals(OffLineFileTransferComm.CANCEL)) {
			OffLineFileTransferUpload.remove(serverFileName);
			OffLineFileTransferComm.deleteServerFile(serverFileName);
			progressBar.setVisible(false);
			progressLabel.setVisible(false);
			titleLabel.setText(Res.getString("message.file.transfer.canceled"));
			cancelButton.setVisible(false);
			retryButton.setVisible(false);
			offlineButton.setVisible(false);
			showAlert(true);
		}
	}

    /***
     * Adds an event text as a message to transcript and saves it to history
     * @param eventText Contains file transfer event text
     */
    private void saveEventToHistory(String eventText) {
        try {
            Message message = new Message(nickname, eventText);
            message.setFrom(SparkManager.getSessionManager().getJID());
            chatRoom.addToTranscript(message, false);
            SparkManager.getWorkspace().getTranscriptPlugin().persistChatRoom(chatRoom);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
    }

    private static class TransferButton extends JButton {
        private static final long serialVersionUID = 8807434179541503654L;

        public TransferButton() {
            decorate();
        }

        /**
         * Decorates the button with the approriate UI configurations.
         */
        private void decorate() {
            setBorderPainted(false);
            setOpaque(true);

            setContentAreaFilled(false);
            setMargin(new Insets(1, 1, 1, 1));
        }
    }

    private boolean isImage(String fileName) {
        fileName = fileName.toLowerCase();

        String[] imageTypes = {"jpeg", "gif", "jpg", "png"};
        for (String imageType : imageTypes) {
            if (fileName.endsWith(imageType)) {
                return true;
            }
        }
        return false;
    }

    private void showAlert(boolean alert) {
        if (alert) {
            titleLabel.setForeground(new Color(211, 174, 102));
            setBackground(new Color(250, 249, 242));
        } else {
            setBackground(new Color(239, 245, 250));
            titleLabel.setForeground(new Color(65, 139, 179));
        }
    }

    public void cancelTransfer() {
        if (transfer != null) {
            transfer.cancel();
        }
    }

}
