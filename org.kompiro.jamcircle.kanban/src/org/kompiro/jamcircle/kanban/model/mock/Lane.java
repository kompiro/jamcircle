package org.kompiro.jamcircle.kanban.model.mock;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.scripting.ScriptTypes;

/**
 * This implementation is mock of Lane and isn't able to store any persistence.
 * @author kompiro
 */
public class Lane extends MockGraphicalEntity implements org.kompiro.jamcircle.kanban.model.Lane{

	private Board board;
	private int height = VALUE_OF_HEIGHT;
	private int width = VALUE_OF_WIDTH;

	private List<Card> cards = new ArrayList<Card>();
	private String status;
	private String script;
	private Date createDate;
	private boolean iconized;
	private ScriptTypes scriptType;
	private File icon;

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
		boolean result = this.cards.add(card);
		fireProperty(PROP_CARD, null, card);
		return result;
	}
	
	public boolean removeCard(Card card) {
		boolean result = this.cards.remove(card);
		fireProperty(PROP_CARD, card,null);
		return result;
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
	
	public void commitConstraint(Object bounds) {
		fireProperty(Lane.PROP_CONSTRAINT,null,bounds);
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
	
	public Board gainBoard() {
		return getBoard();
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

	public File getCustomIcon() {
		return icon;
	}

	public void setCustomIcon(File icon) {
		this.icon = icon;
	}

	public boolean hasCustomIcon() {
		return icon != null;
	}

}