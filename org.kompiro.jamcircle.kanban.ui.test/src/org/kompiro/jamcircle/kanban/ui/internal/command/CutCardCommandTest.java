package org.kompiro.jamcircle.kanban.ui.internal.command;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.CardContainer;


public class CutCardCommandTest extends AbstractCommandTest{
	
	private CardContainer container;
	private CutCardCommand command;
	private Card card;

	@Test
	public void initialize() throws Exception {
		CardContainer container = null;
		Card card = null;
		CutCardCommand command = new CutCardCommand(container, card);
		command.initialize();
		assertThat(command.canExecute(),is(false));
		
		container = mock(CardContainer.class);
		card = mock(Card.class);
		command = new CutCardCommand(container, card);
		command.initialize();
		assertThat(command.canExecute(),is(true));
	}

	@Override
	public void execute() throws Exception {
		command.execute();
		verify(card).setTrashed(true);
		verify(container).removeCard(card);
	}
	
	@Override
	public void undo() throws Exception {
		command.execute();
		assertThat(command.canUndo(),is(true));
		command.undo();
		verify(card).setTrashed(false);
		verify(container).addCard(card);
	}
	
	@Override
	public void redo() throws Exception {
		command.execute();
		command.undo();
		command.redo();
		verify(card,times(2)).setTrashed(true);
		verify(container,times(2)).removeCard(card);
		
	}
	
	@Override
	protected void createCommand() {
		container = mock(CardContainer.class);
		card = mock(Card.class);
		command = new CutCardCommand(container, card);
		command.initialize();
	}

}
