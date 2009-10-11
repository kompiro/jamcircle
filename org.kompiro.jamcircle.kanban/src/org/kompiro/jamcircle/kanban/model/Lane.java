package org.kompiro.jamcircle.kanban.model;

import java.sql.Types;
import java.util.Date;

import org.kompiro.jamcircle.scripting.ScriptTypes;
import org.kompiro.jamcircle.storage.model.GraphicalEntity;

import net.java.ao.Implementation;
import net.java.ao.OneToMany;
import net.java.ao.Preload;
import net.java.ao.schema.Default;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.SQLType;


@Preload
@Implementation(LaneImpl.class)
public interface Lane extends GraphicalEntity ,CardContainer {

	String PROP_STATUS = "status";
	String PROP_SCRIPT = "script";
	String PROP_WIDTH = "width";
	String PROP_HEIGHT = "height";
	String PROP_CARD = "card";
	String PROP_CONSTRAINT = "constraint";
	String PROP_CREATEDATE = "createdate";
	String PROP_TRASHED = "trashed";
	String PROP_ICONIZED = "iconized";
	String PROP_BOARD = "boardid";

	String getStatus();
	
	void setStatus(String status);
	
	@SQLType(Types.CLOB)
	String getScript();
	
	@SQLType(Types.CLOB)
	void setScript(String script);

	@Default(value="0")
	public void setScriptType(ScriptTypes type);

	public ScriptTypes getScriptType();
	
	int getWidth();
	
	@NotNull
	@Default(value="200")
	void setWidth(int width);
	
	int getHeight();
		
	@NotNull
	@Default(value="500")
	void setHeight(int height);
	
	void setBoard(Board board);
	
	Board getBoard();
	
	boolean isTrashed();
	
	@Default(value="false")
	void setTrashed(boolean trashed);
	
	@NotNull
	void setCreateDate(Date date);

	Date getCreateDate();
	
	boolean isIconized();
	
	@Default(value="false")
	void setIconized(boolean iconized);
	
	@OneToMany(where="trashed=false")
	Card[] getCards();
	
	void commitConstraint();

}
