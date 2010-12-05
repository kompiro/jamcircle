package org.kompiro.jamcircle.kanban.model;

import java.sql.Types;
import java.util.Date;

import net.java.ao.*;
import net.java.ao.schema.*;

import org.kompiro.jamcircle.scripting.ScriptTypes;
import org.kompiro.jamcircle.storage.model.ExecutorHandler;

/**
 * This interface describes Board and uses ActiveObject.
 * 
 * @author kompiro
 */
@Preload
@Implementation(BoardImpl.class)
public interface Board extends Entity, CardContainer, LaneContainer {

	String PROP_ID = "id"; //$NON-NLS-1$
	String PROP_CARD = "card"; //$NON-NLS-1$
	String PROP_LANE = "lane"; //$NON-NLS-1$
	String PROP_TITLE = "title"; //$NON-NLS-1$
	String PROP_SCRIPT = "script"; //$NON-NLS-1$
	String PROP_TRASHED = "trashed"; //$NON-NLS-1$
	String PROP_CREATE_DATE = "createdate"; //$NON-NLS-1$

	public void setTitle(String title);

	public String getTitle();

	@SQLType(Types.CLOB)
	public void setScript(String script);

	@SQLType(Types.CLOB)
	public String getScript();

	@Default(value = "0")
	public void setScriptType(ScriptTypes type);

	public ScriptTypes getScriptType();

	boolean isTrashed();

	@Default(value = "false")
	void setTrashed(boolean trashed);

	@NotNull
	void setCreateDate(Date date);

	Date getCreateDate();

	@OneToMany(where = "trashed = false and laneid is null")
	public Card[] getCardsFromDB();

	public boolean addLane(Lane lane);

	public boolean removeLane(Lane lane);

	@Ignore
	public Lane[] getLanes();

	@OneToMany(where = "trashed = false")
	public Lane[] getLanesFromDB();

	/**
	 * clear all mocks on board.
	 */
	@Ignore
	public void clearMocks();

	@Ignore
	public String getContainerName();

	Board getBoard();

	/**
	 * This method calls {@link net.java.ao.RawEntity#save()} and run in
	 * Executor.
	 * 
	 * @param directExecution
	 *            set true if you need direct
	 *            {@link net.java.ao.RawEntity#save()}
	 */
	void save(boolean directExecution);

	@Ignore
	public void setHandler(ExecutorHandler handler);

}