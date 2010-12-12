package org.kompiro.jamcircle.kanban.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.java.ao.DBParam;
import net.java.ao.EntityManager;

import org.junit.*;
import org.kompiro.jamcircle.kanban.KanbanActivator;
import org.kompiro.jamcircle.kanban.service.internal.KanbanServiceEnvironment;
import org.kompiro.jamcircle.kanban.service.internal.KanbanServiceTestHelper;
import org.kompiro.jamcircle.kanban.test.util.TestUtils;
import org.kompiro.jamcircle.storage.service.StorageService;
import org.kompiro.jamcircle.test.OSGiEnvironment;

public class ModelTest {

	private static final int TARGET_CARD_COUNT = 5;
	private static final int CURRENT_CARD_COUNT_ON_LANE = 2;
	private TestUtils util = new TestUtils();
	private KanbanServiceTestHelper helper;
	private EntityManager entityManager;

	@Rule
	public OSGiEnvironment env = new OSGiEnvironment();

	@Rule
	public KanbanServiceEnvironment serviceEnv = new KanbanServiceEnvironment();

	@BeforeClass
	public static void initializeEnvironment() throws Exception {
		Logger.getLogger("net.java.ao").setLevel(Level.FINE);
	}

	@Before
	public void before() throws Exception {
		helper = serviceEnv.getHelper();
		entityManager = serviceEnv.getEntityManager();
	}

	@After
	public void after() throws Exception {
		helper.tearDownKanbanService();
	}

	@Test
	public void files() throws Exception {
		DBParam[] params = new DBParam[] {
				new DBParam(Card.PROP_SUBJECT, "test"),
				new DBParam(Card.PROP_CREATEDATE, new Date()),
				new DBParam(Card.PROP_TRASHED, true),
		};
		Card card = entityManager.create(Card.class, params);
		File target = util.target();
		card.addFile(target);
		StorageService service = KanbanActivator.getKanbanService().getStorageService();
		String path = CardImpl.CARD_PATH + card.getID() + File.separator + "long.txt";
		String message = String.format("'%s' is not exist.", path);
		assertTrue(message, service.getFileService().fileExists(path));
		assertTrue(card.hasFiles());
		FileInputStream stream = new FileInputStream(card.getFiles().get(0));
		assertEquals(util.readFile(TestUtils.LONG_TXT), getString(stream));
	}

	private String getString(InputStream stream) throws IOException {
		Reader r = new InputStreamReader(stream);
		BufferedReader br = new BufferedReader(r);
		StringBuilder builder = new StringBuilder();
		String line = null;
		while ((line = br.readLine()) != null) {
			builder.append(line + "\n");
		}
		return builder.toString();

	}

	@Test
	public void longValue() throws Exception {

		DBParam[] params = new DBParam[] {
				new DBParam(Card.PROP_SUBJECT, "very very long value"),
				new DBParam(Card.PROP_CONTENT, util.readFile(TestUtils.LONG_TXT)),
				new DBParam(Card.PROP_CREATEDATE, new Date()),
				new DBParam(Card.PROP_TRASHED, false),
		};
		entityManager.create(Card.class, params);
		params = new DBParam[] {
				new DBParam(Lane.PROP_STATUS, "very very long value"),
				new DBParam(Lane.PROP_SCRIPT, util.readFile(TestUtils.LONG_TXT)),
				new DBParam(Lane.PROP_CREATE_DATE, new Date()),
		};
		entityManager.create(Lane.class, params);

	}

	@Test
	public void trashed() throws Exception {
		DBParam[] params = new DBParam[] {
				new DBParam(Card.PROP_SUBJECT, "test"),
				new DBParam(Card.PROP_CREATEDATE, new Date()),
				new DBParam(Card.PROP_TRASHED, true),
		};
		entityManager.create(Card.class, params);
		assertNotNull(entityManager.find(Card.class, Card.PROP_TRASHED + " = true"));
	}

	@Test
	public void modelRelation() throws Exception {
		Card card = entityManager.create(Card.class, new DBParam(Card.PROP_SUBJECT, "test"));
		Card card2 = entityManager.create(Card.class, new DBParam(Card.PROP_SUBJECT, "test2"));
		Board board = entityManager.create(Board.class, new DBParam(Board.PROP_TITLE, "test_board"), new DBParam(
				Board.PROP_CREATE_DATE, new Date()));
		Lane lane = entityManager.create(Lane.class, new DBParam(Lane.PROP_STATUS, "todo"), new DBParam(
				Lane.PROP_CREATE_DATE, new Date()));
		lane.setBoard(board);
		PropertyChangeListener listener = new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent evt) {
				System.out.println(evt);
			}

		};
		lane.addPropertyChangeListener(listener);
		Lane lane2 = entityManager.create(Lane.class, new DBParam(Lane.PROP_STATUS, "todo"), new DBParam(
				Lane.PROP_CREATE_DATE, new Date()));
		PropertyChangeListener listener2 = new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent evt) {
				System.out.println(evt);
			}

		};
		System.out.println(lane);
		// needs flushAll() Because lane's status is still before value... Why?
		entityManager.flushAll();
		System.out.println(lane);
		lane2.addPropertyChangeListener(listener2);
		card.setLane(lane);
		card.save(false);
		card2.setLane(lane);
		card2.save(false);
		assertEquals(CURRENT_CARD_COUNT_ON_LANE, lane.getCards().length);
		assertEquals(card, lane.getCards()[0]);
		assertEquals("todo", lane.getStatus());
		assertEquals("todo", card.getStatus());
		assertTrue(card2.getFiles().isEmpty());

		for (int i = 2; i < TARGET_CARD_COUNT; i++) {
			card = entityManager.create(Card.class, new DBParam(Card.PROP_SUBJECT, "test" + i));
			card.setLane(lane2);
			card.save();
			card.setLane(null);
			card.setDeletedVisuals(true);
			card.setX(i * 5);
			card.setY(i * 50);
			card.save();
			card.setDeletedVisuals(false);
			lane.addCard(card);
		}
		assertEquals(TARGET_CARD_COUNT, lane.getCards().length);
	}

}
