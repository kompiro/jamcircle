package org.kompiro.jamcircle.kanban.ui.model;


import org.eclipse.core.runtime.Platform;
import org.kompiro.jamcircle.kanban.model.*;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.storage.model.GraphicalEntity;

public class TrashModel extends AbstractIconModel implements CardContainer,LaneContainer {
	private static final long serialVersionUID = 33231939397478655L;
	public static String PROP_CARD = "trash card";
	public static String PROP_LANE = "trash lane";
	private KanbanService kanbanService;

	public TrashModel(Icon icon,KanbanService kanbanService){
		super(icon);
		this.kanbanService = kanbanService;
	}
	
	public boolean addCard(Card card) {
		kanbanService.discardToTrash(card);
		firePropertyChange(PROP_CARD,null,card);
		return false;
	}

	public boolean removeCard(Card card) {
		kanbanService.pickupFromTrash(card);
		firePropertyChange(PROP_CARD,card,null);
		return false;
	}
	
	public boolean containCard(Card card) {
		return card.isTrashed();
	}
	
	public Card[] getCards(){
		return getKanbanService().findCardsInTrash();
	}
	
	public int countTrashedCard(){
		return countTrashed(Card.class);
	}

	public boolean isEmpty() {
		if(Platform.isRunning()){
			return countTrashedCard() == 0;
		}
		return true;
	}

	public boolean addLane(Lane lane) {
		kanbanService.discardToTrash(lane);
		firePropertyChange(PROP_LANE,lane,null);		
		return false;
	}

	public boolean removeLane(Lane lane) {
		kanbanService.pickupFromTrash(lane);
		firePropertyChange(PROP_LANE,null,lane);
		return false;
	}
	
	public int countTrashedLane() {
		return countTrashed(Lane.class);
	}

	
	public Lane[] getLanes() {
		return getKanbanService().findLanesInTrash();
	}

	public String getContainerName() {
		return "TrashBox";
	}
	
	private KanbanService getKanbanService(){
		return kanbanService;
	}
	
	public void setKanbanService(KanbanService kanbanService) {
		this.kanbanService = kanbanService;
	}

	public int countTrashed(Class<? extends GraphicalEntity> clazz) {
		return getKanbanService().countInTrash(clazz);
	}


}