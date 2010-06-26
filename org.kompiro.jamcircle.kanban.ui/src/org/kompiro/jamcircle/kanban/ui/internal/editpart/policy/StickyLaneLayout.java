package org.kompiro.jamcircle.kanban.ui.internal.editpart.policy;

import org.eclipse.draw2d.geometry.Rectangle;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.model.Lane;

public class StickyLaneLayout {

	private Board board;

	public StickyLaneLayout(Board board) {
		this.board = board;
	}

	public Rectangle rideOn(Lane targetLane, Rectangle target) {
		Lane[] lanes = board.getLanes();
		target = target.getCopy();
		if(lanes == null) return null;
		for (Lane lane : lanes) {
			if(lane.equals(targetLane)) continue;
			Rectangle base = createRectangle(lane);
			if(base.touches(target) == false) continue;
			if(onLeftSide(base,target)){
				return target.setLocation(base.getTopLeft().x - target.width, base.getTopLeft().y);
			}
			if(onRightSide(base,target)){
				return target.setLocation(base.getTopRight().x, base.getTopRight().y);
			}
			if(onUpSide(base,target)){
				return target.setLocation(target.x, base.getTop().y - target.height);
			}
			if(onDownSide(base,target)){
				return target.setLocation(target.x, base.getBottom().y);
			}
		}
		return null;
	}

	private boolean onDownSide(Rectangle base, Rectangle target) {
		return base.getBottom().y <= target.getBottom().y && 
		target.getTop().y <= base.getBottom().y &&
		base.getBottom().y <= target.getBottom().y;
	}

	private boolean onUpSide(Rectangle base, Rectangle target) {
		return target.getTop().y <= base.getTop().y && 
		base.getTop().y <= target.getBottom().y &&
		target.getBottom().y <= base.getBottom().y;
	}

	private boolean onLeftSide(Rectangle base, Rectangle target) {
		return target.getLeft().x <= base.getLeft().x && 
		base.getLeft().x <= target.getRight().x &&
		target.getRight().x <= base.getRight().x;
	}

	private boolean onRightSide(Rectangle base, Rectangle target) {
		return base.getLeft().x <= target.getLeft().x && 
		target.getLeft().x <= base.getRight().x &&
		base.getRight().x <= target.getRight().x;
	}

	private Rectangle createRectangle(Lane lane) {
		return new Rectangle(lane.getX(), lane.getY(), lane.getWidth(), lane.getHeight());
	}

	
}
