package org.kompiro.jamcircle.kanban.ui.internal.editpart.policy;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.*;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.*;
import org.eclipse.gef.commands.CompoundCommand;
import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.mock.Card;
import org.kompiro.jamcircle.kanban.model.mock.Lane;
import org.kompiro.jamcircle.kanban.ui.internal.command.MoveCardCommand;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.*;
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
		Lane lane = new Lane();
		part.setModel(lane);
		part.getFigure();
		rect = new Rectangle();
		rect.setSize(200, 200);
		part.addNotify();
	}

	@Test
	public void calcNoChange() throws Exception {
		calc.calc(part, rect , visualPartMap , command);
		assertThat(command.size(),is(0));
	}
	
	@Test
	public void calcHasOneCard() throws Exception {
		org.kompiro.jamcircle.kanban.model.Lane lane = part.getLaneModel();
		Card card = new Card();
		lane.addCard(card);
		part.refresh();
		List<?> children = part.getChildren();
		assertThat(children.size(),is(1));
		for (Object object : children) {
			assertThat(object,instanceOf(CardEditPart.class));
			CardEditPart cardPart = (CardEditPart)object;
			visualPartMap.put(cardPart.getFigure(), cardPart);
		}
		calc.calc(part, rect , visualPartMap , command);
		assertThat(command.size(),is(0));
	}

	@Test
	public void calcHasOneCardNeedMove() throws Exception {
		org.kompiro.jamcircle.kanban.model.Lane lane = part.getLaneModel();
		Card card = new Card();
		card.setX(1000);
		card.setY(1000);
		lane.addCard(card);
		part.refresh();
		List<?> children = part.getChildren();
		assertThat(children.size(),is(1));
		for (Object object : children) {
			assertThat(object,instanceOf(CardEditPart.class));
			CardEditPart cardPart = (CardEditPart)object;
			visualPartMap.put(cardPart.getFigure(), cardPart);
		}
		calc.calc(part, rect , visualPartMap , command);
		assertThat(command.size(),is(1));
		assertThat(command.getChildren()[0],instanceOf(MoveCardCommand.class));
		MoveCardCommand c = (MoveCardCommand) command.getChildren()[0];
		c.execute();
		assertThat(card.getX(),is(not(1000)));
		assertThat(card.getY(),is(not(1000)));
	}

	
	@Test
	public void calcHasSomeCards() throws Exception {
		org.kompiro.jamcircle.kanban.model.Lane lane = part.getLaneModel();
		Card card = new Card();
		lane.addCard(card);
		lane.addCard(card);
		lane.addCard(card);
		part.refresh();
		List<?> children = part.getChildren();
		assertThat(children.size(),is(3));
		for (Object object : children) {
			assertThat(object,instanceOf(CardEditPart.class));
			CardEditPart cardPart = (CardEditPart)object;
			visualPartMap.put(cardPart.getFigure(), cardPart);
		}
		calc.calc(part, rect , visualPartMap , command);
		assertThat(command.size(),is(0));
	}

	@Test
	public void calcHasSomeCardsNeedMove() throws Exception {
		org.kompiro.jamcircle.kanban.model.Lane lane = part.getLaneModel();
		Card card = new Card();
		card.setX(1000);
		card.setY(1000);
		lane.addCard(card);
		lane.addCard(card);
		card = new Card();
		lane.addCard(card);
		part.refresh();
		List<?> children = part.getChildren();
		assertThat(children.size(),is(3));
		for (Object object : children) {
			assertThat(object,instanceOf(CardEditPart.class));
			CardEditPart cardPart = (CardEditPart)object;
			visualPartMap.put(cardPart.getFigure(), cardPart);
		}
		calc.calc(part, rect , visualPartMap , command);
		assertThat(command.size(),is(2));
		assertThat(command.getChildren()[0],instanceOf(MoveCardCommand.class));
	}

}
