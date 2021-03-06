package org.kompiro.jamcircle.kanban.command;

import java.sql.SQLException;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.CardContainer;

public class AddCardToOnBoardContainerCommand extends AbstractCommand {
	private Card card;
	private Point oldLocation;
	private boolean trashed;
	private Rectangle constraint;
	private CardContainer container;

	public AddCardToOnBoardContainerCommand(Card card, Rectangle constraint, CardContainer container) {
		this.card = card;
		this.constraint = constraint;
		this.container = container;
	}

	@Override
	protected void initialize() {
		if (this.card != null && this.constraint != null && this.container != null)
			setExecute(true);
	}

	@Override
	public void doExecute() {
		try {
			changeContainer();
			setUndoable(true);
		} catch (SQLException e) {
			KanbanCommandStatusHandler.fail(e, "0000:AddCardToContainerCommand"); //$NON-NLS-1$
		}
	}

	private void changeContainer() throws SQLException {
		oldLocation = new Point(card.getX(), card.getY());
		trashed = card.isTrashed();
		card.prepareLocation();
		card.setX(constraint.x);
		card.setY(constraint.y);
		card.setTrashed(false);
		container.addCard(card);
		card.commitLocation(constraint.getLocation());
		card.save(false);

	}

	@Override
	public void undo() {
		card.setX(oldLocation.x);
		card.setY(oldLocation.y);
		card.setTrashed(trashed);
		container.removeCard(card);
		card.save(false);
	}

	@Override
	public String getDebugLabel() {
		String data = String.format("[card=%s,container=%s]", card.toString(), container.toString()); //$NON-NLS-1$
		return super.getDebugLabel() + data;
	}
}
