package org.kompiro.jamcircle.kanban.ui.command;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.junit.Before;
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
	private KanbanUIActivator activatorSpy;
	private CardContainer container;
	private Card card;
	private CardCloneCommand command;

	@Test
	public void execute() throws Exception {
		command.execute();
		assertTrue(command.canUndo());
		verify(container,times(1)).addCard(card);
	}
	
	@Test
	public void undo() throws Exception {
		command.execute();
		command.undo();
		verify(container,times(1)).removeCard(card);
	}
	
	@Test
	public void redo() throws Exception {
		command.execute();
		command.undo();
		command.redo();
		verify(container,times(2)).addCard(card);
		
	}

	@Before
	public void createdCommand() {
		card = new Card();
		activatorSpy = spy(new KanbanUIActivator());
		container = mock(Lane.class);
		command = createCommand(card);
	}

	private CardCloneCommand createCommand(Card card) {
		ChangeBoundsRequest request = createRequest(card);
		CardCloneCommand command = new CardCloneCommand(request, container);
		command.setActivator(activatorSpy);
		createXMPPService();
		createKanbanService();
		return command;
	}

	private ChangeBoundsRequest createRequest(Card card) {
		ChangeBoundsRequest request = new ChangeBoundsRequest();
		request.setLocation(TARGET_POINT);
		EditPart part = createCardEditPart(card);
		request.setEditParts(part);
		return request;
	}

	private EditPart createCardEditPart(Card card) {
		BoardModel board = mock(BoardModel.class);
		EditPart part = new CardEditPart(board);
		part.setModel(card);
		return part;
	}

	private void createKanbanService() {
		KanbanService kanbanService = mock(KanbanService.class);
		when(kanbanService.createClonedCard((Board)any(), (User)any(), eq(card), eq(0), eq(0))).thenReturn(card);
		doReturn(kanbanService).when(activatorSpy).getKanbanService();
	}

	private void createXMPPService() {
		XMPPConnectionService xmppService = mock(XMPPConnectionService.class);
		doReturn(xmppService).when(activatorSpy).getConnectionService();
	}

}
