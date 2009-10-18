package org.kompiro.jamcircle.xmpp.kanban.ui.internal.command;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.CardContainer;


public class CreateCardCommandTest extends AbstractCommandTest {

	private CreateCardCommand command;
	private Card card;
	private CardContainer container;

	@Test
	public void initialize() throws Exception {
		CreateCardCommand command = new CreateCardCommand();
		command.initialize();
		assertThat(command.canExecute(), is(false));
		Card card= mock(Card.class);
		CardContainer container = mock(CardContainer.class);
		command.setModel(card);
		command.setContainer(container);
		command.initialize();
		assertThat(command.canExecute(), is(true));
	}
	
	@Override
	public void execute() throws Exception {
		command.execute();
		verify(container).addCard(card);
	}
	
	@Override
	public void undo() throws Exception {
		assertThat(command.canUndo(),is(false));
		command.execute();
		assertThat(command.canUndo(),is(true));
		command.undo();
		verify(container).removeCard(card);
	}
	
	@Override
	public void redo() throws Exception {
		command.execute();
		command.undo();
		command.redo();
		verify(container,times(2)).addCard(card);
	}
	
	@Override
	protected void createCommand() {
		command = new CreateCardCommand();
		card= mock(Card.class);
		command.setModel(card);
		container = mock(CardContainer.class);
		command.setContainer(container);
		command.initialize();
	}

}
