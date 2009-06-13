package org.kompiro.jamcircle.kanban.service.internal;

import static org.easymock.EasyMock.*;
import static org.easymock.classextension.EasyMock.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNotNull;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

import net.java.ao.DBParam;
import net.java.ao.EntityManager;

import org.easymock.Capture;
import org.junit.*;
import org.kompiro.jamcircle.kanban.model.*;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.storage.service.StorageService;


public class KanbanServiceImplTest {

	private KanbanServiceImpl serviceImpl;
	private KanbanService service;
	private EntityManager managerMock;
	private PropertyChangeListener listener;
	private Capture<PropertyChangeEvent> captured;

	@Before
	public void initialized() throws Exception {
		KanbanServiceImpl service1 = new KanbanServiceImpl();
		StorageService serviceMock = createMock(StorageService.class);
		managerMock = createStrictMock(EntityManager.class);
		expect(serviceMock.getEntityManager()).andStubReturn(managerMock);
		service1.setStorageService(serviceMock);
		replay(serviceMock);
		serviceImpl = service1;
		service = serviceImpl;
		listener = createMock(PropertyChangeListener.class);
		serviceImpl.addPropertyChangeListener(listener);
		captured = new Capture<PropertyChangeEvent>();
		listener.propertyChange(capture(captured));

		assumeNotNull(service,serviceImpl.getEntityManager());
	}
	
	@After
	public void after() throws Exception {
		serviceImpl.removePropertyChangeListener(listener);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void fireProperiesIsCalledWhenCreateBoard() throws Exception {
		
		Board boardMock = createMock(Board.class);
		expect(managerMock.create((Class<Board>)notNull(),(DBParam)anyObject(),(DBParam)anyObject()))
			.andReturn(boardMock);
		replay(listener,boardMock,managerMock);
		
		service.createBoard("test");
		verify(listener);

		PropertyChangeEvent actual = captured.getValue();
		assertFirePropertyWhenCreated(actual, Board.class);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void fireProperiesIsCalledWhenCreateCard() throws Exception {
		Card cardMock = createNiceMock(Card.class);
		
		DBParam[] args = createDBParams(6);
		expect(managerMock.create((Class<Card>)notNull(),args)).andReturn(cardMock);
		expect(managerMock.find((Class<Card>)anyObject(),(String)anyObject(),anyInt())).andReturn(new Card[]{cardMock});
		replay(listener,cardMock,managerMock);
		service.createCard(null, "test", null, 0, 0);
		verify(listener);
		
		PropertyChangeEvent actual = captured.getValue();
		assertFirePropertyWhenCreated(actual, Card.class);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void fireProperiesIsCalledWhenCreateCloneCard() throws Exception {
		Card cardMock = createNiceMock(Card.class);
		Card cardClonedMock = createNiceMock(Card.class);
		
		DBParam[] args = createDBParams(4);
		expect(managerMock.create((Class<Card>)notNull(),args)).andReturn(cardMock);
		expect(managerMock.find((Class<Card>)anyObject(),(String)anyObject(),anyInt())).andReturn(new Card[]{cardMock});
		replay(listener,cardMock,cardClonedMock,managerMock);
		service.createClonedCard(null, null, cardClonedMock, 0, 0);
		verify(listener);
		
		PropertyChangeEvent actual = captured.getValue();
		assertFirePropertyWhenCreated(actual, Card.class);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void fireProperiesIsCalledWhenCopyCard() throws Exception {
		Card cardMock = createNiceMock(Card.class);
		CardDTO dtoMock = createNiceMock(CardDTO.class);
		
		DBParam[] args = createDBParams(5);
		expect(managerMock.create((Class<Card>)notNull(),args)).andReturn(cardMock);
		expect(managerMock.find((Class<Card>)anyObject(),(String)anyObject(),anyInt())).andReturn(new Card[]{cardMock});
		replay(listener,cardMock,dtoMock,managerMock);
		service.createReceiveCard(null, dtoMock , null, null);
		verify(listener);
		
		PropertyChangeEvent actual = captured.getValue();
		assertFirePropertyWhenCreated(actual, Card.class);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void fireProperiesIsCalledWhenAboutLane() throws Exception {
		Lane laneMock = createNiceMock(Lane.class);
		
		DBParam[] args = createDBParams(7);
		expect(managerMock.create((Class<Lane>)notNull(),args)).andReturn(laneMock);
		expect(managerMock.find((Class<Lane>)anyObject(),(String)anyObject(),anyInt())).andReturn(new Lane[]{laneMock});
		replay(listener,laneMock,managerMock);
		service.createLane(null, "test", 0, 0, 100, 100);
		verify(listener);
		
		PropertyChangeEvent actual = captured.getValue();
		assertFirePropertyWhenCreated(actual, Lane.class);
	}


	@SuppressWarnings("unchecked")
	@Test
	public void fireProperiesIsCalledWhenAboutUser() throws Exception {
		User userMock = createNiceMock(User.class);

		DBParam[] args = createDBParams(1);
		expect(managerMock.create((Class<User>)notNull(),args)).andReturn(userMock);
		expect(managerMock.find((Class<User>)anyObject(),(String)anyObject(),(String)anyObject(),anyBoolean())).andReturn(new User[]{userMock});
		replay(listener,userMock,managerMock);
		String user = "kompiro@kompiro.org";
		service.addUser(user);
		verify(listener);
		assertFirePropertyWhenCreated(captured.getValue(),User.class);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void firePropertiesIsCalledWhenUserDeleted() throws Exception {
		User userMock = createNiceMock(User.class);

		expect(managerMock.find((Class<User>)anyObject(),(String)anyObject(),(String)anyObject(),anyBoolean())).andReturn(new User[]{userMock});
		replay(userMock,listener,managerMock);
		
		service.deleteUser("test");
		verify(listener);

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
	
	private DBParam[] createDBParams(int size) {
		List<DBParam> params = new ArrayList<DBParam>();
		for(int i = 0; i < size; i++){
			params.add((DBParam)anyObject());
		}
		return params.toArray(new DBParam[]{});
	}

}
