package org.kompiro.jamcircle.kanban.service.internal;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.service.exception.BoardFileFormatException;
import org.kompiro.jamcircle.kanban.test.util.TestUtils;
import org.kompiro.jamcircle.scripting.ScriptTypes;

public class BoardConverterImplTest {

	// @Rule
	// public ExpectedException exception = ExpectedException.none();

	private BoardConverterImpl conv;

	private TestUtils testUtil;

	@Before
	public void before() throws Exception {

		conv = new BoardConverterImpl();
		testUtil = new TestUtils();

	}

	@Test(expected = IllegalArgumentException.class)
	public void modelToText_throws_IllegalArgumentException_when_null_value()
			throws Exception {

		// exception.expect(IllegalArgumentException.class);
		// exception.expectMessage(any(String.class));
		conv.modelToText(null);
	}

	@Test
	public void modelToText_simple_board() throws Exception {

		Board board = createBoard("test");

		String actual = conv.modelToText(board);
		assertThat(actual, is(not(nullValue())));
		String expected = testUtil.readFile(getClass(),
				"simple_board.txt");
		assertThat(actual, is(expected));
		assertThat(board.getLanes(), is(nullValue()));
	}

	@Test
	public void modelToText_board_has_script() throws Exception {

		Board board = createBoard("test");
		when(board.getScript()).thenReturn("p test");
		when(board.getScriptType()).thenReturn(ScriptTypes.JRuby);

		String actual = conv.modelToText(board);
		assertThat(actual, is(not(nullValue())));
		String expected = testUtil.readFile(getClass(),
				"simple_board.txt");
		assertThat(actual, is(expected));
	}

	@Test
	public void modelToText_no_title() throws Exception {

		Board board = createBoard("");

		String actual = conv.modelToText(board);
		assertThat(actual, is(not(nullValue())));
		String expected = testUtil.readFile(getClass(),
				"no_title_board.txt");
		assertThat(actual, is(expected));

	}

	@Test
	public void modelToText_include_space() throws Exception {

		Board board = createBoard("test 1");

		String actual = conv.modelToText(board);
		assertThat(actual, is(not(nullValue())));
		String expected = testUtil.readFile(getClass(),
				"include_space.txt");
		assertThat(actual, is(expected));

	}

	@Test
	public void modelToText_board_has_a_lane() throws Exception {

		Lane lane = createLane(1, "test1", 0, 0, 250, 600);

		Lane[] lanes = new Lane[] { lane };
		Board board = createBoard("6月30日のボード", lanes);

		String actual = conv.modelToText(board);
		assertThat(actual, is(not(nullValue())));
		String expected = testUtil.readFile(getClass(),
				"board_has_a_lane.txt");
		assertThat(actual, is(expected));

	}

	@Test
	public void modelToText_board_has_some_lanes() throws Exception {

		Lane lane1 = createLane(1, "test1", 0, 0, 200, 500);
		Lane lane2 = createLane(2, "test 2", 200, 0, 200, 500);
		Lane lane3 = createLane(3, "test	3", 0, 300, 200, 500);

		Lane[] lanes = new Lane[] { lane1, lane2, lane3 };
		Board board = createBoard("6月30日のボード", lanes);

		String actual = conv.modelToText(board);
		assertThat(actual, is(not(nullValue())));
		String expected = testUtil.readFile(getClass(),
				"board_has_some_lanes.txt");
		assertThat(actual, is(expected));

	}

	@Test
	public void textToModel_simple_board() throws Exception {

		Board expected = mock(Board.class);
		String title = "test";
		when(expected.getTitle()).thenReturn(title);

		KanbanService service = mock(KanbanService.class);
		when(service.createBoard(title)).thenReturn(expected);
		conv.setKanbanService(service);

		String text = testUtil.readFile(getClass(), "simple_board.txt");
		Board board = conv.textToModel(text);
		assertThat(board, is(not(nullValue())));
		assertThat(board.getTitle(), is("test"));

	}

	@Test
	public void textToModel_no_title_board() throws Exception {

		String boardTitle = "";
		Board expected = createBoard(boardTitle);

		KanbanService service = mock(KanbanService.class);
		when(service.createBoard(boardTitle)).thenReturn(expected);
		conv.setKanbanService(service);

		String text = testUtil
				.readFile(getClass(), "no_title_board.txt");
		Board board = conv.textToModel(text);
		assertThat(board, is(not(nullValue())));
		assertThat(board.getTitle(), is(boardTitle));

	}

	@Test
	public void textToModel_include_space() throws Exception {

		String boardTitle = "test 1";
		Board expected = createBoard(boardTitle);

		KanbanService service = mock(KanbanService.class);
		when(service.createBoard(boardTitle)).thenReturn(expected);
		conv.setKanbanService(service);

		String text = testUtil.readFile(getClass(), "include_space.txt");
		Board board = conv.textToModel(text);
		assertThat(board, is(not(nullValue())));
		assertThat(board.getTitle(), is(boardTitle));

	}

	@Test
	public void textToModel_has_a_lane() throws Exception {

		String boardTitle = "6月30日のボード";
		Board expected = createMockBoard(boardTitle);

		Lane lane = createLane(1, "test1", 0, 0, 250, 600);

		KanbanService service = mock(KanbanService.class);
		when(service.createBoard(boardTitle)).thenReturn(expected);
		when(service.createLane(expected, "test1", 0, 0, 250, 600)).thenReturn(
				lane);
		conv.setKanbanService(service);

		String text = testUtil.readFile(getClass(),
				"board_has_a_lane.txt");
		Board board = conv.textToModel(text);
		assertThat(board, is(not(nullValue())));
		assertThat(board.getTitle(), is(boardTitle));

		Lane[] lanes = board.getLanes();
		assertThat(lanes, is(not(nullValue())));
		assertThat(lanes.length, is(1));
		Lane actualLane = lanes[0];
		assertThat(actualLane, is(not(nullValue())));
		assertThat(actualLane.getID(), is(1));
		assertThat(actualLane.getStatus(), is("test1"));
	}

	@Test
	public void textToModel_has_some_lanes() throws Exception {

		String boardTitle = "6月30日のボード";
		Board expected = createMockBoard(boardTitle);

		Lane lane1 = createLane(1, "test1", 0, 0, 200, 500);
		Lane lane2 = createLane(2, "test 2", 200, 0, 200, 500);
		Lane lane3 = createLane(3, "test	3", 0, 300, 200, 500);

		KanbanService service = mock(KanbanService.class);
		when(service.createBoard(boardTitle)).thenReturn(expected);
		when(service.createLane(expected, "test1", 0, 0, 200, 500))
				.thenReturn(lane1);
		when(service.createLane(expected, "test 2", 200, 0, 200, 500))
				.thenReturn(lane2);
		when(service.createLane(expected, "test	3", 0, 300, 200, 500))
				.thenReturn(lane3);
		conv.setKanbanService(service);

		String text = testUtil.readFile(getClass(),
				"board_has_some_lanes.txt");
		Board board = conv.textToModel(text);
		assertThat(board, is(not(nullValue())));
		assertThat(board.getTitle(), is(boardTitle));

		Lane[] lanes = board.getLanes();
		assertThat(lanes, is(not(nullValue())));
		assertThat(lanes.length, is(3));

		Lane actualLane1 = lanes[0];
		assertThat(actualLane1, is(not(nullValue())));
		assertThat(actualLane1.getID(), is(1));
		assertThat(actualLane1.getStatus(), is("test1"));

		Lane actualLane2 = lanes[1];
		assertThat(actualLane2, is(not(nullValue())));
		assertThat(actualLane2.getID(), is(2));
		assertThat(actualLane2.getStatus(), is("test 2"));

		Lane actualLane3 = lanes[2];
		assertThat(actualLane3, is(not(nullValue())));
		assertThat(actualLane3.getID(), is(3));
		assertThat(actualLane3.getStatus(), is("test	3"));

	}

	@Test(expected = BoardFileFormatException.class)
	public void textToModel_illegal_board_key() throws Exception {

		// exception.expect(BoardFileFormatException.class);
		// exception.expectMessage(any(String.class));

		String text = testUtil.readFile(getClass(),
				"illegal_board_key.txt");

		String boardTitle = "6月30日のボード";
		Board expected = createMockBoard(boardTitle);
		KanbanService service = mock(KanbanService.class);
		when(service.createBoard(boardTitle)).thenReturn(expected);

		conv.setKanbanService(service);
		conv.textToModel(text);

		verify(service, never()).createBoard(boardTitle);

	}

	@Test(expected = BoardFileFormatException.class)
	public void textToModel_illegal_lane_id() throws Exception {

		// exception.expect(BoardFileFormatException.class);
		// exception.expectMessage(any(String.class));

		String text = testUtil
				.readFile(getClass(), "illegal_lane_id.txt");

		String boardTitle = "6月30日のボード";
		Board expected = createMockBoard(boardTitle);
		KanbanService service = mock(KanbanService.class);
		when(service.createBoard(boardTitle)).thenReturn(expected);

		conv.setKanbanService(service);
		conv.textToModel(text);

	}

	@Test(expected = BoardFileFormatException.class)
	public void textToModel_illegal_lane_x() throws Exception {

		// exception.expect(BoardFileFormatException.class);
		// exception.expectMessage(any(String.class));

		String text = testUtil
				.readFile(getClass(), "illegal_lane_x.txt");

		String boardTitle = "6月30日のボード";
		Board expected = createMockBoard(boardTitle);
		KanbanService service = mock(KanbanService.class);
		when(service.createBoard(boardTitle)).thenReturn(expected);

		conv.setKanbanService(service);
		conv.textToModel(text);

	}

	@Test(expected = BoardFileFormatException.class)
	public void textToModel_illegal_lane_y() throws Exception {

		// exception.expect(BoardFileFormatException.class);
		// exception.expectMessage(any(String.class));

		String text = testUtil
				.readFile(getClass(), "illegal_lane_y.txt");

		String boardTitle = "6月30日のボード";
		Board expected = createMockBoard(boardTitle);
		KanbanService service = mock(KanbanService.class);
		when(service.createBoard(boardTitle)).thenReturn(expected);

		conv.setKanbanService(service);
		conv.textToModel(text);

	}

	@Test(expected = BoardFileFormatException.class)
	public void textToModel_illegal_lane_width() throws Exception {

		// exception.expect(BoardFileFormatException.class);
		// exception.expectMessage(any(String.class));

		String text = testUtil
				.readFile(getClass(), "illegal_lane_width.txt");

		String boardTitle = "6月30日のボード";
		Board expected = createMockBoard(boardTitle);
		KanbanService service = mock(KanbanService.class);
		when(service.createBoard(boardTitle)).thenReturn(expected);

		conv.setKanbanService(service);
		conv.textToModel(text);

	}

	@Test(expected = BoardFileFormatException.class)
	public void textToModel_illegal_lane_height() throws Exception {

		// exception.expect(BoardFileFormatException.class);
		// exception.expectMessage(any(String.class));

		String text = testUtil
				.readFile(getClass(), "illegal_lane_height.txt");

		String boardTitle = "6月30日のボード";
		Board expected = createMockBoard(boardTitle);
		KanbanService service = mock(KanbanService.class);
		when(service.createBoard(boardTitle)).thenReturn(expected);

		conv.setKanbanService(service);
		conv.textToModel(text);

	}

	@Test
	public void dump_simple_board() throws Exception {
		Board board = createBoard("test");

		File file = File.createTempFile("simple_board", BoardConverterImpl.BOARD_FORMAT_FILE_EXTENSION_NAME);
		conv.dump(file, board);

		assertThat(file.length(), is(not(0L)));
		ZipFile zip = new ZipFile(file);
		assertThat(zip.size(), is(1));

		String boardFileName = "board.yml";
		String containerName = getContainerName(file);
		ZipEntry entry = new ZipEntry(containerName + boardFileName);

		InputStreamReader reader = new InputStreamReader(zip.getInputStream(entry));
		String actual = testUtil.readFromReader(reader);

		String expected = testUtil.readFile(getClass(),
				"simple_board.txt");
		assertThat(actual, is(expected));
	}

	@Test
	public void dump_board_has_jruby_script() throws Exception {
		Board board = createMockBoard("test");
		board.setScript("p 'hello jruby'");
		board.setScriptType(ScriptTypes.JRuby);

		File file = File.createTempFile("board_has_script", BoardConverterImpl.BOARD_FORMAT_FILE_EXTENSION_NAME);
		conv.dump(file, board);

		assertThat(file.length(), is(not(0L)));
		ZipFile zip = new ZipFile(file);
		assertThat(zip.size(), is(2));

		String boardFileName = "board.yml";
		ZipEntry entry = new ZipEntry(getContainerName(file) + boardFileName);

		InputStreamReader reader = new InputStreamReader(zip.getInputStream(entry));
		String actual = testUtil.readFromReader(reader);

		String expected = testUtil.readFile(getClass(),
				"simple_board.txt");
		assertThat(actual, is(expected));

		ZipEntry scriptEntry = new ZipEntry(getContainerName(file) + "board.rb");

		reader = new InputStreamReader(zip.getInputStream(scriptEntry));
		actual = testUtil.readFromReader(reader);

		expected = testUtil.readFile(getClass(),
				"board.rb");
		assertThat(actual, is(expected));

	}

	@Test
	public void dump_board_has_javascript_script() throws Exception {
		Board board = createMockBoard("test");
		board.setScript("print \"hello rhino\"");
		board.setScriptType(ScriptTypes.JavaScript);

		File file = File.createTempFile("board_has_javascript", BoardConverterImpl.BOARD_FORMAT_FILE_EXTENSION_NAME);
		conv.dump(file, board);

		assertThat(file.length(), is(not(0L)));
		ZipFile zip = new ZipFile(file);
		assertThat(zip.size(), is(2));

		String boardFileName = "board.yml";
		ZipEntry entry = new ZipEntry(getContainerName(file) + boardFileName);

		InputStreamReader reader = new InputStreamReader(zip.getInputStream(entry));
		String actual = testUtil.readFromReader(reader);

		String expected = testUtil.readFile(getClass(),
				"simple_board.txt");
		assertThat(actual, is(expected));

		ZipEntry scriptEntry = new ZipEntry(getContainerName(file) + "board.js");

		reader = new InputStreamReader(zip.getInputStream(scriptEntry));
		actual = testUtil.readFromReader(reader);

		expected = testUtil.readFile(getClass(),
				"board.js");
		assertThat(actual, is(expected));

	}

	@Test
	public void dump_board_has_some_lanes() throws Exception {
		String title = "6月30日のボード";
		Lane lane1 = createLane(1, "test1", 0, 0, 200, 500);
		Lane lane2 = createLane(2, "test 2", 200, 0, 200, 500);
		Lane lane3 = createLane(3, "test	3", 0, 300, 200, 500);

		Lane[] lanes = new Lane[] { lane1, lane2, lane3 };
		Board board = createBoard(title, lanes);

		KanbanService service = mock(KanbanService.class);
		Board created = createMockBoard(title);
		when(service.createBoard(title)).thenReturn(created);
		Lane lane4 = createLane(4, "test1", 0, 0, 200, 500);
		when(service.createLane(created, "test1", 0, 0, 200, 500)).thenReturn(lane4);
		Lane lane5 = createLane(5, "test 2", 200, 0, 200, 500);
		when(service.createLane(created, "test 2", 200, 0, 200, 500)).thenReturn(lane5);
		Lane lane6 = createLane(6, "test	3", 0, 300, 200, 500);
		when(service.createLane(created, "test	3", 0, 300, 200, 500)).thenReturn(lane6);
		conv.setKanbanService(service);

		File file = File.createTempFile("board_has_some_lanes", BoardConverterImpl.BOARD_FORMAT_FILE_EXTENSION_NAME);
		conv.dump(file, board);
		Board actual = conv.load(file);
		assertThat(actual.getLanes().length, is(3));

	}

	@Test
	public void dump_a_lane_has_jruby_script() throws Exception {
		String title = "6月30日のボード";
		Lane lane1 = createLane(1, "test1", 0, 0, 200, 500);
		when(lane1.getScript()).thenReturn("p 'hello jruby'");
		when(lane1.getScriptType()).thenReturn(ScriptTypes.JRuby);

		Lane[] lanes = new Lane[] { lane1 };
		Board board = createBoard(title, lanes);

		KanbanService service = mock(KanbanService.class);
		Board created = createMockBoard(title);
		when(service.createBoard(title)).thenReturn(created);
		Lane lane4 = createLane(1, "test1", 0, 0, 200, 500);
		when(service.createLane(created, "test1", 0, 0, 200, 500)).thenReturn(lane4);
		conv.setKanbanService(service);

		File file = File.createTempFile("lane_has_a_script", BoardConverterImpl.BOARD_FORMAT_FILE_EXTENSION_NAME);
		conv.dump(file, board);

		assertThat(file.length(), is(not(0L)));
		ZipFile zip = new ZipFile(file);
		assertThat(zip.size(), is(2));

		String boardFileName = "board.yml";
		ZipEntry entry = new ZipEntry(getContainerName(file) + boardFileName);

		InputStreamReader reader = new InputStreamReader(zip.getInputStream(entry));
		String actual = testUtil.readFromReader(reader);

		String expected = testUtil.readFile(getClass(),
				"lane_has_a_script.txt");
		assertThat(actual, is(expected));

		ZipEntry scriptEntry = new ZipEntry(getContainerName(file) + "lanes/1_test1.rb");

		reader = new InputStreamReader(zip.getInputStream(scriptEntry));
		actual = testUtil.readFromReader(reader);

		expected = testUtil.readFile(getClass(),
				"1_test1.rb");
		assertThat(actual, is(expected));

	}

	@Test
	public void dump_some_lanes_have_javascript_script() throws Exception {
		String title = "6月30日のボード";
		Lane lane1 = createLane(1, "test1", 0, 0, 200, 500);
		when(lane1.getScript()).thenReturn("print \"hello javascript\"");
		when(lane1.getScriptType()).thenReturn(ScriptTypes.JavaScript);

		Lane lane2 = createLane(2, "test2", 300, 0, 200, 500);
		when(lane2.getScript()).thenReturn("print \"hello javascript\"");
		when(lane2.getScriptType()).thenReturn(ScriptTypes.JavaScript);

		Lane[] lanes = new Lane[] { lane1, lane2 };
		Board board = createBoard(title, lanes);

		KanbanService service = mock(KanbanService.class);
		Board created = createMockBoard(title);
		when(service.createBoard(title)).thenReturn(created);
		Lane lane3 = createLane(1, "test1", 0, 0, 200, 500);
		when(service.createLane(created, "test1", 0, 0, 200, 500)).thenReturn(lane3);
		Lane lane4 = createLane(2, "test2", 300, 0, 200, 500);
		when(service.createLane(created, "test2", 300, 0, 200, 500)).thenReturn(lane4);
		conv.setKanbanService(service);

		File file = File.createTempFile("lanes_have_some_script", BoardConverterImpl.BOARD_FORMAT_FILE_EXTENSION_NAME);
		conv.dump(file, board);

		assertThat(file.length(), is(not(0L)));
		ZipFile zip = new ZipFile(file);
		assertThat(zip.size(), is(3));

		String boardFileName = "board.yml";
		ZipEntry entry = new ZipEntry(getContainerName(file) + boardFileName);

		InputStreamReader reader = new InputStreamReader(zip.getInputStream(entry));
		String actual = testUtil.readFromReader(reader);

		String expected = testUtil.readFile(getClass(),
				"lanes_have_some_script.txt");
		assertThat(actual, is(expected));

		ZipEntry scriptEntry = new ZipEntry(getContainerName(file) + "lanes/1_test1.js");

		reader = new InputStreamReader(zip.getInputStream(scriptEntry));
		actual = testUtil.readFromReader(reader);

		expected = testUtil.readFile(getClass(),
				"1_test1.js");
		assertThat(actual, is(expected));

		scriptEntry = new ZipEntry(getContainerName(file) + "lanes/2_test2.js");

		reader = new InputStreamReader(zip.getInputStream(scriptEntry));
		actual = testUtil.readFromReader(reader);

		expected = testUtil.readFile(getClass(),
				"2_test2.js");
		assertThat(actual, is(expected));

	}

	@Test
	public void load_simple_board() throws Exception {
		String title = "test";
		Board board = createBoard(title);

		KanbanService service = mock(KanbanService.class);
		when(service.createBoard(title)).thenReturn(board);
		conv.setKanbanService(service);

		File file = File.createTempFile("simple_board", BoardConverterImpl.BOARD_FORMAT_FILE_EXTENSION_NAME);
		conv.dump(file, board);
		Board actual = conv.load(file);

		assertThat(actual.getTitle(), is("test"));
		assertThat(actual.getScriptType(), is(nullValue()));
	}

	@Test
	public void load_board_has_a_jruby_script() throws Exception {
		String title = "test";
		String script = "p 'hello jruby'";

		Board board = createMockBoard(title);
		board.setScript(script);
		board.setScriptType(ScriptTypes.JRuby);

		KanbanService service = mock(KanbanService.class);
		Board createBoard = createBoard(title);
		when(service.createBoard(title)).thenReturn(createBoard);
		conv.setKanbanService(service);

		File file = File
				.createTempFile("board_has_a_jruby_script", BoardConverterImpl.BOARD_FORMAT_FILE_EXTENSION_NAME);
		conv.dump(file, board);
		Board actual = conv.load(file);

		verify(actual).setScriptType(ScriptTypes.JRuby);
		verify(actual).setScript(script);
		verify(actual).save();
	}

	@Test
	public void load_board_has_a_javascript_script() throws Exception {
		String title = "test";
		String script = "print \"hello javascript\"";

		Board board = createMockBoard(title);
		board.setScript(script);
		board.setScriptType(ScriptTypes.JavaScript);

		KanbanService service = mock(KanbanService.class);
		when(service.createBoard(title)).thenReturn(createMockBoard(title));
		conv.setKanbanService(service);

		File file = File.createTempFile("board_has_a_javascript_script",
				BoardConverterImpl.BOARD_FORMAT_FILE_EXTENSION_NAME);
		conv.dump(file, board);
		Board actual = conv.load(file);

		assertThat(actual.getTitle(), is("test"));
		assertThat(actual.getScriptType(), is(ScriptTypes.JavaScript));
		assertThat(actual.getScript().trim(), is(script));
	}

	@Test
	public void load_a_lane_has_jruby_script() throws Exception {
		String title = "6月30日のボード";
		Lane lane1 = createLane(1, "test1", 0, 0, 200, 500);
		when(lane1.getScript()).thenReturn("p 'hello jruby'");
		when(lane1.getScriptType()).thenReturn(ScriptTypes.JRuby);

		Lane[] lanes = new Lane[] { lane1 };
		Board board = createBoard(title, lanes);

		KanbanService service = mock(KanbanService.class);
		Board created = createMockBoard(title);
		when(service.createBoard(title)).thenReturn(created);
		Lane lane4 = createLane(1, "test1", 0, 0, 200, 500);
		when(service.createLane(created, "test1", 0, 0, 200, 500)).thenReturn(lane4);
		conv.setKanbanService(service);

		File file = File.createTempFile("lane_has_a_script", BoardConverterImpl.BOARD_FORMAT_FILE_EXTENSION_NAME);
		conv.dump(file, board);
		Board actualBoard = conv.load(file);

		Lane[] actualLanes = actualBoard.getLanes();
		assertThat(actualLanes.length, is(1));

		String expected = testUtil.readFile(getClass(),
				"1_test1.rb");
		verify(actualLanes[0]).setScript(expected);
		verify(actualLanes[0]).setScriptType(ScriptTypes.JRuby);

	}

	@Test
	public void load_some_lanes_have_javascript_script() throws Exception {
		String title = "6月30日のボード";
		Lane lane1 = createLane(1, "test1", 0, 0, 200, 500);
		when(lane1.getScript()).thenReturn("print \"hello javascript\"");
		when(lane1.getScriptType()).thenReturn(ScriptTypes.JavaScript);

		Lane lane2 = createLane(2, "test2", 300, 0, 200, 500);
		when(lane2.getScript()).thenReturn("print \"hello javascript\"");
		when(lane2.getScriptType()).thenReturn(ScriptTypes.JavaScript);

		Lane[] lanes = new Lane[] { lane1, lane2 };
		Board board = createBoard(title, lanes);

		KanbanService service = mock(KanbanService.class);
		Board created = createMockBoard(title);
		when(service.createBoard(title)).thenReturn(created);
		Lane lane3 = createLane(1, "test1", 0, 0, 200, 500);
		when(service.createLane(created, "test1", 0, 0, 200, 500)).thenReturn(lane3);
		Lane lane4 = createLane(2, "test2", 300, 0, 200, 500);
		when(service.createLane(created, "test2", 300, 0, 200, 500)).thenReturn(lane4);
		conv.setKanbanService(service);

		File file = File.createTempFile("lanes_have_some_script", BoardConverterImpl.BOARD_FORMAT_FILE_EXTENSION_NAME);
		conv.dump(file, board);
		Board actualBoard = conv.load(file);

		Lane[] actualLanes = actualBoard.getLanes();
		assertThat(actualLanes.length, is(2));

		String expected = testUtil.readFile(getClass(),
				"1_test1.js");
		verify(actualLanes[0]).setScript(expected);
		verify(actualLanes[0]).setScriptType(ScriptTypes.JavaScript);
		expected = testUtil.readFile(getClass(),
				"2_test2.js");
		verify(actualLanes[1]).setScript(expected);
		verify(actualLanes[1]).setScriptType(ScriptTypes.JavaScript);

	}

	private Board createMockBoard(String title) {
		org.kompiro.jamcircle.kanban.model.mock.Board board = new org.kompiro.jamcircle.kanban.model.mock.Board();
		board.setTitle(title);
		return board;
	}

	private Board createBoard(String title) {
		return createBoard(title, null);
	}

	private Board createBoard(String title, Lane[] lanes) {
		Board board = mock(Board.class);
		when(board.getTitle()).thenReturn(title);
		if (lanes != null) {
			when(board.getLanes()).thenReturn(lanes);
		}
		return board;
	}

	private Lane createLane(int id, String status, int locationX,
			int locationY, int width, int height) {
		Lane lane = mock(Lane.class);
		when(lane.getID()).thenReturn(id);
		when(lane.getStatus()).thenReturn(status);
		when(lane.getX()).thenReturn(locationX);
		when(lane.getY()).thenReturn(locationY);
		when(lane.getWidth()).thenReturn(width);
		when(lane.getHeight()).thenReturn(height);
		return lane;
	}

	private String getContainerName(File file) {
		String name = file.getName();
		String containerName = name.substring(0, name.lastIndexOf('.')) + "/";
		return containerName;
	}

}
