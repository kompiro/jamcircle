package org.kompiro.jamcircle.kanban.ui.internal.command;

import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.CardContainer;
import org.kompiro.jamcircle.kanban.ui.command.AbstractCommand;

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
		assert container != null && card != null;
		container.addCard(card);
	}
	
	@Override
	public void undo() {
		assert container != null && card != null;
		container.removeCard(card);
	}
	
	@Override
	public String getDebugLabel() {
		return String.format("%s container=[%s],card=[%s]",super.getDebugLabel(),container,card);
	}

//	public String getComfirmMessage() {
//		return String.format("Do you want to move %s to %s?", card.toString(),container.getContainerName());
//	}
	
}
