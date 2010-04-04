package org.kompiro.jamcircle.kanban.ui.model;

import org.apache.commons.lang.NotImplementedException;
import org.kompiro.jamcircle.kanban.model.*;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.KanbanUIActivator;
import org.kompiro.jamcircle.kanban.ui.Messages;


public class InboxIconModel extends AbstractIconModel implements CardContainer{

	public static final String NAME = Messages.InboxIconModel_name;

	private static final long serialVersionUID = -6517334594320763535L;
	private static final String PROP_CARD = "inbox card"; //$NON-NLS-1$

	public InboxIconModel(Icon icon){
		super(icon);
	}

	public boolean addCard(Card card) {
		card.setBoard(null);
		card.setLane(null);
		card.save(false);
		firePropertyChange(PROP_CARD,null,card);
		return false;
	}

	public boolean containCard(Card card) {
		return card.getBoard() == null;
	}

	public Card[] getCards() {
		return getKanbanService().findCards("boardId is null and trashed = false"); //$NON-NLS-1$
	}

	public String getContainerName() {
		return NAME;
	}
	
	public String getName(){
		return NAME;
	}

	public boolean removeCard(Card card) {
		firePropertyChange(PROP_CARD,card,null);
		return false;
	}
	
	private KanbanService getKanbanService(){
		return KanbanUIActivator.getDefault().getKanbanService();
	}

	public Board gainBoard() {
		throw new NotImplementedException();
	}

	
}
