package org.kompiro.jamcircle.kanban.ui.internal.editpart.policy;

import org.eclipse.draw2d.geometry.Rectangle;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.model.Lane;

public class StickyLaneLayout {

	private Board board;

	public StickyLaneLayout(Board board) {
		this.board = board;
	}

	public Rectangle around(Lane target) {
		Lane[] lanes = board.getLanes();
		Rectangle targetRect = createRectange(target);
		if(lanes == null) return null;
		for (Lane lane : lanes) {
			Rectangle rect = createRectange(lane);
			if(onLeftSide(rect,targetRect)){
				return targetRect.setLocation(rect.x - targetRect.width, targetRect.y);
			}
			if(onRightSide(rect,targetRect)){
				return targetRect.setLocation(rect.x + rect.width, targetRect.y);
			}
		}
		return null;
	}

	private boolean onLeftSide(Rectangle base, Rectangle target) {
		return target.x <= base.x && base.x <= target.x + target.width;
	}

	private boolean onRightSide(Rectangle base, Rectangle target) {
		return base.x <= target.x && target.x <= base.x + base.width;
	}

	private Rectangle createRectange(Lane lane) {
		return new Rectangle(lane.getX(), lane.getY(), lane.getWidth(), lane.getHeight());
	}

	
}
