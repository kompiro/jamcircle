package org.kompiro.jamcircle.kanban.ui.internal.editpart;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;

import org.eclipse.draw2d.Clickable;
import org.eclipse.draw2d.geometry.Point;
import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.ui.editpart.IPropertyChangeDelegator;
import org.kompiro.jamcircle.kanban.ui.internal.figure.CardFigure;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;


public class CardEditPartTest {
	private CardFigure figure;
	private Card card;
	private CardEditPart part;

	@Before
	public void before() {
		BoardModel board = mock(BoardModel.class);
		figure = mock(CardFigure.class);
		part = new CardEditPart(board);
		part.setFigure(figure);
		IPropertyChangeDelegator delegator = new IPropertyChangeDelegatorForTest();
		part.setDelegator(delegator );
		card = new org.kompiro.jamcircle.kanban.model.mock.Card();
		part.setModel(card);
		
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
		
		verify(figure).setSubject(eq("test"));
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
	
}
