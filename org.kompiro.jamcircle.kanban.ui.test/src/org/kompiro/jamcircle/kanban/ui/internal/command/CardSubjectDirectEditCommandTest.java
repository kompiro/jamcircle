package org.kompiro.jamcircle.kanban.ui.internal.command;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.Card;


public class CardSubjectDirectEditCommandTest extends AbstractCommandTest{

	private static final String EXPECTED_SUBJECT = "subject";
	private Card card;
	private String subject;
	private CardSubjectDirectEditCommand command;

	@Test
	public void initialize() throws Exception {
		Card card = null;
		String subject = null;
		CardSubjectDirectEditCommand command = new CardSubjectDirectEditCommand(card, subject);
		command.initialize();
		assertThat(command.canExecute(),is(false));
		
		card = mock(Card.class);
		subject = EXPECTED_SUBJECT;
		command = new CardSubjectDirectEditCommand(card, subject);
		command.initialize();
		assertThat(command.canExecute(),is(true));
	}
	
	@Override
	public void execute() throws Exception {
		command.execute();
		verify(card).setSubject(EXPECTED_SUBJECT);
	}
	
	@Override
	public void undo() throws Exception {
		assertThat(command.canUndo(),is(false));
		command.execute();
		assertThat(command.canUndo(),is(true));
		command.undo();
		verify(card).setSubject(null);
	}
	
	@Override
	public void redo() throws Exception {
		command.execute();
		command.undo();
		command.redo();
		verify(card,times(2)).setSubject(EXPECTED_SUBJECT);
	}
	
	@Override
	protected void createCommand() {
		card = mock(Card.class);
		subject = EXPECTED_SUBJECT;
		command = new CardSubjectDirectEditCommand(card, subject);
	}

}
