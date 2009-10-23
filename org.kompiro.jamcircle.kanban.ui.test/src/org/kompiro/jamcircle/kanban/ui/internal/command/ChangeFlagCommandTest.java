package org.kompiro.jamcircle.kanban.ui.internal.command;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.FlagTypes;


public class ChangeFlagCommandTest extends AbstractCommandTest{
	
	private Card card;
	private FlagTypes type;
	private ChangeFlagCommand command;

	@Test
	public void initialize() throws Exception {
		Card card = null;
		FlagTypes type = null;
		ChangeFlagCommand command = new ChangeFlagCommand(card, type);
		assertThat(command.canExecute(),is(false));
		card = mock(Card.class);
		type = FlagTypes.BLUE;
		command = new ChangeFlagCommand(card, type);
		assertThat(command.canExecute(),is(true));
	}
	
	@Override
	public void execute() throws Exception {
		command.execute();
		verify(card).setFlagType(FlagTypes.BLUE);
	}
	
	@Override
	public void undo() throws Exception {
		command.execute();
		assertThat(command.canExecute(), is(true));
		command.undo();
		verify(card).setFlagType(null);
	}
	
	@Override
	public void redo() throws Exception {
		command.execute();
		command.undo();
		command.redo();
		verify(card,times(2)).setFlagType(FlagTypes.BLUE);
	}

	@Override
	protected void createCommand() {
		card = mock(Card.class);
		type = FlagTypes.BLUE;
		command = new ChangeFlagCommand(card, type);
	}

}
