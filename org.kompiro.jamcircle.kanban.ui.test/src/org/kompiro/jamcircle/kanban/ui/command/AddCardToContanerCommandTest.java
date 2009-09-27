package org.kompiro.jamcircle.kanban.ui.command;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.CardContainer;
import org.kompiro.jamcircle.kanban.model.CardContainer.Mock;

public class AddCardToContanerCommandTest {

	private Mock container;
	private AddCardToContanerCommand command;
	
	@Before
	public void before() throws Exception{
		createCommand();
	}

	@Test
	public void undo() {
		command.execute();
		command.undo();
		assertThat(container.getCards().length,is(0));
	}


	@Test
	public void execute() {
		command.execute();
		assertTrue(command.canUndo());
		assertThat(container.getCards().length,is(1));
	}


	private void createCommand() {
		container = new CardContainer.Mock();
		Card card = mock(Card.class);
		command = new AddCardToContanerCommand(container , card);
	}

	@Test
	public void redo() {
		command.execute();
		command.undo();
		command.redo();
		assertThat(container.getCards().length,is(1));
	}

}
