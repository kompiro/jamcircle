package org.kompiro.jamcircle.kanban.command;

import org.kompiro.jamcircle.kanban.model.CardContainer;
import org.kompiro.jamcircle.kanban.model.mock.Card;

/**
 * This command store card to Database from Mock.
 * @author kompiro
 */
public class CardStoreCommand extends AbstractCommand{
	
	private Card mock;
	private CardContainer container;

	public CardStoreCommand(Card mock,CardContainer container){
		this.mock = mock;
		this.container = container;
	}

	@Override
	public void doExecute() {
		org.kompiro.jamcircle.kanban.model.Card card = getKanbanService().createClonedCard(mock.getBoard(), mock.getOwner(), mock, mock.getX(), mock.getY());
		container.addCard(card);
	}

	@Override
	protected void initialize() throws IllegalStateException {
		if(mock == null || container == null) return;
		setExecute(true);
		setUndoable(false);
	}

}
