package org.kompiro.jamcircle.kanban.ui.command;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.mock.Icon;
import org.kompiro.jamcircle.kanban.model.mock.Lane;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.model.mock.TrashModel;

public class AddLaneTrashCommandTest {
	
	private AddLaneTrashCommand command;
	private TrashModel trash;
	
	@Before
	public void before() throws Exception{
		createCommand();
	}

	@Test
	public void execute() throws Exception {
		command.execute();
		assertThat(trash.getLanes().length,is(1));
	}

	
	@Test
	public void undo() throws Exception {
		command.execute();
		assertTrue(command.canUndo());
		command.undo();
		assertThat(trash.getLanes().length,is(0));		
	}

	@Test
	public void redo() throws Exception {
		command.execute();
		assertTrue(command.canUndo());
		command.undo();
		command.redo();
		assertThat(trash.getLanes().length,is(1));
	}
	
	private void createCommand() {
		Icon icon = new Icon();
		KanbanService service = mock(KanbanService.class);
		trash = new TrashModel(icon,service );
		Lane lane = new Lane();
		command = new AddLaneTrashCommand(trash, lane);
	}

}
