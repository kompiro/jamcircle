package org.kompiro.jamcircle.kanban.ui.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.mock.*;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.service.internal.KanbanServiceImpl;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class TrashModelTest {
	
	private TrashModel trash;
	private KanbanService service;
	private Card card;
	private Lane lane;
	
	@Before
	public void before() throws Exception{
		createModel();
	}

	@Test
	public void containCard() throws Exception {
		doCallRealMethod().when(service).discardToTrash(card);
		doCallRealMethod().when(service).pickupFromTrash(card);
		assumeThat(trash.containCard(card),is(false));
		trash.addCard(card);
		assertTrue(trash.containCard(card));
		trash.removeCard(card);
		assertFalse(trash.containCard(card));
	}
	
	@Test
	public void getCards() throws Exception {
		doCallRealMethod().when(service).discardToTrash(card);
		doCallRealMethod().when(service).pickupFromTrash(card);
		assertThat(trash.getCards(),nullValue());
		doAnswer(new Answer<?>() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				when(service.findCardsInTrash()).thenReturn(new Card[]{card});
				return null;
			}
		}).when(card).setTrashed(true);
		doAnswer(new Answer<?>() {

			public Object answer(InvocationOnMock invocation) throws Throwable {
				when(service.findCardsInTrash()).thenReturn(null);
				return null;
			}
		}).when(card).setTrashed(false);
		trash.addCard(card);
		assertThat(trash.getCards().length,is(1));
		trash.removeCard(card);
		assertThat(trash.getCards(),nullValue());
	}
	
	@Test
	public void addCard() throws Exception {
		doCallRealMethod().when(service).discardToTrash(card);
		doCallRealMethod().when(service).pickupFromTrash(card);
		trash.addCard(card);
		assertTrue(card.isTrashed());
	}
	
	@Test
	public void removeCard() throws Exception {
		doCallRealMethod().when(service).discardToTrash(card);
		doCallRealMethod().when(service).pickupFromTrash(card);
		trash.addCard(card);
		trash.removeCard(card);
		assertFalse(card.isTrashed());
	}
	
	@Test
	public void countTrashedCard() throws Exception {
		doCallRealMethod().when(service).discardToTrash(card);
		doCallRealMethod().when(service).pickupFromTrash(card);
		doAnswer(new Answer<?>() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				when(service.countInTrash(eq(org.kompiro.jamcircle.kanban.model.Card.class))).thenReturn(1);
				return null;
			}
		}).when(card).setTrashed(true);
		doAnswer(new Answer<?>() {

			public Object answer(InvocationOnMock invocation) throws Throwable {
				when(service.countInTrash(eq(org.kompiro.jamcircle.kanban.model.Card.class))).thenReturn(0);
				return null;
			}
		}).when(card).setTrashed(false);
		assumeThat(trash.countTrashedCard(),is(0));
		trash.addCard(card);
		assertThat(trash.countTrashedCard(),is(1));
		trash.removeCard(card);
		assertThat(trash.countTrashedCard(),is(0));
	}
	
	@Test
	public void addLane() throws Exception {
		doCallRealMethod().when(service).discardToTrash(lane);
		doCallRealMethod().when(service).pickupFromTrash(lane);
		trash.addLane(lane);
		assertTrue(lane.isTrashed());
	}
	
	@Test
	public void removeLane() throws Exception {
		doCallRealMethod().when(service).discardToTrash(lane);
		doCallRealMethod().when(service).pickupFromTrash(lane);
		trash.addLane(lane);
		trash.removeLane(lane);
		assertFalse(lane.isTrashed());
	}
	
	@Test
	public void getLanes() throws Exception {
		doCallRealMethod().when(service).discardToTrash(lane);
		doCallRealMethod().when(service).pickupFromTrash(lane);
		assertThat(trash.getLanes(),nullValue());
		doAnswer(new Answer<?>() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				when(service.findLanesInTrash()).thenReturn(new Lane[]{lane});
				return null;
			}
		}).when(lane).setTrashed(true);
		doAnswer(new Answer<?>() {

			public Object answer(InvocationOnMock invocation) throws Throwable {
				when(service.findLanesInTrash()).thenReturn(null);
				return null;
			}
		}).when(lane).setTrashed(false);
		trash.addLane(lane);
		assertThat(trash.getLanes().length,is(1));
		trash.removeLane(lane);
		assertThat(trash.getLanes(),nullValue());
	}
	
	@Test
	public void countTrashedLane() throws Exception {
		doCallRealMethod().when(service).discardToTrash(lane);
		doCallRealMethod().when(service).pickupFromTrash(lane);
		doAnswer(new Answer<?>() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				when(service.countInTrash(eq(org.kompiro.jamcircle.kanban.model.Lane.class))).thenReturn(1);
				return null;
			}
		}).when(lane).setTrashed(true);
		doAnswer(new Answer<?>() {

			public Object answer(InvocationOnMock invocation) throws Throwable {
				when(service.countInTrash(eq(org.kompiro.jamcircle.kanban.model.Lane.class))).thenReturn(0);
				return null;
			}
		}).when(lane).setTrashed(false);

		assumeThat(trash.countTrashedLane(),is(0));
		trash.addLane(lane);
		assertThat(trash.countTrashedLane(),is(1));
		trash.removeLane(lane);
		assertThat(trash.countTrashedLane(),is(0));
	}


	private void createModel() {
		Icon icon = new Icon();
		service = mock(KanbanServiceImpl.class);
		
		card = mock(Card.class);
		when(card.isTrashed()).thenCallRealMethod();
		doCallRealMethod().when(card).setTrashed(true);
		doCallRealMethod().when(card).setTrashed(false);

		lane = mock(Lane.class);
		when(lane.isTrashed()).thenCallRealMethod();
		doCallRealMethod().when(lane).setTrashed(true);
		doCallRealMethod().when(lane).setTrashed(false);
		
		trash = new TrashModel(icon,service);
	}

}
