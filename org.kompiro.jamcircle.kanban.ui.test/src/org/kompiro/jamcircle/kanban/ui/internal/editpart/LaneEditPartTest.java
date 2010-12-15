package org.kompiro.jamcircle.kanban.ui.internal.editpart;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.beans.PropertyChangeEvent;
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
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.model.mock.Card;
import org.kompiro.jamcircle.kanban.ui.editpart.AbstractEditPart.DelegatorRunner;
import org.kompiro.jamcircle.kanban.ui.editpart.IPropertyChangeDelegator;
import org.kompiro.jamcircle.scripting.ScriptTypes;
import org.mockito.ArgumentCaptor;

public class LaneEditPartTest extends AbstractEditPartTest {

	private LaneEditPart todoLanePart;
	private LaneEditPart doingLanePart;
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

		assertEquals(3 + INIT_BOARD_CHIHLDREN_SIZE, boardPart.getChildren().size());
		Map<Object, GraphicalEditPart> partMap = getChildlenPartmap(boardPart);

		todoLanePart = (LaneEditPart) partMap.get(todo);
		assertNotNull(todoLanePart);

		doingLanePart = (LaneEditPart) partMap.get(doing);
		assertNotNull(doingLanePart);
		assertEquals(0, doingLanePart.getChildren().size());

	}

	@Test
	public void add_card_to_lane() throws Exception {
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
		assertEquals(1, todoLanePart.getChildren().size());
	}

	@Test
	public void move_card_in_lane() throws Exception {
		Card card = new Card();
		todo.addCard(card);
		todoLanePart.refresh();
		cardPartMap = getChildlenPartmap(todoLanePart);
		GraphicalEditPart cardPart = cardPartMap.get(card);

		ChangeBoundsRequest request = new ChangeBoundsRequest();
		request.setEditParts(cardPart);
		// request.setSizeDelta(new Dimension(0,0));
		Point expect = new Point(100, 100);
		request.setMoveDelta(expect);
		request.setType(RequestConstants.REQ_RESIZE_CHILDREN);
		todoLanePart.getCommand(request).execute();
		todoLanePart.refresh();
		// moved internal
		assertThat(card.getX(), is(not(expect.x)));
		assertThat(card.getY(), is(not(expect.y)));
	}

	@Test
	public void remove_card_to_lane() throws Exception {
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
		CompoundCommand c = (CompoundCommand) command;
		assertEquals(1, c.getCommands().size());
		command.execute();
		todoLanePart.refresh();
		assertEquals(0, todoLanePart.getChildren().size());
	}

	@Test
	public void call_property_change_when_set_script() throws Exception {
		IPropertyChangeDelegator delegator = mock(IPropertyChangeDelegator.class);
		todoLanePart.setDelegator(delegator);
		todo.setScript("p 'test'");
		todo.setScriptType(ScriptTypes.JRuby);
		todo.save();

		ArgumentCaptor<DelegatorRunner> captor = ArgumentCaptor.forClass(DelegatorRunner.class);
		verify(delegator).run(captor.capture());
		DelegatorRunner value = captor.getValue();
		PropertyChangeEvent evt = value.getEvt();
		assertThat(evt.getPropertyName(), is(Lane.PROP_SCRIPT));
	}

	@Test
	public void when_call_do_prperty_change() throws Exception {
		todoLanePart.doPropertyChange(new PropertyChangeEvent(todo, Lane.PROP_SCRIPT, null, "p 'test'"));

	}

}
