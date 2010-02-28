package org.kompiro.jamcircle.kanban.ui.internal.editpart.policy;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.*;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.CardEditPart;
import org.kompiro.jamcircle.kanban.ui.internal.figure.CardFigure;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class BoardLocalLayoutTest {
	private BoardLocalLayout layout;
	@Mock private EditPartViewer viewer;
	private Map<IFigure,EditPart> partMap = new HashMap<IFigure, EditPart>();

	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);
		when(viewer.getVisualPartMap()).thenReturn(partMap);
		layout = new BoardLocalLayout(viewer);
	}

	@Test
	public void normalCase() throws Exception {
		Rectangle targetRect = new Rectangle(new Point(20,20),CardFigure.CARD_SIZE);
		Rectangle expect = targetRect.getCopy();
		Rectangle containerRect = new Rectangle(new Point(0,0),new Dimension(500, 500));
		layout.calc(targetRect , containerRect);
		assertThat(targetRect,is(expect));
	}
	
	@Test
	public void moveWhenOutOfBounds() throws Exception {
		Rectangle targetRect = new Rectangle(new Point(450,20),CardFigure.CARD_SIZE);
		Rectangle expect = targetRect.getCopy();
		Rectangle containerRect = new Rectangle(new Point(0,0),new Dimension(500, 500));
		layout.calc(targetRect , containerRect);
		assertThat(targetRect,is(not(expect)));
	}

	@Test
	public void moveWhenOutOfBoundsAndOnOneEditPart() throws Exception {
		IFigure cardFigure = mock(IFigure.class);
		Rectangle mRect = mock(Rectangle.class);
		doAnswer(new Answer<Boolean>(){
			int count = 0;
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				if(count < 1){
					count++;
					return true;
				}
				return false;
			}
		}).when(mRect).contains((Point)any());
		when(cardFigure.getBounds()).thenReturn(mRect );
		EditPart cardPart = mock(CardEditPart.class);
		partMap.put(cardFigure , cardPart);

		Rectangle targetRect = new Rectangle(new Point(450,20),CardFigure.CARD_SIZE);
		Rectangle expect = targetRect.getCopy();
		Rectangle containerRect = new Rectangle(new Point(0,0),new Dimension(500, 500));
		layout.calc(targetRect , containerRect);
		assertThat(targetRect,is(not(expect)));
	}

	@Test
	public void moveWhenOutOfBoundsAndOnSomeEditParts() throws Exception {
		IFigure cardFigure = mock(IFigure.class);
		Rectangle mRect = mock(Rectangle.class);
		doAnswer(new Answer<Boolean>(){
			int count = 0;
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				if(count < 5){
					count++;
					return true;
				}
				return false;
			}
		}).when(mRect).contains((Point)any());
		when(cardFigure.getBounds()).thenReturn(mRect );
		EditPart cardPart = mock(CardEditPart.class);
		partMap.put(cardFigure , cardPart );
		Rectangle targetRect = new Rectangle(new Point(450,20),CardFigure.CARD_SIZE);
		Rectangle expect = targetRect.getCopy();
		Rectangle containerRect = new Rectangle(new Point(0,0),new Dimension(500, 500));
		layout.calc(targetRect , containerRect);
		assertThat(targetRect,is(not(expect)));
	}

}
