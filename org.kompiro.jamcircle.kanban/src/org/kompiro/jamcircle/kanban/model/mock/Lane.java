/**
 * 
 */
package org.kompiro.jamcircle.kanban.model.mock;

import java.util.Date;
import java.util.List;


import org.apache.commons.lang.NotImplementedException;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.ScriptTypes;

public class Lane extends MockGraphicalEntity implements org.kompiro.jamcircle.kanban.model.Lane{

	private Board board;
	private int height;
	private List<Card> cards;
	private String status;
	private String script;
	private int width;
	private Date createDate;
	private boolean iconized;
	private ScriptTypes scriptType;

	public void setHeight(int height) {
		this.height = height;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public void setScript(String script) {
		this.script = script;
	}
	
	public String getScript() {
		return this.script;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public Card[] getCards() {
		return this.cards.toArray(new Card[]{});
	}
	
	public boolean addCard(Card card) {
		return this.cards.add(card);
	}
	
	public boolean removeCard(Card card) {
		return this.cards.remove(card);
	}
	
	public boolean containCard(Card card){
		return this.cards.contains(card);
	}

	public Date getCreateDate() {
		return this.createDate;
	}
	
	public void setCreateDate(Date date) {
		this.createDate = date;
	}
	
	public void commitConstraint() {
		throw new NotImplementedException();
	}
		
	public boolean isIconized(){
		return this.iconized;
	}
	
	public void setIconized(boolean iconized){
		this.iconized = iconized;
	}
	
	public void setBoard(Board board){
		this.board = board;
	}
	
	public Board getBoard(){
		return board;
	}
	
	public String getContainerName() {
		return "Lane";
	}

	public int getHeight() {
		return this.height;
	}

	public ScriptTypes getScriptType() {
		return this.scriptType;
	}

	public String getStatus() {
		return this.status;
	}

	public int getWidth() {
		return this.width;
	}

	public void setScriptType(ScriptTypes type) {
		this.scriptType = type;
	}

}