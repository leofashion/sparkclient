package com.sweet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import com.sweet.status.ExtendXMLConfig;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.jivesoftware.Spark;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.filter.*;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.filetransfer.SparkTransferManager;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.smack.packet.UnparsedIQ;
import org.jxmpp.stringprep.XmppStringprepException;

public class IAngelClient {

	private static ArrayList<String> groups = null;
	private static UserListIQ groupUsers = new UserListIQ();
	private static Map<String, String> userDeptCache = new HashMap<String, String>();

	public static ArrayList<String> getGroups() {
		return groups;
	}

	public static void setGroups(ArrayList<String> groups) {
		IAngelClient.groups = groups;
	}

	public static ArrayList<String> getUsers(String gname) {
		return groupUsers.getUsers(gname);
	}

	public static Map<String, String> getDeptName() {
		return userDeptCache;
	}

	public static String getUserDeptName(String jid) {
		return userDeptCache.get(jid);
	}

	public static void setUserDeptCache(String jid, String deptName) {
		userDeptCache.put(jid, deptName);
	}

	public static void init() {
		try {
			ProviderManager.addIQProvider(ServerPortIQ.IQ_ELEMENT,
					ServerPortIQ.IQ_NAMESPACE, new ServerPortIQ.Provider());

//			ProviderManager.addIQProvider(GroupListIQ.IQ_ELEMENT,
//					GroupListIQ.IQ_NAMESPACE, new GroupListIQ.Provider());
//
			ProviderManager.addIQProvider(UserListIQ.IQ_ELEMENT,
					UserListIQ.IQ_NAMESPACE, new UserListIQ.Provider());

			ProviderManager.addIQProvider(FileTransferIQ.IQ_ELEMENT,
					FileTransferIQ.IQ_NAMESPACE, new FileTransferIQProvider());

			ProviderManager.addIQProvider(DownFileTransferIQ.IQ_ELEMENT,
					DownFileTransferIQ.IQ_NAMESPACE, new DownFileTransferIQProvider());

			ProviderManager.addIQProvider(FileOperateIQ.IQ_ELEMENT,
					FileOperateIQ.IQ_NAMESPACE, new FileOperateIQProvider());

//			SparkManager.getConnection().addStanzaSendingListener(new StanzaListener() {
//                @Override
//                public void processStanza(Stanza stanza)
//                    throws NotConnectedException, InterruptedException, NotLoggedInException {
//                    ServerPortIQ myIQ = (ServerPortIQ) stanza;
//                    Log.debug("myIQ.getPort():"+myIQ.getPort());
//                    Spark.setServerPort(myIQ.getPort());
//                }
//            }, new StanzaTypeFilter(ServerPortIQ.class));
//            SparkManager.getConnection().addStanzaInterceptor(new StanzaListener() {
//                @Override
//                public void processStanza(Stanza stanza)
//                    throws NotConnectedException, InterruptedException, NotLoggedInException {
//                    ServerPortIQ myIQ = (ServerPortIQ) stanza;
//                    Log.debug("myIQ.getPort():"+myIQ.getPort());
//                    Spark.setServerPort(myIQ.getPort());
//                }
//            }, new StanzaTypeFilter(ServerPortIQ.class));
            SparkManager.getConnection().addAsyncStanzaListener(new StanzaListener() {
                @Override
                public void processStanza(Stanza stanza)
                    throws NotConnectedException, InterruptedException, NotLoggedInException {
                    UnparsedIQ myIQ = (UnparsedIQ) stanza;
                    //获取服务端配置端口
                    if(StringUtils.equals("serverport",myIQ.getChildElementName())){
                        CharSequence content = myIQ.getContent();
                        try {
                            Document document = DocumentHelper.parseText(content.toString());
                            Element portel = document.getRootElement().element("port");
//                            System.out.println(""+portel.getData());
                            Spark.setServerPort(portel.getData().toString());

                            Element ipel = document.getRootElement().element("ip");
//                            System.out.println(""+ipel.getData());
                            Spark.setServerName(ipel.getData().toString());
                        } catch (DocumentException e) {
                            e.printStackTrace();
                        }
                    }
//                    Log.debug("myIQ.getPort():"+myIQ.getContent());
//
                }
            }, new StanzaTypeFilter(UnparsedIQ.class));
//            SparkManager.getConnection().addSyncStanzaListener(new StanzaListener() {
//                @Override
//                public void processStanza(Stanza stanza)
//                    throws NotConnectedException, InterruptedException, NotLoggedInException {
//                    UnparsedIQ myIQ = (UnparsedIQ) stanza;
//                    Log.debug("myIQ.getPort():"+myIQ.getContent());
////                    Spark.setServerPort(myIQ.getPort());
//                }
//            }, new StanzaTypeFilter(UnparsedIQ.class));

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    ServerPortIQ spIQ = new ServerPortIQ();
                    spIQ.setStanzaId("admin" + "@" + SparkManager.getConnection().getXMPPServiceDomain());
                    SparkManager.getConnection().sendIqRequestAsync(spIQ);
                }
            });
//            SparkManager.getConnection().addAsyncStanzaListener(new StanzaListener() {
//                @Override
//                public void processStanza(Stanza stanza)
//                    throws NotConnectedException, InterruptedException, NotLoggedInException {
//                    GroupListIQ myIQ = (GroupListIQ) stanza;
//                    groups = myIQ.getGroups();
//                    SwingUtilities.invokeLater(new Runnable() {
//                        @Override
//                        public void run() {
//                            UserListIQ glIQ = new UserListIQ();
//
//                            glIQ.setStanzaId("admin" + "@" + SparkManager.getConnection().getXMPPServiceDomain());
////                            glIQ.setTo(
////                                "admin" + "@" + SparkManager.getConnection().getServiceName());
//                            //TODO 修改
//                            SparkManager.getConnection().sendIqRequestAsync(glIQ);
////							SparkManager.getConnection().sendPacket(glIQ);
//                        }
//                    });
//                }
//            }, new StanzaFilter() {
//                @Override
//                public boolean accept(Stanza stanza) {
//                    return true;
//                }
//            });
//			SparkManager.getConnection().addPacketListener(new PacketListener() {
//				@Override
//				public void processPacket(Packet p) {
//					GroupListIQ myIQ = (GroupListIQ) p;
//					groups = myIQ.getGroups();
//					SwingUtilities.invokeLater(new Runnable() {
//						public void run() {
//							UserListIQ glIQ = new UserListIQ();
//							glIQ.setTo(
//									"admin" + "@" + SparkManager.getConnection().getServiceName());
//							//TODO 修改
//							SparkManager.getConnection().sendIqRequestAsync(glIQ);
////							SparkManager.getConnection().sendPacket(glIQ);
//						}
//					});
//				}
//			}, new PacketTypeFilter(GroupListIQ.class));

//			SparkManager.getConnection().addPacketListener(new PacketListener() {
//				@Override
//				public void processPacket(Packet p) {
//					UserListIQ myIQ = (UserListIQ) p;
//					groupUsers = myIQ;
//					SparkManager.getContactList().initialize();
//					SparkManager.getWorkspace().getStatusBar().loadVCard();
//					SparkTransferManager.getInstance();
//				}
//			}, new PacketTypeFilter(UserListIQ.class));

//            SparkManager.getConnection().addAsyncStanzaListener(new StanzaListener() {
//                @Override
//                public void processStanza(Stanza stanza)
//                    throws NotConnectedException, InterruptedException, NotLoggedInException {
//                    UserListIQ myIQ = (UserListIQ) stanza;
//                    groupUsers = myIQ;
//                    SparkManager.getContactList().initialize();
//                    SparkManager.getWorkspace().getStatusBar().loadVCard();
//                    SparkTransferManager.getInstance();
//                }
//            }, new StanzaFilter() {
//                @Override
//                public boolean accept(Stanza stanza) {
//                    return true;
//                }
//            });

            // upload offline file
            SparkManager.getConnection().addAsyncStanzaListener(new StanzaListener() {
                @Override
                public void processStanza(Stanza stanza)
                    throws NotConnectedException, InterruptedException, NotLoggedInException {
                    FileTransferIQ myIQ = (FileTransferIQ) stanza;
                    if (myIQ.getStatus().equals("ok")) {
                        OffLineFileTransferUpload oftu = OffLineFileTransferUpload
                            .get(myIQ.getFileName());
                        if (oftu.getPosition() < oftu.getFile().length()
                            && oftu.getTransStatus().equals(OffLineFileTransferComm.ING)) {
                            try {
                                oftu.sendFile(oftu.getPosition());
                            } catch (XmppStringprepException e) {
                                e.printStackTrace();
                            }
                        } else if (!oftu.getTransStatus().equals(OffLineFileTransferComm.CANCEL)) {
                            oftu.setTransStatus(OffLineFileTransferComm.SUCCESS);
                        }
                    }
                }
            }, new StanzaFilter() {
                @Override
                public boolean accept(Stanza stanza) {
                    return true;
                }
            });

			SparkManager.getConnection().addAsyncStanzaListener(new StanzaListener() {
				@Override
				public void processStanza(Stanza p){
					FileTransferIQ myIQ = (FileTransferIQ) p;
					if (myIQ.getStatus().equals("ok")) {
						OffLineFileTransferUpload oftu = OffLineFileTransferUpload
								.get(myIQ.getFileName());
						if (oftu.getPosition() < oftu.getFile().length()
								&& oftu.getTransStatus().equals(OffLineFileTransferComm.ING)) {
                            try {
                                oftu.sendFile(oftu.getPosition());
                            } catch (XmppStringprepException e) {
                                e.printStackTrace();
                            }
                        } else if (!oftu.getTransStatus().equals(OffLineFileTransferComm.CANCEL)) {
							oftu.setTransStatus(OffLineFileTransferComm.SUCCESS);
						}
					}
				}
			}, new StanzaTypeFilter(FileTransferIQ.class));

			// download file

            SparkManager.getConnection().addAsyncStanzaListener(new StanzaListener() {
                @Override
                public void processStanza(Stanza stanza)
                    throws NotConnectedException, InterruptedException, NotLoggedInException {
                    DownFileTransferIQ myIQ = (DownFileTransferIQ) stanza;
                    String data = myIQ.getData();
                    long fsize = myIQ.getFsize();
                    String serverFileName = myIQ.getFileName();
                    OffLineFileTransferDown oftd = OffLineFileTransferDown.get(serverFileName);
                    if (oftd != null && oftd.getTransStatus().equals(OffLineFileTransferComm.ING)) {
                        if (data != null && data.length() > 0) {
                            oftd.setFsize(fsize);
                            oftd.writeFile(data);
                        } else {
                            oftd.setTransStatus(OffLineFileTransferComm.SUCCESS);
                        }
                    }
                }
            }, new StanzaFilter() {
                @Override
                public boolean accept(Stanza stanza) {
                    return true;
                }
            });
			SparkManager.getConnection().addAsyncStanzaListener(new StanzaListener() {
				@Override
				public void processStanza(Stanza p) {
					DownFileTransferIQ myIQ = (DownFileTransferIQ) p;
					String data = myIQ.getData();
					long fsize = myIQ.getFsize();
					String serverFileName = myIQ.getFileName();
					OffLineFileTransferDown oftd = OffLineFileTransferDown.get(serverFileName);
					if (oftd != null && oftd.getTransStatus().equals(OffLineFileTransferComm.ING)) {
						if (data != null && data.length() > 0) {
							oftd.setFsize(fsize);
							oftd.writeFile(data);
						} else {
							oftd.setTransStatus(OffLineFileTransferComm.SUCCESS);
						}
					}
				}
			}, new StanzaTypeFilter(DownFileTransferIQ.class));




//			SwingUtilities.invokeLater(new Runnable() {
//				@Override
//                public void run() {
//					GroupListIQ glIQ = new GroupListIQ();
//                    glIQ.setStanzaId("admin" + "@" + SparkManager.getConnection().getXMPPServiceDomain());
////					glIQ.setTo("admin" + "@" + SparkManager.getConnection().getServiceName());
//
//					//TODO 修改
//					SparkManager.getConnection().sendIqRequestAsync(glIQ);
////					SparkManager.getConnection().sendPacket(glIQ);
//				}
//			});
		} catch (Exception e) {
			Log.error(e);
		}
	}
}
