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
	private User from;
	
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
		fireProperty(PROP_CONTENT, oldValue, content);
	}

	public void setSubject(String subject) {
		String oldValue = this.subject;
		this.subject = subject;
		fireProperty(PROP_SUBJECT, oldValue, subject);
	}

	public Lane getLane() {
		return lane;
	}

	public void setLane(Lane lane) {
		Lane oldValue = this.lane;
		this.lane = lane;
		fireProperty(PROP_LANE,oldValue,lane);
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
		fireProperty(PROP_OWNER, oldValue, user);
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String user) {
		String oldValue = this.created;
		this.created = user;
		fireProperty(PROP_CREATED, oldValue, user);
	}

	public Date getCreateDate() {
		return this.createdDate;
	}

	public void setCreateDate(Date date) {
		Date oldValue = createdDate;
		this.createdDate = date;
		fireProperty(PROP_COMPLETED_DATE, oldValue, date);
	}

	public boolean isTrashed() {
		return this.trashed;
	}

	public void setTrashed(boolean trashed) {
		boolean oldValue = this.trashed;
		this.trashed = trashed;
		fireProperty(PROP_TRASHED, oldValue, trashed);
	}

	public User getTo() {
		return to;
	}

	public void setTo(User user) {
		User oldValue = this.to;
		this.to = user;
		fireProperty(PROP_TO, oldValue, user);
	}

	public User getFrom() {
		return from;
	}

	public void setFrom(User user) {
		User oldValue = this.from;
		this.from = user;
		fireProperty(PROP_FROM, oldValue, user);
	}

	public void addFile(File file) {
		this.files.add(file);
		fireProperty(PROP_FILES, null, file);
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
		fireProperty(PROP_FILES, file, null);
	}

	public String getUUID() {
		return this.uuid;
	}

	public void setUUID(String uuid) {
		Object oldValue = this.uuid;		
		this.uuid = uuid;
		fireProperty(PROP_UUID, oldValue, uuid);
	}

	public ColorTypes getColorType() {
		return this.colorType;
	}

	public void setColorType(ColorTypes colorType) {
		Object oldValue = this.colorType;
		this.colorType = colorType;
		fireProperty(PROP_COLOR_TYPE, oldValue, colorType);
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
		Object oldValue = this.completed;
		this.completed = completed;
		fireProperty(PROP_COMPLETED, oldValue, completed);
	}

	public Board getBoard() {
		return this.board;
	}

	public void setBoard(Board board) {
		Object oldValue = this.board;
		this.board = board;
		fireProperty(PROP_BOARD, oldValue, board);
	}

	public Date getCompletedDate() {
		return this.completedDate;
	}

	public void setCompletedDate(Date date) {
		Object oldValue = this.completedDate;
		this.completedDate = date;
		fireProperty(PROP_COMPLETED_DATE, oldValue, date);
	}

	public Date getDueDate() {
		return this.dueDate;
	}

	public void setDueDate(Date date) {
		Object oldValue = this.dueDate;
		this.dueDate = date;
		fireProperty(PROP_DUE_DATE, oldValue, date);
	}

	public FlagTypes getFlagType() {
		return flagType;
	}

	public void setFlagType(FlagTypes flagType) {
		Object oldValue = this.flagType;
		this.flagType = flagType;
		fireProperty(PROP_FLAG_TYPE, oldValue, flagType);
	}

	public void setID(int id) {
		this.id = id;
	}
	
	@Override
	public int getID() {
		return this.id;
	}
	

	public void commitLocation() {
		fireProperty(PROP_COMMIT_LOCATION, null, null);
	}
	
	public void prepareLocation() {
		fireProperty(PROP_PREPARE_LOCATION, null, null);
	}


}