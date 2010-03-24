package org.kompiro.jamcircle.kanban.ui.internal.command;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.*;
import org.kompiro.jamcircle.kanban.model.mock.Card;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.KanbanUIActivator;
import org.mockito.ArgumentCaptor;


public class CardStoreCommandTest extends AbstractCommandTest{

	private CardContainer container;
	private Card mock;
	private CardStoreCommand command;
	
	@Test
	public void initialize() throws Exception {
		assertThat(command.canExecute(),is(true));	
	}

	@Test
	public void execute() throws Exception {
		command.execute();
		ArgumentCaptor<Card> args = ArgumentCaptor.forClass(Card.class);
		verify(container).addCard(args.capture());
		assertThat(args.getValue().getSubject(),is("test"));
	}
	
	@Test
	public void undo() throws Exception {
		assertThat(command.canUndo(),is(false));
	}
	
	@Override
	public void redo() throws Exception {
		// no op
	}
	
	@Override
	protected void createCommand() {
		mock = new Card();
		mock.setSubject("test");
		container = mock(CardContainer.class);
		command = new CardStoreCommand(mock, container);
		KanbanUIActivator activator = mock(KanbanUIActivator.class);
		KanbanService service = mock(KanbanService.class);
		when(service.createClonedCard((Board)any(), (User) any(), (Card)any(), anyInt(), anyInt())).thenReturn(mock);
		when(activator.getKanbanService()).thenReturn(service);
		command.setActivator(activator );
	}

}
