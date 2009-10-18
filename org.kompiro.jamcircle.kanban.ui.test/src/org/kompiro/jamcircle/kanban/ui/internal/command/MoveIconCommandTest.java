package org.kompiro.jamcircle.kanban.ui.internal.command;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.ui.model.IconModel;

public class MoveIconCommandTest extends AbstractCommandTest {

	private static final int EXPECTED_X = 10;
	private static final int EXPECTED_Y = 20;
	private MoveIconCommand command;
	private IconModel model;

	@Test
	public void initialize() throws Exception {
		MoveIconCommand command = new MoveIconCommand();
		command.initialize();
		assertThat(command.canExecute(),is(false));
		IconModel model = mock(IconModel.class);
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
		command = new MoveIconCommand();
		model = mock(IconModel.class);
		command.setModel(model);
		Rectangle rect = new Rectangle(EXPECTED_X, EXPECTED_Y, 70, 70);
		command.setRectangle(rect);
	}

}
