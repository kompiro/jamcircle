package org.kompiro.jamcircle.kanban.ui.model;

import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.CardContainer;
import org.kompiro.jamcircle.kanban.model.Icon;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.KanbanUIActivator;


public class InboxIconModel extends AbstractIconModel implements CardContainer{

	private static final long serialVersionUID = -6517334594320763535L;
	private static final String PROP_CARD = "inbox card";

	public InboxIconModel(Icon icon){
		super(icon);
	}

	public boolean addCard(Card card) {
		card.setBoard(null);
		card.setLane(null);
		card.save();
		firePropertyChange(PROP_CARD,null,card);
		return false;
	}

	public boolean containCard(Card card) {
		return card.getBoard() == null;
	}

	public Card[] getCards() {
		return getKanbanService().findCards("boardId is null and trashed = false");
	}

	public String getContainerName() {
		return "INBOX";
	}

	public boolean removeCard(Card card) {
		firePropertyChange(PROP_CARD,card,null);
		return false;
	}
	
	private KanbanService getKanbanService(){
		return KanbanUIActivator.getDefault().getKanbanService();
	}

	
}
