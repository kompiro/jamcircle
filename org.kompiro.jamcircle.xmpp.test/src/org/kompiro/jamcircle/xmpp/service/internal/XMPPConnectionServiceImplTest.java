package org.kompiro.jamcircle.xmpp.service.internal;

import static org.junit.Assert.*;

import org.jivesoftware.smack.XMPPConnection;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.User;
import org.kompiro.jamcircle.xmpp.XMPPActivator;
import org.kompiro.jamcircle.xmpp.service.internal.XMPPConnectionServiceImpl;

public class XMPPConnectionServiceImplTest {
	
	
	@Test
	public void getCurrentUser() throws Exception {
		XMPPConnectionServiceImpl service = new XMPPConnectionServiceImpl();
		service.setConnection(new XMPPConnection("MockService"){
			@Override
			public String getUser() {
				return "kompiro@test";
			}
			@Override
			public boolean isConnected() {
				return true;
			}
		});
		assertNotNull(service.getConnection());
		User currentUser = service.getCurrentUser();
		assertNotNull(currentUser);
	}
}