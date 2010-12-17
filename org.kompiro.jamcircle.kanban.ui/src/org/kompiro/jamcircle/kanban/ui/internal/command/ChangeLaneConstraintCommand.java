package org.kompiro.jamcircle.kanban.ui.internal.command;

import static java.lang.String.format;

import org.eclipse.draw2d.geometry.Rectangle;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.ui.Messages;
import org.kompiro.jamcircle.kanban.ui.command.MoveCommand;

/**
 * TODO Divide to change location and change size commands for lane.
 * 
 * @author kompiro
 * 
 */
public class ChangeLaneConstraintCommand extends MoveCommand<Lane> {

	private Lane lane;
	private Rectangle rect;
	private Rectangle oldRect;

	public ChangeLaneConstraintCommand() {
	}

	@Override
	protected void initialize() {
		this.lane = getModel();
		this.oldRect = new Rectangle(lane.getX(), lane.getY(), lane.getWidth(), lane.getHeight());
		this.rect = getRectangle();
		if (isChangeSizeMode()) {
			setLabel(format(Messages.ChangeLaneConstraintCommand_change_label, lane.getStatus()));
		} else {
			setLabel(format(Messages.ChangeLaneConstraintCommand_move_label, lane.getStatus()));
		}
		if (this.lane != null && this.rect != null) {
			setExecute(true);
		}
	}

	private boolean isChangeSizeMode() {
		return oldRect.getLocation().equals(rect.getLocation()) && !lane.isIconized();
	}

	@Override
	public void move() {
		lane.setX(rect.getLocation().x);
		lane.setY(rect.getLocation().y);
		if (!lane.isIconized()) {
			lane.setWidth(rect.width);
			lane.setHeight(rect.height);
		}
		lane.commitConstraint(rect);
		lane.save(false);
	}

	@Override
	public void undo() {
		if (oldRect == null)
			return;

		lane.setX(oldRect.getLocation().x);
		lane.setY(oldRect.getLocation().y);
		lane.setWidth(oldRect.width);
		lane.setHeight(oldRect.height);
		lane.commitConstraint(oldRect);
		lane.save(false);
	}

}
