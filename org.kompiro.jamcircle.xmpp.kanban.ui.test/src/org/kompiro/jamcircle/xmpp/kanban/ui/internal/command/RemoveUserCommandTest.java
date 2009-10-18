package org.kompiro.jamcircle.xmpp.kanban.ui.internal.command;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;
import org.kompiro.jamcircle.xmpp.kanban.ui.internal.model.UserModel;

public class RemoveUserCommandTest {
	
	private UserModel user;
	private BoardModel board;
	private RemoveUserCommand command;
	
	@Before
	public void before() throws Exception{
		createCommand();
	}

	@Test
	public void initialize() throws Exception {
		RemoveUserCommand command = new RemoveUserCommand(null, null);
		command.initialize();
		assertThat(command.canExecute(),is(false));
		command = new RemoveUserCommand(mock(UserModel.class), mock(BoardModel.class));
		command.initialize();
		assertThat(command.canExecute(),is(true));
	}
	
	@Test
	public void execute() throws Exception {
		command.execute();
		verify(board).removeIcon(user);
	}
	
	@Test
	public void undo() throws Exception {
		assertThat(command.canUndo(),is(false));
		command.execute();
		assertThat(command.canUndo(),is(true));		
		command.undo();
		verify(board).addIcon(user);
	}
	
	@Test
	public void redo() throws Exception {
		command.execute();
		command.undo();
		command.redo();
		verify(board,times(2)).removeIcon(user);
	}

	private void createCommand() {
		user = mock(UserModel.class);
		board = mock(BoardModel.class);
		command = new RemoveUserCommand(user, board);
	}
}
