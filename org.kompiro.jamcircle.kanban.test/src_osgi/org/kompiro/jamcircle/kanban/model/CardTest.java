package org.kompiro.jamcircle.kanban.model;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.*;
import org.kompiro.jamcircle.kanban.OSGiEnvironment;
import org.kompiro.jamcircle.kanban.service.internal.KanbanServiceEnvironment;
import org.kompiro.jamcircle.kanban.service.internal.KanbanServiceTestHelper;
import org.kompiro.jamcircle.storage.model.ExecutorHandler;

public class CardTest {

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
	public void call_handler() throws Exception {
		Board board = helper.createBoardForTest("TEST_BOARD");
		Card card = helper.createCardForTest(board, "test");

		ExecutorHandler handler = mock(ExecutorHandler.class);
		card.setHandler(handler);

		card.save(false);
		verify(handler).handle((Runnable) any());
	}

	@Test
	public void no_exceptions_are_occured_when_call_toString() throws Exception {

		Board board = helper.createBoardForTest("TEST_BOARD");
		Card card = helper.createCardForTest(board, "Test");
		card.toString();

	}

}
