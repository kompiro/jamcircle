package org.kompiro.jamcircle.kanban.boardtemplate.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.*;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.service.internal.*;
import org.kompiro.jamcircle.test.OSGiEnvironment;

public class TaskBoardTemplateTest {

	@Rule
	public OSGiEnvironment env = new OSGiEnvironment();

	@Rule
	public KanbanServiceEnvironment serviceEnv = new KanbanServiceEnvironment();

	private KanbanServiceTestHelper helper;

	@BeforeClass
	public static void initializeEnvironment() throws Exception {
		Logger.getLogger("net.java.ao").setLevel(Level.FINE);
	}

	@Before
	public void before() throws Exception {
		helper = serviceEnv.getHelper();
	}

	@After
	public void after() throws Exception {
		serviceEnv.getHelper().tearDownKanbanService();
	}

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
