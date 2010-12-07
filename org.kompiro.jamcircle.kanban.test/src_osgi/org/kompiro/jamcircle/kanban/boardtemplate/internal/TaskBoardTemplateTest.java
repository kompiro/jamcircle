package org.kompiro.jamcircle.kanban.boardtemplate.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.service.internal.AbstractKanbanTest;
import org.kompiro.jamcircle.kanban.service.internal.KanbanServiceImpl;

public class TaskBoardTemplateTest extends AbstractKanbanTest {

	@Test
	public void testInitialize() {
		KanbanServiceImpl service = helper.getKanbanService();
		TaskBoardTemplate template = new TaskBoardTemplate();
		Board board = service.createBoard("test");
		template.initialize(board);
		Lane[] lanes = board.getLanes();
		assertNotNull(lanes);
		assertEquals(3, lanes.length);
		assertEquals("ToDo", lanes[0].getStatus());
		assertEquals("In Progress", lanes[1].getStatus());
		assertEquals("Done", lanes[2].getStatus());
		assertNotNull(lanes[2].getScript());
	}

}
