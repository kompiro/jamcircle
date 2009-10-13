package org.kompiro.jamcircle.kanban.model;

import static java.lang.String.format;

import java.beans.PropertyChangeEvent;
import java.sql.SQLException;

import org.kompiro.jamcircle.kanban.KanbanStatusHandler;
import org.kompiro.jamcircle.scripting.ScriptTypes;


public class LaneImpl extends GraphicalImpl {

	private final Lane lane;

	public LaneImpl(Lane lane) throws IllegalArgumentException{
		super(lane);
		this.lane = lane;
	}

	public boolean addCard(final Card card) {
		if(lane.getBoard() == null){
			String message = format("This lane doesn't have parent boardl.:%s",lane);
			throw new IllegalArgumentException(message);
		}
		card.setLane(lane);
		Board cardsBoard = card.getBoard();
		Board lanesBoard = lane.getBoard();
		if(cardsBoard == null || lanesBoard.getID() != cardsBoard.getID()){
			card.setBoard(lanesBoard);
		}
		card.setTrashed(false);
		card.setDeletedVisuals(false);
		card.save(false);
		if(!card.isMock()){
			lane.getEntityManager().flush(card);
			try {
				lane.getEntityManager().find(Card.class,Card.PROP_ID + " = ?", card.getID());
			} catch (SQLException e) {
				KanbanStatusHandler.fail(e, "SQLException has occured.");
			}
		}
		PropertyChangeEvent event = new PropertyChangeEvent(lane,Lane.PROP_CARD,null,card);
		fireEvent(event);
		return true;
	}
	
	public boolean containCard(Card card) {
		return lane.equals(card.getLane());
	}

	@Override
	protected void fireEvent(PropertyChangeEvent event) {
		boolean added = event.getNewValue() != null;
		KanbanStatusHandler.debug("LaneImpl.fireEvent() lane:'" + lane.getStatus() + "'" +
				" event:'" + event.getPropertyName() + "' added:'" + added + "'");
		super.fireEvent(event);
	}

	public boolean removeCard(final Card card) {
		card.setLane(null);
		card.setBoard(null);
		card.setDeletedVisuals(true);
		card.save(false);
		PropertyChangeEvent event = new PropertyChangeEvent(lane,Lane.PROP_CARD,card,null);
		fireEvent(event);
		return true;
	}
	
	public ScriptTypes getScriptType(){
		ScriptTypes scriptType = lane.getScriptType();
		if(scriptType == null) return ScriptTypes.JavaScript;
		return scriptType;
	}
	
	public void commitConstraint(){
		fireEvent(new PropertyChangeEvent(this,Lane.PROP_CONSTRAINT,null,null));
	}

	public String getContainerName() {
		return format("Lane[%s]",lane.getStatus());
	}
	
	@Override
	public String toString() {
		return format("['#%d':'%s' trashed:'%s']", lane.getID(),lane.getStatus(),lane.isTrashed());
	}

}
