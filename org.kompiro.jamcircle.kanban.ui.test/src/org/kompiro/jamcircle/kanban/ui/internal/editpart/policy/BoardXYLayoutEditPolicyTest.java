package org.kompiro.jamcircle.kanban.ui.internal.editpart.policy;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.CreationFactory;
import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.model.mock.Card;
import org.kompiro.jamcircle.kanban.ui.command.MoveCommand;
import org.kompiro.jamcircle.kanban.ui.internal.command.AddCardToOnBoardContainerCommand;
import org.kompiro.jamcircle.kanban.ui.internal.command.CreateCardCommand;
import org.kompiro.jamcircle.kanban.ui.internal.command.CreateLaneCommand;
import org.kompiro.jamcircle.kanban.ui.internal.command.RemoveCardCommand;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.BoardEditPart;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.CardEditPart;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.LaneEditPart;
import org.kompiro.jamcircle.kanban.ui.internal.figure.LaneFigureLayer;
import org.kompiro.jamcircle.kanban.ui.internal.figure.LaneFigure.CardArea;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;


public class BoardXYLayoutEditPolicyTest {
	
	private BoardXYLayoutEditPolicy policy;
	private BoardModel boardModel;
	
	@Before
	public void before() throws Exception {
		Board board = new org.kompiro.jamcircle.kanban.model.mock.Board();
		boardModel = new BoardModel(board );
		BoardEditPart part = new BoardEditPart(boardModel);
		EditPart parent = mock(EditPart.class);
		RootEditPart root = mock(RootEditPart.class);
		EditPartViewer viewer = mock(EditPartViewer.class);
		when(root.getViewer()).thenReturn(viewer);
		when(parent.getRoot()).thenReturn(root);
		part.setParent(parent );
		policy = new BoardXYLayoutEditPolicy(part);
		policy.setHost(part);
	}

	@Test
	public void getAddCardCommand() throws Exception {
		CardEditPart cardPart = createCardEditPartMock();
		Command command = policy.getCommand(createRequest(RequestConstants.REQ_ADD,cardPart));
		assertThat(command,instanceOf(CompoundCommand.class));
		CompoundCommand c = (CompoundCommand) command;
		assertThat(c.size(),is(1));
		command = (Command)c.getCommands().get(0);
		assertThat(command,instanceOf(AddCardToOnBoardContainerCommand.class));
		BoardLocalLayout boardLocalLayout = mock(BoardLocalLayout.class);
		policy.setBoardLocalLayout(boardLocalLayout );
	}

	@Test
	public void getAddCardCommandNeedToMove() throws Exception {
		CardEditPart cardPart = createCardEditPart();
		BoardLocalLayout boardLocalLayout = mock(BoardLocalLayout.class);
		policy.setBoardLocalLayout(boardLocalLayout );
		ChangeBoundsRequest request = createMoveCardRequest(cardPart);
		Command command = policy.getCommand(request);
		
		assertThat(command,instanceOf(CompoundCommand.class));
		CompoundCommand c = (CompoundCommand) command;
		assertThat(c.size(),is(1));
		command = (Command)c.getCommands().get(0);
		assertThat(command,instanceOf(AddCardToOnBoardContainerCommand.class));
		assertThat(command.canExecute(),is(true));
		command.execute();
	}

	private ChangeBoundsRequest createMoveCardRequest(CardEditPart cardPart) {
		ChangeBoundsRequest request = createRequest(RequestConstants.REQ_ADD,cardPart);
		request.setMoveDelta(new Point(1000,1000));
		return request;
	}


	@Test
	public void getCreateCardCommand() throws Exception {
		Command command = policy.getCommand(createCreateRequest(RequestConstants.REQ_CREATE,createCardEditPartMock()));
		assertThat(command,instanceOf(CreateCardCommand.class));
	}

	@Test
	public void getCreateLaneCommand() throws Exception {
		Command command = policy.getCommand(createCreateRequest(RequestConstants.REQ_CREATE,createLaneEditPart()));
		assertThat(command,instanceOf(CreateLaneCommand.class));
	}

	
	@Test
	public void getDeleteDependantCommand() throws Exception {
		Command command = policy.getCommand(createRequest(RequestConstants.REQ_DELETE_DEPENDANT));
		assertThat(command,is(nullValue()));
	}
	
	@Test
	public void getOrphanChildrenCommand() throws Exception {
		Command command = policy.getCommand(createRequest(RequestConstants.REQ_ORPHAN_CHILDREN));
		assertThat(command,is(notNullValue()));
		assertThat(command,instanceOf(CompoundCommand.class));
		CompoundCommand c = (CompoundCommand) command;
		assertThat(c.size(),is(1));
		command = (Command)c.getCommands().get(0);
		assertThat(command,instanceOf(RemoveCardCommand.class));
	}
	
	@Test
	public void getMoveCardCommand() throws Exception {
		Command command = policy.getCommand(createRequest(RequestConstants.REQ_RESIZE_CHILDREN));
		assertThat(command,is(notNullValue()));
		assertThat(command,instanceOf(MoveCommand.class));
	}

	@Test
	public void getMoveLaneCommand() throws Exception {
		CardAreaCalcurator calc = mock(CardAreaCalcurator.class);
		policy.setLaneLocalLayout(calc);
		Command command = policy.getCommand(createRequest(RequestConstants.REQ_RESIZE_CHILDREN,createLaneEditPart()));
		assertThat(command,is(notNullValue()));
		assertThat(command,instanceOf(CompoundCommand.class));
		CompoundCommand c = (CompoundCommand) command;
		assertThat(c.size(),is(1));
		command = (Command)c.getCommands().get(0);
		assertThat(command,instanceOf(MoveCommand.class));
		verify(calc).calc((LaneEditPart)any(),(Rectangle)any(), (Map<?, ?>)any(), (CompoundCommand)any());
	}
	
	private Request createRequest(String key) {
		CardEditPart part = createCardEditPartMock();
		return createRequest(key,part);
	}
	
	private ChangeBoundsRequest createRequest(String key,EditPart part){
		ChangeBoundsRequest request = new ChangeBoundsRequest(key);
		request.setEditParts(part);
		return request;		
	}
	
	private Request createCreateRequest(String key,
			EditPart createLaneEditPart) {
		CreateRequest request = new CreateRequest(key);
		CreationFactory factory = mock(CreationFactory.class);
		Object model = createLaneEditPart.getModel();
		when(factory.getNewObject()).thenReturn(model);
		request.setFactory(factory );
		return request;
	}


	private CardEditPart createCardEditPartMock() {
		CardEditPart part = mock(CardEditPart.class);
		Card card = new Card();
		when(part.getCardModel()).thenReturn(card);
		when(part.getModel()).thenReturn(card);
		Object command = mock(MoveCommand.class);
		when(part.getAdapter(eq(MoveCommand.class))).thenReturn(command );

		IFigure figure = mock(IFigure.class);
		when(part.getFigure()).thenReturn(figure);
		when(figure.getParent()).thenReturn(figure);
		
		LayoutManager manager = mock(LayoutManager.class);
		when(figure.getLayoutManager()).thenReturn(manager);
		
		Rectangle rect = new Rectangle();
		when(figure.getBounds()).thenReturn(rect);
		when(manager.getConstraint(eq(figure))).thenReturn(rect);
		return part;
	}
	
	private CardEditPart createCardEditPart(){
		CardEditPart part = new CardEditPart(boardModel);
		Card card = new Card();
		part.setModel(card);
		return part;
	}
	
	private LaneEditPart createLaneEditPart() {
		LaneEditPart part = mock(LaneEditPart.class);
		Lane lane = mock(Lane.class);
		when(part.getLaneModel()).thenReturn(lane);
		when(part.getModel()).thenReturn(lane);
		when(lane.isIconized()).thenReturn(false);
		
		GraphicalViewer viewer = mock(GraphicalViewer.class);
		when(part.getViewer()).thenReturn(viewer);
		Map<?,?> partMap = new HashMap<IFigure, EditPart>();
		when(viewer.getVisualPartMap()).thenReturn(partMap );
		
		LaneFigureLayer laneFigure = mock(LaneFigureLayer.class);
		when(part.getLaneFigure()).thenReturn(laneFigure);
		CardArea cardArea = mock(CardArea.class);
		when(laneFigure.getCardArea()).thenReturn(cardArea );
		
		Object command = mock(MoveCommand.class);
		when(part.getAdapter(eq(MoveCommand.class))).thenReturn(command );

		IFigure figure = mock(IFigure.class);
		when(part.getFigure()).thenReturn(figure);
		when(figure.getParent()).thenReturn(figure);
		
		LayoutManager manager = mock(LayoutManager.class);
		when(figure.getLayoutManager()).thenReturn(manager);
		
		Rectangle rect = new Rectangle();
		when(figure.getBounds()).thenReturn(rect);
		when(manager.getConstraint(eq(figure))).thenReturn(rect);
		return part;
	}


}
