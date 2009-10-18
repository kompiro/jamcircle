package org.kompiro.jamcircle.xmpp.kanban.ui.internal.command;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.junit.Test;
import org.kompiro.jamcircle.xmpp.kanban.ui.internal.model.UserModel;


public class MoveUserCommandTest extends AbstractCommandTest{
	private static final int EXPECTED_X = 10;
	private static final int EXPECTED_Y = 20;
	private MoveUserCommand command;
	private UserModel model;

	@Test
	public void initialize() throws Exception {
		MoveUserCommand command = new MoveUserCommand();
		command.initialize();
		assertThat(command.canExecute(),is(false));
		UserModel model = mock(UserModel.class);
		command.setModel(model);
		Rectangle rect = new Rectangle(EXPECTED_X, EXPECTED_Y, 70, 70);
		command.setRectangle(rect);
		command.initialize();
		assertThat(command.canExecute(),is(true));		
	}
	
	@Override
	public void execute() throws Exception {
		command.execute();
		verify(model).setLocation(new Point(EXPECTED_X,EXPECTED_Y));
	}
	
	@Override
	public void undo() throws Exception {
		assertThat(command.canUndo(),is(false));
		command.execute();
		verify(model).setLocation(new Point(EXPECTED_X,EXPECTED_Y));
		assertThat(command.canUndo(),is(true));
		command.undo();
		verify(model).setLocation(null);
	}
	
	@Override
	public void redo() throws Exception {
		command.execute();
		command.undo();
		command.redo();
		verify(model,times(2)).setLocation(new Point(EXPECTED_X,EXPECTED_Y));
	}
	
	@Override
	protected void createCommand() {
		command = new MoveUserCommand();
		model = mock(UserModel.class);
		command.setModel(model);
		Rectangle rect = new Rectangle(EXPECTED_X, EXPECTED_Y, 70, 70);
		command.setRectangle(rect);
	}

}
