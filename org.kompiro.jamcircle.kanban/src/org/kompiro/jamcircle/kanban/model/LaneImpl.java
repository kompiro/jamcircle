package org.kompiro.jamcircle.kanban.model;

import java.beans.PropertyChangeEvent;
import java.sql.SQLException;

import org.kompiro.jamcircle.kanban.KanbanStatusHandler;


public class LaneImpl extends GraphicalImpl {

	private Lane lane;

	public LaneImpl(Lane lane){
		super(lane);
		this.lane = lane;
	}

	public boolean addCard(final Card card) {
		card.setLane(lane);
		if(card.getBoard() == null || lane.getBoard().getID() != card.getBoard().getID()){
			card.setBoard(lane.getBoard());
		}
		card.setTrashed(false);
		card.setDeletedVisuals(false);
		card.save();
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
		card.save();
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
		return String.format("Lane[%s]",lane.getStatus());
	}
	
	@Override
	public String toString() {
		return String.format("['#%d':'%s' trashed:'%s']", lane.getID(),lane.getStatus(),lane.isTrashed());
	}

}
