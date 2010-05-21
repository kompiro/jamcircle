package org.kompiro.jamcircle.kanban.ui.internal.editpart.policy;

import java.util.Map;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.*;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.CardEditPart;
import org.kompiro.jamcircle.kanban.ui.internal.figure.CardFigure;

public class BoardLocalLayout {

	private EditPartViewer viewer;

	public BoardLocalLayout(EditPartViewer viewer) {
		this.viewer = viewer;
	}

	public synchronized void calc(Rectangle targetRect,
			Rectangle containerRect) {
		Point start = targetRect.getLocation();
		if(!containerRect.contains(targetRect)){
			if(targetRect.getTop().y < containerRect.getTop().y){
				start.y = 0;
			}
			else if(containerRect.getBottom().y < targetRect.getBottom().y){
				start.y = containerRect.getBottom().y - targetRect.getSize().height; 
			}
			else if(containerRect.getRight().x < targetRect.getRight().x){
				start.x = containerRect.getRight().x - targetRect.getSize().width;				
			}else if(targetRect.getLeft().x < containerRect.getLeft().x){
				start.x = 0;
			}
		}
		EditPart editPart = getCardEditPart(start);
		while(editPart != null) {
			start.x += CardFigure.CARD_WIDTH + 5;
			Dimension cardSize = new Dimension(CardFigure.CARD_WIDTH,CardFigure.CARD_HEIGHT);
			Rectangle localRect = new Rectangle(start,cardSize);
			
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
			Rectangle cardRect = fig.getBounds().getCopy();
			if(cardRect.contains(start)){
				EditPart editPart = (EditPart) visualPartMap.get(key);
				if(editPart instanceof CardEditPart) return editPart;
			}
		}
		return null;
	}

}
