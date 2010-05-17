package org.kompiro.jamcircle.kanban.ui.internal.editpart.policy;

import java.util.Map;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.LaneEditPart;
import org.kompiro.jamcircle.kanban.ui.internal.figure.CardFigureLayer;
import org.kompiro.jamcircle.kanban.ui.internal.figure.LaneFigure;
import org.kompiro.jamcircle.kanban.ui.internal.figure.LaneFigure.CardArea;

public class CardAreaCalcurator {
	
	public void calc(LaneEditPart part, Rectangle rect, Map<?,?> visualPartMap, CompoundCommand command){
		LaneFigure laneFigure = part.getLaneFigure();
		CardArea area = laneFigure.getCardArea();
		for (Object o : area.getChildren()) {
			if (o instanceof CardFigureLayer) {
				CardFigureLayer cardFigure = (CardFigureLayer) o;
				EditPart card = (EditPart) visualPartMap.get(cardFigure);
				ChangeBoundsRequest request = translateCard(card,rect, laneFigure, cardFigure);
				if(request == null) continue;
				command.add(part.getCommand(request));
			}
		}		
	}

	private ChangeBoundsRequest translateCard(EditPart card, Rectangle rect,
			LaneFigure laneFigure, CardFigureLayer cardFigure) {
		Dimension size = cardFigure.getSize();
		Point translate = cardFigure.getLocation().getCopy().translate(size);
		Rectangle localRect = rect.getCopy();
		localRect.setLocation(0, 0);
		if (localRect.contains(translate)) return null;
		ChangeBoundsRequest request = new ChangeBoundsRequest();
		request.setConstrainedMove(true);
		request.setEditParts(card);
		Point p = cardFigure.getLocation().getCopy();
		if (translate.x + size.width > localRect.width) {
			p.x = laneFigure.getMaxCardLocationX(rect.getSize(),
					size);
		}
		if (translate.y + size.height > localRect.height) {
			p.y = laneFigure.getMaxCardLocationY(rect.getSize(),
					size);
		}

		request.setMoveDelta(cardFigure.getLocation().translate(
				p.getNegated()).getNegated());
		request.setType(RequestConstants.REQ_RESIZE_CHILDREN);
		return request;
	}
	
}
