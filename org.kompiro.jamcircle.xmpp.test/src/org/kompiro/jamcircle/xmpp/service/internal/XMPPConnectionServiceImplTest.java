package org.kompiro.jamcircle.xmpp.service.internal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.jivesoftware.smack.XMPPConnection;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.User;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.xmpp.XMPPActivator;
import org.kompiro.jamcircle.xmpp.service.internal.XMPPConnectionServiceImpl;

public class XMPPConnectionServiceImplTest {
	
	@Test
	public void getCurrentUser() throws Exception {
		XMPPActivator activator = mock(XMPPActivator.class);
		KanbanService kanbanService = mock(KanbanService.class);
		when(activator.getKanbanService()).thenReturn(kanbanService);
		when(kanbanService.hasUser("kompiro@test")).thenReturn(true);
		User user = mock(User.class);
		when(kanbanService.findUser("kompiro@test")).thenReturn(user);
		when(user.getUserName()).thenReturn("testing_value");
		XMPPConnectionServiceImpl service = new XMPPConnectionServiceImpl();
		service.setActivator(activator);
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
		assertEquals("testing_value",user.getUserName());
	}
}