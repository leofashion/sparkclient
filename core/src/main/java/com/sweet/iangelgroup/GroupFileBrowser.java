//package com.sweet.iangelgroup;
//
//import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.Desktop;
//import java.awt.FileDialog;
//import java.awt.GridBagConstraints;
//import java.awt.GridBagLayout;
//import java.awt.Insets;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.io.File;
//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import javax.swing.JButton;
//import javax.swing.JDialog;
//import javax.swing.JFrame;
//import javax.swing.JLabel;
//import javax.swing.JOptionPane;
//import javax.swing.JPanel;
//import javax.swing.JProgressBar;
//import javax.swing.JScrollPane;
//
//import org.jivesoftware.resource.Res;
//import org.jivesoftware.resource.SparkRes;
//import org.jivesoftware.spark.SparkManager;
//import org.jivesoftware.spark.component.TitlePanel;
//import org.jivesoftware.spark.component.VerticalFlowLayout;
//
//import com.sweet.OffLineFileTransferComm;
//import com.sweet.OffLineFileTransferDown;
//
//public class GroupFileBrowser extends JPanel {
//
//	private static final long serialVersionUID = 235919593865652333L;
//
//	private JPanel listPanel = new JPanel();
//	private JProgressBar progressBar = new JProgressBar();
//	private String roomName = null;
//
//	public void display(final String jid, final String roomName) {
//
//		this.roomName = roomName;
//
//		TitlePanel titlePanel;
//		titlePanel = new TitlePanel("群共享文件", "仅供查看和下载，管理员可维护",
//				SparkRes.getImageIcon(SparkRes.BLANK_IMAGE), true);
//
//		final JPanel mainPanel = new JPanel();
//		mainPanel.setLayout(new BorderLayout());
//		mainPanel.add(titlePanel, BorderLayout.NORTH);
//
//		listPanel.setLayout(new GridBagLayout());
//
//		initFileInfo();
//
//		JPanel contentPanel = new JPanel();
//		contentPanel.setLayout(new VerticalFlowLayout());
//		contentPanel.add(listPanel);
//
//		mainPanel.add(new JScrollPane(contentPanel), BorderLayout.CENTER);
//
//		final JOptionPane p = new JOptionPane();
//
//		JFrame parent = SparkManager.getChatManager().getChatContainer().getChatFrame();
//		if (parent == null || !parent.isVisible()) {
//			parent = SparkManager.getMainWindow();
//		}
//
//		final JDialog dlg = p.createDialog(parent, "群共享文件");
//		dlg.setModal(false);
//
//		dlg.pack();
//		dlg.setSize(550, 350);
//		dlg.setResizable(true);
//		dlg.setContentPane(mainPanel);
//		dlg.setLocationRelativeTo(SparkManager.getMainWindow());
//
//		dlg.setVisible(true);
//		dlg.toFront();
//		dlg.requestFocus();
//	}
//
//	private void initFileInfo() {
//		if (listPanel.getComponentCount() > 0) {
//			listPanel.removeAll();
//		}
//		initTitle();
//		initData(roomName);
//	}
//
//	private void initTitle() {
//		String[] columnName = { "文件名称", "大小", "上传者", "", "" };
//		for (int i = 0; i < columnName.length; i++) {
//			GridBagConstraints gbc = new GridBagConstraints();
//			gbc.gridx = i;
//			gbc.gridy = 0;
//			listPanel.add(new JLabel(columnName[i]), gbc);
//		}
//	}
//
//	private void initData(final String roomTitle) {
//		ArrayList<GroupFile> files = GroupHelper.getGroupFiles(roomTitle);
//		GroupUserIQ groupUser = GroupHelper.getGroupUsers(roomTitle);
//
//		String userId = SparkManager.getSessionManager().getBareAddress();
//		boolean admin = groupUser.isManager(userId);
//
//		Insets insets = new Insets(5, 5, 5, 5);
//		for (int i = 0; i < files.size(); i++) {
//			final GroupFile f = files.get(i);
//			final int row = i + 1;
//
//			String fn = f.getFileName();
//			fn = fn.replaceAll("[0-9]{13}\\-", "");
//			JLabel fname = new JLabel(fn);
//
//			listPanel.add(fname, new GridBagConstraints(0, row, 1, 1, 1.0, 0.0,
//					GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
//
//			JLabel fsize = new JLabel(convertFileSize(f.getFileSize()));
//			listPanel.add(fsize, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0,
//					GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));
//
//			JLabel jid = new JLabel(SparkManager.getUserManager().getNickname(f.getJid()));
//			listPanel.add(jid, new GridBagConstraints(2, row, 1, 1, 0.0, 0.0,
//					GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
//
//			JButton dl = new JButton("下载");
//			dl.addActionListener(new ActionListener() {
//
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					FileDialog fileChooser = new FileDialog(SparkManager.getMainWindow(),
//							Res.getString("selectfilePath"), FileDialog.SAVE);
//					String fileName = f.getFileName();
//					fileChooser.setFile(fileName.substring(fileName.indexOf("-") + 1));
//					fileChooser.setVisible(true);
//
//					if (fileChooser.getDirectory() == null || fileChooser.getFile() == null) {
//						return;
//					}
//					String url = fileChooser.getDirectory();
//					if (url.lastIndexOf("\\") == url.length() - 1) {
//						url = url.substring(0, url.lastIndexOf("\\"));
//					}
//					File file = new File(fileChooser.getDirectory(), fileChooser.getFile());
//
//					String fromUser = roomTitle + "@" + GroupHelper.group();
//					String toUser = SparkManager.getVCardManager().getVCard().getJabberId();
//					OffLineFileTransferDown oftd = new OffLineFileTransferDown(url, file.getName(),
//							fileName, fromUser, toUser);
//					OffLineFileTransferDown.put(fileName, oftd);
//					oftd.downFile();
//					requestOffLineFile(file, fileName, row);
//				}
//			});
//
//			listPanel.add(dl, new GridBagConstraints(3, row, 1, 1, 0.0, 0.0,
//					GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
//
//			final JButton del = new JButton("删除");
//			del.addActionListener(new ActionListener() {
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					int ok = JOptionPane.showConfirmDialog(del, "请确认是否删除该文件?", "确认",
//							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
//					if (ok == JOptionPane.NO_OPTION) {
//						return;
//					} else if (ok == JOptionPane.CLOSED_OPTION) {
//						return;
//					}
//
//					GroupFile groupFile = new GroupFile();
//					groupFile.setFileName(f.getFileName());
//					GroupHelper.deleteGroupFile(roomTitle, groupFile);
//					initFileInfo();
//					listPanel.validate();
//					listPanel.repaint();
//				}
//			});
//			if (admin) {
//				listPanel.add(del, new GridBagConstraints(4, row, 1, 1, 0.0, 0.0,
//						GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
//			}
//		}
//	}
//
//	private void requestOffLineFile(final File savedFile, final String serverFileName, int row) {
//		setBackground(new Color(239, 245, 250));
//
//		listPanel.add(progressBar, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0,
//				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
//
//		progressBar.setMaximum(100);
//		progressBar.setStringPainted(true);
//
//		final Timer timer = new Timer();
//		TimerTask updateProgessBar = new TimerTask() {
//			@Override
//			public void run() {
//				OffLineFileTransferDown oftd = OffLineFileTransferDown.get(serverFileName);
//				if (oftd != null && oftd.finished()) {
//					updateOffLineonFinished(savedFile, oftd);
//					this.cancel();
//					timer.cancel();
//					OffLineFileTransferDown.remove(serverFileName);
//				} else if (oftd != null && oftd.downloading() && oftd.getFsize() > 0) {
//					long p = (Long.valueOf(oftd.getPosition()) * 100 / oftd.getFsize());
//					progressBar.setValue(Math.round(p));
//				} else if (oftd != null && oftd.canceled()) {
//					this.cancel();
//					timer.cancel();
//					progressBar.setVisible(false);
//					OffLineFileTransferDown.remove(serverFileName);
//				}
//			}
//		};
//
//		final Timer timer2 = new Timer();
//		TimerTask updatePrograssBarText = new TimerTask() {
//			@Override
//			public void run() {
//				OffLineFileTransferDown oftd = OffLineFileTransferDown.get(serverFileName);
//				if (oftd.getFsize() > 0 && oftd.getPosition() >= oftd.getFsize()) {
//					this.cancel();
//					timer2.cancel();
//				} else if (oftd.getFsize() > 0) {
//					if (oftd.getTransStatus().equals(OffLineFileTransferComm.SUCCESS)) {
//						OffLineFileTransferDown.remove(serverFileName);
//						updateOffLineonFinished(savedFile, oftd);
//					}
//					if (oftd.getTransStatus().equals(OffLineFileTransferComm.CANCEL)) {
//						this.cancel();
//						timer2.cancel();
//						progressBar.setVisible(false);
//						OffLineFileTransferComm.deleteServerFile(serverFileName);
//					}
//				}
//			}
//		};
//		timer.scheduleAtFixedRate(updateProgessBar, 10, 10);
//		timer2.scheduleAtFixedRate(updatePrograssBarText, 10, 500);
//	}
//
//	private void updateOffLineonFinished(final File downloadedFile,
//			final OffLineFileTransferDown ofd) {
//		if (Long.valueOf(ofd.getPosition()) >= ofd.getFsize()) {
//			progressBar.setVisible(false);
//			launchFile(downloadedFile);
//		}
//	}
//
//	private void launchFile(File file) {
//		if (!Desktop.isDesktopSupported())
//			return;
//		Desktop dt = Desktop.getDesktop();
//		try {
//			dt.open(file);
//		} catch (IOException ex) {
//			launchFile(file.getPath());
//		}
//	}
//
//	private void launchFile(String filePath) {
//		if (filePath == null || filePath.trim().length() == 0)
//			return;
//		if (!Desktop.isDesktopSupported())
//			return;
//		Desktop dt = Desktop.getDesktop();
//		try {
//			dt.browse(getFileURI(filePath));
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//	}
//
//	private static URI getFileURI(String filePath) {
//		URI uri = null;
//		filePath = filePath.trim();
//		if (filePath.indexOf("http") == 0 || filePath.indexOf("\\") == 0) {
//			if (filePath.indexOf("\\") == 0)
//				filePath = "file:" + filePath;
//			try {
//				filePath = filePath.replaceAll(" ", "%20");
//				URL url = new URL(filePath);
//				uri = url.toURI();
//			} catch (MalformedURLException ex) {
//				ex.printStackTrace();
//			} catch (URISyntaxException ex) {
//				ex.printStackTrace();
//			}
//		} else {
//			File file = new File(filePath);
//			uri = file.toURI();
//		}
//		return uri;
//	}
//
//	public static String convertFileSize(long size) {
//		long kb = 1024;
//		long mb = kb * 1024;
//		long gb = mb * 1024;
//
//		if (size >= gb) {
//			return String.format("%.1f GB", (float) size / gb);
//		} else if (size >= mb) {
//			float f = (float) size / mb;
//			return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
//		} else if (size >= kb) {
//			float f = (float) size / kb;
//			return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
//		} else
//			return String.format("%d B", size);
//	}
//}
