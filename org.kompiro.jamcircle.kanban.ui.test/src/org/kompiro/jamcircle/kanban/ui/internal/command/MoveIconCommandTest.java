package org.kompiro.jamcircle.kanban.ui.internal.command;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import org.eclipse.draw2d.geometry.Rectangle;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.Icon;
import org.kompiro.jamcircle.kanban.ui.model.AbstractIconModel;
import org.kompiro.jamcircle.kanban.ui.model.IconModel;

public class MoveIconCommandTest extends AbstractCommandTest {

	private static final int EXPECTED_X = 10;
	private static final int EXPECTED_Y = 20;
	private MoveIconCommand command;
	private IconModel model;
	private Icon icon;

	@Test
	public void initialize() throws Exception {
		MoveIconCommand command = new MoveIconCommand();
		command.initialize();
		assertThat(command.canExecute(),is(false));
		Rectangle rect = new Rectangle(EXPECTED_X, EXPECTED_Y, 70, 70);
		command.setRectangle(rect);
		icon = mock(Icon.class);
		model = new AbstractIconModel(icon){
			private static final long serialVersionUID = 1L;
			public String getName() {
				return "mock";
			}
		};
		command.setModel(model);
		command.initialize();
		assertThat(command.canExecute(),is(true));		
	}
	
	@Override
	public void execute() throws Exception {
		command.execute();
		verify(icon).setX(EXPECTED_X);
		verify(icon).setY(EXPECTED_Y);
		verify(icon).commitLocation();
	}
	
	@Override
	public void undo() throws Exception {
		assertThat(command.canUndo(),is(false));
		command.execute();
		verify(icon).setX(EXPECTED_X);
		verify(icon).setY(EXPECTED_Y);
		verify(icon).commitLocation();
		assertThat(command.canUndo(),is(true));
		command.undo();
		verify(icon).setX(0);
		verify(icon).setY(0);
	}
	
	@Override
	public void redo() throws Exception {
		command.execute();
		command.undo();
		command.redo();
		verify(icon,times(2)).setX(EXPECTED_X);
		verify(icon).setX(0);
		verify(icon,times(2)).setY(EXPECTED_Y);
		verify(icon).setY(0);
		verify(icon,times(3)).commitLocation();
	}
	
	@Override
	protected void createCommand() {
		command = new MoveIconCommand();
		icon = mock(Icon.class);
		model = new AbstractIconModel(icon){
			private static final long serialVersionUID = 1L;
			public String getName() {
				return "mock";
			}
			
		};
		command.setModel(model);
		Rectangle rect = new Rectangle(EXPECTED_X, EXPECTED_Y, 70, 70);
		command.setRectangle(rect);
	}

}
