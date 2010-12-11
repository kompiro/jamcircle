package org.kompiro.jamcircle.kanban.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import net.java.ao.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.storage.model.ExecutorHandler;
import org.mockito.ArgumentCaptor;

public class LaneImplTest {

	private EntityManager manager;
	private LaneImpl impl;
	private Lane lane;
	private PropertyChangeListener listener;
	private ExecutorHandler handler;

	@Before
	public void before() throws Exception {
		handler = mock(ExecutorHandler.class);
		manager = mock(EntityManager.class);
		Board board = mock(Board.class);
		lane = mock(Lane.class);
		when(lane.getBoard()).thenReturn(board);
		when(lane.getEntityManager()).thenReturn(manager);
		impl = new LaneImpl(lane);
		listener = mock(PropertyChangeListener.class);
		impl.addPropertyChangeListener(listener);

	}

	@Test
	public void add_card() throws Exception {
		Card card = mock(Card.class);
		impl.addCard(card);

		verify(card).setLane(lane);
		verify(card).save(false);
		verify(card).setTrashed(false);
		verify(card).setDeletedVisuals(false);
		verify(manager).flush(card);

		ArgumentCaptor<PropertyChangeEvent> captor = ArgumentCaptor.forClass(PropertyChangeEvent.class);
		verify(listener).propertyChange(captor.capture());
		PropertyChangeEvent value = captor.getValue();
		assertThat(value.getPropertyName(), is(Lane.PROP_CARD));
		assertThat((Card) value.getNewValue(), is(card));
	}

	@Test
	public void add_mock_card() throws Exception {

		Card card = mock(Card.class);
		when(card.isMock()).thenReturn(true);

		impl.addCard(card);

		verify(card).setLane(lane);
		verify(card).save(false);
		verify(card).setTrashed(false);
		verify(card).setDeletedVisuals(false);
		verify(manager, never()).flush(card);

		ArgumentCaptor<PropertyChangeEvent> captor = ArgumentCaptor.forClass(PropertyChangeEvent.class);
		verify(listener).propertyChange(captor.capture());
		PropertyChangeEvent value = captor.getValue();
		assertThat(value.getPropertyName(), is(Lane.PROP_CARD));
		assertThat((Card) value.getNewValue(), is(card));

	}

	@Test(expected = IllegalArgumentException.class)
	public void add_null_card() throws Exception {
		impl.addCard(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void add_card_when_lanes_board_is_null() throws Exception {

		when(lane.getBoard()).thenReturn(null);
		Card card = mock(Card.class);
		impl.addCard(card);

	}

	@Test
	public void remove_card() throws Exception {
		Card card = mock(Card.class);

		impl.removeCard(card);

		verify(card).setLane(null);
		verify(card).setBoard(null);
		verify(card).save(false);
		verify(card).setDeletedVisuals(true);
		verify(manager).flush(card);

		ArgumentCaptor<PropertyChangeEvent> captor = ArgumentCaptor.forClass(PropertyChangeEvent.class);
		verify(listener).propertyChange(captor.capture());
		PropertyChangeEvent value = captor.getValue();
		assertThat(value.getPropertyName(), is(Lane.PROP_CARD));
		assertThat((Card) value.getOldValue(), is(card));

	}

	@Test
	public void remove_mock_card() throws Exception {
		Card card = mock(Card.class);
		when(card.isMock()).thenReturn(true);
		impl.removeCard(card);

		verify(card).setLane(null);
		verify(card).setBoard(null);
		verify(card).save(false);
		verify(card).setDeletedVisuals(true);
		verify(manager, never()).flush(card);

		ArgumentCaptor<PropertyChangeEvent> captor = ArgumentCaptor.forClass(PropertyChangeEvent.class);
		verify(listener).propertyChange(captor.capture());
		PropertyChangeEvent value = captor.getValue();
		assertThat(value.getPropertyName(), is(Lane.PROP_CARD));
		assertThat((Card) value.getOldValue(), is(card));

	}

	@Test(expected = IllegalArgumentException.class)
	public void remove_null_card() throws Exception {
		impl.removeCard(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void remove_card_when_lanes_board_is_null() throws Exception {

		when(lane.getBoard()).thenReturn(null);
		Card card = mock(Card.class);
		impl.removeCard(card);

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
}
