package org.kompiro.jamcircle.kanban.service.internal;

import org.junit.*;

public class KanbanMigratorTest {

	private KanbanServiceTestHelper helper;

	@Before
	public void init() throws Exception {
		helper = new KanbanServiceTestHelper();
		helper.tearDownKanbanService();
	}

	@After
	public void after() throws Exception {
		helper.tearDownKanbanService();
	}

	@Test
	public void migrate() throws Exception {
		KanbanServiceImpl service = helper.getKanbanService();
		KanbanMigrator migrator = new KanbanMigrator(service);
		migrator.migrate();
		// if migrate is OK, then these methods don't throw Exception
		service.findAllUsers();
		service.findAllBoard();
		service.findAllLanes();
		service.findAllCards();
		service.findAllIcons();
	}
}
