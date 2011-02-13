package org.kompiro.jamcircle.kanban.command;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.CardContainer;

public class RemoveCardCommandTest extends AbstractCommandTest {
	
	private Card card;
	private CardContainer container;
	private RemoveCardCommand command;

	@Test
	@Override
	public void initialize() throws Exception {
		Card card = null;
		CardContainer container = null;
		RemoveCardCommand command = new RemoveCardCommand(card, container);
		command.initialize();
		assertThat(command.canExecute(),is(false));
		card = mock(Card.class);
		container = mock(CardContainer.class);
		command = new RemoveCardCommand(card, container);
		command.initialize();
		assertThat(command.canExecute(),is(true));		
	}

	@Override
	public void execute() throws Exception {
		command.execute();
		verify(container).removeCard(card);
	}
	
	@Override
	public void undo() throws Exception {
		command.execute();
		assertThat(command.canUndo(),is(true));
		command.undo();
		verify(container).addCard(card);
	}
	
	@Override
	public void redo() throws Exception {
		command.execute();
		command.undo();
		command.redo();
		verify(container,times(2)).removeCard(card);		
	}
	
	@Override
	protected void createCommand() {
		card = mock(Card.class);
		container = mock(CardContainer.class);
		command = new RemoveCardCommand(card, container);
		command.initialize();
	}

}
