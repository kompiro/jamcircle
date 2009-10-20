package org.kompiro.jamcircle.kanban.ui.internal.command;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.Lane;

public class LaneToggleIconizedCommandTest extends AbstractCommandTest {

	private Lane lane;
	private LaneToggleIconizedCommand command;

	@Test
	public void initialize() throws Exception {
		Lane lane = null;
		LaneToggleIconizedCommand command = new LaneToggleIconizedCommand(lane);
		command.initialize();
		assertThat(command.canExecute(),is(false));
		lane = mock(Lane.class);
		command = new LaneToggleIconizedCommand(lane);
		command.initialize();
		assertThat(command.canExecute(),is(true));		
	}
	
	@Override
	public void execute() throws Exception {
		command.execute();
		verify(lane).setIconized(true);
	}
	
	@Override
	public void undo() throws Exception {
		command.execute();
		assertThat(command.canUndo(),is(true));
		command.undo();
		verify(lane).setIconized(false);
	}
	
	@Override
	public void redo() throws Exception {
		command.execute();
		command.undo();
		command.redo();
		verify(lane,times(2)).setIconized(true);
	}
	
	@Override
	protected void createCommand() {
		lane = mock(Lane.class);
		command = new LaneToggleIconizedCommand(lane);
		command.initialize();
	}

}
