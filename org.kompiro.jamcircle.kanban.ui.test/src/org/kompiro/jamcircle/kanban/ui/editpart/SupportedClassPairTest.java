package org.kompiro.jamcircle.kanban.ui.editpart;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.eclipse.gef.EditPart;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.User;


public class SupportedClassPairTest {

	@Test
	public void isSupportFalse() throws Exception {
		SupportedClassPair pair = new SupportedClassPair(IBoardEditPart.class, User.class);
		EditPart context = mock(IBoardEditPart.class);
		Object model = mock(Card.class);
		assertThat(pair.isSupported(context , model),is(false));
	}

	@Test
	public void isSupportTrue() throws Exception {
		SupportedClassPair pair = new SupportedClassPair(IBoardEditPart.class, User.class);
		EditPart context = mock(IBoardEditPart.class);
		Object model = mock(User.class);
		assertThat(pair.isSupported(context , model),is(true));
	}

	@Test
	public void isSupportTrueWhenModelIsAChild() throws Exception {
		SupportedClassPair pair = new SupportedClassPair(IBoardEditPart.class, User.class);
		EditPart context = mock(IBoardEditPart.class);
		Object model = new org.kompiro.jamcircle.kanban.model.mock.User();
		assertThat(pair.isSupported(context , model),is(true));
	}

}
