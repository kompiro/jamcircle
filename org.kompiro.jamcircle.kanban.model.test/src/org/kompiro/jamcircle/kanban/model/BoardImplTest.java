package org.kompiro.jamcircle.kanban.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import net.java.ao.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.storage.model.ExecutorHandler;
import org.mockito.ArgumentCaptor;

public class BoardImplTest {

	private ExecutorHandler handler;
	private EntityManager manager;
	private Board board;
	private BoardImpl impl;
	private PropertyChangeListener listener;
	private List<Lane> mockLanes;
	private List<Card> mockCards;

	@SuppressWarnings("unchecked")
	@Before
	public void before() throws Exception {
		handler = mock(ExecutorHandler.class);
		manager = mock(EntityManager.class);

		board = mock(Board.class);
		when(board.getID()).thenReturn(1);
		when(board.getEntityManager()).thenReturn(manager);

		impl = new BoardImpl(board);
		listener = mock(PropertyChangeListener.class);
		impl.addPropertyChangeListener(listener);

		mockCards = mock(List.class);
		impl.setMockCards(mockCards);
		mockLanes = mock(List.class);
		impl.setMockLanes(mockLanes);

	}

	@Test
	public void add_card() throws Exception {

		Card card = mockCard();
		when(card.isMock()).thenReturn(false);
		impl.addCard(card);

		verify(manager).flush(card, board);
		verify(card).setTrashed(false);
		verify(card).save(false);
		verify(card).setBoard(board);

		PropertyChangeEvent value = captorPropertyChangeEvent();
		assertThat(value, is(not(nullValue())));
		assertThat(value.getPropertyName(), is(Board.PROP_CARD));
		assertThat((Card) value.getNewValue(), is(card));

	}

	@Test
	public void add_card_to_same_board() throws Exception {

		Card card = mockCard();
		when(card.getBoard()).thenReturn(board);
		when(card.isMock()).thenReturn(false);
		impl.addCard(card);

		verify(mockCards, never()).add(card);
		verify(manager).flush(card, board);
		verify(card).setTrashed(false);
		verify(card).save(false);
		verify(card, never()).setBoard(board);
	}

	@Test
	public void add_mock_card() throws Exception {

		Card card = mockCard();
		when(card.isMock()).thenReturn(true);
		impl.addCard(card);

		verify(manager, never()).flush(card, board);
		verify(mockCards).add(card);
		verify(card).setTrashed(false);

		PropertyChangeEvent value = captorPropertyChangeEvent();
		assertThat(value, is(not(nullValue())));
		assertThat(value.getPropertyName(), is(Board.PROP_CARD));
		assertThat((Card) value.getNewValue(), is(card));

	}

	@Test(expected = IllegalArgumentException.class)
	public void add_null_card() throws Exception {
		impl.addCard(null);
	}

	@Test
	public void remove_card() throws Exception {
		Card card = mockCard();
		when(card.isMock()).thenReturn(false);
		impl.removeCard(card);

		verify(mockCards, never()).remove(card);
		verify(card).setBoard(null);
		verify(manager).flush(card, board);
		verify(card).save(false);

		PropertyChangeEvent value = captorPropertyChangeEvent();
		assertThat(value, is(not(nullValue())));
		assertThat(value.getPropertyName(), is(Board.PROP_CARD));
		assertThat((Card) value.getOldValue(), is(card));

	}

	@Test
	public void remove_mock_card() throws Exception {

		Card card = mockCard();
		when(card.isMock()).thenReturn(true);
		impl.removeCard(card);

		verify(mockCards).remove(card);
		verify(card).setBoard(null);
		verify(manager, never()).flush(card, board);

		PropertyChangeEvent value = captorPropertyChangeEvent();
		assertThat(value, is(not(nullValue())));
		assertThat(value.getPropertyName(), is(Board.PROP_CARD));
		assertThat((Card) value.getOldValue(), is(card));

	}

	@Test(expected = IllegalArgumentException.class)
	public void remove_null_card() throws Exception {
		impl.removeCard(null);
	}

	@Test
	public void add_lane() throws Exception {

		Lane lane = mockLane();
		when(lane.isMock()).thenReturn(false);
		impl.addLane(lane);

		verify(mockLanes, never()).add(lane);
		verify(manager).flush(lane, board);
		verify(lane).save(false);
		verify(lane).setBoard(board);

		PropertyChangeEvent value = captorPropertyChangeEvent();
		assertThat(value, is(not(nullValue())));
		assertThat(value.getPropertyName(), is(Board.PROP_LANE));
		assertThat((Lane) value.getNewValue(), is(lane));
	}

	@Test
	public void add_mock_lane() throws Exception {

		Lane lane = mockLane();
		impl.addLane(lane);

		verify(mockLanes).add(lane);
		verify(manager, never()).flush(lane, board);

		PropertyChangeEvent value = captorPropertyChangeEvent();
		assertThat(value, is(not(nullValue())));
		assertThat(value.getPropertyName(), is(Board.PROP_LANE));
		assertThat((Lane) value.getNewValue(), is(lane));

	}

	@Test(expected = IllegalArgumentException.class)
	public void add_null_lane() throws Exception {
		impl.addLane(null);
	}

	@Test
	public void remove_lane() throws Exception {

		Lane lane = mockLane();
		when(lane.isMock()).thenReturn(false);
		impl.removeLane(lane);

		verify(mockLanes, never()).remove(lane);
		verify(manager).flush(lane, board);
		verify(lane).save(false);
		verify(lane).setBoard(null);

		PropertyChangeEvent value = captorPropertyChangeEvent();
		assertThat(value, is(not(nullValue())));
		assertThat(value.getPropertyName(), is(Board.PROP_LANE));
		assertThat((Lane) value.getOldValue(), is(lane));
	}

	@Test
	public void remove_mock_lane() throws Exception {
		Lane lane = mockLane();

		impl.removeLane(lane);

		verify(mockLanes).remove(lane);
		verify(lane).setBoard(null);

		PropertyChangeEvent value = captorPropertyChangeEvent();
		assertThat(value, is(not(nullValue())));
		assertThat(value.getPropertyName(), is(Board.PROP_LANE));
		assertThat((Lane) value.getOldValue(), is(lane));

	}

	@Test(expected = IllegalArgumentException.class)
	public void remove_null_lane() throws Exception {
		impl.removeLane(null);
	}

	@Test
	public void clearMocks() throws Exception {

		ArrayList<Card> cards = new ArrayList<Card>();
		Card card1 = mockCard();
		cards.add(card1);

		ArrayList<Lane> lanes = new ArrayList<Lane>();
		Lane lane1 = mockLane();
		lanes.add(lane1);

		impl.setMockCards(cards);
		impl.setMockLanes(lanes);
		impl.clearMocks();

		verify(card1).setBoard(null);
		verify(card1).setDeletedVisuals(true);
		verify(lane1).setBoard(null);
		verify(lane1).setDeletedVisuals(true);

		ArgumentCaptor<PropertyChangeEvent> captor = ArgumentCaptor.forClass(PropertyChangeEvent.class);
		verify(listener, times(2)).propertyChange(captor.capture());
		List<PropertyChangeEvent> values = captor.getAllValues();

		PropertyChangeEvent cardEvent = values.get(0);
		assertThat((Card) cardEvent.getOldValue(), is(card1));
		assertThat(cardEvent.getPropertyName(), is(Board.PROP_CARD));

		PropertyChangeEvent laneEvent = values.get(1);
		assertThat((Lane) laneEvent.getOldValue(), is(lane1));
		assertThat(laneEvent.getPropertyName(), is(Board.PROP_LANE));

		assertThat(cards.size(), is(0));
		assertThat(lanes.size(), is(0));

	}

	@Test
	public void should_call_save_by_handler() throws Exception {

		impl.setHandler(handler);
		impl.save(false);

		verify(handler, only()).handle((Runnable) anyObject());
	}

	@Test
	public void should_not_call_save_by_handler_when_set_directory() throws Exception {

		impl.setHandler(handler);
		impl.save(true);

		verify(handler, never()).handle((Runnable) anyObject());
	}

	private Lane mockLane() {
		Lane lane = mock(Lane.class);
		when(lane.isMock()).thenReturn(true);
		return lane;
	}

	private Card mockCard() {
		Card mock = mock(Card.class);
		when(mock.isMock()).thenReturn(true);
		return mock;
	}

	private PropertyChangeEvent captorPropertyChangeEvent() {
		ArgumentCaptor<PropertyChangeEvent> captor = ArgumentCaptor.forClass(PropertyChangeEvent.class);
		verify(listener).propertyChange(captor.capture());
		PropertyChangeEvent value = captor.getValue();
		return value;
	}

}
