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
		this.content = content;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Lane getLane() {
		return lane;
	}

	public void setLane(Lane lane) {
		this.lane = lane;
	}

	public String getStatus() {
		return lane.getStatus();
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User user) {
		this.owner = user;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String user) {
		this.created = user;
	}

	public Date getCreateDate() {
		return this.createdDate;
	}

	public void setCreateDate(Date date) {
		this.createdDate = date;
	}

	public boolean isTrashed() {
		return this.trashed;
	}

	public void setTrashed(boolean trashed) {
		this.trashed = trashed;
	}

	public User getTo() {
		return null;
	}

	public void setTo(User user) {
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