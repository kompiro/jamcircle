package org.kompiro.jamcircle.kanban.ui.internal.editpart;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.util.Calendar;

import org.eclipse.draw2d.Clickable;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.ColorTypes;
import org.kompiro.jamcircle.kanban.ui.editpart.IPropertyChangeDelegator;
import org.kompiro.jamcircle.kanban.ui.figure.StatusIcon;
import org.kompiro.jamcircle.kanban.ui.internal.figure.AnnotationArea;
import org.kompiro.jamcircle.kanban.ui.internal.figure.CardFigure;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;


public class CardEditPartTest {
	private CardFigure cardFigure;
	private Card card;
	private CardEditPart part;
	private IFigure figure;

	@Before
	public void before() {
		BoardModel board = mock(BoardModel.class);
		cardFigure = mock(CardFigure.class);
		part = new CardEditPart(board);
		card = new org.kompiro.jamcircle.kanban.model.mock.Card();
		part.setModel(card);
		figure = mock(AnnotationArea.class);
		part.createFigure();
		part.setFigure(figure);
		part.setCardFigure(cardFigure);
		IFigure dueDummy = mock(IFigure.class);
		part.setDueDummy(dueDummy);
		IPropertyChangeDelegator delegator = new IPropertyChangeDelegatorForTest();
		part.setDelegator(delegator);
		
		part.activate();
	}

	@Test
	public void doPropX() throws Exception {		
		card.setX(10);
		verify(figure).setLocation((Point)any());
	}

	
	@Test
	public void doPropY() throws Exception {
		card.setY(10);
		verify(figure).setLocation((Point)any());		
	}
	
	@Test
	public void doPropSubject() throws Exception {
		card.setSubject("test");
		
		verify(cardFigure).setSubject(eq("test"));
	}
	
	@Test
	public void doPropFile() throws Exception {
		Clickable fileIcon = mock(Clickable.class);
		part.setFileIcon(fileIcon );
		File file = mock(File.class);
		
		card.addFile(file);
		verify(fileIcon).setVisible(eq(true));

		card.deleteFile(file);
		verify(fileIcon).setVisible(eq(false));
	}
	
	@Test
	public void doPropBody() throws Exception {
		Clickable pageIcon = mock(Clickable.class);
		part.setPageIcon(pageIcon);
		card.setContent("test");
		
		verify(pageIcon).setVisible(eq(true));
		card.setContent("");
		verify(pageIcon).setVisible(eq(false));
		card.setContent(null);
		verify(pageIcon,times(2)).setVisible(eq(false));
	}
	
	@Test
	public void doPropComplete() throws Exception {
		StatusIcon completedIcon = mock(StatusIcon.class);
		part.setCompletedIcon(completedIcon);

		card.setCompleted(true);
		verify(completedIcon).setVisible(eq(true));
		card.setCompleted(false);
		verify(completedIcon).setVisible(eq(false));
	}
	
	@Test
	public void doPropDueCurrent() throws Exception {
		StatusIcon dueIcon = mock(StatusIcon.class);
		part.setDueIcon(dueIcon );
		IFigure dueDummy = mock(IFigure.class);
		part.setDueDummy(dueDummy );
		StatusIcon overDueIcon = mock(StatusIcon.class);
		part.setOverDueIcon(overDueIcon );

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1);
		card.setDueDate(cal.getTime());
		verify(dueIcon).setVisible(eq(true));
		cal.add(Calendar.DATE, -1);
		card.setDueDate(cal.getTime());
		verify(dueIcon).setVisible(eq(false));
	}
	
	@Test
	public void doPropDueSetNull() throws Exception {
		StatusIcon dueIcon = mock(StatusIcon.class);
		part.setDueIcon(dueIcon );
		IFigure dueDummy = mock(IFigure.class);
		part.setDueDummy(dueDummy );
		StatusIcon overDueIcon = mock(StatusIcon.class);
		part.setOverDueIcon(overDueIcon );

		Calendar cal = Calendar.getInstance();
		cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1);
		card.setDueDate(cal.getTime());
		verify(dueIcon).setVisible(eq(true));
		
		card.setDueDate(null);
		verify(dueIcon).setVisible(eq(false));
	}
	
	@Test
	public void doColorTypeChanged() throws Exception {
		card.setColorType(ColorTypes.BLUE);
		verify(cardFigure).setColorType(ColorTypes.BLUE);
	}
	
}
