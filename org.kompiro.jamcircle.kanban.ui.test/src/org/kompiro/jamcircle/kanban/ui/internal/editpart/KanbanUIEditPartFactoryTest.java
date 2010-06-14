package org.kompiro.jamcircle.kanban.ui.internal.editpart;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.eclipse.gef.EditPart;
import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;

public class KanbanUIEditPartFactoryTest {

	private KanbanUIEditPartFactory factory;

	@Before
	public void before() throws Exception{
		createFactory();
	}
	
	@Test
	public void createCardEditPart() throws Exception {
		Card card = mock(Card.class);
		EditPart part = factory.createEditPart(null, card);
		assertThat(part, not(nullValue()));
		assertThat(part, is(instanceOf(CardEditPart.class)));
	}

//	@Test
//	public void createUserEditPart() throws Exception {
//		User user = mock(User.class);
//		EditPart part = factory.createEditPart(null, user);		
//		assertThat(part, not(nullValue()));
//		assertThat(part, is(instanceOf(EditPart.class)));
//	}
	
	private void createFactory() {
		BoardModel board = mock(BoardModel.class);
		factory = new KanbanUIEditPartFactory();
		factory.setBoardModel(board);
	}

}
