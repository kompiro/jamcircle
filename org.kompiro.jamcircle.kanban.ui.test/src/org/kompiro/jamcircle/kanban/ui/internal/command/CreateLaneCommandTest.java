package org.kompiro.jamcircle.kanban.ui.internal.command;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.model.mock.Board;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;

public class CreateLaneCommandTest extends AbstractCommandTest{
	private Board board;
	private CreateLaneCommand command;
	
	@Override
	public void initialize() throws Exception {
		CreateLaneCommand command = new CreateLaneCommand();
		command.initialize();
		assertThat(command.canExecute(), is(false));
		command = new CreateLaneCommand();
		BoardModel boardModel = new BoardModel(board);
		command.setContainer(boardModel);
		Lane model = mock(Lane.class);
		command.setModel(model);
		command.initialize();
		assertThat(command.canExecute(),is(true));
	}
	
	@Test
	public void execute() throws Exception {
		command.execute();
		assertThat(board.getLanes().length,is(1));
	}

	@Test
	public void undo() throws Exception {
		command.execute();
		assertTrue(command.canUndo());
		command.undo();
		assertThat(board.getLanes().length,is(0));
	}
	
	@Test
	public void redo() throws Exception {
		command.execute();
		command.undo();
		command.redo();
		assertThat(board.getLanes().length,is(1));
	}
	
	protected void createCommand() {
		board = new Board();
		command = new CreateLaneCommand();
		BoardModel boardModel = new BoardModel(board);
		command.setContainer(boardModel);
		Lane model = mock(Lane.class);
		command.setModel(model);
		command.initialize();
	}
	
}
