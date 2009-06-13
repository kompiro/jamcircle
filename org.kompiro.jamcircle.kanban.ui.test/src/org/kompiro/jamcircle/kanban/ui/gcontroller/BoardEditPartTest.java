package org.kompiro.jamcircle.kanban.ui.gcontroller;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Map;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.requests.*;
import org.junit.*;
import org.kompiro.jamcircle.kanban.model.User;
import org.kompiro.jamcircle.kanban.model.mock.Card;
import org.kompiro.jamcircle.kanban.ui.command.ChangeLaneConstraintCommand;
import org.kompiro.jamcircle.kanban.ui.model.UserModel;

public class BoardEditPartTest extends AbstractControllerTest{

	private Map<Object, GraphicalEditPart> boardChildrenPartMap;
	
	@Before
	public void init() throws Exception {
		super.init();
//		assertFalse(boardPart.isActive());
//		boardPart.activate();
		assertEquals(INIT_BOARD_CHIHLDREN_SIZE,boardPart.getChildren().size());
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
		
		assertEquals(expect.x,card.getX());
		assertEquals(expect.y,card.getY());
	}

	@Test
	public void changeLaneConstraint() throws Exception {
		LaneMock lane = new LaneMock("test");
		board.addLane(lane);
		boardPart.refresh();
		boardChildrenPartMap = getChildlenPartmap(boardPart);
		GraphicalEditPart lanePart = boardChildrenPartMap.get(lane);
		assertNotNull(lanePart);
		
		ChangeBoundsRequest request = new ChangeBoundsRequest(RequestConstants.REQ_RESIZE_CHILDREN);
		request.setEditParts(lanePart);
		request.setSizeDelta(new Dimension(100,100));
		Point expect = new Point(100,100);
		request.setMoveDelta(expect);
		Command command = boardPart.getCommand(request);
		assertNotNull(command);
		assertTrue(command instanceof CompoundCommand);
		command = (Command)((CompoundCommand) command).getCommands().get(0);
		assertTrue(command instanceof ChangeLaneConstraintCommand);
		command.execute();
		boardPart.refresh();
		assertEquals(expect.x,lane.getX());
		assertEquals(expect.y,lane.getY());
		assertEquals(300,lane.getWidth());
		assertEquals(300,lane.getHeight());		
	}
	
	@Test
	public void removeCardToLane() throws Exception {
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

	@Test
	public void addUser() throws Exception {
		UserModel model1 = createUserModel("kompiro@localhost");
		board.addUser(model1);
//		while(jobSemaphore){}
//		job.join();
		assertEquals(1 + INIT_BOARD_CHIHLDREN_SIZE,boardPart.getChildren().size());
//		assertEquals(new Point(0,74),model1.getLocation());
	}
	
	@Test
	public void addUserTwice() throws Exception {
		UserModel model1 = createUserModel("kompiro@localhost");
		UserModel model2 = createUserModel("kompiro2@localhost");
		board.addUser(model1);
		board.addUser(model2);
		assertEquals(2 + INIT_BOARD_CHIHLDREN_SIZE,boardPart.getChildren().size());
//		while(jobSemaphore){}
//		job.join();
//		assertEquals(new Point(0,74),model1.getLocation());
//		assertEquals(new Point(0,148),model2.getLocation());
	}
	
	@Test
	public void addManyUsers() throws Exception {
		UserModel model1 = createUserModel("kompiro@localhost");
		UserModel model2 = createUserModel("kompiro2@localhost");
		ArrayList<UserModel> users = new ArrayList<UserModel>();
		users.add(model1);
		users.add(model2);
		board.addAllUsers(users);
		assertEquals(2 + INIT_BOARD_CHIHLDREN_SIZE,boardPart.getChildren().size());
//		while(jobSemaphore){}
//		job.join();
//		assertEquals(new Point(0,74),model1.getLocation());
//		assertEquals(new Point(0,148),model2.getLocation());
	}
	
	@Test
	public void cleareUsers() throws Exception {
		UserModel model1 = createUserModel("kompiro@localhost");
		UserModel model2 = createUserModel("kompiro2@localhost");
		ArrayList<UserModel> users = new ArrayList<UserModel>();
		users.add(model1);
		users.add(model2);
		board.addAllUsers(users);
		board.clearUsers();
		assertEquals(INIT_BOARD_CHIHLDREN_SIZE,boardPart.getChildren().size());
	}
	
	private UserModel createUserModel(String userId){
		User user = new org.kompiro.jamcircle.kanban.model.mock.User(){
			@Override
			public void commitLocation() {
			}
		};
		user.setUserId(userId);
		return new UserModel(user);
	}
	
	
}
