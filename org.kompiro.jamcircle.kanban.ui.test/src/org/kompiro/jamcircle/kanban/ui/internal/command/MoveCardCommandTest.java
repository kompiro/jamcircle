package org.kompiro.jamcircle.kanban.ui.internal.command;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import org.eclipse.draw2d.geometry.Rectangle;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.Card;

public class MoveCardCommandTest extends AbstractCommandTest {
	
	private static final int EXPECTED_X = 10;
	private static final int EXPECTED_Y = 20;
	private MoveCardCommand command;
	private Rectangle rect;
	private Card card;

	@Test
	public void initialize() throws Exception {
		MoveCardCommand command = new MoveCardCommand();
		command.initialize();
		assertThat(command.canExecute(),is(false));

		Rectangle rect = new Rectangle();
		command.setRectangle(rect);
		
		Card card = mock(Card.class);
		command.setModel(card);
		command.initialize();

		assertThat(command.canExecute(),is(true));
	}
	
	@Override
	public void execute() throws Exception {
		command.execute();
		verify(card).setX(EXPECTED_X);
		verify(card).setY(EXPECTED_Y);
	}
	
	@Override
	public void undo() throws Exception {
		assertThat(command.canUndo(),is(false));
		command.execute();
		assertThat(command.canUndo(),is(true));
		command.undo();
		verify(card).setX(0);
		verify(card).setY(0);
	}

	@Override
	public void redo() throws Exception {
		command.execute();
		command.undo();
		command.redo();
		verify(card,times(2)).setX(EXPECTED_X);
		verify(card,times(2)).setY(EXPECTED_Y);		
	}
	
	@Override
	protected void createCommand() {
		command = new MoveCardCommand();
		rect = new Rectangle(EXPECTED_X,EXPECTED_Y,100,150);
		card = mock(Card.class);
		command.setRectangle(rect);
		command.setModel(card);
	}

}
