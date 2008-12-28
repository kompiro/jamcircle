package org.kompiro.jamcircle.kanban.ui.model;


import org.eclipse.core.runtime.Platform;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.CardContainer;
import org.kompiro.jamcircle.kanban.model.Icon;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.model.LaneContainer;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.KanbanUIActivator;

public class TrashModel extends AbstractIconModel implements CardContainer,LaneContainer {
	private static final long serialVersionUID = 33231939397478655L;
	public static String PROP_CARD = "trash card";
	public static String PROP_LANE = "trash lane";

	public TrashModel(Icon icon){
		super(icon);
	}
	
	public boolean addCard(Card card) {
		if(!card.isMock()){
			card.setTrashed(true);
			card.save();
		}
		firePropertyChange(PROP_CARD,null,card);
		return false;
	}

	public boolean removeCard(Card card) {
		if(!card.isMock()){
			card.setTrashed(false);
			card.save();
		}
		firePropertyChange(PROP_CARD,card,null);
		return false;
	}
	
	public boolean containCard(Card card) {
		return card.isTrashed();
	}
	
	public Card[] getCards(){
		return getKanbanService().findCards(Card.PROP_TRASHED + " = ?", true);
	}
	
	public int getCardCount(){
		return getKanbanService().countCards();
	}

	public boolean isEmpty() {
		if(Platform.isRunning()){
			return getCardCount() == 0;
		}
		return true;
	}

	public boolean addLane(Lane lane) {
		if(!lane.isMock()){
			lane.setTrashed(true);
			lane.save();
		}
		firePropertyChange(PROP_LANE,lane,null);		
		return false;
	}

	public boolean removeLane(Lane lane) {
		if(!lane.isMock()){
			lane.setTrashed(false);
			lane.save();
		}
		firePropertyChange(PROP_LANE,null,lane);
		return false;
	}
	
	public Lane[] getLanes() {
		return getKanbanService().findLanesInTrash();
	}

	public String getContainerName() {
		return "TrashBox";
	}
	
	private KanbanService getKanbanService(){
		return KanbanUIActivator.getDefault().getKanbanService();
	}


}