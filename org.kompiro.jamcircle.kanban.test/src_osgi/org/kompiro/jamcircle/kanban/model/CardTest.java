package org.kompiro.jamcircle.kanban.model;

import org.junit.Test;
import org.kompiro.jamcircle.kanban.service.internal.AbstractKanbanTest;

public class CardTest  extends AbstractKanbanTest{

	@Test
	public void no_exceptions_are_occured_when_call_toString() throws Exception {
		
		Board board = createBoardForTest("TEST_BOARD");
		Card card = createCardForTest(board, "Test");
		System.out.println(card.toString());
		
	}
	
}
