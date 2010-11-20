package org.kompiro.jamcircle.kanban.model;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.storage.model.ExecutorHandler;

public class BoardImplTest {

	private ExecutorHandler handler;

	@Before
	public void before() throws Exception {
		handler = mock(ExecutorHandler.class);
	}

	@Test
	public void should_call_save() throws Exception {
		Board board = mock(Board.class);

		BoardImpl impl = new BoardImpl(board);
		impl.setHandler(handler);
		impl.save(false);

		verify(handler, only()).handle((Runnable) anyObject());
		verify(board, never()).save();
	}

	@Test
	public void should_call_save_directory() throws Exception {
		Board board = mock(Board.class);

		BoardImpl impl = new BoardImpl(board);
		impl.setHandler(handler);
		impl.save(true);

		verify(handler, never()).handle((Runnable) anyObject());
		verify(board, only()).save();
	}

}
