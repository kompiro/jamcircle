package org.kompiro.jamcircle.xmpp.service;


import org.eclipse.core.runtime.IProgressMonitor;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.kompiro.jamcircle.kanban.model.User;

public interface XMPPConnectionService {

	public static final String DEFAULT_RESOURCE_NAME = "JAM_CIRCLE";

	void login(IProgressMonitor monitor,String host , String resource,String serviceName, int port,String username,String password) throws XMPPException;
	
	void logout(IProgressMonitor monitor);
	
	boolean isConnecting();
	
	XMPPConnection getConnection();
	
	User[] getUsers();
	
	boolean hasUser(String user);
	
	void addUser(String user);

	void deleteUser(String user);
	
	XMPPSettings getSettings();
	
	void addXMPPLoginListener(XMPPLoginListener listener);
	
	void removeXMPPLoginListener(XMPPLoginListener listener);
}
