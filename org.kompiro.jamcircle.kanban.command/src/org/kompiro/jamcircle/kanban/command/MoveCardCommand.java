package org.kompiro.jamcircle.kanban.command;

import org.eclipse.draw2d.geometry.Point;
import org.kompiro.jamcircle.kanban.model.Card;

public class MoveCardCommand extends MoveCommand<Card> {

	private Card card;
	private Point location;
	private Point oldLocation;

	public MoveCardCommand() {
	}

	@Override
	public void move() {
		moveCard(location);
	}

	@Override
	public void undo() {
		moveCard(oldLocation);
	}

	private void moveCard(Point location) {
		if (card != null) {
			card.prepareLocation();
			card.setX(location.x);
			card.setY(location.y);
			card.commitLocation(location);
			card.save(false);
		} else {
			KanbanCommandStatusHandler.fail(new RuntimeException(), "MoveCardCommand:0001:Exception is occured"); //$NON-NLS-1$
		}
	}

	@Override
	protected void initialize() {
		this.card = getModel();
		if (card != null && getRectangle() != null) {
			setExecute(true);
			this.location = getRectangle().getLocation();
			this.oldLocation = new Point(card.getX(), card.getY());
			setLabel(String.format(Messages.MoveCardCommand_move_label, card.getSubject()));
		}
	}

}
