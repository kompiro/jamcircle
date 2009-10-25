package org.kompiro.jamcircle.xmpp.service.internal;

import java.io.File;
import java.util.*;

import org.eclipse.core.runtime.IProgressMonitor;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.kompiro.jamcircle.kanban.model.*;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.xmpp.XMPPActivator;
import org.kompiro.jamcircle.xmpp.XMPPStatusHandler;
import org.kompiro.jamcircle.xmpp.service.*;
import org.kompiro.jamcircle.xmpp.util.XMPPUtil;

/**
 * 
 * @author Hiroki Kondo<kompiro@gmail.com>
 */
public class XMPPConnectionServiceImpl implements XMPPConnectionService {

	public static final String KEY_OF_SYSTEM_PROP_XMPP_CONNECT = XMPPActivator.PLUGIN_ID + ".connect";
	private XMPPConnection connection;
	private List<XMPPLoginListener> listeners = new ArrayList<XMPPLoginListener>();
	private XMPPActivator activator;
	private FileTransferManager manager;
	
	static final MessageListener doEmpty = new DoEmptyMessageListener();
	
	private static final class DoEmptyMessageListener implements
			MessageListener {
		public void processMessage(Chat chat, Message message) {
		}
	}


	public void login(IProgressMonitor monitor, 
			String host, String resource,
			String serviceName, int port, String username,
			String password) throws XMPPException {
		monitor.beginTask("Connectiong to " + host + "...", 100);
		if (getConnection() != null) {
			getConnection().disconnect();
		}
		XMPPStatusHandler.debug("createConnection: host:'%s' resource:'%s' serviceName:'%s' port:'%d' username:'%s' password'%s'"
					,host,resource,serviceName,port,username,password);
		ConnectionConfiguration connConfig = new ConnectionConfiguration(host,
				port, serviceName);
		XMPPConnection connection = new XMPPConnection(connConfig);
		try {
			monitor.internalWorked(10);
			connection.connect();
			monitor.subTask("connected.");
			monitor.internalWorked(60);
			if(resource == null || "".equals(resource)){
				resource = DEFAULT_RESOURCE_NAME;
			}
		    SASLAuthentication.supportSASLMechanism("PLAIN", 0);
		    // hack : http://www.igniterealtime.org/community/thread/35976
		    String loginname = username;
		    if(host.equals("talk.google.com") && username.indexOf("@") < 0){
		    	loginname += "@" + serviceName;
		    }
		    connection.login(loginname, password, resource);
			monitor.subTask("logged in.");
			monitor.internalWorked(30);
			getSettings().add(host,resource,serviceName,username,password,port);
			// call for SecureUICallbackProvider
			// Master Password is set by call this provider. 
			getSettings().storeSttings();
			this.connection = connection;
			if(XMPPStatusHandler.isDebug()){
				this.connection.addPacketListener(new PacketListener(){
					public void processPacket(Packet packet) {
						XMPPStatusHandler.debug(packet.toXML());
					}
				}, null);
			}
			for (XMPPLoginListener listener : listeners) {
				listener.afterLoggedIn(connection);
			}
			getKanbanService().changeCurrentUser(getCurrentUser());
			// FIXME system handling
			System.setProperty(KEY_OF_SYSTEM_PROP_XMPP_CONNECT, String.valueOf(true));
			createFileTransferManager(connection);
		} catch (XMPPException e) {
			XMPPStatusHandler.fail(e, "can't disconnect",false);
			try {
				connection.disconnect();
			} catch (Exception ex) {
				XMPPStatusHandler.fail(ex, "can't disconnect",false);
			}
			throw e;
		}
		monitor.done();
	}

	private void createFileTransferManager(
			XMPPConnection connection) {
		this.manager = new FileTransferManager(connection);
	}	
	
	public XMPPConnection getConnection() {
		if (connection != null && connection.isConnected()) {
			return connection;
		}
		return null;
	}
	
	public void addXMPPLoginListener(XMPPLoginListener listener) {
		listeners.add(listener);
	}

	public void removeXMPPLoginListener(XMPPLoginListener listener) {
		listeners.remove(listener);
	}

	public XMPPSettings getSettings() {
		return XMPPActivator.getDefault().getSettings();
	}
	
	public void logout(IProgressMonitor monitor) {
		monitor.beginTask("Disconnect from server.", 100);
		monitor.internalWorked(10);
		for (XMPPLoginListener listener : listeners) {
			listener.beforeLoggedOut(connection);
		}
		getConnection().disconnect();
		monitor.internalWorked(80);
		System.setProperty(KEY_OF_SYSTEM_PROP_XMPP_CONNECT, String.valueOf(false));
		monitor.done();
	}
	
	public boolean isConnecting() {
		return connection != null && connection.isConnected() && connection.isAuthenticated() ;
	}

	public User[] getUsers() {
		return getKanbanService().findAllUsers();
	}

	public void addUser(String userId) {
		getKanbanService().addUser(userId);
	}

	public void deleteUser(String userId) {
		getKanbanService().deleteUser(userId);
	}

	
	public boolean hasUser(String user) {
		return getKanbanService().hasUser(user);
	}
	
	User getCurrentUser(){
		XMPPStatusHandler.debug("getCurrentUser()");
		XMPPConnection connection = getConnection();
		User user = null;
		if(connection != null){
			String userId = XMPPUtil.getRemovedResourceUser(connection.getUser());
			XMPPStatusHandler.debug("userId:'%s'",userId);
			KanbanService service = getKanbanService();
			if(!service.hasUser(userId)){
				user = service.addUser(userId);
			}
			user = service.findUser(userId);
		}
		return user;
	}
		
	private KanbanService getKanbanService(){
		return activator.getKanbanService();
	}
	
	public void setActivator(XMPPActivator activator) {
		this.activator = activator;
	}
	
	void setConnection(XMPPConnection connection) {
		this.connection = connection;
	}

	public void sendCard(User user,Card card) {
		XMPPConnection connection = getConnection();
		String userId = user.getUserId();
		Presence precense = connection.getRoster().getPresence(userId);
		String debugMessage = String.format("target:'%s'", user);
		XMPPStatusHandler.debug(debugMessage);
		if(!precense.isAvailable()){
			return;
		}

		ChatManager chatManager = connection.getChatManager();
		// Create the file transfer manager
		FileTransferManager manager = getFileTransferManager(connection);
		
		// Create the outgoing file transfer
		CardDTO dto = new CardDTO(card);
		Message messageObject = new Message();
		messageObject.setBody("message form JAM Circle");
		messageObject.setProperty(XMPPConnectionService.PROP_SEND_CARD, dto);
		Iterator<Presence> precenses = connection.getRoster().getPresences(userId);
		try {
			Chat chat = chatManager.createChat(userId,doEmpty);
			chat.sendMessage(messageObject);
			while(precenses.hasNext()){
				Presence targetPrecense = precenses.next();
				String targetUser = targetPrecense.getFrom();
				// Send the file
				for(File file : card.getFiles()){
					String uuid = card.getUUID();
					OutgoingFileTransfer transfer = manager.createOutgoingFileTransfer(targetUser);
					transfer.sendFile(file,uuid);
				}
				
			}
		} catch (XMPPException e) {
			XMPPStatusHandler.fail(e, "send message failed.",true);
			return;
		}
		card.setTo(getUser(userId));
		card.save(false);
	}

	public boolean isAvailable(User user) {
		if(user == null) return false;
		Presence presence = getConnection().getRoster().getPresence(user.getUserId());
		if(presence == null) return false;
		return presence.isAvailable();
	}

	FileTransferManager getFileTransferManager(XMPPConnection connection) {
		return this.manager;
	}
	
	void setFileTransferManager(FileTransferManager manager){
		this.manager = manager;
	}
	
	private User getUser(String userId) {
		return getKanbanService().findUser(userId);
	}


}
