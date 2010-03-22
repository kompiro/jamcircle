package org.kompiro.jamcircle.kanban.ui.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.Board;
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
	private StorageServiceImpl storageService;
	
	@Before
	public void before() throws Exception{
		Icon icon = new Icon();
		kanbanService = spy(new KanbanServiceImpl());
		storageService = spy(new StorageServiceImpl());
		kanbanService.setStorageService(storageService);
		card = spy(new Card());
		lane = spy(new Lane());
		trash = new TrashModel(icon,kanbanService);
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
		doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				doReturn(new Card[]{card}).when(kanbanService).findCardsInTrash();
				return null;
			}
		}).when(card).setTrashed(true);
		doAnswer(new Answer<Object>() {

			public Object answer(InvocationOnMock invocation) throws Throwable {
				doReturn(null).when(kanbanService).findCardsInTrash();
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
		doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				doReturn(1).when(kanbanService).countInTrash(eq(org.kompiro.jamcircle.kanban.model.Card.class));
				return null;
			}
		}).when(card).setTrashed(true);
		doAnswer(new Answer<Object>() {

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
		doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				when(kanbanService.findLanesInTrash()).thenReturn(new Lane[]{lane});
				return null;
			}
		}).when(lane).setTrashed(true);
		doAnswer(new Answer<Object>() {

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
		doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				doReturn(1).when(kanbanService).countInTrash(eq(org.kompiro.jamcircle.kanban.model.Lane.class));
				return null;
			}
		}).when(lane).setTrashed(true);
		doAnswer(new Answer<Object>() {

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
	
	@Test
	public void addBoard_BoardOnly() throws Exception {
		ConfirmStrategy confirmStrategy = createConfirm(true);
		trash.setConfirmStrategy(confirmStrategy );
		Board board = mock(Board.class);
		doAnswer(new Answer<Boolean>() {
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				return true;
			}
		}).when(storageService).delete(eq(board));
		trash.addBoard(board);
		verify(storageService).delete(eq(board));
	}
	
	@Test
	public void addBoard_not_confirmed() throws Exception {
		ConfirmStrategy confirmStrategy = createConfirm(false);
		trash.setConfirmStrategy(confirmStrategy );
		Board board = mock(Board.class);
		trash.addBoard(board);
		verify(kanbanService,never()).delete(eq(board));
		verify(storageService,never()).delete(eq(board));
	}
	
	@Test
	public void addBoard_some_cards_and_some_lanes() throws Exception {
		
		ConfirmStrategy confirmStrategy = createConfirm(true);
		trash.setConfirmStrategy(confirmStrategy);
		Board board = mock(Board.class);
		doAnswer(new Answer<Boolean>() {
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				return true;
			}
		}).when(storageService).delete(eq(board));
		Card[] cards = new Card[]{
				mock(Card.class),
				mock(Card.class),
		};
		when(board.getCards()).thenReturn(cards);
		Lane laneMock = mock(Lane.class);
		Lane[] lanes = new Lane[]{
				laneMock,
				laneMock,
				laneMock,
		};
		when(board.getLanes()).thenReturn(lanes);
		trash.addBoard(board);
		verify(kanbanService).delete(eq(board));
		verify(kanbanService,times(3)).discardToTrash(eq(laneMock));
	}

	private ConfirmStrategy createConfirm(final boolean result) {
		ConfirmStrategy confirmStrategy = new ConfirmStrategy() {
			public boolean confirm(String message) {
				return result;
			}
		};
		return confirmStrategy;
	}

}
