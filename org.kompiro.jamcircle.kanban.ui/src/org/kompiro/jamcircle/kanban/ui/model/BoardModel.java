package org.kompiro.jamcircle.kanban.ui.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.kompiro.jamcircle.kanban.model.*;


public class BoardModel extends AbstractModel implements CardContainer,LaneContainer, PropertyChangeListener{
	
	private static final long serialVersionUID = -7710219380100629938L;
	public static final String PROP_CARD = "PROP_CARD";
	public static final String PROP_LANE = "PROP_LANE";
//	public static final String PROP_USER = "PROP_USER";
//	public static final String PROP_USER_CLEAR = PROP_USER + "_CLEAR";
	public static final String PROP_ICON = "PROP_ICON";

//	private Map<String,UserModel> users = new LinkedHashMap<String,UserModel>();
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
//		getDisplay().asyncExec(new Runnable() {
//			
//			public void run() {
//				result[0] = board.addCard(card);
//			}
//		});
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
//		getDisplay().asyncExec(new Runnable() {
//			
//			public void run() {
//				result[0] = board.removeCard(card);
//			}
//		});
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
	
	@Override
	public void firePropertyChange(String propName, Object oldValue,
			Object newValue) {
//		if(KanbanUIStatusHandler.isDebug()){
//			String lanesMessage = "lanes " + sizeLane() + " " + board.getLanes();
//			String cardsMessage = "cards " + sizeCard() + " " + board.getCards();
//			String usersMessage = "users " + users.size() + " " + users;
//			KanbanUIStatusHandler.debug("BoardModel.firePropertyChange()\n" + lanesMessage + cardsMessage + usersMessage);
//		}
		super.firePropertyChange(propName, oldValue, newValue);
	}

	public String getContainerName() {
		return "Board";
	}

//	public boolean containsUser(UserModel value) {
//		return users.containsValue(value);
//	}

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
		// FIXME trash is more specified?
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
	
	
}