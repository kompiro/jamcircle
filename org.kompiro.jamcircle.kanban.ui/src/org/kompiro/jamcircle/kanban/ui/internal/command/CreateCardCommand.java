package org.kompiro.jamcircle.kanban.ui.internal.command;

import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.CardContainer;
import org.kompiro.jamcircle.kanban.ui.command.AbstractCommand;

public class CreateCardCommand extends AbstractCommand {
    private CardContainer container;

    private Card model;
    
    public void doExecute() {
    	container.addCard(model);
    }
    
    @Override
    public void undo() {
		model.setDeletedVisuals(true);
    	container.removeCard(model);
    }
    
    @Override
    public void redo() {
		model.setDeletedVisuals(false);
    	container.addCard(model);
    }

    public void setContainer(CardContainer container) {
    	this.container = container;
    }

    public void setModel(Card model) {
        this.model = model;
    }

	public Card getModel() {
		return model;
	}

}