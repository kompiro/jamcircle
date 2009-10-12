package org.kompiro.jamcircle.kanban.ui.command;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeThat;

import org.eclipse.draw2d.geometry.Rectangle;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.CardContainer;
import org.kompiro.jamcircle.kanban.model.mock.Board;
import org.kompiro.jamcircle.kanban.model.mock.Card;
import org.kompiro.jamcircle.kanban.ui.internal.command.AddCardToOnBoardContainerCommand;


public class AddCardToOnBoardContainerCommandTest {
	
	private Card card;
	private AddCardToOnBoardContainerCommand command;
	private CardContainer board = new Board();

	@Test
	public void execute() throws Exception {
		card = new Card();
		int expectedX = 10;
		assumeThat(card.getX(),not(expectedX));
		int expectedY = 20;
		assumeThat(card.getX(),not(expectedY));
		assumeThat(card.getBoard(),nullValue(CardContainer.class));
		
		Rectangle constraint = new Rectangle(expectedX, expectedY, 100, 50);
		command = new AddCardToOnBoardContainerCommand(card, constraint , board);
		assertTrue(command.canExecute());
		command.execute();
		assertTrue(command.canUndo());
		assertThat(card.getX(),is(expectedX));
		assertThat(card.getY(),is(expectedY));
		assertThat(card.getBoard(),is(board));
	}
	

}
