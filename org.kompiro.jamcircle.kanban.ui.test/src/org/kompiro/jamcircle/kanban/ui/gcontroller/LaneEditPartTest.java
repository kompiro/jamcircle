package org.kompiro.jamcircle.kanban.ui.gcontroller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.GroupRequest;
import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.mock.Card;

public class LaneEditPartTest extends AbstractControllerTest{

	private GraphicalEditPart todoLanePart;
	private GraphicalEditPart doingLanePart;
	private Map<Object, GraphicalEditPart> cardPartMap;
	private LaneMock todo;
	private LaneMock doing;
	private LaneMock done;

	@Before
	public void init() throws Exception {
		super.init();

		todo = new LaneMock("Todo");
		doing = new LaneMock("Doing");
		done = new LaneMock("DONE");

		board.addLane(todo);
		board.addLane(doing);
		board.addLane(done);
		boardPart.refresh();
		
		assertEquals(3 + INIT_BOARD_CHIHLDREN_SIZE,boardPart.getChildren().size());
		Map<Object, GraphicalEditPart> partMap = getChildlenPartmap(boardPart);

		todoLanePart = partMap.get(todo);
		assertNotNull(todoLanePart);

		doingLanePart = partMap.get(doing);
		assertNotNull(doingLanePart);
		assertEquals(0,doingLanePart.getChildren().size());
		
	}

	@Test
	public void addCardToLane() throws Exception {
		Card card = new Card();
		CardEditPart part = new CardEditPart(board);
		part.setModel(card);
		ChangeBoundsRequest request = new ChangeBoundsRequest();
		request.setEditParts(part);
		request.setType(RequestConstants.REQ_ADD);
		Command command = todoLanePart.getCommand(request);
		assertTrue(command instanceof CompoundCommand);
		command.execute();
		todoLanePart.refresh();
		assertEquals(1,todoLanePart.getChildren().size());
	}

	@Test
	public void moveCardInLane() throws Exception {
		Card card = new Card();
		todo.addCard(card);
		todoLanePart.refresh();
		cardPartMap = getChildlenPartmap(todoLanePart);
		GraphicalEditPart cardPart = cardPartMap.get(card);

		ChangeBoundsRequest request = new ChangeBoundsRequest();
		request.setEditParts(cardPart);
//		request.setSizeDelta(new Dimension(0,0));
		Point expect = new Point(100,100);
		request.setMoveDelta(expect);
		request.setType(RequestConstants.REQ_RESIZE_CHILDREN);
		todoLanePart.getCommand(request).execute();
		todoLanePart.refresh();
		assertEquals(expect.x,card.getX());
		assertEquals(expect.y,card.getY());
	}

	
	@Test
	public void removeCardToLane() throws Exception {
		Card card = new Card();
		todo.addCard(card);
		todoLanePart.refresh();
		cardPartMap = getChildlenPartmap(todoLanePart);
		GraphicalEditPart cardPart = cardPartMap.get(card);
		assertNotNull(cardPart);
		
		GroupRequest request = new GroupRequest();
		request.setEditParts(cardPart);
		request.setType(RequestConstants.REQ_ORPHAN_CHILDREN);
		Command command = todoLanePart.getCommand(request);
		assertTrue(command instanceof CompoundCommand);
		CompoundCommand c = (CompoundCommand)command;
		assertEquals(1,c.getCommands().size());
		command.execute();
		todoLanePart.refresh();
		assertEquals(0,todoLanePart.getChildren().size());
	}
	
}
