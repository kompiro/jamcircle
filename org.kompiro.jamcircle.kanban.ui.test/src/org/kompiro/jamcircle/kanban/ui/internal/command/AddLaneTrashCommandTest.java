package org.kompiro.jamcircle.kanban.ui.internal.command;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.mock.Icon;
import org.kompiro.jamcircle.kanban.model.mock.Lane;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.model.TrashModel;

public class AddLaneTrashCommandTest  extends AbstractCommandTest{
	
	private AddLaneTrashCommand command;
	private TrashModel trash;
	private KanbanService service;
	private Lane lane;
	
	@Test
	public void execute() throws Exception {
		command.execute();
		verify(service).discardToTrash(lane);
	}
	
	@Test
	public void undo() throws Exception {
		command.execute();
		assertTrue(command.canUndo());
		command.undo();
		verify(service).pickupFromTrash(lane);
	}

	@Test
	public void redo() throws Exception {
		command.execute();
		assertTrue(command.canUndo());
		command.undo();
		command.redo();
		verify(service,times(2)).discardToTrash(lane);
	}
	
	@Override
	public void initialize() throws Exception {
		AddLaneTrashCommand command = new AddLaneTrashCommand(null, null);
		command.initialize();
		assertThat(command.canExecute(),is(false));
		Icon icon = new Icon();
		service = mock(KanbanService.class);
		trash = new TrashModel(icon,service);
		lane = new Lane();
		command = new AddLaneTrashCommand(trash, lane);
		command.initialize();
		assertThat(command.canExecute(),is(true));
		
	}
	
	protected void createCommand() {
		Icon icon = new Icon();
		service = mock(KanbanService.class);
		trash = new TrashModel(icon,service);
		lane = new Lane();
		command = new AddLaneTrashCommand(trash, lane);
	}

}
