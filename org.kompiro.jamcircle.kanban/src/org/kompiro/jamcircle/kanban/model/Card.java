package org.kompiro.jamcircle.kanban.model;

import java.io.File;
import java.sql.Types;
import java.util.Date;
import java.util.List;

import net.java.ao.Implementation;
import net.java.ao.Preload;
import net.java.ao.schema.*;

/**
 * This interface describes Card model using ActiveObjects.
 * 
 * @author kompiro
 */
@Preload
@Implementation(CardImpl.class)
public interface Card extends HasColorTypeEntity {

	String PROP_SUBJECT = "subject"; //$NON-NLS-1$
	String PROP_CONTENT = "content"; //$NON-NLS-1$

	String PROP_BOARD = "boardID"; //$NON-NLS-1$
	String PROP_LANE = "laneID"; //$NON-NLS-1$

	String PROP_CREATED = "created"; //$NON-NLS-1$
	String PROP_CREATEDATE = "createdate"; //$NON-NLS-1$

	// TODO necessary?
	String PROP_OWNER = "ownerID"; //$NON-NLS-1$
	String PROP_FROM = "fromID"; //$NON-NLS-1$
	String PROP_TO = "toID"; //$NON-NLS-1$
	String PROP_FILES = "files"; //$NON-NLS-1$
	String PROP_UUID = "uuid"; //$NON-NLS-1$

	String PROP_COMPLETED = "completed"; //$NON-NLS-1$
	String PROP_COMPLETED_DATE = "completedDate"; //$NON-NLS-1$

	String PROP_DUE_DATE = "dueDate"; //$NON-NLS-1$
	String PROP_FLAG_TYPE = "flagType"; //$NON-NLS-1$

	@Ignore
	void setID(int id);

	String getSubject();

	void setSubject(String subject);

	@SQLType(Types.CLOB)
	String getContent();

	@SQLType(Types.CLOB)
	void setContent(String content);

	Lane getLane();

	void setLane(Lane lane);

	Board getBoard();

	void setBoard(Board board);

	String getCreated();

	void setCreated(String user);

	Date getCreateDate();

	void setCreateDate(Date date);

	boolean isTrashed();

	@Default(value = "false")
	void setTrashed(boolean trashed);

	boolean isCompleted();

	@Default(value = "false")
	void setCompleted(boolean completed);

	Date getCompletedDate();

	void setCompletedDate(Date date);

	Date getDueDate();

	void setDueDate(Date date);

	User getTo();

	void setTo(User user);

	User getOwner();

	void setOwner(User user);

	User getFrom();

	void setFrom(User user);

	String getUUID();

	/**
	 * Card's uuid isn't unique on database. Because this uuid is for identify
	 * same card when sending.
	 * 
	 * @param uuid
	 */
	void setUUID(String uuid);

	FlagTypes getFlagType();

	void setFlagType(FlagTypes flagType);

	@Ignore
	String getStatus();

	@Ignore
	List<File> getFiles();

	@Ignore
	void addFile(File file);

	@Ignore
	void deleteFile(File file);

	@Ignore
	boolean hasFile(File file);

	@Ignore
	boolean hasFiles();

	@Ignore
	String getFilePath();
}
