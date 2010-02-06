package org.kompiro.jamcircle.xmpp.service.internal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Iterator;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.*;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.xmpp.service.XMPPConnectionService;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class XMPPConnectionServiceImplTest {
	
	private XMPPConnectionServiceImpl service;
	private XMPPConnection connection;
	private KanbanService kanbanService;
	private User user;
	private Roster roster;

	@Before
	public void before() throws Exception{
		initialize();
	}
	
	@Test
	public void getCurrentUser() throws Exception {
		User user = mock(User.class);
		when(user.getUserName()).thenReturn("testing_value");

		when(kanbanService.hasUser("kompiro@test")).thenReturn(true);
		when(kanbanService.findUser("kompiro@test")).thenReturn(user);
				
		XMPPConnection mockService = spy(new XMPPConnection("MockService"));
		when(mockService.getUser()).thenReturn("kompiro@test");
		when(mockService.isConnected()).thenReturn(true);
		service.setConnection(mockService);
		assertNotNull(service.getConnection());
		User currentUser = service.getCurrentUser();
		assertNotNull(currentUser);
		assertEquals("testing_value",user.getUserName());
	}
	
	@Test
	public void sendCard() throws Exception {
		Chat chat;
		Card card;
		String expectedMessage;
		// prepare
		{
			FileTransferManager transferManager = mock(FileTransferManager.class);
			service.setFileTransferManager(transferManager);
			
			Presence presence = mock(Presence.class);
			user = mock(User.class);
			String target = "target@kompiro.org";
			when(user.getUserId()).thenReturn(target);
			when(roster.getPresence(target)).thenReturn(presence);
			
			Presence[] presences = new Presence[]{
				presence	
			};

			Iterator<Presence> itr = createPresences(presences);

			when(roster.getPresences(target)).thenReturn(itr);
			when(presence.isAvailable()).thenReturn(true);
			
			chat = mock(Chat.class);
			ChatManager manager = mock(ChatManager.class);		
			when(connection.getChatManager()).thenReturn(manager);
			when(manager.createChat(target, XMPPConnectionServiceImpl.doEmpty)).thenReturn(chat);
			
			card = mock(Card.class);
			expectedMessage = "for send test";
			when(card.getSubject()).thenReturn(expectedMessage);
		}
		
		service.sendCard(user,card);

		// verify
		{
			ArgumentCaptor<Message> capture = ArgumentCaptor.forClass(Message.class);
			verify(chat).sendMessage(capture.capture());
			Message value = capture.getValue();
			assertThat(value.getBody(),equalTo("message form JAM Circle"));
			Object actualCard = value.getProperty(XMPPConnectionService.PROP_SEND_CARD);
			assertThat(actualCard,is(CardDTO.class));
			assertThat(((CardDTO)actualCard).getSubject(),equalTo(expectedMessage));
			
		}
	}
	

	@Test
	public void isAlivable() throws Exception {
		User user1_exist_and_alive;
		User user2_exist_but_disconnect;
		User user3_not_exist;
		// prepare
		{
			{
				Presence presence1 = mock(Presence.class);
				user1_exist_and_alive = mock(User.class);
				String target1 = "target@kompiro.org";
				when(user1_exist_and_alive.getUserId()).thenReturn(target1);
				when(roster.getPresence(target1)).thenReturn(presence1);
				when(presence1.isAvailable()).thenReturn(true);
				Presence[] presences1 = new Presence[]{
						presence1,
				};
				
				Iterator<Presence> itr1 = createPresences(presences1);
				when(roster.getPresences(target1)).thenReturn(itr1);
			}
			
			{
				Presence presence2 = mock(Presence.class);
				String target2 = "notAilve@kompiro.org";
				user2_exist_but_disconnect = mock(User.class);
				when(user2_exist_but_disconnect.getUserId()).thenReturn(target2);
				when(roster.getPresence(target2)).thenReturn(presence2);
				when(presence2.isAvailable()).thenReturn(false);
				Presence[] presences2 = new Presence[]{
						presence2,
				};
				
				Iterator<Presence> itr2 = createPresences(presences2);
				when(roster.getPresences(target2)).thenReturn(itr2);
			}

			user3_not_exist = mock(User.class);
		}
		
		assertThat(service.isAvailable(user1_exist_and_alive) , is(true));
		assertThat(service.isAvailable(user2_exist_but_disconnect) , is(false));
		assertThat(service.isAvailable(user3_not_exist) , is(false));
		assertThat(service.isAvailable(null) ,is(false));
	}
	
	@SuppressWarnings("unchecked")
	private Iterator<Presence> createPresences(final Presence[] presences) {
		final Iterator<Presence> itr = mock(Iterator.class);
		when(itr.hasNext()).thenAnswer(new Answer<Boolean>() {
			int index = 0;
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				if(index < presences.length){
					when(itr.next()).thenReturn(presences[index]);
					index++;
					return true;
				}
				return false;
			}
		});
		return itr;
	}

	
	private void initialize() {
		kanbanService = mock(KanbanService.class);

		service = new XMPPConnectionServiceImpl();
//		service.setActivator(activator);
		service.setKanbanService(kanbanService);
		
		connection = mock(XMPPConnection.class);
		when(connection.isConnected()).thenReturn(true);
		service.setConnection(connection);
		roster = mock(Roster.class);
		when(connection.getRoster()).thenReturn(roster);
	}
	
}