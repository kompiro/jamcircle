package org.kompiro.jamcircle.xmpp.kanban.ui.internal.editpart;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.eclipse.gef.commands.CompoundCommand;
import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.ui.command.*;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;
import org.kompiro.jamcircle.xmpp.kanban.ui.internal.command.DeleteUserCommand;


public class UserEditPartTest {
	
	private UserEditPart part;

	@Before
	public void before() throws Exception {
		createEditPart();
	}
	
	@Test
	public void supportMoveCommand() throws Exception {
		Object object = part.getAdapter(MoveCommand.class);
		assertThat(object,not(nullValue()));
		assertThat(object,instanceOf(MoveCommand.class));
	}

	
	@Test
	public void supportDeleteCommand() throws Exception {
		Object object = part.getAdapter(DeleteCommand.class);
		assertThat(object,not(nullValue()));
		assertThat(object,instanceOf(CompoundCommand.class));
		CompoundCommand compoundCommand = (CompoundCommand)object;
		assertThat(compoundCommand.size(),is(2));
		assertThat(compoundCommand.getCommands().get(0),instanceOf(DeleteIconModelCommand.class));
		assertThat(compoundCommand.getCommands().get(1),instanceOf(DeleteUserCommand.class));
	}

	private void createEditPart() {
		Board board = mock(Board.class);
		BoardModel boardModel = new BoardModel(board);
		part = new UserEditPart(boardModel);
	}
	
	
}
