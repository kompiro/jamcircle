package org.kompiro.jamcircle.kanban.service.internal;

import org.junit.Before;
import org.junit.Test;

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
		// if migrate is OK, then these methods don't throw Exception
		service.findAllBoard();
		service.findAllLanes();
		service.findAllCards();
		service.findAllUsers();
		service.findAllIcons();
	}
}
