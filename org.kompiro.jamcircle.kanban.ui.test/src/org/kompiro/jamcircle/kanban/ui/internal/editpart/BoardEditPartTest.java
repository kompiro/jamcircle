package org.kompiro.jamcircle.kanban.ui.internal.editpart;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeThat;
import static org.mockito.Mockito.*;

import java.util.Map;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.*;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.requests.*;
import org.junit.*;
import org.kompiro.jamcircle.kanban.model.mock.Board;
import org.kompiro.jamcircle.kanban.model.mock.Card;
import org.kompiro.jamcircle.kanban.ui.command.DeleteCommand;
import org.kompiro.jamcircle.kanban.ui.editpart.IconEditPart;
import org.kompiro.jamcircle.kanban.ui.internal.command.ChangeLaneConstraintCommand;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;
import org.kompiro.jamcircle.kanban.ui.model.IconModel;

public class BoardEditPartTest extends AbstractEditPartTest{

	private Map<Object, GraphicalEditPart> boardChildrenPartMap;
	
	@Before
	public void init() throws Exception {
		super.init();
		assumeThat(boardPart.getChildren().size(),is(INIT_BOARD_CHIHLDREN_SIZE));
	}
	
	@After
	public void finished() throws Exception{
		boardPart.deactivate();
	}

	@Test
	public void createCard() throws Exception {
		CreateRequest request = new CardCreateRequest(null,null);
		request.setType(RequestConstants.REQ_CREATE);
		request.setLocation(new Point(10,10));
		boardPart.getCommand(request).execute();
		boardPart.refresh();
		assertEquals(INIT_BOARD_CHIHLDREN_SIZE + 1,boardPart.getChildren().size());
	}
	
	@Test
	public void createLane() throws Exception {
		CreateRequest request = new LaneCreateRequest(null,board.getBoard());
		request.setType(RequestConstants.REQ_CREATE);
		Point expect = new Point(100,100);
		request.setLocation(expect);
		boardPart.getCommand(request).execute();
		boardPart.refresh();
		assertEquals(INIT_BOARD_CHIHLDREN_SIZE + 1,boardPart.getChildren().size());
		assertNotNull(board.getLane(0));
		assertEquals(100,board.getLane(0).getX());
		assertEquals(100,board.getLane(0).getY());
	}

	@Test
	public void moveCardInBoard() throws Exception {
		Card card = new Card();
		board.addCard(card);
		boardPart.refresh();
		boardChildrenPartMap = getChildlenPartmap(boardPart);
		GraphicalEditPart cardPart = boardChildrenPartMap.get(card);

		ChangeBoundsRequest request = new ChangeBoundsRequest(RequestConstants.REQ_RESIZE_CHILDREN);
		request.setEditParts(cardPart);
		request.setSizeDelta(new Dimension(100,100));
		Point expect = new Point(100,100);
		request.setMoveDelta(expect);
		boardPart.getCommand(request).execute();
		boardPart.refresh();
		
		assertEquals(expect.x,card.getX());{
			
		}
		assertEquals(expect.y,card.getY());
	}
	
	@Test
	public void moveLane() throws Exception{
		LaneMock lane = new LaneMock("test");
		board.addLane(lane);
		boardPart.refresh();
		boardChildrenPartMap = getChildlenPartmap(boardPart);
		GraphicalEditPart lanePart = boardChildrenPartMap.get(lane);

		ChangeBoundsRequest request = new ChangeBoundsRequest(RequestConstants.REQ_RESIZE_CHILDREN);
		request.setEditParts(lanePart);

		// location: 100, 100
		Point expect = new Point(100,100);
		request.setMoveDelta(expect);
		Command command = boardPart.getCommand(request);
		assertNotNull(command);
		assertTrue(command instanceof CompoundCommand);
		assertTrue(((CompoundCommand)command).getCommands().get(0) instanceof ChangeLaneConstraintCommand);
		command.execute();
		boardPart.refresh();
		assertEquals(expect.x,lane.getX());
		assertEquals(expect.y,lane.getY());
		assertEquals(LaneMock.INIT_WIDTH,lane.getWidth());
		assertEquals(LaneMock.INIT_HEIGHT,lane.getHeight());		
	}
	
	@Test
	public void moveLaneWithCard() throws Exception {
		LaneMock lane = new LaneMock("test");
		Card card = new Card();
		card.setSubject("#1 card");
		card.setX(10);
		card.setY(10);
		lane.addCard(card);
		board.addLane(lane);
				boardPart.refresh();
		boardChildrenPartMap = getChildlenPartmap(boardPart);
		GraphicalEditPart lanePart = boardChildrenPartMap.get(lane);

		ChangeBoundsRequest request = new ChangeBoundsRequest(RequestConstants.REQ_RESIZE_CHILDREN);
		request.setEditParts(lanePart);

		// location: 100, 100
		Point expect = new Point(100,100);
		request.setMoveDelta(expect);
		Command command = boardPart.getCommand(request);
		assertNotNull(command);
		assertTrue(command instanceof CompoundCommand);
		command.execute();
		
		assertEquals(expect.x,lane.getX());
		assertEquals(expect.y,lane.getY());
		assertEquals(LaneMock.INIT_WIDTH,lane.getWidth());
		assertEquals(LaneMock.INIT_HEIGHT,lane.getHeight());		
	}
	
	
	@Test
	public void changeSizeLane() throws Exception {
		LaneMock lane = new LaneMock("test");
		board.addLane(lane);
		boardPart.refresh();
		boardChildrenPartMap = getChildlenPartmap(boardPart);
		GraphicalEditPart lanePart = boardChildrenPartMap.get(lane);
		
		ChangeBoundsRequest request = new ChangeBoundsRequest(RequestConstants.REQ_RESIZE_CHILDREN);
		request.setEditParts(lanePart);
		// size: +100, +100
		Dimension sizeDelta = new Dimension(100,100);
		request.setSizeDelta(sizeDelta);
		Command command = boardPart.getCommand(request);
		assertNotNull(command);
		assertTrue(command instanceof CompoundCommand);
		assertTrue(((CompoundCommand)command).getCommands().get(0) instanceof ChangeLaneConstraintCommand);
		command.execute();

		assertEquals(0,lane.getX());
		assertEquals(0,lane.getY());
		assertEquals(LaneMock.INIT_WIDTH + sizeDelta.width,lane.getWidth());
		assertEquals(LaneMock.INIT_HEIGHT + sizeDelta.height,lane.getHeight());		
	}
	
	
	@Test
	public void getLanePart() throws Exception {
		LaneMock lane = new LaneMock("test");
		board.addLane(lane);
		boardPart.refresh();
		boardChildrenPartMap = getChildlenPartmap(boardPart);
		GraphicalEditPart lanePart = boardChildrenPartMap.get(lane);
		assertNotNull(lanePart);
	}
	
	@Test
	public void changeLaneConstraint() throws Exception {
		LaneMock lane = new LaneMock("test");
		board.addLane(lane);
		boardPart.refresh();
		boardChildrenPartMap = getChildlenPartmap(boardPart);
		GraphicalEditPart lanePart = boardChildrenPartMap.get(lane);
		
		ChangeBoundsRequest request = new ChangeBoundsRequest(RequestConstants.REQ_RESIZE_CHILDREN);
		request.setEditParts(lanePart);
		// size: +100, +100
		Dimension sizeDelta = new Dimension(100,100);
		request.setSizeDelta(sizeDelta);
		// location: 100, 100
		Point expect = new Point(100,100);
		request.setMoveDelta(expect);
		Command command = boardPart.getCommand(request);
		assertNotNull(command);
		assertTrue(command instanceof CompoundCommand);
		assertTrue(((CompoundCommand)command).getCommands().get(0) instanceof ChangeLaneConstraintCommand);
		command.execute();

		assertEquals(expect.x,lane.getX());
		assertEquals(expect.y,lane.getY());
		assertEquals(LaneMock.INIT_WIDTH + sizeDelta.width,lane.getWidth());
		assertEquals(LaneMock.INIT_HEIGHT + sizeDelta.height,lane.getHeight());		
	}
	
	@Test
	public void removeCard() throws Exception {
		Card card = new Card();
		board.addCard(card);
		boardPart.refresh();
		boardChildrenPartMap = getChildlenPartmap(boardPart);
		GraphicalEditPart cardPart = boardChildrenPartMap.get(card);
		assertNotNull(cardPart);
		
		GroupRequest request = new GroupRequest();
		request.setEditParts(cardPart);
		request.setType(RequestConstants.REQ_ORPHAN_CHILDREN);
		Command command = boardPart.getCommand(request);
		assertTrue(command instanceof CompoundCommand);
		CompoundCommand c = (CompoundCommand)command;
		assertEquals(1,c.getCommands().size());
		command.execute();
		boardPart.refresh();
		assertEquals(INIT_BOARD_CHIHLDREN_SIZE,boardPart.getChildren().size());
	}
	
	@Test
	public void deleteIcon() throws Exception {
		Board boardMock = new Board();
		board = new BoardModel(boardMock);
		IconModel icon = mock(IconModel.class);
		board.addIcon(icon);

		EditPartFactory factory = mock(EditPartFactory.class);
		BoardEditPart boardEditPart = new BoardEditPart(board);
		boardEditPart.setModel(board);
		when(factory.createEditPart(null, board)).thenReturn(boardEditPart);
		GraphicalEditPart iconEditPart = mock(IconEditPart.class);
		when(iconEditPart.getModel()).thenReturn(icon);
		IFigure figure = mock(IFigure.class);
		when(iconEditPart.getFigure()).thenReturn(figure );
		when(factory.createEditPart(boardEditPart, icon)).thenReturn(iconEditPart);
		viewer.setEditPartFactory(factory);
		
		viewer.setContents(board);
		Object object = viewer.getRootEditPart().getChildren().get(0);
		assertThat(object,instanceOf(BoardEditPart.class));
		BoardEditPart actualBoardEditPart = (BoardEditPart) object;
		boardChildrenPartMap = getChildlenPartmap(actualBoardEditPart);
		assertThat(boardChildrenPartMap.get(icon),notNullValue());
		
		GroupRequest request = new GroupRequest();
		request.setEditParts(iconEditPart);
		request.setType(RequestConstants.REQ_ORPHAN_CHILDREN);
		boardPart.getCommand(request);
		verify(iconEditPart).getAdapter(DeleteCommand.class);
		boardEditPart.deactivate();
	}
	
	@Test
	public void addCard() throws Exception {
		Card card = new Card();
		card.setSubject("card on board.");
		CardEditPart part = new CardEditPart(board);
		part.setModel(card);
		ChangeBoundsRequest request = new ChangeBoundsRequest();
		request.setEditParts(part);
		request.setType(RequestConstants.REQ_ADD);
		boardPart.getCommand(request).execute();
		boardPart.refresh();
		assertEquals(INIT_BOARD_CHIHLDREN_SIZE + 1,boardPart.getChildren().size());
	}
	
	
//	private static Job job;
//	private static boolean jobSemaphore = true;

//	@Test
//	public void addUser() throws Exception {
//		UserModel model1 = createUserModel("kompiro@localhost");
//		board.addUser(model1);
////		while(jobSemaphore){}
////		job.join();
//		assertEquals(1 + INIT_BOARD_CHIHLDREN_SIZE,boardPart.getChildren().size());
////		assertEquals(new Point(0,74),model1.getLocation());
//	}
//	
//	@Test
//	public void addUserTwice() throws Exception {
//		UserModel model1 = createUserModel("kompiro@localhost");
//		UserModel model2 = createUserModel("kompiro2@localhost");
//		board.addUser(model1);
//		board.addUser(model2);
//		assertEquals(2 + INIT_BOARD_CHIHLDREN_SIZE,boardPart.getChildren().size());
////		while(jobSemaphore){}
////		job.join();
////		assertEquals(new Point(0,74),model1.getLocation());
////		assertEquals(new Point(0,148),model2.getLocation());
//	}
//	
//	@Test
//	public void addManyUsers() throws Exception {
//		UserModel model1 = createUserModel("kompiro@localhost");
//		UserModel model2 = createUserModel("kompiro2@localhost");
//		ArrayList<UserModel> users = new ArrayList<UserModel>();
//		users.add(model1);
//		users.add(model2);
//		board.addAllUsers(users);
//		assertEquals(2 + INIT_BOARD_CHIHLDREN_SIZE,boardPart.getChildren().size());
////		while(jobSemaphore){}
////		job.join();
////		assertEquals(new Point(0,74),model1.getLocation());
////		assertEquals(new Point(0,148),model2.getLocation());
//	}
//	
//	@Test
//	public void cleareUsers() throws Exception {
//		UserModel model1 = createUserModel("kompiro@localhost");
//		UserModel model2 = createUserModel("kompiro2@localhost");
//		ArrayList<UserModel> users = new ArrayList<UserModel>();
//		users.add(model1);
//		users.add(model2);
//		board.addAllUsers(users);
//		board.clearUsers();
//		assertEquals(INIT_BOARD_CHIHLDREN_SIZE,boardPart.getChildren().size());
//	}
//	
//	private UserModel createUserModel(String userId){
//		User user = new org.kompiro.jamcircle.kanban.model.mock.User(){
//			@Override
//			public void commitLocation() {
//			}
//		};
//		user.setUserId(userId);
//		return new UserModel(user);
//	}
	
}
