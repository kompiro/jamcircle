package org.kompiro.jamcircle.kanban.ui.internal.editpart.policy;

import java.util.HashSet;

import org.eclipse.draw2d.geometry.*;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.EditPartViewer.Conditional;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.BoardEditPart;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.CardEditPart;
import org.kompiro.jamcircle.kanban.ui.internal.figure.CardFigure;

public class BoardLocalLayout {

	private EditPartViewer viewer;
	private Conditional contitional;
	private HashSet<Object> exclusionSet;

	public BoardLocalLayout(BoardEditPart part) {
		this.viewer = part.getViewer();
		this.contitional = new Conditional() {
			public boolean evaluate(EditPart editpart) {
				return (editpart instanceof CardEditPart);
			}
		};
		exclusionSet = new HashSet<Object>();
		exclusionSet.add(part);
	}

	public void calc(Rectangle targetRect,
			Rectangle containerRect) {
		if(!containerRect.contains(targetRect) && targetRect.intersects(containerRect)){
			Point start = new Point(0,0);
			EditPart editPart = viewer.findObjectAtExcluding(start,exclusionSet,contitional);
			while(editPart != null) {
				start = new Point(start.x + CardFigure.CARD_WIDTH + 5,start.y);
				Dimension size = new Dimension(CardFigure.CARD_WIDTH,CardFigure.CARD_HEIGHT);
				Rectangle localRect = new Rectangle(start,size);
				if(!containerRect.contains(localRect)){
					start.y += CardFigure.CARD_HEIGHT + 5;
					start.x = 0;
				}
				editPart = viewer.findObjectAtExcluding(start,exclusionSet,contitional);
				if(editPart instanceof BoardEditPart) break;
			}
			targetRect.setLocation(start);
		}
	}

}
