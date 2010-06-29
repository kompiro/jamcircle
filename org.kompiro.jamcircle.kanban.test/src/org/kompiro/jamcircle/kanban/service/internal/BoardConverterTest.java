package org.kompiro.jamcircle.kanban.service.internal;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.test.util.TestUtils;
import org.kompiro.jamcircle.scripting.ScriptTypes;


public class BoardConverterTest {

	private BoardConverter conv;

	@Before
	public void before() throws Exception{
		
		conv = new BoardConverter();
		
	}
	
	@Test
	public void modelToText_simple_board() throws Exception {
		
		Board board = createBoard("test");
		
		String actual = conv.modelToText(board);
		assertThat(actual,is(not(nullValue())));
		String expected = new TestUtils().readFile(getClass(),"simple_board.txt");
		assertThat(actual,is(expected));
		assertThat(board.getLanes(),is(nullValue()));
	}

	@Test
	public void modelToText_board_has_script() throws Exception {
		
		Board board = createBoard("test");
		when(board.getScript()).thenReturn("p test");
		when(board.getScriptType()).thenReturn(ScriptTypes.JRuby);

		String actual = conv.modelToText(board);
		assertThat(actual,is(not(nullValue())));
		String expected = new TestUtils().readFile(getClass(),"simple_board.txt");
		assertThat(actual,is(expected));
	}
	
	@Test
	public void modelToText_no_title() throws Exception {
		
		Board board = createBoard("");
		
		String actual = conv.modelToText(board);
		assertThat(actual,is(not(nullValue())));
		String expected = new TestUtils().readFile(getClass(),"no_title_board.txt");
		assertThat(actual,is(expected));
		
	}
	
	@Test
	public void modelToText_include_space() throws Exception {
		
		Board board = createBoard("test 1");
		
		String actual = conv.modelToText(board);
		assertThat(actual,is(not(nullValue())));
		String expected = new TestUtils().readFile(getClass(),"include_space.txt");
		assertThat(actual,is(expected));
		
	}
	
	@Test
	public void modelToText_board_has_a_lane() throws Exception {
		
		Lane lane = createLane(1,"test1",0,0,250,600);
		
		Lane[] lanes = new Lane[]{lane};
		Board board = createBoard("6月30日のボード",lanes);
		
		String actual = conv.modelToText(board);
		assertThat(actual,is(not(nullValue())));
		String expected = new TestUtils().readFile(getClass(),"board_has_a_lane.txt");
		assertThat(actual,is(expected));
		
	}
	
	@Test
	public void modelToText_board_has_some_lanes() throws Exception {
		
		Lane lane1 = createLane(1,"test1",0,0,200,500);
		Lane lane2 = createLane(2,"test 2",200,0,200,500);
		Lane lane3 = createLane(3,"test	3",0,300,200,500);
		
		Lane[] lanes = new Lane[]{lane1,lane2,lane3};
		Board board = createBoard("6月30日のボード",lanes);
		
		String actual = conv.modelToText(board);
		assertThat(actual,is(not(nullValue())));
		String expected = new TestUtils().readFile(getClass(),"board_has_some_lanes.txt");
		assertThat(actual,is(expected));
		
	}
	
	@Test
	public void textToModel_simple_board() throws Exception {

		Board expected = mock(Board.class);
		String title = "test";
		when(expected.getTitle()).thenReturn(title);
		
		KanbanService service = mock(KanbanService.class);
		when(service.createBoard(title)).thenReturn(expected);
		conv.setKanbanService(service);
		
		String text = new TestUtils().readFile(getClass(),"simple_board.txt");
		Board board = conv.textToModel(text);
		assertThat(board,is(not(nullValue())));
		assertThat(board.getTitle(),is("test"));
		
	}

	
	@Test
	public void textToModel_no_title_board() throws Exception {

		String boardTitle = "";
		Board expected = createBoard(boardTitle);
		
		KanbanService service = mock(KanbanService.class);
		when(service.createBoard(boardTitle)).thenReturn(expected);
		conv.setKanbanService(service);
		
		String text = new TestUtils().readFile(getClass(),"no_title_board.txt");
		Board board = conv.textToModel(text);
		assertThat(board,is(not(nullValue())));
		assertThat(board.getTitle(),is(boardTitle));
		
	}
	
	@Test
	public void textToModel_include_space() throws Exception {

		String boardTitle = "test 1";
		Board expected = createBoard(boardTitle);
		
		KanbanService service = mock(KanbanService.class);
		when(service.createBoard(boardTitle)).thenReturn(expected);
		conv.setKanbanService(service);
		 
		String text = new TestUtils().readFile(getClass(),"include_space.txt");
		Board board = conv.textToModel(text);
		assertThat(board,is(not(nullValue())));
		assertThat(board.getTitle(),is(boardTitle));
		
	}
	
	@Test
	public void textToModel_has_a_lane() throws Exception {

		String boardTitle = "6月30日のボード";
		Board expected = createMockBoard(boardTitle);

		Lane lane = createLane(1,"test1",0,0,250,600);

		KanbanService service = mock(KanbanService.class);
		when(service.createBoard(boardTitle)).thenReturn(expected);
		when(service.createLane(expected, "test1", 0, 0, 250, 600)).thenReturn(lane);
		conv.setKanbanService(service);
		 
		String text = new TestUtils().readFile(getClass(),"board_has_a_lane.txt");
		Board board = conv.textToModel(text);
		assertThat(board,is(not(nullValue())));
		assertThat(board.getTitle(),is(boardTitle));

		Lane[] lanes = board.getLanes();
		assertThat(lanes,is(not(nullValue())));
		assertThat(lanes.length, is(1));
		Lane actualLane = lanes[0];
		assertThat(actualLane,is(not(nullValue())));
		assertThat(actualLane.getID(), is(1));
		assertThat(actualLane.getStatus(), is("test1"));
	}

	private Board createMockBoard(String title) {
		org.kompiro.jamcircle.kanban.model.mock.Board board = new org.kompiro.jamcircle.kanban.model.mock.Board();
		board.setTitle(title);
		return board;
	}

	@Test
	public void textToModel_has_some_lanes() throws Exception {

		String boardTitle = "6月30日のボード";
		Board expected = createMockBoard(boardTitle);

		Lane lane1 = createLane(1,"test1",0,0,200,500);
		Lane lane2 = createLane(2,"test 2",200,0,200,500);
		Lane lane3 = createLane(3,"test	3",0,300,200,500);

		KanbanService service = mock(KanbanService.class);
		when(service.createBoard(boardTitle)).thenReturn(expected);
		when(service.createLane(expected, "test1", 0, 0, 200, 500)).thenReturn(lane1);
		when(service.createLane(expected, "test 2", 200, 0, 200, 500)).thenReturn(lane2);
		when(service.createLane(expected, "test	3", 0, 300, 200, 500)).thenReturn(lane3);
		conv.setKanbanService(service);
		 
		String text = new TestUtils().readFile(getClass(),"board_has_some_lanes.txt");
		Board board = conv.textToModel(text);
		assertThat(board,is(not(nullValue())));
		assertThat(board.getTitle(),is(boardTitle));

		Lane[] lanes = board.getLanes();
		assertThat(lanes,is(not(nullValue())));
		assertThat(lanes.length, is(3));
		
		Lane actualLane1 = lanes[0];
		assertThat(actualLane1,is(not(nullValue())));
		assertThat(actualLane1.getID(), is(1));
		assertThat(actualLane1.getStatus(), is("test1"));

		Lane actualLane2 = lanes[1];
		assertThat(actualLane2,is(not(nullValue())));
		assertThat(actualLane2.getID(), is(2));
		assertThat(actualLane2.getStatus(), is("test 2"));

		Lane actualLane3 = lanes[2];
		assertThat(actualLane3,is(not(nullValue())));
		assertThat(actualLane3.getID(), is(3));
		assertThat(actualLane3.getStatus(), is("test	3"));

	}
	
	private Board createBoard(String title) {
		return createBoard(title, null);
	}
	
	private Board createBoard(String title, Lane[] lanes) {
		Board board = mock(Board.class);
		when(board.getTitle()).thenReturn(title);
		when(board.getLanes()).thenReturn(lanes);
		return board;
	}

	private Lane createLane(int id, String status, int locationX, int locationY, int width, int height) {
		Lane lane = mock(Lane.class);
		when(lane.getID()).thenReturn(id);
		when(lane.getStatus()).thenReturn(status);
		when(lane.getX()).thenReturn(locationX);
		when(lane.getY()).thenReturn(locationY);
		when(lane.getWidth()).thenReturn(width);
		when(lane.getHeight()).thenReturn(height);
		return lane;
	}

	
}
