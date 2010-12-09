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
	private EntityManager manager;
	private Board board;

	@Before
	public void before() throws Exception {
		handler = mock(ExecutorHandler.class);
		manager = mock(EntityManager.class);
		board = mock(Board.class);
		when(board.getID()).thenReturn(1);
		when(board.getEntityManager()).thenReturn(manager);

	}

	@Test
	public void add_card() throws Exception {

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

		BoardImpl impl = new BoardImpl(board);
		Card card = mock(Card.class);
		when(card.isMock()).thenReturn(true);
		impl.addCard(card);

		verify(manager, never()).flush(card, board);
	}

	@Test(expected = IllegalArgumentException.class)
	public void add_null_card() throws Exception {
		BoardImpl impl = new BoardImpl(board);
		impl.addCard(null);
	}

	@Test
	public void remove_card() throws Exception {
		BoardImpl impl = new BoardImpl(board);
		Card card = mock(Card.class);
		when(card.isMock()).thenReturn(false);
		impl.removeCard(card);

		verify(card).setBoard(null);
		verify(manager).flush(card, board);
		verify(card).save(false);
	}

	@Test
	public void remove_mock_card() throws Exception {
		BoardImpl impl = new BoardImpl(board);
		Card card = mock(Card.class);
		when(card.isMock()).thenReturn(true);
		impl.removeCard(card);

		verify(card).setBoard(null);
		verify(manager, never()).flush(card, board);
	}

	@Test(expected = IllegalArgumentException.class)
	public void remove_null_card() throws Exception {
		BoardImpl impl = new BoardImpl(board);
		impl.removeCard(null);
	}

	@Test
	public void add_lane() throws Exception {

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

		BoardImpl impl = new BoardImpl(board);
		Lane lane = mock(Lane.class);
		when(lane.isMock()).thenReturn(true);
		impl.addLane(lane);

		verify(manager, never()).flush(lane, board);
	}

	@Test(expected = IllegalArgumentException.class)
	public void add_null_lane() throws Exception {
		BoardImpl impl = new BoardImpl(board);
		impl.addLane(null);
	}

	@Test
	public void should_call_save() throws Exception {

		BoardImpl impl = new BoardImpl(board);
		impl.setHandler(handler);
		impl.save(false);

		verify(handler, only()).handle((Runnable) anyObject());
		verify(board, never()).save();
	}

	@Test
	public void should_call_save_directory() throws Exception {

		BoardImpl impl = new BoardImpl(board);
		impl.setHandler(handler);
		impl.save(true);

		verify(handler, never()).handle((Runnable) anyObject());
		verify(board, only()).save();
	}

}
