package org.kompiro.jamcircle.kanban.ui.internal.command;

import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.CardContainer;
import org.kompiro.jamcircle.kanban.ui.command.AbstractCommand;

public class RemoveCardCommand extends AbstractCommand {

	private Card card;
	private CardContainer container;

	public RemoveCardCommand(Card card, CardContainer container) {
		this.card = card;
		this.container = container;
	}
	
	@Override
	protected void initialize() {
		if(card != null && container != null) setExecute(true);
	}

	@Override
	public void doExecute() {
		removeCardFromBoard();
		setUndoable(true);
	}

	private void removeCardFromBoard() {
		card.setDeletedVisuals(true);
		container.removeCard(card);
	}
	
	@Override
	public void undo() {
		card.setDeletedVisuals(false);
		container.addCard(card);
	}

}
