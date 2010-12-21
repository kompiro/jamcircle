package org.kompiro.jamcircle.kanban.ui.internal.editpart;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.*;
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

public class BoardEditPartTest extends AbstractEditPartTest {

	private Map<Object, GraphicalEditPart> boardChildrenPartMap;

	@Before
	public void init() throws Exception {
		super.init();
		assumeThat(boardPart.getChildren().size(), is(INIT_BOARD_CHIHLDREN_SIZE));
	}

	@After
	public void finished() throws Exception {
		boardPart.deactivate();
	}

	@Test
	public void createCard() throws Exception {
		CreateRequest request = new CardCreateRequest(null, null);
		request.setType(RequestConstants.REQ_CREATE);
		request.setLocation(new Point(10, 10));
		boardPart.getCommand(request).execute();
		boardPart.refresh();
		assertEquals(INIT_BOARD_CHIHLDREN_SIZE + 1, boardPart.getChildren().size());
	}

	@Test
	public void createLane() throws Exception {
		CreateRequest request = new LaneCreateRequest(null, board.getBoard());
		request.setType(RequestConstants.REQ_CREATE);
		Point expect = new Point(100, 100);
		request.setLocation(expect);
		boardPart.getCommand(request).execute();
		boardPart.refresh();
		assertEquals(INIT_BOARD_CHIHLDREN_SIZE + 1, boardPart.getChildren().size());
		assertNotNull(board.getLane(0));
		assertEquals(100, board.getLane(0).getX());
		assertEquals(100, board.getLane(0).getY());
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
		request.setSizeDelta(new Dimension(100, 100));
		Point expect = new Point(100, 100);
		request.setMoveDelta(expect);
		boardPart.getCommand(request).execute();
		boardPart.refresh();

		assertEquals(expect.x, card.getX());
		{

		}
		assertEquals(expect.y, card.getY());
	}

	@Test
	public void moveLane() throws Exception {
		LaneMock lane = new LaneMock("test");
		board.addLane(lane);
		boardPart.refresh();
		boardChildrenPartMap = getChildlenPartmap(boardPart);
		GraphicalEditPart lanePart = boardChildrenPartMap.get(lane);
		Dimension initSize = lanePart.getFigure().getSize();

		ChangeBoundsRequest request = new ChangeBoundsRequest(RequestConstants.REQ_RESIZE_CHILDREN);
		request.setEditParts(lanePart);

		// location: 100, 100
		Point expect = new Point(100, 100);
		request.setMoveDelta(expect);
		Command command = boardPart.getCommand(request);
		assertNotNull(command);
		assertTrue(command instanceof CompoundCommand);
		assertTrue(((CompoundCommand) command).getCommands().get(0) instanceof ChangeLaneConstraintCommand);
		command.execute();
		boardPart.refresh();
		assertEquals(expect.x, lane.getX());
		assertEquals(expect.y, lane.getY());
		assertEquals(initSize.width, lane.getWidth());
		assertEquals(initSize.height, lane.getHeight());
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
		Dimension initSize = lanePart.getFigure().getSize();

		ChangeBoundsRequest request = new ChangeBoundsRequest(RequestConstants.REQ_RESIZE_CHILDREN);
		request.setEditParts(lanePart);

		// location: 100, 100
		Point expect = new Point(100, 100);
		request.setMoveDelta(expect);
		Command command = boardPart.getCommand(request);
		assertNotNull(command);
		assertTrue(command instanceof CompoundCommand);
		command.execute();

		assertEquals(expect.x, lane.getX());
		assertEquals(expect.y, lane.getY());
		assertEquals(initSize.width, lane.getWidth(), 5);
		assertEquals(initSize.height, lane.getHeight(), 5);
	}

	@Test
	public void changeSizeLane() throws Exception {
		LaneMock lane = new LaneMock("test");
		board.addLane(lane);
		boardPart.refresh();
		boardChildrenPartMap = getChildlenPartmap(boardPart);
		GraphicalEditPart lanePart = boardChildrenPartMap.get(lane);
		Dimension initSize = lanePart.getFigure().getSize();

		ChangeBoundsRequest request = new ChangeBoundsRequest(RequestConstants.REQ_RESIZE_CHILDREN);
		request.setEditParts(lanePart);
		// size: +100, +100
		Dimension sizeDelta = new Dimension(100, 100);
		request.setSizeDelta(sizeDelta);
		Command command = boardPart.getCommand(request);
		assertNotNull(command);
		assertTrue(command instanceof CompoundCommand);
		assertTrue(((CompoundCommand) command).getCommands().get(0) instanceof ChangeLaneConstraintCommand);
		command.execute();

		assertEquals(0, lane.getX());
		assertEquals(0, lane.getY());
		assertEquals(initSize.width + sizeDelta.width, lane.getWidth());
		assertEquals(initSize.height + sizeDelta.height, lane.getHeight());
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
		Dimension initSize = lanePart.getFigure().getSize();

		ChangeBoundsRequest request = new ChangeBoundsRequest(RequestConstants.REQ_RESIZE_CHILDREN);
		request.setEditParts(lanePart);
		// size: +100, +100
		Dimension sizeDelta = new Dimension(100, 100);
		request.setSizeDelta(sizeDelta);
		// location: 100, 100
		Point expect = new Point(100, 100);
		request.setMoveDelta(expect);
		Command command = boardPart.getCommand(request);
		assertNotNull(command);
		assertTrue(command instanceof CompoundCommand);
		assertTrue(((CompoundCommand) command).getCommands().get(0) instanceof ChangeLaneConstraintCommand);
		command.execute();

		assertEquals(expect.x, lane.getX());
		assertEquals(expect.y, lane.getY());
		assertEquals(initSize.width + sizeDelta.width, lane.getWidth());
		assertEquals(initSize.height + sizeDelta.height, lane.getHeight());
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
		CompoundCommand c = (CompoundCommand) command;
		assertEquals(1, c.getCommands().size());
		command.execute();
		boardPart.refresh();
		assertEquals(INIT_BOARD_CHIHLDREN_SIZE, boardPart.getChildren().size());
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
		when(iconEditPart.getFigure()).thenReturn(figure);
		when(factory.createEditPart(boardEditPart, icon)).thenReturn(iconEditPart);
		viewer.setEditPartFactory(factory);

		viewer.setContents(board);
		Object object = viewer.getRootEditPart().getChildren().get(0);
		assertThat(object, instanceOf(BoardEditPart.class));
		BoardEditPart actualBoardEditPart = (BoardEditPart) object;
		boardChildrenPartMap = getChildlenPartmap(actualBoardEditPart);
		assertThat(boardChildrenPartMap.get(icon), notNullValue());

		GroupRequest request = new GroupRequest();
		request.setEditParts(iconEditPart);
		request.setType(RequestConstants.REQ_ORPHAN_CHILDREN);
		boardPart.getCommand(request);
		verify(iconEditPart).getAdapter(DeleteCommand.class);
		boardEditPart.deactivate();
	}

	@Test
	public void addCard() throws Exception {
		CardEditPart part = createCardEditPart();

		ChangeBoundsRequest request = new ChangeBoundsRequest();
		request.setEditParts(part);
		request.setType(RequestConstants.REQ_ADD);
		boardPart.getCommand(request).execute();
		boardPart.refresh();
		assertEquals(INIT_BOARD_CHIHLDREN_SIZE + 1, boardPart.getChildren().size());
	}

	@Test
	public void addTwoCard() throws Exception {
		CardEditPart partA = createCardEditPart();
		ChangeBoundsRequest request = new ChangeBoundsRequest();
		request.setEditParts(partA);
		request.setType(RequestConstants.REQ_ADD);
		boardPart.getCommand(request).execute();
		boardPart.refresh();

		CardEditPart partB = createCardEditPart();
		request = new ChangeBoundsRequest();
		request.setEditParts(partB);
		request.setType(RequestConstants.REQ_ADD);
		boardPart.getCommand(request).execute();
		boardPart.refresh();

		assertEquals(INIT_BOARD_CHIHLDREN_SIZE + 2, boardPart.getChildren().size());

	}

	private CardEditPart createCardEditPart() {
		CardEditPart part = mock(CardEditPart.class);
		org.kompiro.jamcircle.kanban.model.Card card = mock(org.kompiro.jamcircle.kanban.model.Card.class);
		when(part.getCardModel()).thenReturn(card);

		IFigure figure = mock(IFigure.class);
		when(part.getFigure()).thenReturn(figure);

		Rectangle rect = new Rectangle();
		when(figure.getBounds()).thenReturn(rect);
		return part;
	}

}
