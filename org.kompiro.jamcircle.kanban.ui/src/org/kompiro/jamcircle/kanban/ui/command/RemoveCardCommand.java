package org.kompiro.jamcircle.kanban.ui.command;

import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.CardContainer;

public class RemoveCardCommand extends AbstractCommand {

	private Card card;
	private CardContainer container;

	public RemoveCardCommand(Card card, CardContainer container) {
		this.card = card;
		this.container = container;
	}

	@Override
	public void doExecute() {
		removeCardFromBoard();
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
