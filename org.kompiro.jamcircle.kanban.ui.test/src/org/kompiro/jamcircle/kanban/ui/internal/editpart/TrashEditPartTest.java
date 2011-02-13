package org.kompiro.jamcircle.kanban.ui.internal.editpart;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNotNull;
import static org.junit.Assume.assumeThat;
import static org.junit.Assume.assumeTrue;

import java.util.Map;

import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.GroupRequest;
import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.command.*;
import org.kompiro.jamcircle.kanban.model.mock.Card;
import org.kompiro.jamcircle.kanban.ui.model.TrashModel;

public class TrashEditPartTest extends AbstractEditPartTest{
	
	private static final int INIT_CARD_SIZE = 1;
	private static final int INIT_LANE_SIZE = 3;
	private LaneEditPart todoLanePart;
	private GraphicalEditPart doingLanePart;
	private LaneMock todo;
	private LaneMock doing;
	private LaneMock done;
	private GraphicalEditPart trashPart;
	private CardEditPart cardPartOnLane;
	private CardEditPart cardPartOnBoard;

	private void addCardToBoard() {
		assertEquals(INIT_BOARD_CHIHLDREN_SIZE + INIT_LANE_SIZE + INIT_CARD_SIZE,boardPart.getChildren().size());
		cardPartOnBoard = (CardEditPart) boardPart.getChildren().get(INIT_BOARD_CHIHLDREN_SIZE + INIT_LANE_SIZE + INIT_CARD_SIZE - 1);
		assertNotNull(cardPartOnLane.getParent());
	}

	private void addCardToTodoLane() {
		assertTrue(todoLanePart.getChildren().get(0) instanceof CardEditPart);
		cardPartOnLane = (CardEditPart) todoLanePart.getChildren().get(0);
		assertNotNull(cardPartOnLane.getParent());
	}

	@Test
	public void deleteCardFromBoard() throws Exception {
		assertEquals(4 + INIT_BOARD_CHIHLDREN_SIZE,boardPart.getChildren().size());
		
		GroupRequest request = new GroupRequest();
		request.setEditParts(cardPartOnBoard);
		request.setType(RequestConstants.REQ_ORPHAN_CHILDREN);
		CompoundCommand command = (CompoundCommand) boardPart.getCommand(request);
		assertEquals(1,command.getCommands().size());
		assertTrue(command.getCommands().get(0) instanceof RemoveCardCommand);
		command.execute();

		request = new ChangeBoundsRequest();
		request.setEditParts(cardPartOnLane);
		request.setType(RequestConstants.REQ_ADD);
		trashPart.getCommand(request).execute();
		boardPart.refresh();
		assertEquals(3 + INIT_BOARD_CHIHLDREN_SIZE,boardPart.getChildren().size());
		assertEquals(1,board.getTrashModel().getCards().length);
	}

	
	@Test
	public void deleteCardFromLane() throws Exception {
		GroupRequest request = new GroupRequest();
		request.setEditParts(cardPartOnLane);
		request.setType(RequestConstants.REQ_ORPHAN_CHILDREN);
		Command command = todoLanePart.getCommand(request);
		assertNotNull(command);
		command.execute();

		request = new ChangeBoundsRequest();
		request.setEditParts(cardPartOnLane);
		request.setType(RequestConstants.REQ_ADD);
		Command trashCommand = trashPart.getCommand(request);
		trashCommand.execute();
		todoLanePart.refresh();
		assertEquals(0,todoLanePart.getChildren().size());
		TrashModel trashModel = board.getTrashModel();
		assertEquals(1,trashModel.getCards().length);
	}

	@Test
	public void deleteLaneFromBoard() throws Exception {
		assertEquals(4 + INIT_BOARD_CHIHLDREN_SIZE,boardPart.getChildren().size());
		
		GroupRequest request = new GroupRequest();
		request.setEditParts(doingLanePart);
		request.setType(RequestConstants.REQ_ORPHAN_CHILDREN);
		CompoundCommand command = (CompoundCommand) boardPart.getCommand(request);
		Object current = command.getCommands().get(0);
		assertTrue("expect 'RemoveLaneCommand',but '" + current.getClass().getName() + "'",current instanceof RemoveLaneCommand);
		command.execute();

		request = new ChangeBoundsRequest();
		request.setEditParts(doingLanePart);
		request.setType(RequestConstants.REQ_ADD);
		command = (CompoundCommand) trashPart.getCommand(request);
		current = command.getCommands().get(0);
		assertTrue("expect 'AddLaneTrashCommand',but '" + current.getClass().getName() + "'",current instanceof AddLaneTrashCommand);
		command.execute();
		boardPart.refresh();
		assertEquals(3 + INIT_BOARD_CHIHLDREN_SIZE,boardPart.getChildren().size());
		assertEquals(0,board.getTrashModel().getCards().length);
	}

	@Before
	public void init() throws Exception {
		super.init();

		todo = new LaneMock("Todo");
		Card card = new Card();
		card.setSubject("card on lane.");
		todo.addCard(card);
		doing = new LaneMock("Doing");
		done = new LaneMock("DONE");

		board.addLane(todo);
		card = new Card();
		card.setSubject("card on board.");
		board.addCard(card);
		board.addLane(doing);
		board.addLane(done);
		boardPart.refresh();
		assumeThat(board.getChildren().size(),is(INIT_BOARD_CHIHLDREN_SIZE + INIT_LANE_SIZE +INIT_CARD_SIZE));
		assumeTrue(boardPart.isActive());
		
		Map<Object, GraphicalEditPart> partMap = getChildlenPartmap(boardPart);
		trashPart = partMap.get(trashMock);
		assumeNotNull(trashPart);
		
		GraphicalEditPart gPart = partMap.get(todo);
		assumeTrue(gPart instanceof LaneEditPart);
		todoLanePart = (LaneEditPart) gPart;
		assumeNotNull(todoLanePart);

		doingLanePart = partMap.get(doing);
		assumeNotNull(doingLanePart);
		assumeThat(doingLanePart.getChildren().size(),is(0));

		addCardToTodoLane();
		addCardToBoard();
		
	}

	
}
