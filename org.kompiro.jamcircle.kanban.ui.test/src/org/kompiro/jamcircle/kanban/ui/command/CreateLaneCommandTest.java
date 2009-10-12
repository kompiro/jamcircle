package org.kompiro.jamcircle.kanban.ui.command;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.mock.Board;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.ui.internal.command.CreateLaneCommand;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;

public class CreateLaneCommandTest {
	private Board board;
	private CreateLaneCommand command;
	
	@Before
	public void init() throws Exception{
		createCommand();
	}

	@Test
	public void execute() throws Exception {
		command.execute();
		assertTrue(command.canUndo());
		assertThat(board.getLanes().length,is(1));
	}

	@Test
	public void undo() throws Exception {
		command.execute();
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
	
	private void createCommand() {
		board = new Board();
		command = new CreateLaneCommand();
		BoardModel boardModel = new BoardModel(board);
		command.setContainer(boardModel);
		Lane model = mock(Lane.class);
		command.setModel(model);
	}
	
}
