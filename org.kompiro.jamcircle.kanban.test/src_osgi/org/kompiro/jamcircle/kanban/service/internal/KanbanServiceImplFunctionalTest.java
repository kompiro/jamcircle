package org.kompiro.jamcircle.kanban.service.internal;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.kompiro.jamcircle.kanban.model.*;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.test.OSGiEnvironment;

public class KanbanServiceImplFunctionalTest {

	@Rule
	public OSGiEnvironment env = new OSGiEnvironment();

	@Rule
	public KanbanServiceEnvironment serviceEnv = new KanbanServiceEnvironment();

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private KanbanServiceTestHelper helper;

	private KanbanService service;

	@BeforeClass
	public static void initializeEnvironment() throws Exception {
		Logger.getLogger("net.java.ao").setLevel(Level.FINE);
	}

	@Before
	public void before() throws Exception {
		helper = serviceEnv.getHelper();
		service = helper.getKanbanService();
	}

	@After
	public void after() throws Exception {
		serviceEnv.getHelper().tearDownKanbanService();
	}

	@Test
	public void initialize() throws Exception {
		Board[] boards = service.findAllBoard();
		assertEquals(1, boards.length);
		boards = service.findAllBoard();
		assertEquals(1, boards.length);
		Lane[] lanes = boards[0].getLanes();
		assertEquals(3, lanes.length);
	}

	@Test
	public void create_board() throws Exception {
		Board board = service.createBoard("test kanban");
		assertThat(board, is(notNullValue()));
		assertThat(board.getTitle(), is("test kanban"));

		Board[] boards = service.findAllBoard();
		assertThat(boards.length, is(2));
		assertThat(boards[1], is(board));
	}

	@Test
	public void create_some_board() throws Exception {
		Board board1 = service.createBoard("test kanban");
		Board board2 = service.createBoard("added");

		Board[] boards = service.findAllBoard();
		assertThat(boards.length, is(3));
		assertThat(boards[1], is(board1));
		assertThat(boards[2], is(board2));
	}

	@Test
	public void create_card() throws Exception {
		Board board = service.createBoard("test kanban");
		Card card = service.createCard(board, "test", null, 10, 20);
		Card[] cards = service.findAllCards();
		assertEquals(1, cards.length);

		Card actualCard1 = cards[0];
		assertThat(actualCard1.getSubject(), is("test"));
		assertThat(actualCard1.getOwner(), is(nullValue()));
		assertThat(actualCard1.getCreated(), is(nullValue()));
		assertThat(actualCard1.getCreateDate(), is(notNullValue()));
		assertThat(actualCard1.getX(), is(10));
		assertThat(actualCard1.getY(), is(20));
		assertThat(card, is(actualCard1));
	}

	@Test
	public void create_some_card() throws Exception {
		Board board = service.createBoard("test kanban");
		Card card1 = service.createCard(board, "test", null, 10, 20);
		Card card2 = service.createCard(board, "test2", null, 15, 30);

		Card[] cards = service.findAllCards();

		assertThat(cards.length, is(2));

		Card actualCard1 = cards[0];
		assertThat(actualCard1.getSubject(), is("test"));
		assertThat(actualCard1.getOwner(), is(nullValue()));
		assertThat(actualCard1.getCreated(), is(nullValue()));
		assertThat(actualCard1.getCreateDate(), is(notNullValue()));
		assertThat(actualCard1.getX(), is(10));
		assertThat(actualCard1.getY(), is(20));
		assertThat(card1, is(actualCard1));

		Card actualCard2 = cards[1];
		assertThat(actualCard2.getSubject(), is("test2"));
		assertThat(actualCard2.getOwner(), is(nullValue()));
		assertThat(actualCard2.getCreated(), is(nullValue()));
		assertThat(actualCard2.getCreateDate(), is(notNullValue()));
		assertThat(actualCard2.getX(), is(15));
		assertThat(actualCard2.getY(), is(30));
		assertThat(card2, is(actualCard2));
	}

	@Test
	public void create_card_when_user_name_is_null() throws Exception {
		Board board = service.createBoard("test kanban");
		User testUser = service.addUser("kompiro@test");
		service.createCard(board, "test for username is null", testUser, 15, 30);
		Card[] cards = service.findAllCards();
		Card actualCard = cards[0];
		assertEquals("kompiro@test", actualCard.getCreated());
	}

	@Test
	public void create_card_when_user_name_is_not_null() throws Exception {
		Board board = service.createBoard("test kanban");
		User testUser = service.addUser("kompiro@test");
		testUser.setUserName("Hiroki Kondo");
		testUser.save(true);
		service.createCard(board, "test for username is null", testUser, 15, 30);

		Card[] cards = service.findAllCards();
		Card actualCard = cards[0];
		assertEquals("Hiroki Kondo", actualCard.getCreated());

	}

	@Test
	public void add_file_to_card() throws Exception {

		Board board = service.createBoard("test kanban");
		Card card = service.createCard(board, "test", null, 10, 20);
		File file = folder.newFile("test.txt");
		card.addFile(file);
		List<File> actualFiles = card.getFiles();
		assertThat(actualFiles.size(), is(1));
		assertThat(actualFiles.get(0).getName(), is(file.getName()));

		Card[] cards = service.findAllCards();
		Card actualCard = cards[0];
		actualFiles = actualCard.getFiles();
		assertThat(actualFiles.size(), is(1));
		assertThat(actualFiles.get(0).getName(), is(file.getName()));

	}

	@Test
	public void delete_all_cards() throws Exception {
		Board board = service.createBoard("test kanban");
		service.createCard(board, "test", null, 10, 20);

		Card[] cards = service.findAllCards();
		assertThat(cards.length, is(1));

		service.deleteAllCards();
		cards = service.findAllCards();

		assertThat(cards.length, is(0));
	}

	@Test
	public void find_card_sent_to() throws Exception {
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
	public void has_user() throws Exception {
		assertFalse(service.hasUser("kompiro@test"));
		service.addUser("kompiro@test");
		assertTrue(service.hasUser("kompiro@test"));
	}

	@Test
	public void add_user() throws Exception {
		User user = service.addUser("kompiro@test");
		assertNotNull(user);
		assertTrue(service.hasUser("kompiro@test"));
		assertNotNull(service.findUser("kompiro@test"));
	}

	@Test
	public void add_icon() throws Exception {
		Icon icon = service.addIcon("test_type", 0, 0);
		assertNotNull(icon);
	}

	@Test
	public void export_and_import() throws Exception {
		Board board = service.createBoard("test board");
		Card card = service.createCard(board, "test", null, 100, 200);
		card.save(false);
		File cards_csv = File.createTempFile("test_card", "csv");
		service.exportCards(cards_csv);
		service.importCards(cards_csv);
	}

}
