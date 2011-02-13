package org.kompiro.jamcircle.kanban.command;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.eclipse.draw2d.geometry.Rectangle;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.CardContainer;
import org.kompiro.jamcircle.kanban.model.mock.Card;


public class AddCardToOnBoardContainerCommandTest extends AbstractCommandTest{
	
	private Card card;
	private AddCardToOnBoardContainerCommand command;
	private CardContainer container;
	private Rectangle constraint;
	
	@Test
	public void initialize() throws Exception {
		AddCardToOnBoardContainerCommand command = new AddCardToOnBoardContainerCommand(null, null , null);
		assertThat(command.canExecute(),is(false));
		command = new AddCardToOnBoardContainerCommand(mock(Card.class), null , null);
		assertThat(command.canExecute(),is(false));
		command = new AddCardToOnBoardContainerCommand(null, mock(Rectangle.class) , null);
		assertThat(command.canExecute(),is(false));
		command = new AddCardToOnBoardContainerCommand(null, null , mock(CardContainer.class));
		assertThat(command.canExecute(),is(false));
	}
	
	@Test
	public void execute() throws Exception {
		assertThat(card.getBoard(),nullValue(CardContainer.class));
		assertThat(card.getX(),not(constraint.x));
		assertThat(card.getX(),not(constraint.y));

		command.initialize();
		assertTrue(command.canExecute());
		command.execute();
		int expectedX = constraint.x;
		int expectedY = constraint.y;
		assertThat(card.getX(),is(expectedX));
		assertThat(card.getY(),is(expectedY));
		verify(container).addCard(card);
	}
	
	@Override
	public void undo() throws Exception {
		command.execute();
		assertThat(command.canUndo(),is(true));
		command.undo();
		assertThat(card.getX(),is(0));
		assertThat(card.getY(),is(0));
		verify(container).removeCard(card);
	}
	
	@Override
	public void redo() throws Exception {
		command.execute();
		command.undo();
		command.redo();
		int expectedX = constraint.x;
		int expectedY = constraint.y;
		assertThat(card.getX(),is(expectedX));
		assertThat(card.getY(),is(expectedY));
		verify(container,times(2)).addCard(card);
	}

	@Override
	protected void createCommand() {
		card = new Card();
		
		int expectedX = 10;
		int expectedY = 20;
		constraint = new Rectangle(expectedX, expectedY, 100, 50);
		container = mock(CardContainer.class);
		command = new AddCardToOnBoardContainerCommand(card, constraint , container);
	}
	

}
