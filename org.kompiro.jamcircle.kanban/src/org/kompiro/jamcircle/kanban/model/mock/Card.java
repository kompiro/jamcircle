/**
 * 
 */
package org.kompiro.jamcircle.kanban.model.mock;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.model.ColorTypes;
import org.kompiro.jamcircle.kanban.model.FlagTypes;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.model.User;


public class Card extends MockGraphicalEntity implements org.kompiro.jamcircle.kanban.model.Card {
	
	private int id;
	private String content;
	private String subject;
	private Lane lane;
	private Board board;
	private User owner;
	private String created;
	private Date createdDate;
	private List<File> files = new ArrayList<File>();
	private String uuid;
	private ColorTypes colorType = ColorTypes.GREEN;
	private boolean completed;
	private Date completedDate;
	private boolean trashed;
	private Date dueDate;
	private FlagTypes flagType;
	private User to;
	
	public Card(){
		this.createdDate = new Date();
	}
	
	public Card(String subject){
		this();
		this.subject = subject;
	}
	
	public String getContent() {
		return content;
	}

	public String getSubject() {
		return subject;
	}

	public void setContent(String content) {
		String oldValue = this.content;
		this.content = content;
		fireProperty(Card.PROP_CONTENT, oldValue, content);
	}

	public void setSubject(String subject) {
		String oldValue = this.subject;
		this.subject = subject;
		fireProperty(Card.PROP_SUBJECT, oldValue, subject);
	}

	public Lane getLane() {
		return lane;
	}

	public void setLane(Lane lane) {
		Lane oldValue = this.lane;
		this.lane = lane;
		fireProperty(Card.PROP_LANE,oldValue,lane);
	}

	public String getStatus() {
		return lane.getStatus();
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User user) {
		User oldValue = this.owner;
		this.owner = user;
		fireProperty(Card.PROP_OWNER, oldValue, user);
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String user) {
		String oldValue = this.created;
		this.created = user;
		fireProperty(Card.PROP_CREATED, oldValue, user);
	}

	public Date getCreateDate() {
		return this.createdDate;
	}

	public void setCreateDate(Date date) {
		Date oldValue = createdDate;
		this.createdDate = date;
		fireProperty(Card.PROP_COMPLETED_DATE, oldValue, date);
	}

	public boolean isTrashed() {
		return this.trashed;
	}

	public void setTrashed(boolean trashed) {
		boolean oldValue = this.trashed;
		this.trashed = trashed;
		fireProperty(Card.PROP_TRASHED, oldValue, trashed);
	}

	public User getTo() {
		return to;
	}

	public void setTo(User user) {
		User oldValue = this.to;
		this.to = user;
		fireProperty(Card.PROP_TO, oldValue, user);
	}

	public User getFrom() {
		return null;
	}

	public void setFrom(User user) {
	}

	public void addFile(File file) {
		this.files.add(file);
	}

	public List<File> getFiles() {
		return this.files;
	}

	public boolean hasFiles() {
		return ! files.isEmpty();
	}

	public String getFilePath() {
		return null;
	}

	public boolean hasFile(File file) {
		if( ! file.exists()) return false;
		for(File target : files){
			if(target.getAbsolutePath().equals(file.getAbsolutePath())){
				return true;
			}
		}
		return false;
	}

	public void deleteFile(File file) {
		files.remove(file);
	}

	public String getUUID() {
		return this.uuid;
	}

	public void setUUID(String uuid) {
		this.uuid = uuid;
	}

	public ColorTypes getColorType() {
		return this.colorType;
	}

	public void setColorType(ColorTypes colorType) {
		this.colorType = colorType;
	}
	
	public void setColorType(int colorType){
		for(ColorTypes type :ColorTypes.values()){
			if(colorType == type.ordinal()){
				setColorType(type);
			}
		}
	}

	public boolean isCompleted() {
		return this.completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public Board getBoard() {
		return this.board;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public Date getCompletedDate() {
		return this.completedDate;
	}

	public void setCompletedDate(Date date) {
		this.completedDate = date;
	}

	public Date getDueDate() {
		return this.dueDate;
	}

	public void setDueDate(Date date) {
		this.dueDate = date;
	}

	public FlagTypes getFlagType() {
		return flagType;
	}

	public void setFlagType(FlagTypes flagType) {
		this.flagType = flagType;
	}

	public void setID(int id) {
		this.id = id;
	}
	
	@Override
	public int getID() {
		return this.id;
	}

}