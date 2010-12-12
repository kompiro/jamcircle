package org.kompiro.jamcircle.kanban.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.*;
import org.kompiro.jamcircle.kanban.service.internal.KanbanServiceEnvironment;
import org.kompiro.jamcircle.kanban.service.internal.KanbanServiceTestHelper;
import org.kompiro.jamcircle.scripting.ScriptTypes;
import org.kompiro.jamcircle.storage.model.ExecutorHandler;
import org.kompiro.jamcircle.test.OSGiEnvironment;

public class BoardTest {

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

		ExecutorHandler handler = mock(ExecutorHandler.class);
		board.setHandler(handler);

		board.save(false);
		verify(handler).handle((Runnable) any());
	}

	@Test
	@Ignore
	public void get_script() throws Exception {
		Board board = helper.createBoardForTest("TEST_BOARD");
		for (int i = 0; i < 100000; i++) {
			Random r = new Random(i);
			String script = "p 'test" + r.nextInt() + "'";
			board.setScript(script);
			board.setScriptType(ScriptTypes.JRuby);
			board.save(false);

			String actual = board.getScript();
			assertThat(actual, is(script));
		}
	}

	@Test
	public void cards() throws Exception {
		Board board = helper.createBoardForTest("TEST_BOARD");
		Card card = helper.createCardForTest(board, "BoardTest.cards");

		board.addCard(card);
		assertThat(board.getCards()[0], sameInstance(card));
		assertThat(board.containCard(card), is(true));

		board.removeCard(card);
		assertThat(board.getCards().length, is(0));
		assertThat(board.containCard(card), is(false));
	}

	@Test
	public void mock_cards() throws Exception {
		Board board = helper.createBoardForTest("TEST_BOARD");
		Card card = new org.kompiro.jamcircle.kanban.model.mock.Card();

		board.addCard(card);
		assertThat(board.getCards()[0], sameInstance(card));
		assertThat(board.containCard(card), is(true));

		board.removeCard(card);
		assertThat(board.getCards().length, is(0));
		assertThat(board.containCard(card), is(false));
	}

	@Test
	public void lanes() throws Exception {
		Board board = helper.createBoardForTest("TEST_BOARD");
		Lane lane = helper.createLaneForTest(board, "BoardTest.lanes");

		board.addLane(lane);
		assertThat(board.getLanes()[0], sameInstance(lane));
		assertThat(board.containLane(lane), is(true));

		board.removeLane(lane);
		assertThat(board.getCards().length, is(0));
		assertThat(board.containLane(lane), is(false));
	}

	@Test
	public void mock_lanes() throws Exception {
		Board board = helper.createBoardForTest("TEST_BOARD");
		Lane lane = new org.kompiro.jamcircle.kanban.model.mock.Lane();

		board.addLane(lane);
		assertThat(board.getLanes()[0], sameInstance(lane));
		assertThat(board.containLane(lane), is(true));

		board.removeLane(lane);
		assertThat(board.getCards().length, is(0));
		assertThat(board.containLane(lane), is(false));
	}

	@Test
	public void no_exceptions_are_occured_when_call_toString() throws Exception {

		Board board = helper.createBoardForTest("TEST_BOARD");
		board.toString();

	}

}
