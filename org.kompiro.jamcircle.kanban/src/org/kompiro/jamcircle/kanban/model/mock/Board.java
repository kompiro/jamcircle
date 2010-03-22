/**
 * 
 */
package org.kompiro.jamcircle.kanban.model.mock;

import java.util.*;

import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.scripting.ScriptTypes;


public class Board extends MockEntity implements org.kompiro.jamcircle.kanban.model.Board{
	
	private List<Card> cards = new ArrayList<Card>();
	private List<Lane> lanes = new ArrayList<Lane>();
	private Date createdDate;
	private String title;
	private boolean trashed;
	private String script;
	private ScriptTypes scriptType;

	public boolean addCard(Card card) {
		boolean added = cards.add(card);
		card.setBoard(this);
		return added;
	}

	public boolean addLane(Lane lane) {
		return lanes.add(lane);
	}

	public boolean containCard(Card card) {
		return cards.contains(card);
	}

	public Card[] getCards() {
		return cards.toArray(new Card[]{});
	}
	
	public Card[] getCardsFromDB(){
		return new Card[]{};
	}

	public String getContainerName() {
		return "Board";
	}

	public Date getCreateDate() {
		return this.createdDate;
	}

	public Lane[] getLanes() {
		return lanes.toArray(new Lane[]{});
	}
	
	public Lane[] getLanesFromDB(){
		return new Lane[]{};		
	}

	public String getTitle() {
		return this.title;
	}

	public boolean isTrashed() {
		return this.trashed;
	}

	public boolean removeCard(Card card) {
		boolean remove = cards.remove(card);
		card.setBoard(null);
		return remove;
	}

	public boolean removeLane(Lane lane) {
		return lanes.remove(lane);
	}

	public void setCreateDate(Date date) {
		this.createdDate = date;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setTrashed(boolean trashed) {
		this.trashed = trashed;
	}

	public String getScript() {
		return this.script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public ScriptTypes getScriptType() {
		return this.scriptType;
	}

	public void setScriptType(ScriptTypes type) {
		this.scriptType = type;
	}

	public void clearMocks() {
		// do nothing.
	}
	
	public void save(boolean directExecution) {
		// do nothing.
	}

	public org.kompiro.jamcircle.kanban.model.Board getBoard() {
		return this;
	}

	public org.kompiro.jamcircle.kanban.model.Board gainBoard() {
		return getBoard();
	}

}