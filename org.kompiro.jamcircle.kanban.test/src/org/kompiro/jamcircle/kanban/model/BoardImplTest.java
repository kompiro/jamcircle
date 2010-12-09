package org.kompiro.jamcircle.kanban.model;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import net.java.ao.EntityManager;

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
	public void add_card() throws Exception {
		Board board = mock(Board.class);
		EntityManager manager = mock(EntityManager.class);
		when(board.getEntityManager()).thenReturn(manager);

		BoardImpl impl = new BoardImpl(board);
		Card card = mock(Card.class);
		when(card.isMock()).thenReturn(false);
		impl.addCard(card);

		verify(manager).flush(card, board);
		verify(card).save(false);
		verify(card).setBoard(board);
	}

	@Test
	public void add_card_to_same_board() throws Exception {
		Board board = mock(Board.class);
		when(board.getID()).thenReturn(1);

		EntityManager manager = mock(EntityManager.class);
		when(board.getEntityManager()).thenReturn(manager);

		BoardImpl impl = new BoardImpl(board);
		Card card = mock(Card.class);
		when(card.getBoard()).thenReturn(board);
		when(card.isMock()).thenReturn(false);
		impl.addCard(card);

		verify(manager).flush(card, board);
		verify(card).save(false);
		verify(card, never()).setBoard(board);
	}

	@Test
	public void add_mock_card() throws Exception {
		Board board = mock(Board.class);
		EntityManager manager = mock(EntityManager.class);
		when(board.getEntityManager()).thenReturn(manager);

		BoardImpl impl = new BoardImpl(board);
		Card card = mock(Card.class);
		when(card.isMock()).thenReturn(true);
		impl.addCard(card);

		verify(manager, never()).flush(card, board);
	}

	@Test
	public void add_lane() throws Exception {
		Board board = mock(Board.class);
		EntityManager manager = mock(EntityManager.class);
		when(board.getEntityManager()).thenReturn(manager);

		BoardImpl impl = new BoardImpl(board);
		Lane lane = mock(Lane.class);
		when(lane.isMock()).thenReturn(false);
		impl.addLane(lane);

		verify(manager).flush(lane, board);
		verify(lane).save(false);
		verify(lane).setBoard(board);
	}

	@Test
	public void add_mock_lane() throws Exception {
		Board board = mock(Board.class);
		EntityManager manager = mock(EntityManager.class);
		when(board.getEntityManager()).thenReturn(manager);

		BoardImpl impl = new BoardImpl(board);
		Lane lane = mock(Lane.class);
		when(lane.isMock()).thenReturn(true);
		impl.addLane(lane);

		verify(manager, never()).flush(lane, board);
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
