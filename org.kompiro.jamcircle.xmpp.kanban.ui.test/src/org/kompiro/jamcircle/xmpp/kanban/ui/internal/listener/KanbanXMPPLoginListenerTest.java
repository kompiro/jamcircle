package org.kompiro.jamcircle.xmpp.kanban.ui.internal.listener;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.jivesoftware.smack.*;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.KanbanView;
import org.kompiro.jamcircle.xmpp.kanban.ui.internal.XMPPKanbanUIContext;


public class KanbanXMPPLoginListenerTest {
	
	@Test
	public void afterLoggedInNoBehavior() throws Exception {
		XMPPConnection connection = mock(XMPPConnection.class);
		Roster roster = mock(Roster.class);
		ChatManager manager = mock(ChatManager.class);
		KanbanService kanbanService = mock(KanbanService.class);
		XMPPKanbanUIContext context = new XMPPKanbanUIContext();
		context.setKanbanService(kanbanService);
		KanbanView view = mock(KanbanView.class);
		
		when(connection.getRoster()).thenReturn(roster);
		when(connection.getChatManager()).thenReturn(manager);
		
		KanbanXMPPLoginListener listener = new KanbanXMPPLoginListener();
		listener.setKanbanView(view);
		listener.afterLoggedIn(connection);
	}

}
