package org.kompiro.jamcircle.kanban.ui.command;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.*;
import org.kompiro.jamcircle.kanban.model.mock.Card;
import org.kompiro.jamcircle.kanban.model.mock.Lane;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.KanbanUIActivator;
import org.kompiro.jamcircle.kanban.ui.gcontroller.CardEditPart;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;
import org.kompiro.jamcircle.xmpp.service.XMPPConnectionService;


public class CardCloneCommandTest {
	
	private static final Point TARGET_POINT = new Point(10,10);

	@Test
	public void execute() throws Exception {
		Card card = new Card();
		CardContainer target = mock(Lane.class);
		ChangeBoundsRequest request = new ChangeBoundsRequest();
		request.setLocation(TARGET_POINT);
		BoardModel board = mock(BoardModel.class);
		EditPart part = new CardEditPart(board);
		part.setModel(card);
		request.setEditParts(part);
		CardCloneCommand command = new CardCloneCommand(request, target);
		
		KanbanUIActivator spy = spy(new KanbanUIActivator());
		command.setActivator(spy);

		XMPPConnectionService xmppService = mock(XMPPConnectionService.class);
		doReturn(xmppService).when(spy).getConnectionService();
		
		KanbanService kanbanService = mock(KanbanService.class);
		when(kanbanService.createClonedCard((Board)any(), (User)any(), eq(card), eq(0), eq(0))).thenReturn(card);
		doReturn(kanbanService).when(spy).getKanbanService();

		command.execute();
		assertTrue(command.canUndo());
		verify(target,times(1)).addCard((Card)any());
	}

}
