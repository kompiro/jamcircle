package org.kompiro.jamcircle.kanban.model;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.kompiro.jamcircle.kanban.service.internal.AbstractKanbanTest;
import org.kompiro.jamcircle.storage.model.ExecutorHandler;

public class CardTest extends AbstractKanbanTest {

	@Test
	public void call_handler() throws Exception {
		Board board = createBoardForTest("TEST_BOARD");
		Card card = createCardForTest(board, "test");

		ExecutorHandler handler = mock(ExecutorHandler.class);
		card.setHandler(handler);

		card.save(false);
		verify(handler).handle((Runnable) any());
	}

	@Test
	public void no_exceptions_are_occured_when_call_toString() throws Exception {

		Board board = createBoardForTest("TEST_BOARD");
		Card card = createCardForTest(board, "Test");
		System.out.println(card.toString());

	}

}
