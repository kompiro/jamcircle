package org.kompiro.jamcircle.kanban.ui.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.kompiro.jamcircle.kanban.model.*;
import org.kompiro.jamcircle.scripting.ScriptTypes;


public class BoardModel extends AbstractModel implements CardContainer,LaneContainer, PropertyChangeListener{
	
	private static final long serialVersionUID = -7710219380100629938L;
	public static final String PROP_CARD = "PROP_CARD"; //$NON-NLS-1$
	public static final String PROP_LANE = "PROP_LANE"; //$NON-NLS-1$
	public static final String PROP_ICON = "PROP_ICON"; //$NON-NLS-1$

	private List<Object> children = new ArrayList<Object>();
	private TrashModel trash;
	private Board board;
	private boolean initialized = false;
	private List<IconModel> icons = new ArrayList<IconModel>();
	
	public BoardModel(Board board){
		this.board = board;
	}
	
	public boolean addCard(final Card card) {
		final boolean[] result = new boolean[1];
		result[0] = board.addCard(card);
		return result[0];
	}

	public boolean containCard(Card o) {
		return board.containCard(o);
	}
	
	public boolean isEmptyCards(){
		return board.getCards().length == 0;
	}

	public boolean removeCard(final Card card) {
		final boolean[] result = new boolean[1];
		result[0] = board.removeCard(card);
		return result[0];
	}
	
	public int sizeCard() {
		return board.getCards().length;
	}

	public Card[] getCards() {
		return board.getCards();
	}

	public boolean addLane(Lane lane) {
		return board.addLane(lane);
	}
	
	public boolean containLane(Lane o) {
		return board.containLane(o);
	}

	public boolean isEmptyLanes(){
		return board.getLanes().length == 0;
	}

	public boolean removeLane(Lane lane) {
		return board.removeLane(lane);
	}

	public int sizeLane() {
		return board.getLanes().length;
	}

	public Lane[] getLanes() {
		return board.getLanes();
	}

	public Lane getLane(int i) {
		return getLanes()[i];
	}
	
	public List<Object> getChildren(){
		children.clear();
		children.addAll(icons);
		for(Lane lane: getLanes()){
			children.add(lane);
		}
		for(Card card:getCards()){
			children.add(card);
		}
		return children;
	}
	
	public String getContainerName() {
		return Messages.BoardModel_container_name;
	}

	public Board getBoard() {
		return this.board;
	}

	public Board gainBoard() {
		return getBoard();
	}
	
	public TrashModel getTrashModel() {
		return this.trash;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		firePropertyChange(evt.getPropertyName(),evt.getOldValue(),evt.getNewValue());
	}

	public void setAnimated(boolean initialized){
		this.initialized = initialized;
	}

	public boolean isAnimated() {
		return initialized;
	}

	public void addIcon(IconModel model) {
		if(trash == null && model instanceof TrashModel){
			this.trash = (TrashModel) model;
		}
		// FIXME is trash more specified?
		this.icons.add(model);
		firePropertyChange(PROP_ICON, null, model);
	}
	
	public void removeIcon(IconModel model){
		if(model instanceof TrashModel) return;
		this.icons.remove(model);
		firePropertyChange(PROP_ICON, model, null);
	}

	public List<IconModel> getIconModels(){
		return icons;
	}
	
	public void clearMocks() {
		board.clearMocks();
	}

	public boolean hasScript() {
		String script = board.getScript();
		ScriptTypes scriptType = board.getScriptType();
		return (script != null && script.length() != 0) && scriptType != null;
	}

	public String getTitle() {
		return board.getTitle();
	}

	public int getID() {
		return board.getID();
	}
	
	
}