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
import org.kompiro.jamcircle.kanban.service.internal.KanbanServiceImpl;
import org.kompiro.jamcircle.storage.service.internal.StorageServiceImpl;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

@SuppressWarnings("restriction")
public class TrashModelTest {
	
	private TrashModel trash;
	private KanbanServiceImpl kanbanService;
	private Card card;
	private Lane lane;
	
	@Before
	public void before() throws Exception{
		createModel();
	}

	@Test
	public void containCard() throws Exception {
		assumeThat(trash.containCard(card),is(false));
		trash.addCard(card);
		assertTrue(trash.containCard(card));
		trash.removeCard(card);
		assertFalse(trash.containCard(card));
	}
	
	@Test
	public void getCards() throws Exception {
		assertThat(trash.getCards(),nullValue());
		doAnswer(new Answer<?>() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				when(kanbanService.findCardsInTrash()).thenReturn(new Card[]{card});
				return null;
			}
		}).when(card).setTrashed(true);
		doAnswer(new Answer<?>() {

			public Object answer(InvocationOnMock invocation) throws Throwable {
				when(kanbanService.findCardsInTrash()).thenReturn(null);
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
		trash.addCard(card);
		assertTrue(card.isTrashed());
	}
	
	@Test
	public void removeCard() throws Exception {
		trash.addCard(card);
		trash.removeCard(card);
		assertFalse(card.isTrashed());
	}
	
	@Test
	public void countTrashedCard() throws Exception {
		doAnswer(new Answer<?>() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				doReturn(1).when(kanbanService).countInTrash(eq(org.kompiro.jamcircle.kanban.model.Card.class));
				return null;
			}
		}).when(card).setTrashed(true);
		doAnswer(new Answer<?>() {

			public Object answer(InvocationOnMock invocation) throws Throwable {
				doReturn(0).when(kanbanService).countInTrash(eq(org.kompiro.jamcircle.kanban.model.Card.class));
				return null;
			}
		}).when(card).setTrashed(false);
		trash.addCard(card);
		assertThat(trash.countTrashedCard(),is(1));
		trash.removeCard(card);
		assertThat(trash.countTrashedCard(),is(0));
	}
	
	@Test
	public void addLane() throws Exception {
		trash.addLane(lane);
		assertTrue(lane.isTrashed());
	}
	
	@Test
	public void removeLane() throws Exception {
		trash.addLane(lane);
		trash.removeLane(lane);
		assertFalse(lane.isTrashed());
	}
	
	@Test
	public void getLanes() throws Exception {
		doReturn(null).when(kanbanService).findLanesInTrash();
		assertThat(trash.getLanes(),nullValue());
		doAnswer(new Answer<?>() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				when(kanbanService.findLanesInTrash()).thenReturn(new Lane[]{lane});
				return null;
			}
		}).when(lane).setTrashed(true);
		doAnswer(new Answer<?>() {

			public Object answer(InvocationOnMock invocation) throws Throwable {
				when(kanbanService.findLanesInTrash()).thenReturn(null);
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
		doAnswer(new Answer<?>() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				doReturn(1).when(kanbanService).countInTrash(eq(org.kompiro.jamcircle.kanban.model.Lane.class));
				return null;
			}
		}).when(lane).setTrashed(true);
		doAnswer(new Answer<?>() {

			public Object answer(InvocationOnMock invocation) throws Throwable {
				doReturn(0).when(kanbanService).countInTrash(eq(org.kompiro.jamcircle.kanban.model.Lane.class));
				return null;
			}
		}).when(lane).setTrashed(false);

		trash.addLane(lane);
		assertThat(trash.countTrashedLane(),is(1));
		trash.removeLane(lane);
		assertThat(trash.countTrashedLane(),is(0));
	}


	private void createModel() {
		Icon icon = new Icon();
		KanbanServiceImpl kanbanServiceImpl = new KanbanServiceImpl();
		kanbanService = spy(kanbanServiceImpl);
		StorageServiceImpl storageService = spy(new StorageServiceImpl());
		kanbanService.setStorageService(storageService);
		card = spy(new Card());
		lane = spy(new Lane());
		trash = new TrashModel(icon,kanbanService);
	}

}
