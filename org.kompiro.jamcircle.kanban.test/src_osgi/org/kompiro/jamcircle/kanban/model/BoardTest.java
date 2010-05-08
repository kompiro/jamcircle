package org.kompiro.jamcircle.kanban.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.kompiro.jamcircle.kanban.service.internal.AbstractKanbanTest;


public class BoardTest extends AbstractKanbanTest{

	@Test
	public void cards() throws Exception {
		Board board = createBoardForTest("TEST_BOARD");
		Card card = createCardForTest(board,"BoardTest.cards");

		board.addCard(card);
		assertThat(board.getCards()[0],sameInstance(card));
		assertThat(board.containCard(card),is(true));

		board.removeCard(card);
		assertThat(board.getCards().length,is(0));
		assertThat(board.containCard(card),is(false));
	}
	
	@Test
	public void lanes() throws Exception {
		Board board = createBoardForTest("TEST_BOARD");
		Lane lane = createLaneForTest(board,"BoardTest.lanes");

		board.addLane(lane);
		assertThat(board.getLanes()[0],sameInstance(lane));
		assertThat(board.containLane(lane),is(true));

		board.removeLane(lane);
		assertThat(board.getCards().length,is(0));
		assertThat(board.containLane(lane),is(false));
	}
	
}
