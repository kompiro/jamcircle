package org.kompiro.jamcircle.kanban.ui.internal.command;

import java.sql.SQLException;


import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.CardContainer;
import org.kompiro.jamcircle.kanban.ui.KanbanUIStatusHandler;
import org.kompiro.jamcircle.kanban.ui.command.AbstractCommand;

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
		if(this.card != null && this.constraint != null && this.container != null) setExecute(true);
	}

	@Override
	public void doExecute() {
		try {
			changeContainer();
			setUndoable(true);
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
		card.save(false);
		container.addCard(card);
	}
	
	@Override
	public void undo() {
		card.setX(oldLocation.x);
		card.setY(oldLocation.y);
		card.setTrashed(trashed);
		card.save(false);
		container.removeCard(card);
	}
	
	@Override
	public String getDebugLabel() {
		String data = String.format("[card=%s,container=%s]",card.toString(),container.toString());
		return super.getDebugLabel() + data;
	}
}
