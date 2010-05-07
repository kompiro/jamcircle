package org.kompiro.jamcircle.kanban.model.mock;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Ignore;
import org.junit.Test;


public class BoardTest {

	@Test
	public void cards() throws Exception {
		
		Card card = mock(Card.class);
		Board board = new Board();
		board.addCard(card);
		assertThat(card,sameInstance(board.getCards()[0]));
		
	}
	
	@Ignore
	@Test
	public void icons() throws Exception {
	}
}
