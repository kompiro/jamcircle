package org.kompiro.jamcircle.kanban.service.internal;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import net.java.ao.DBParam;
import net.java.ao.EntityManager;

import org.junit.*;
import org.kompiro.jamcircle.kanban.model.*;
import org.kompiro.jamcircle.storage.service.StorageService;
import org.kompiro.jamcircle.storage.service.internal.StorageServiceImpl;
import org.mockito.ArgumentCaptor;

public class KanbanServiceImplTest {

	private KanbanServiceImpl serviceImpl;
	private EntityManager managerMock;
	private PropertyChangeListener listener;
	private ArgumentCaptor<PropertyChangeEvent> captured;
	private StorageService storageService;

	@Before
	public void initialized() throws Exception {
		storageService = mock(StorageServiceImpl.class);
		managerMock = mock(EntityManager.class);
		when(storageService.getEntityManager()).thenReturn(managerMock);
		serviceImpl = new KanbanServiceImpl();
		serviceImpl.setStorageService(storageService);
		listener = mock(PropertyChangeListener.class);
		serviceImpl.addPropertyChangeListener(listener);
		captured = ArgumentCaptor.forClass(PropertyChangeEvent.class);

		assumeNotNull(serviceImpl.getEntityManager());
	}

	@After
	public void after() throws Exception {
		assumeNotNull(serviceImpl, listener);
		serviceImpl.removePropertyChangeListener(listener);
		reset(managerMock);
	}

	@Test
	public void fireProperiesIsCalledWhenCreateBoard() throws Exception {
		Board boardMock = mock(Board.class);
		when(managerMock.create(eq(Board.class), (DBParam) anyObject(), (DBParam) anyObject())).thenReturn(boardMock);

		serviceImpl.createBoard("test");
		verify(listener).propertyChange(captured.capture());

		PropertyChangeEvent actual = captured.getValue();
		assertFirePropertyWhenCreated(actual, Board.class);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void fireProperiesIsCalledWhenCreateCard() throws Exception {
		Card cardMock = mock(Card.class);

		when(storageService.createEntity(eq(Card.class), (DBParam[]) any())).thenReturn(cardMock);

		when(managerMock.find((Class<Card>) anyObject(), (String) anyObject(), anyInt())).thenReturn(
				new Card[] { cardMock });
		serviceImpl.createCard(null, "test", null, 0, 0);
		verify(listener).propertyChange(captured.capture());
		PropertyChangeEvent actual = captured.getValue();
		assertFirePropertyWhenCreated(actual, Card.class);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void fireProperiesIsCalledWhenCreateCloneCard() throws Exception {
		Card cardMock = mock(Card.class);
		Card cardClonedMock = mock(Card.class);

		when(storageService.createEntity(eq(Card.class), (DBParam[]) any())).thenReturn(cardMock);
		when(managerMock.find((Class<Card>) anyObject(), (String) anyObject(), anyInt())).thenReturn(
				new Card[] { cardMock });
		serviceImpl.createClonedCard(null, null, cardClonedMock, 0, 0);
		verify(listener).propertyChange(captured.capture());

		PropertyChangeEvent actual = captured.getValue();
		assertFirePropertyWhenCreated(actual, Card.class);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void fireProperiesIsCalledWhenCopyCard() throws Exception {
		Card cardMock = mock(Card.class);
		CardDTO dtoMock = mock(CardDTO.class);

		when(storageService.createEntity(eq(Card.class), (DBParam[]) any())).thenReturn(cardMock);
		when(managerMock.find((Class<Card>) anyObject(), (String) anyObject(), anyInt())).thenReturn(
				new Card[] { cardMock });
		serviceImpl.createReceiveCard(null, dtoMock, null);
		verify(listener).propertyChange(captured.capture());

		PropertyChangeEvent actual = captured.getValue();
		assertFirePropertyWhenCreated(actual, Card.class);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void fireProperiesIsCalledWhenAboutLane() throws Exception {
		Lane laneMock = mock(Lane.class);

		when(managerMock.create(eq(Lane.class),
				(DBParam) anyObject(),
				(DBParam) anyObject(),
				(DBParam) anyObject(),
				(DBParam) anyObject(),
				(DBParam) anyObject(),
				(DBParam) anyObject(),
				(DBParam) anyObject()
				)).thenReturn(laneMock);
		when(managerMock.find((Class<Lane>) anyObject(), (String) anyObject(), anyInt())).thenReturn(
				new Lane[] { laneMock });
		serviceImpl.createLane(null, "test", 0, 0, 100, 100);
		verify(listener).propertyChange(captured.capture());

		PropertyChangeEvent actual = captured.getValue();
		assertFirePropertyWhenCreated(actual, Lane.class);
	}

	@Test
	public void fireProperiesIsCalledWhenAboutUser() throws Exception {
		User userMock = mock(User.class);
		when(storageService.createEntity(eq(User.class), (DBParam[]) anyObject())).thenCallRealMethod();
		when(managerMock.create(eq(User.class), (DBParam[]) anyObject())).thenReturn(userMock);
		String user = "kompiro@kompiro.org";
		serviceImpl.addUser(user);
		verify(listener).propertyChange(captured.capture());
		assertFirePropertyWhenCreated(captured.getValue(), User.class);
	}

	@Test
	public void firePropertiesIsCalledWhenUserDeleted() throws Exception {
		User userMock = mock(User.class);

		when(managerMock.find(eq(User.class), (String) anyObject(), (String) anyObject(), anyBoolean())).thenReturn(
				new User[] { userMock });

		serviceImpl.deleteUser("test");
		verify(listener).propertyChange(captured.capture());

		PropertyChangeEvent actual = captured.getValue();
		assertThat(actual.getPropertyName(), is("User"));
		assertThat(actual.getNewValue(), nullValue());
		assertThat(actual.getOldValue(), notNullValue());
		assertThat(actual.getOldValue(), instanceOf(User.class));
	}

	@Test
	public void addUser() throws Exception {
		String userId = "kompiro@gmail.com";
		DBParam[] params = new DBParam[] {
				new DBParam(User.PROP_USERID, userId)
		};
		when(storageService.createEntity(User.class, params)).thenCallRealMethod();
		serviceImpl.addUser(userId);
		verify(storageService, times(1)).createEntity(User.class, params);
	}

	@Test
	public void createCloneCard() throws Exception {

		Card card = new org.kompiro.jamcircle.kanban.model.mock.Card();
		card.setSubject("card_1");
		Board board = new org.kompiro.jamcircle.kanban.model.mock.Board();
		User user = new org.kompiro.jamcircle.kanban.model.mock.User();
		user.setUserId("test");
		when(storageService.createEntity(eq(Card.class), (DBParam[]) any())).thenReturn(card);
		when(managerMock.find(eq(Card.class), (String) any(), anyInt())).thenReturn(new Card[] { card });
		serviceImpl.createClonedCard(board, user, card, 10, 10);
		ArgumentCaptor<DBParam[]> arg1 = ArgumentCaptor.forClass(DBParam[].class);
		verify(storageService).createEntity(eq(Card.class),
				arg1.capture()
				);
		assertThat(arg1.getValue()[0].getValue().toString(), is("card_1"));
	}

	private void assertFirePropertyWhenCreated(
			PropertyChangeEvent actual,
			Class<?> clazz) {
		assertThat(actual.getPropertyName(), is(clazz.getSimpleName()));
		assertThat(actual.getNewValue(), notNullValue());
		assertTrue(clazz.isInstance(actual.getNewValue()));
		assertThat(actual.getOldValue(), nullValue());
	}

}
