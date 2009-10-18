package org.kompiro.jamcircle.kanban.ui.internal.command;

import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.CardContainer;
import org.kompiro.jamcircle.kanban.ui.command.AbstractCommand;

public class CreateCardCommand extends AbstractCommand {
    private CardContainer container;

    private Card card;
    
    public void doExecute() {
    	container.addCard(card);
    	setUndoable(true);
    }
    
    @Override
    public void undo() {
		card.setDeletedVisuals(true);
    	container.removeCard(card);
    }
    
    @Override
    public void redo() {
		card.setDeletedVisuals(false);
    	container.addCard(card);
    }

    public void setContainer(CardContainer container) {
    	this.container = container;
    }

    public void setModel(Card card) {
        this.card = card;
    }

	public Card getModel() {
		return card;
	}

	@Override
	protected void initialize() {
		if(card != null && container != null) setExecute(true);
	}

}