package org.kompiro.jamcircle.xmpp.service;

import org.jivesoftware.smack.XMPPConnection;

public interface XMPPLoginListener {

	void afterLoggedIn(XMPPConnection connection);
	
	void beforeLoggedOut(XMPPConnection connection);
	
}
