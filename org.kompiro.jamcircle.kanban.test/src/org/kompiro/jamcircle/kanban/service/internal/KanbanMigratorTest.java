package org.kompiro.jamcircle.kanban.service.internal;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.model.Lane;

public class KanbanMigratorTest extends AbstractKanbanTest{
	
	@Override
	@Before
	public void init() throws Exception {
		// This test needs before initialized Kanban Service.
	}
	
	@Test
	public void migrate() throws Exception {
		KanbanServiceImpl service = getKanbanService();
		KanbanMigrator migrator = new KanbanMigrator(service);
		migrator.migrate();
		Board[] boards = service.findAllBoard();
		assertEquals(0,boards.length);
		Lane[] lanes = service.findAllLanes();
		assertEquals(0,lanes.length);
	}
}
