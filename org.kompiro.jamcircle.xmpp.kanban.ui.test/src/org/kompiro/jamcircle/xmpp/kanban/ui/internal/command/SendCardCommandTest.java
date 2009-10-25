package org.kompiro.jamcircle.xmpp.kanban.ui.internal.command;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.eclipse.gef.EditPart;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.User;
import org.kompiro.jamcircle.kanban.model.mock.Card;
import org.kompiro.jamcircle.xmpp.kanban.ui.internal.BridgeXMPPActivator;
import org.kompiro.jamcircle.xmpp.service.XMPPConnectionService;

public class SendCardCommandTest extends AbstractCommandTest {

	private SendCardCommand command;
	private XMPPConnectionService service;
	private Card card;
	private User user;

	@Test
	public void initialize() throws Exception {
		whenAllNull();
		whenSetOnlyTarget();
		whenSetButNotAlive();
		whenSetButNotTargetModel();
		command.execute();
	}

	@Override
	public void execute() throws Exception {
		command.execute();
		verify(service).sendCard(user, card);
	}

	@Override
	public void undo() throws Exception {
		command.execute();
		verify(service).sendCard(user, card);
		assertThat(command.canUndo(), is(false));
	}
	
	@Override
	public void redo() throws Exception {
		// OK
	}

	private void whenSetButNotAlive() {
		SendCardCommand command = new SendCardCommand();
		try {
			User user = mock(User.class);
			command.setTarget(user);
			EditPart part = mock(EditPart.class);
			command.setPart(part);
			command.execute();
			fail();
		} catch (IllegalStateException e) {
		}
	}

	private void whenSetButNotTargetModel() {
		SendCardCommand command = new SendCardCommand();
		BridgeXMPPActivator activator = mock(BridgeXMPPActivator.class);
		XMPPConnectionService service = mock(XMPPConnectionService.class);
		User user = mock(User.class);
		when(service.isAvailable(user)).thenReturn(true);
		when(activator.getConnectionService()).thenReturn(service);
		command.setActivator(activator);
		try {
			command.setTarget(user);
			EditPart part = mock(EditPart.class);
			command.setPart(part);
			command.execute();
			fail();
		} catch (IllegalStateException e) {
		}
	}

	private void whenAllNull() {
		SendCardCommand command = new SendCardCommand();
		try {
			command.execute();
			fail();
		} catch (IllegalStateException e) {
		}
	}

	private void whenSetOnlyTarget() {
		SendCardCommand command;
		command = new SendCardCommand();
		try {
			User target = mock(User.class);
			command.setTarget(target);
			command.execute();
			fail();
		} catch (IllegalStateException e) {
		}
	}

	@Override
	protected void createCommand() {
		command = new SendCardCommand();
		BridgeXMPPActivator activator = mock(BridgeXMPPActivator.class);
		service = mock(XMPPConnectionService.class);
		user = mock(User.class);
		when(service.isAvailable(user)).thenReturn(true);
		when(activator.getConnectionService()).thenReturn(service);
		command.setActivator(activator);
		command.setTarget(user);
		EditPart part = mock(EditPart.class);
		card = mock(Card.class);
		when(part.getModel()).thenReturn(card);
		command.setPart(part);
	}

}
