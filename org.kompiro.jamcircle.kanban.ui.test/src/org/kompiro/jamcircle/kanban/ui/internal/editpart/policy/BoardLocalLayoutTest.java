package org.kompiro.jamcircle.kanban.ui.internal.editpart.policy;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import java.util.Set;

import org.eclipse.draw2d.geometry.*;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.EditPartViewer.Conditional;
import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.BoardEditPart;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.CardEditPart;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class BoardLocalLayoutTest {
	private BoardLocalLayout layout;
	private final Dimension cardSize = new Dimension(150,100);
	private BoardEditPart part;
	private EditPartViewer viewer;

	@Before
	public void before() {
		part = mock(BoardEditPart.class);
		viewer = mock(EditPartViewer.class);
		when(part.getViewer()).thenReturn(viewer);
		layout = new BoardLocalLayout(part);
	}

	@Test
	public void normalCase() throws Exception {
		Rectangle targetRect = new Rectangle(new Point(20,20),cardSize);
		Rectangle expect = targetRect.getCopy();
		Rectangle containerRect = new Rectangle(new Point(0,0),new Dimension(500, 500));
		layout.calc(targetRect , containerRect);
		assertThat(targetRect,is(expect));
	}
	
	@Test
	public void moveWhenOutOfBounds() throws Exception {
		Rectangle targetRect = new Rectangle(new Point(450,20),cardSize);
		Rectangle expect = targetRect.getCopy();
		Rectangle containerRect = new Rectangle(new Point(0,0),new Dimension(500, 500));
		layout.calc(targetRect , containerRect);
		assertThat(targetRect,is(not(expect)));
	}

	@Test
	public void moveWhenOutOfBoundsAndOnOneEditPart() throws Exception {
		when(viewer.findObjectAtExcluding((Point)any(), (Set<?>)any(), (Conditional)any())).thenAnswer(new Answer<EditPart>() {
			private int count = 0;
			public EditPart answer(InvocationOnMock invocation)
					throws Throwable {
				CardEditPart mock = mock(CardEditPart.class);
				if(count < 1){
					count++;
					return mock;
				}
				return mock(BoardEditPart.class);
			}
		});
		Rectangle targetRect = new Rectangle(new Point(450,20),cardSize);
		Rectangle expect = targetRect.getCopy();
		Rectangle containerRect = new Rectangle(new Point(0,0),new Dimension(500, 500));
		layout.calc(targetRect , containerRect);
		assertThat(targetRect,is(not(expect)));
	}

	@Test
	public void moveWhenOutOfBoundsAndOnSomeEditParts() throws Exception {
		when(viewer.findObjectAtExcluding((Point)any(), (Set<?>)any(), (Conditional)any())).thenAnswer(new Answer<EditPart>() {
			private int count = 0;
			public EditPart answer(InvocationOnMock invocation)
					throws Throwable {
				CardEditPart mock = mock(CardEditPart.class);
				if(count < 4){
					count++;
					return mock;
				}
				return mock(BoardEditPart.class);
			}
		});
		Rectangle targetRect = new Rectangle(new Point(450,20),cardSize);
		Rectangle expect = targetRect.getCopy();
		Rectangle containerRect = new Rectangle(new Point(0,0),new Dimension(500, 500));
		layout.calc(targetRect , containerRect);
		assertThat(targetRect,is(not(expect)));
	}

}
