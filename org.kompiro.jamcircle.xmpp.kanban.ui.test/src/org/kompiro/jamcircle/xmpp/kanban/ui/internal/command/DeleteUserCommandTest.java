package org.kompiro.jamcircle.xmpp.kanban.ui.internal.command;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.User;
import org.kompiro.jamcircle.xmpp.kanban.ui.model.UserModel;


public class DeleteUserCommandTest extends AbstractCommandTest {

	private User user;
	private DeleteUserCommand command;



	@Test
	public void initialize() throws Exception {
		UserModel userModel = null;
		DeleteUserCommand command = new DeleteUserCommand(userModel);
		command.initialize();
		assertThat(command.canExecute(),is(false));
		userModel = mock(UserModel.class);
		doReturn(mock(User.class)).when(userModel).getUser();
		command = new DeleteUserCommand(userModel);
		command.initialize();
		assertThat(command.canExecute(),is(true));
	}
	
	@Override
	public void execute() throws Exception {
		command.execute();
		verify(user).setTrashed(true);
	}
	
	@Override
	public void undo() throws Exception {
		command.execute();
		assertThat(command.canUndo(),is(true));
		command.undo();
		verify(user).setTrashed(false);
	}
	
	@Override
	public void redo() throws Exception {
		command.execute();
		assertThat(command.canUndo(),is(true));
		command.undo();
		command.redo();
		verify(user,times(2)).setTrashed(true);
	}
	
	@Override
	protected void createCommand() {
		UserModel userModel = mock(UserModel.class);
		user = mock(User.class);
		doReturn(user).when(userModel).getUser();
		command = new DeleteUserCommand(userModel);
		command.initialize();
	}

}
