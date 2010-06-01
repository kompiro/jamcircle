package org.kompiro.jamcircle.kanban.ui.internal.editpart.policy;

import java.util.Map;

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
		Point start = targetRect.getLocation().getCopy();
		translateToInset(targetRect, containerRect, start);
		translateToNotCrossOverCard(targetRect, containerRect, start);
	}

	private void translateToNotCrossOverCard(Rectangle targetRect,
			Rectangle containerRect, Point start) {
		start.translate(0, AnnotationArea.ACTION_ICON_SIZE);
		EditPart editPart = getCardEditPart(new Rectangle(start,CardFigure.CARD_SIZE));
		while(editPart != null) {
			start.x += CardFigure.CARD_WIDTH + 5;
			Rectangle localRect = new Rectangle(start,CardFigure.CARD_SIZE);
			
			if(isProtrudedCard(containerRect, localRect)){
				start.y += CardFigure.CARD_HEIGHT + 5 + AnnotationArea.ACTION_ICON_SIZE;
				start.x = 0;
			}
			editPart = getCardEditPart(new Rectangle(start,CardFigure.CARD_SIZE));
		}
		start.translate(0, - AnnotationArea.ACTION_ICON_SIZE );
		targetRect.setLocation(start);
	}

	private void translateToInset(Rectangle targetRect,
			Rectangle containerRect, Point start) {
		if(isProtrudedCard(containerRect, targetRect)){
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
	}

	private boolean isProtrudedCard(Rectangle containerRect, Rectangle localRect) {
		return !containerRect.contains(localRect);
	}

	private EditPart getCardEditPart(Rectangle target) {
		Map<?,?> visualPartMap = viewer.getVisualPartMap();
		for(Object key :visualPartMap.keySet()){
			if ((key instanceof AnnotationArea<?>) == false) continue;
			AnnotationArea<?> area = (AnnotationArea<?>) key;
			Object targetFig = area.getTargetFigure();
			if ((targetFig instanceof CardFigure) == false) continue;
			CardFigure card = (CardFigure) targetFig;
			Rectangle cardRect = card.getBounds().getCopy();
			card.translateToAbsolute(cardRect);
			if(isTouchedTargetCard(target, cardRect)){
				EditPart editPart = (EditPart) visualPartMap.get(key);
				if(editPart instanceof CardEditPart) return editPart;
			}
		}
		return null;
	}

	private boolean isTouchedTargetCard(Rectangle target, Rectangle cardRect) {
		return target.touches(cardRect);
	}

}
