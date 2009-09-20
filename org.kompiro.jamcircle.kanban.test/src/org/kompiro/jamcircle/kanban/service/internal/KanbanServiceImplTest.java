package org.kompiro.jamcircle.kanban.service.internal;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNotNull;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import net.java.ao.DBParam;
import net.java.ao.EntityManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.CardDTO;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.model.User;
import org.kompiro.jamcircle.storage.service.StorageService;
import org.mockito.ArgumentCaptor;
import org.mockito.runners.VerboseMockitoJUnitRunner;

@RunWith(VerboseMockitoJUnitRunner.class)
public class KanbanServiceImplTest {

	private KanbanServiceImpl serviceImpl;
	private EntityManager managerMock;
	private PropertyChangeListener listener;
	private ArgumentCaptor<PropertyChangeEvent> captured;

	@Before
	public void initialized() throws Exception {
		KanbanServiceImpl service1 = new KanbanServiceImpl();
		StorageService serviceMock = mock(StorageService.class);
		managerMock = mock(EntityManager.class);
		when(serviceMock.getEntityManager()).thenReturn(managerMock);
		service1.setStorageService(serviceMock);
		serviceImpl = service1;
		listener = mock(PropertyChangeListener.class);
		serviceImpl.addPropertyChangeListener(listener);
		captured = ArgumentCaptor.forClass(PropertyChangeEvent.class);

		assumeNotNull(serviceImpl.getEntityManager());
	}
	
	@After
	public void after() throws Exception {
		assumeNotNull(serviceImpl,listener);
		serviceImpl.removePropertyChangeListener(listener);
		reset(managerMock);
	}
	
	@Test
	public void fireProperiesIsCalledWhenCreateBoard() throws Exception {
		
		Board boardMock = mock(Board.class);
		when(managerMock.create(eq(Board.class), (DBParam)anyObject(),(DBParam)anyObject())).thenReturn(boardMock);

		serviceImpl.createBoard("test");
		verify(listener).propertyChange(captured.capture());

		PropertyChangeEvent actual = captured.getValue();
		assertFirePropertyWhenCreated(actual, Board.class);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void fireProperiesIsCalledWhenCreateCard() throws Exception {
		Card cardMock = mock(Card.class);
		
		when(managerMock.create(eq(Card.class),
				(DBParam)anyObject(),
				(DBParam)anyObject(),
				(DBParam)anyObject(),
				(DBParam)anyObject(),
				(DBParam)anyObject(),
				(DBParam)anyObject()
				)).thenReturn(cardMock);
		when(managerMock.find((Class<Card>)anyObject(),(String)anyObject(),anyInt())).thenReturn(new Card[]{cardMock});
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
		
		when(managerMock.create((Class<Card>)notNull(),
				(DBParam)anyObject(),
				(DBParam)anyObject(),
				(DBParam)anyObject(),
				(DBParam)anyObject()
				)).thenReturn(cardMock);
		when(managerMock.find((Class<Card>)anyObject(),(String)anyObject(),anyInt())).thenReturn(new Card[]{cardMock});
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
		
		when(managerMock.create((Class<Card>)notNull(),
				(DBParam)anyObject(),
				(DBParam)anyObject(),
				(DBParam)anyObject(),
				(DBParam)anyObject(),
				(DBParam)anyObject()
				)).thenReturn(cardMock);
		when(managerMock.find((Class<Card>)anyObject(),(String)anyObject(),anyInt())).thenReturn(new Card[]{cardMock});
		serviceImpl.createReceiveCard(null, dtoMock , null, null);
		verify(listener).propertyChange(captured.capture());
		
		PropertyChangeEvent actual = captured.getValue();
		assertFirePropertyWhenCreated(actual, Card.class);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void fireProperiesIsCalledWhenAboutLane() throws Exception {
		Lane laneMock = mock(Lane.class);
		
		when(managerMock.create((Class<Lane>)notNull(),
				(DBParam)anyObject(),
				(DBParam)anyObject(),
				(DBParam)anyObject(),
				(DBParam)anyObject(),
				(DBParam)anyObject(),
				(DBParam)anyObject(),
				(DBParam)anyObject()
				)).thenReturn(laneMock);
		when(managerMock.find((Class<Lane>)anyObject(),(String)anyObject(),anyInt())).thenReturn(new Lane[]{laneMock});
		serviceImpl.createLane(null, "test", 0, 0, 100, 100);
		verify(listener).propertyChange(captured.capture());
		
		PropertyChangeEvent actual = captured.getValue();
		assertFirePropertyWhenCreated(actual, Lane.class);
	}


	@SuppressWarnings("unchecked")
	@Test
	public void fireProperiesIsCalledWhenAboutUser() throws Exception {
		User userMock = mock(User.class);

		when(managerMock.create((Class<User>)notNull(),(DBParam)anyObject())).thenReturn(userMock);
		when(managerMock.find((Class<User>)anyObject(),(String)anyObject(),(String)anyObject(),anyBoolean())).thenReturn(new User[]{userMock});
		String user = "kompiro@kompiro.org";
		serviceImpl.addUser(user);
		verify(listener).propertyChange(captured.capture());
		assertFirePropertyWhenCreated(captured.getValue(),User.class);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void firePropertiesIsCalledWhenUserDeleted() throws Exception {
		User userMock = mock(User.class);

		when(managerMock.find((Class<User>)anyObject(),(String)anyObject(),(String)anyObject(),anyBoolean())).thenReturn(new User[]{userMock});
		
		serviceImpl.deleteUser("test");
		verify(listener).propertyChange(captured.capture());

		PropertyChangeEvent actual = captured.getValue();
		assertThat(actual.getPropertyName(), is("User"));
		assertThat(actual.getNewValue(), nullValue());
		assertThat(actual.getOldValue(), notNullValue());
		assertThat(actual.getOldValue(),instanceOf(User.class));
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
