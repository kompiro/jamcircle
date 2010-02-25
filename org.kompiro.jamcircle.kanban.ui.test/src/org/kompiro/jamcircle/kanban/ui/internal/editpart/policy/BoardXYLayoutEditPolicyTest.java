package org.kompiro.jamcircle.kanban.ui.internal.editpart.policy;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.*;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.requests.*;
import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.model.mock.Card;
import org.kompiro.jamcircle.kanban.ui.command.MoveCommand;
import org.kompiro.jamcircle.kanban.ui.internal.command.*;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.*;
import org.kompiro.jamcircle.kanban.ui.internal.figure.LaneFigure;
import org.kompiro.jamcircle.kanban.ui.internal.figure.LaneFigure.CardArea;


public class BoardXYLayoutEditPolicyTest {
	
	private BoardXYLayoutEditPolicy policy;
	
	@Before
	public void before() throws Exception {
		BoardEditPart part = new BoardEditPart(null);
		policy = new BoardXYLayoutEditPolicy(part);
		policy.setHost(part);
	}

	@Test
	public void getAddCardCommand() throws Exception {
		Command command = policy.getCommand(createRequest(RequestConstants.REQ_ADD,createCardEditPart()));
		assertThat(command,instanceOf(CompoundCommand.class));
		CompoundCommand c = (CompoundCommand) command;
		assertThat(c.size(),is(1));
		command = (Command)c.getCommands().get(0);
		assertThat(command,instanceOf(AddCardToOnBoardContainerCommand.class));
	}

	@Test
	public void getCreateCardCommand() throws Exception {
		Command command = policy.getCommand(createCreateRequest(RequestConstants.REQ_CREATE,createCardEditPart()));
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
		policy.setCalc(calc);
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
		CardEditPart part = createCardEditPart();
		return createRequest(key,part);
	}
	
	private Request createRequest(String key,EditPart part){
		GroupRequest request = new ChangeBoundsRequest(key);
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


	private CardEditPart createCardEditPart() {
		CardEditPart part = mock(CardEditPart.class);
		Card card = mock(Card.class);
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
		
		LaneFigure laneFigure = mock(LaneFigure.class);
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
