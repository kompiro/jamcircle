package org.kompiro.jamcircle.kanban.service.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.*;
import org.kompiro.jamcircle.kanban.OSGiEnvironment;
import org.kompiro.jamcircle.kanban.model.*;
import org.kompiro.jamcircle.kanban.service.KanbanService;

public class KanbanServiceImplPDETest {

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
	public void serviceInitialize() throws Exception {
		KanbanService service = helper.getKanbanService();
		service.init();
		Board[] boards = service.findAllBoard();
		assertEquals(1, boards.length);
		boards = service.findAllBoard();
		assertEquals(1, boards.length);
		Lane[] lanes = boards[0].getLanes();
		assertEquals(3, lanes.length);
	}

	@Test
	public void createCard() throws Exception {
		KanbanService service = helper.getKanbanService();
		service.init();

		Board board = service.createBoard("test kanban");
		Card card = service.createCard(board, "test", null, 10, 20);
		Card[] cards = service.findAllCards();
		assertEquals(1, cards.length);
		assertEquals("test", cards[0].getSubject());
		assertNull(cards[0].getOwner());
		assertEquals(card, cards[0]);

		User testUser = service.addUser("kompiro@test");
		card = service.createCard(board, "test for username is null", testUser, 15, 30);
		cards = service.findAllCards();
		assertEquals(2, cards.length);
		assertEquals("kompiro@test", cards[1].getCreated());

		testUser.setUserName("Hiroki Kondo");
		testUser.save(true);

		card = service.createCard(board, "test for username is NOT null", testUser, 45, 130);
		cards = service.findAllCards();
		assertEquals(3, cards.length);
		assertEquals("Hiroki Kondo", cards[2].getCreated());

		service.deleteAllCards();

		cards = service.findAllCards();
		assertEquals(0, cards.length);
	}

	@Test
	public void deleteAllCards() throws Exception {
		KanbanService service = helper.getKanbanService();
		service.init();

		Board board = service.createBoard("test kanban");
		service.createCard(board, "test", null, 10, 20);
		Card[] cards = service.findAllCards();
		assertEquals(1, cards.length);
		service.deleteAllCards();
		cards = service.findAllCards();

		assertEquals(0, cards.length);
	}

	@Test
	public void findCardSentTo() throws Exception {
		KanbanService service = helper.getKanbanService();
		service.init();

		User sentTo = null;
		Card[] cards = service.findCardsSentTo(sentTo);
		assertEquals(0, cards.length);
		User testUser = service.addUser("kompiro@test");
		Board board = service.createBoard("test kanban");
		Card testCard = service.createCard(board, "test sent to kompiro", null, 0, 0);
		testCard.setTo(testUser);
		testCard.save(true);
		Card[] sentcards = service.findCardsSentTo(testUser);
		assertEquals(1, sentcards.length);
	}

	@Test
	public void hasUser() throws Exception {
		KanbanService service = helper.getKanbanService();
		service.init();

		assertFalse(service.hasUser("kompiro@test"));
		service.addUser("kompiro@test");
		assertTrue(service.hasUser("kompiro@test"));
	}

	@Test
	public void addUser() throws Exception {
		KanbanService service = helper.getKanbanService();
		service.init();

		User user = service.addUser("kompiro@test");
		assertNotNull(user);
		assertTrue(service.hasUser("kompiro@test"));
		assertNotNull(service.findUser("kompiro@test"));
	}

	@Test
	public void addIcon() throws Exception {
		KanbanService service = helper.getKanbanService();
		service.init();

		Icon icon = service.addIcon("test_type", 0, 0);
		assertNotNull(icon);
	}

	@Test
	public void exportAndImportCards() throws Exception {
		KanbanService service = helper.getKanbanService();
		service.init();

		Board board = service.createBoard("test board");
		Card card = service.createCard(board, "test", null, 100, 200);
		card.save(false);
		File cards_csv = File.createTempFile("test_card", "csv");
		service.exportCards(cards_csv);
		service.importCards(cards_csv);
	}

}
