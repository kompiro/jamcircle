package org.kompiro.jamcircle.kanban.model;

import java.sql.Types;
import java.util.*;

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

	public class Mock extends CardContainer.Mock implements Board{

		private Date createDate;
		private String script;
		private ScriptTypes scriptType;
		private String title;
		private boolean trashed;
		private List<Lane> lanes = new ArrayList<Lane>();

		public boolean addLane(Lane lane) {
			return lanes.add(lane);
		}

		public void clearMocks() {
		}

		public Card[] getCardsFromDB() {
			return null;
		}

		public Date getCreateDate() {
			return createDate;
		}

		public Lane[] getLanes() {
			return lanes.toArray(new Lane[]{});
		}

		public Lane[] getLanesFromDB() {
			return null;
		}

		public String getScript() {
			return this.script;
		}

		public ScriptTypes getScriptType() {
			return this.scriptType;
		}

		public String getTitle() {
			return this.title;
		}

		public boolean isTrashed() {
			return this.trashed;
		}

		public boolean removeLane(Lane lane) {
			return lanes.remove(lane);
		}

		public void setCreateDate(Date date) {
			this.createDate = date;
		}

		public void setScript(String script) {
			this.script = script;
		}

		public void setScriptType(ScriptTypes type) {
			this.scriptType = type;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public void setTrashed(boolean trashed) {
			this.trashed = trashed;
		}

		public int getID() {
			return 0;
		}

		public EntityManager getEntityManager() {
			return null;
		}

		public Class<? extends RawEntity<Integer>> getEntityType() {
			return null;
		}

		public void init() {
		}

		public void save() {
		}
		
	}

}