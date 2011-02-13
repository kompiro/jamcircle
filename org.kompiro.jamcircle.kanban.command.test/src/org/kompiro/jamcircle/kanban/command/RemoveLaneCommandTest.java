package org.kompiro.jamcircle.kanban.command;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;

public class RemoveLaneCommandTest extends AbstractCommandTest {

	private RemoveLaneCommand command;
	private BoardModel board;
	private Lane lane;

	@Test
	public void initialize() throws Exception {
		RemoveLaneCommand command = new RemoveLaneCommand(null, null);
		command.initialize();
		assertThat(command.canExecute(), is(false));
		command = new RemoveLaneCommand(mock(Lane.class), mock(BoardModel.class));
		command.initialize();
		assertThat(command.canExecute(), is(true));
	}

	@Test
	public void execute() throws Exception {
		command.execute();
		verify(board).removeLane(lane);
	}

	@Test
	public void undo() throws Exception {
		assertThat(command.canUndo(), is(false));
		command.execute();
		assertThat(command.canUndo(), is(true));
		command.undo();
		verify(board).addLane(lane);
	}

	@Test
	public void redo() throws Exception {
		command.execute();
		command.undo();
		command.redo();
		verify(board, times(2)).removeLane(lane);
	}

	protected void createCommand() {
		board = mock(BoardModel.class);
		lane = mock(Lane.class);
		command = new RemoveLaneCommand(lane, board);
	}

}
