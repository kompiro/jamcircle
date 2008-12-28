package org.kompiro.jamcircle.kanban.ui.command;

import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.CardContainer;

public class CutCardCommand extends AbstractCommand {

	private CardContainer container;
	private Card card;

	public CutCardCommand(CardContainer container,Card card){
		this.container = container;
		this.card = card;
	}
	
	@Override
	public void doExecute() {
		container.removeCard(card);
		card.setTrashed(true);
	}
	
	@Override
	public void undo() {
		card.setTrashed(false);
		container.addCard(card);
	}

}
