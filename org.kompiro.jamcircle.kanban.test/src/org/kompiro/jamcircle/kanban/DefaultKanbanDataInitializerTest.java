package org.kompiro.jamcircle.kanban;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.logging.Level;
import java.util.logging.Logger;


import org.junit.After;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.KanbanActivator;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.osgi.framework.Bundle;

public class DefaultKanbanDataInitializerTest {

	@Test
	public void initialize() throws Exception {
		Logger.getLogger("net.java.ao").setLevel(Level.FINE);
		KanbanActivator activator = getActivator();
		assertEquals(Bundle.ACTIVE,activator.getBundle().getState());
		Board[] boards = getKanbanService().findAllBoard();
		assertNotNull(boards);
		assertEquals(1,boards.length);
		Board board = boards[0];
		Lane[] lanes = board.getLanes();
		assertNotNull(lanes);
		assertEquals(3,lanes.length);
		assertEquals("To Do",lanes[0].getStatus());
		assertEquals("In Progress",lanes[1].getStatus());
		assertEquals("Completed",lanes[2].getStatus());
		assertNotNull(lanes[2].getScript());
	}

	@After
	public void afterTest() throws Exception{
		getKanbanService().deleteAllCards();
		getKanbanService().deleteAllLanes();
		getKanbanService().deleteAllUsers();
		getKanbanService().deleteAllIcons();
		getKanbanService().deleteAllBoards();
	}
	
	private KanbanService getKanbanService(){
		KanbanService kanbanService = getActivator().getKanbanService();
		kanbanService.init();
		return kanbanService;
	}
	
	private KanbanActivator getActivator() {
		return KanbanActivator.getDefault();
	}
	
}
