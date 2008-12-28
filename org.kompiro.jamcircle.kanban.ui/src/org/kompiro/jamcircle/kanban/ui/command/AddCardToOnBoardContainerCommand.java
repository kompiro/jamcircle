package org.kompiro.jamcircle.kanban.ui.command;

import java.sql.SQLException;


import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.CardContainer;
import org.kompiro.jamcircle.kanban.ui.KanbanUIStatusHandler;

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
	public void doExecute() {
		try {
			changeContainer();
		} catch (SQLException e) {
			KanbanUIStatusHandler.fail(e, "0000:AddCardToContainerCommand");
		}
	}

	private void changeContainer() throws SQLException {
		oldLocation = new Point(card.getX(),card.getY());
		trashed = card.isTrashed();
		card.setX(constraint.x);
		card.setY(constraint.y);
		card.setTrashed(false);
		card.save();
		container.addCard(card);
	}
	
	@Override
	public void undo() {
		card.setX(oldLocation.x);
		card.setY(oldLocation.y);
		card.setTrashed(trashed);
		card.save();
		container.removeCard(card);
	}
}
