package org.kompiro.jamcircle.xmpp.kanban.ui.model;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.beans.PropertyChangeListener;

import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.User;
import org.kompiro.jamcircle.xmpp.kanban.ui.internal.editpart.UserEditPart;


public class UserModelTest {

	private UserEditPart part;
	private User user;
	private UserModel model;

	@Before
	public void before() throws Exception {
		user = mock(User.class);
		model = new UserModel(user);
		part = new UserEditPart(null);
		part.setModel(model);		
	}
	
	@Test
	public void executeAddPropertyChangeListener() throws Exception {
		part.activate();
		
		verify(user).addPropertyChangeListener((PropertyChangeListener)any());
		verify(user,never()).removePropertyChangeListener((PropertyChangeListener)any());
	}
	
	@Test
	public void executeRemovePropertyChangeListener() throws Exception {
		part.activate();
		part.deactivate();

		verify(user).removePropertyChangeListener((PropertyChangeListener)any());
		
	}
	
}
