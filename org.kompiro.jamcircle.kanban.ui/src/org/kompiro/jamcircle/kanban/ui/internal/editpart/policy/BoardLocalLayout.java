package org.kompiro.jamcircle.kanban.ui.internal.editpart.policy;

import java.util.Map;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.*;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.CardEditPart;
import org.kompiro.jamcircle.kanban.ui.internal.figure.AnnotationArea;
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
		Dimension cardSize = new Dimension(CardFigure.CARD_WIDTH,CardFigure.CARD_HEIGHT + AnnotationArea.ANNOTATION_HEIGHT * 2);
		EditPart editPart = getCardEditPart(new Rectangle(start,cardSize));
		while(editPart != null) {
			start.x += CardFigure.CARD_WIDTH + 5;
			Rectangle localRect = new Rectangle(start,cardSize);
			
			if(!containerRect.contains(localRect)){
				start.y += CardFigure.CARD_HEIGHT + AnnotationArea.ANNOTATION_HEIGHT * 2 + 5;
				start.x = 0;
			}
			editPart = getCardEditPart(new Rectangle(start,cardSize));
		}
		targetRect.setLocation(start);
	}

	private EditPart getCardEditPart(Rectangle start) {
		Map<?,?> visualPartMap = viewer.getVisualPartMap();
		for(Object key :visualPartMap.keySet()){
			IFigure fig = (IFigure) key;
			if (fig instanceof AnnotationArea<?> == false) continue;
			AnnotationArea<?> area = (AnnotationArea<?>) fig;
			Object target = area.getTargetFigure();
			if (target instanceof CardFigure == false) continue;
			CardFigure card = (CardFigure) target;
			Rectangle cardRect = card.getBounds().getCopy();
			if(start.touches(cardRect)){
				EditPart editPart = (EditPart) visualPartMap.get(key);
				if(editPart instanceof CardEditPart) return editPart;
			}
		}
		return null;
	}

}
