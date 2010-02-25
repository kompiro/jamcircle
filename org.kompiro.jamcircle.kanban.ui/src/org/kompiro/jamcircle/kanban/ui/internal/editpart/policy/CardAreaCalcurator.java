package org.kompiro.jamcircle.kanban.ui.internal.editpart.policy;

import java.util.Map;

import org.eclipse.draw2d.geometry.*;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.LaneEditPart;
import org.kompiro.jamcircle.kanban.ui.internal.figure.CardFigure;
import org.kompiro.jamcircle.kanban.ui.internal.figure.LaneFigure;
import org.kompiro.jamcircle.kanban.ui.internal.figure.LaneFigure.CardArea;

public class CardAreaCalcurator {
	
	public void calc(LaneEditPart part, Rectangle rect, Map<?,?> visualPartMap, CompoundCommand command){
		LaneFigure laneFigure = part.getLaneFigure();
		CardArea area = laneFigure.getCardArea();
		for (Object o : area.getChildren()) {
			if (o instanceof CardFigure) {
				CardFigure cardFigure = (CardFigure) o;
				translateCard(visualPartMap,command, rect, part, laneFigure, cardFigure);
			}
		}		
	}

	private void translateCard(Map<?, ?> visualPartMap, CompoundCommand command, Rectangle rect,
			LaneEditPart part, LaneFigure laneFigure, CardFigure cardFigure) {
		Dimension size = cardFigure.getSize();
		Point translate = cardFigure.getLocation().getCopy().translate(size);
		Rectangle localRect = rect.getCopy();
		localRect.setLocation(0, 0);
		if (!localRect.contains(translate)) {
			ChangeBoundsRequest request = new ChangeBoundsRequest();
			request.setConstrainedMove(true);
			EditPart card = (EditPart) visualPartMap.get(cardFigure);
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
			command.add(part.getCommand(request));
		}
	}
	
}
