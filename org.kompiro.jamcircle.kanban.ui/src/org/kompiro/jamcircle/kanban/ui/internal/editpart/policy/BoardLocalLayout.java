package org.kompiro.jamcircle.kanban.ui.internal.editpart.policy;

import java.util.Map;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.*;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.CardEditPart;
import org.kompiro.jamcircle.kanban.ui.internal.figure.CardFigureLayer;

public class BoardLocalLayout {

	private EditPartViewer viewer;

	public BoardLocalLayout(EditPartViewer viewer) {
		this.viewer = viewer;
	}

	public synchronized void calc(Rectangle targetRect,
			Rectangle containerRect) {
		Point start = targetRect.getLocation();
		if(!containerRect.contains(targetRect)){
			start.x = 0;
		}
		EditPart editPart = getCardEditPart(start);
		while(editPart != null) {
			start.x += CardFigureLayer.CARD_WIDTH + 5;
			Dimension cardSize = new Dimension(CardFigureLayer.CARD_WIDTH,CardFigureLayer.CARD_HEIGHT);
			Rectangle localRect = new Rectangle(start,cardSize);
			
			if(!containerRect.contains(localRect)){
				start.y += CardFigureLayer.CARD_HEIGHT + 5;
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
			Rectangle cardRect = fig.getBounds().getCopy();
			cardRect.height = cardRect.height - CardFigureLayer.FOOTER_SECTION_HEIGHT;
			if(cardRect.contains(start)){
				EditPart editPart = (EditPart) visualPartMap.get(key);
				if(editPart instanceof CardEditPart) return editPart;
			}
		}
		return null;
	}

}