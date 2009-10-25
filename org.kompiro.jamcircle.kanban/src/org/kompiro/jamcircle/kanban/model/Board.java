package org.kompiro.jamcircle.kanban.model;

import java.sql.Types;
import java.util.*;

import org.kompiro.jamcircle.scripting.ScriptTypes;

import net.java.ao.*;
import net.java.ao.schema.Default;
import net.java.ao.schema.Ignore;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.SQLType;

@Preload
@Implementation(BoardImpl.class)
public interface Board extends Entity,CardContainer,LaneContainer {

	String PROP_ID = "id";
	String PROP_CARD = "card";
	String PROP_LANE = "lane";
	String PROP_TITLE = "title";
	String PROP_SCRIPT = "script";
	String PROP_TRASHED = "trashed";
	String PROP_CREATE_DATE = "createdate";

	public void setTitle(String title);
	
	public String getTitle();
	
	@SQLType(Types.CLOB)
	public void setScript(String script);

	@SQLType(Types.CLOB)
	public String getScript();

	@Default(value="0")
	public void setScriptType(ScriptTypes type);

	public ScriptTypes getScriptType();
	
	boolean isTrashed();
	
	@Default(value="false")
	void setTrashed(boolean trashed);
	
	@NotNull
	void setCreateDate(Date date);

	Date getCreateDate();
	
	public  boolean addCard(Card card);

	public  boolean removeCard(Card card);

	public  boolean containCard(Card card);

	@Ignore
	public Card[] getCards();
		
	@OneToMany(where="trashed = false and laneid is null")
	public Card[] getCardsFromDB();

	public  boolean addLane(Lane lane);

	public  boolean removeLane(Lane lane);
	
	@Ignore
	public Lane[] getLanes();

	@OneToMany(where="trashed = false")
	public Lane[] getLanesFromDB();
	
	@Ignore
	public void clearMocks();
	
	@Ignore
	public  String getContainerName();
	
	Board getBoard();
	
	/**
	 * This method calls {@link net.java.ao.RawEntity#save(false)} and run in  Executor.
	 * @param directExecution set true if you need direct {@link net.java.ao.RawEntity#save(false)}
	 */
	void save(boolean directExecution);

}