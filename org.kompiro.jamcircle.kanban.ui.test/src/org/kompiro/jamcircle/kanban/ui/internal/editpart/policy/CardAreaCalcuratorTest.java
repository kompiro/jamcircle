package org.kompiro.jamcircle.kanban.ui.internal.editpart.policy;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.*;
import org.eclipse.gef.commands.CompoundCommand;
import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.mock.Lane;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.KanbanUIEditPartFactory;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.LaneEditPart;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;


public class CardAreaCalcuratorTest {
	
	private Rectangle rect;
	private LaneEditPart part;
	private Map<IFigure, EditPart> visualPartMap;
	private CardAreaCalcurator calc;
	private CompoundCommand command;

	@Before
	public void before() {
		calc = new CardAreaCalcurator();
		command = new CompoundCommand();
		visualPartMap = new HashMap<IFigure, EditPart>();
		BoardModel boardModel = mock(BoardModel.class);
		part = new LaneEditPart(boardModel);
		RootEditPart parent = mock(RootEditPart.class);
		when(parent.getRoot()).thenReturn(parent);
		EditPartViewer viewer = mock(EditPartViewer.class);
		when(parent.getViewer()).thenReturn(viewer );
		when(viewer.getEditPartFactory()).thenReturn(new KanbanUIEditPartFactory(boardModel));
		part.setParent(parent);
		Object lane = new Lane();
		part.setModel(lane);
		part.getFigure();
		rect = new Rectangle();
		rect.setSize(200, 200);
	}

	@Test
	public void calc_no_change() throws Exception {
		calc.calc(part, rect , visualPartMap , command);
		assertThat(command.size(),is(0));
	}
	
	@Test
	public void calcHasOneCard() throws Exception {
		org.kompiro.jamcircle.kanban.model.Lane lane = part.getLaneModel();
		Card card = mock(Card.class);
		lane.addCard(card);
		part.refresh();
		assertThat(part.getChildren().size(),is(1));
		calc.calc(part, rect , visualPartMap , command);
		assertThat(command.size(),is(0));
		
	}

	@Test
	public void calcHasSomeCards() throws Exception {
		org.kompiro.jamcircle.kanban.model.Lane lane = part.getLaneModel();
		Card card = mock(Card.class);
		lane.addCard(card);
		lane.addCard(card);
		lane.addCard(card);
		part.refresh();
		assertThat(part.getChildren().size(),is(3));
		calc.calc(part, rect , visualPartMap , command);
		assertThat(command.size(),is(0));
		
	}


}
