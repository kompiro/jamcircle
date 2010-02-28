package org.kompiro.jamcircle.kanban.ui.internal.editpart.policy;

import java.util.Map;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.*;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.BoardEditPart;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.CardEditPart;
import org.kompiro.jamcircle.kanban.ui.internal.figure.CardFigure;

public class BoardLocalLayout {

	private EditPartViewer viewer;
	private static final Dimension size = new Dimension(CardFigure.CARD_WIDTH,CardFigure.CARD_HEIGHT);

	public BoardLocalLayout(BoardEditPart part) {
		this.viewer = part.getViewer();
	}

	public synchronized void calc(Rectangle targetRect,
			Rectangle containerRect) {
		Point start = targetRect.getLocation();
		if(!containerRect.contains(targetRect)){
			start.x = 0;
		}
		EditPart editPart = getCardEditPart(start);
		while(editPart != null) {
			start.x += CardFigure.CARD_WIDTH + 5;
			Rectangle localRect = new Rectangle(start,size);
			if(!containerRect.contains(localRect)){
				start.y += CardFigure.CARD_HEIGHT + 5;
				start.x = 0;
			}
			editPart = getCardEditPart(start);
		}
		targetRect.setLocation(start);
	}

	private EditPart getCardEditPart(Point start) {
		Map<?,?> visualPartMap = viewer.getVisualPartMap();
		for(Object key :visualPartMap.keySet()){
			IFigure fig = (IFigure) key;
			if(fig.getBounds().contains(start)){
				EditPart editPart = (EditPart) visualPartMap.get(key);
				if(editPart instanceof CardEditPart) return editPart;
			}
		}
		return null;
	}

}
