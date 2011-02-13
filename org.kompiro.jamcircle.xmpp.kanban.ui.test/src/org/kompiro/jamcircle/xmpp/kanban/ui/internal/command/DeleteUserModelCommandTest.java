package org.kompiro.jamcircle.xmpp.kanban.ui.internal.command;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.command.DeleteIconModelCommand;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;
import org.kompiro.jamcircle.xmpp.kanban.ui.model.UserModel;

public class DeleteUserModelCommandTest {
	
	private UserModel user;
	private BoardModel board;
	private DeleteIconModelCommand<UserModel> command;
	
	@Before
	public void before() throws Exception{
		createCommand();
	}

	@Test
	public void initialize() throws Exception {
		DeleteIconModelCommand<UserModel> command = new DeleteIconModelCommand<UserModel>();
		assertThat(command.canExecute(),is(false));
		UserModel userModel = mock(UserModel.class);
		BoardModel boardModel = mock(BoardModel.class);
		command = new DeleteIconModelCommand<UserModel>();
		command.setModel(userModel);
		command.setContainer(boardModel);
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
		command = new DeleteIconModelCommand<UserModel>();
		command.setModel(user);
		command.setContainer(board);
	}
}
