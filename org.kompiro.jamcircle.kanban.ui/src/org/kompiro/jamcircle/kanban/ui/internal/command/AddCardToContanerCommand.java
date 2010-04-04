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
	protected void initialize() {
		if(container != null && card != null){
			setExecute(true);
		}
	}
	
	@Override
	public void doExecute() {
		container.addCard(card);
		setUndoable(true);
	}
	
	@Override
	public void undo() {
		assert container != null && card != null;
		container.removeCard(card);
	}
	
	@Override
	public String getDebugLabel() {
		return String.format("%s container=[%s],card=[%s]",super.getDebugLabel(),container,card); //$NON-NLS-1$
	}

//	public String getComfirmMessage() {
//		return String.format("Do you want to move %s to %s?", card.toString(),container.getContainerName());
//	}
	
}
