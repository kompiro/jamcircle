package org.kompiro.jamcircle.kanban.ui.command;

import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.CardContainer;

public class AddCardToContanerCommand extends AbstractCommand
//implements CancelableCommand
{

	private CardContainer container;
	private Card card;

	public AddCardToContanerCommand(CardContainer container, Card card) {
		this.container = container;
		this.card = card;
	}
	
	@Override
	public void doExecute() {
		container.addCard(card);
	}
	
	@Override
	public void undo() {
		container.removeCard(card);
	}

//	public String getComfirmMessage() {
//		return String.format("Do you want to move %s to %s?", card.toString(),container.getContainerName());
//	}
	
}
