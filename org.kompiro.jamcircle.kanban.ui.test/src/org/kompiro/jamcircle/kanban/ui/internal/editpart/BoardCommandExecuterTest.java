package org.kompiro.jamcircle.kanban.ui.internal.editpart;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import org.eclipse.gef.*;
import org.eclipse.gef.commands.*;
import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.*;
import org.kompiro.jamcircle.kanban.ui.internal.command.*;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;
import org.mockito.*;


public class BoardCommandExecuterTest {

	private BoardEditPart part;
	private Board board;
	@Mock private CommandStack stack;
	@Mock private EditPart parent;
	@Mock private RootEditPart root;
	private BoardCommandExecuter executer;
	
	
	@Before
	public void before() throws Exception {
		MockitoAnnotations.initMocks(this);
		board = new org.kompiro.jamcircle.kanban.model.mock.Board();
		when(parent.getRoot()).thenReturn(root);
		EditPartViewer viewer = mock(EditPartViewer.class);
		EditDomain domain = mock(EditDomain.class);
		when(viewer.getEditDomain()).thenReturn(domain);
		when(domain.getCommandStack()).thenReturn(stack);
		when(root.getViewer()).thenReturn(viewer );
		BoardModel boardModel = new BoardModel(board);
		EditPartFactory factory = new KanbanUIEditPartFactory(boardModel);
		when(viewer.getEditPartFactory()).thenReturn(factory);
		part = new BoardEditPart(boardModel);
		part.setParent(parent);
		part.createEditPolicies();
		executer = new BoardCommandExecuter(part);
	}

	@Test
	public void addCard() throws Exception {
		Card card = mock(Card.class);
		ArgumentCaptor<Command> argument = ArgumentCaptor.forClass(Command.class);
		executer.addCard(card);
		verify(stack).execute(argument.capture());
		assertThat(argument.getValue(),is(instanceOf(CreateCardCommand.class)));
	}
	
	@Test
	public void addLane() throws Exception {
		Lane lane = mock(Lane.class);
		ArgumentCaptor<Command> argument = ArgumentCaptor.forClass(Command.class);
		executer.addLane(lane);
		verify(stack).execute(argument.capture());
		assertThat(argument.getValue(),is(instanceOf(CreateLaneCommand.class)));
	}
	
	@Test
	public void removeCard() throws Exception {
		Card card = mock(Card.class);
		board.addCard(card);
		part.refresh();
		ArgumentCaptor<Command> argument = ArgumentCaptor.forClass(Command.class);
		executer.removeCard(card);
		
		verify(stack).execute(argument.capture());
		Command value = argument.getValue();
		assertThat(value,is(instanceOf(CompoundCommand.class)));
		CompoundCommand cc = (CompoundCommand) value;
		assertThat(cc.size(),is(1));
		assertThat(cc.getChildren()[0],is(instanceOf(RemoveCardCommand.class)));
	}
	
	@Test
	public void removeLane() throws Exception {
		Lane lane = mock(Lane.class,Mockito.RETURNS_MOCKS);
		board.addLane(lane);
		part.refresh();
		ArgumentCaptor<Command> argument = ArgumentCaptor.forClass(Command.class);
		executer.removeLane(lane);
		
		verify(stack).execute(argument.capture());
		Command value = argument.getValue();
		assertThat(value,is(instanceOf(CompoundCommand.class)));
		CompoundCommand cc = (CompoundCommand) value;
		assertThat(cc.size(),is(1));
		assertThat(cc.getChildren()[0],is(instanceOf(RemoveLaneCommand.class)));
	}

}
