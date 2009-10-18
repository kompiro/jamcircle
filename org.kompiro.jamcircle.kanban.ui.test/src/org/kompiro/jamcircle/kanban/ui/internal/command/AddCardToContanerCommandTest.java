package org.kompiro.jamcircle.kanban.ui.internal.command;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.CardContainer;
import org.kompiro.jamcircle.kanban.model.CardContainer.Mock;

public class AddCardToContanerCommandTest extends AbstractCommandTest{

	private Mock container;
	private AddCardToContanerCommand command;

	@Test
	public void initialize() throws Exception {
		assertThat(command.canExecute(),is(true));
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

	@Test
	public void redo() {
		command.execute();
		command.undo();
		command.redo();
		assertThat(container.getCards().length,is(1));
	}

	protected void createCommand() {
		container = new CardContainer.Mock();
		Card card = mock(Card.class);
		command = new AddCardToContanerCommand(container , card);
		command.initialize();
	}

}
