package org.kompiro.jamcircle.xmpp.kanban.ui.internal.editpart;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.CompoundCommand;
import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.model.User;
import org.kompiro.jamcircle.kanban.ui.command.*;
import org.kompiro.jamcircle.kanban.ui.editpart.IPropertyChangeDelegator;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;
import org.kompiro.jamcircle.xmpp.kanban.ui.internal.command.DeleteUserCommand;
import org.kompiro.jamcircle.xmpp.kanban.ui.model.UserModel;


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
	
	@Test
	public void performCommitLocation() throws Exception {
		User user = mock(User.class);
		UserModel model = new UserModel(user );
		part.setModel(model);
		part.activate();
		IPropertyChangeDelegator delegator = new IPropertyChangeDelegator(){
			public void run(Runnable runner) {
				runner.run();
			}			
		};
		part.setDelegator(delegator );
		Point location = new Point(100,200);
		model.setLocation(location);
		
	}

	private void createEditPart() {
		Board board = mock(Board.class);
		BoardModel boardModel = new BoardModel(board);
		part = new UserEditPart(boardModel);
	}
	
	
}
