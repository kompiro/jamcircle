package org.kompiro.jamcircle.kanban.model;

import java.sql.Types;
import java.util.Date;

import net.java.ao.*;
import net.java.ao.schema.*;

import org.kompiro.jamcircle.scripting.ScriptTypes;
import org.kompiro.jamcircle.storage.model.GraphicalEntity;


/**
 * This interface describes Lane model using ActiveObjects.
 * @author kompiro
 */
@Preload
@Implementation(LaneImpl.class)
public interface Lane extends GraphicalEntity ,CardContainer {

	public static final String HEIGHT = "500";//$NON-NLS-1$
	public static final int VALUE_OF_HEIGHT = Integer.valueOf(HEIGHT);

	public static final String WIDTH = "200";//$NON-NLS-1$
	public static final int VALUE_OF_WIDTH = Integer.valueOf(WIDTH);

	
	String PROP_STATUS = "status";//$NON-NLS-1$
	String PROP_SCRIPT = "script";//$NON-NLS-1$
	String PROP_WIDTH = "width";//$NON-NLS-1$
	String PROP_HEIGHT = "height";//$NON-NLS-1$
	String PROP_CARD = "card";//$NON-NLS-1$
	String PROP_CONSTRAINT = "constraint";//$NON-NLS-1$
	String PROP_CREATE_DATE = "createdate";//$NON-NLS-1$
	String PROP_TRASHED = "trashed";//$NON-NLS-1$
	String PROP_ICONIZED = "iconized";//$NON-NLS-1$
	String PROP_BOARD = "boardid";//$NON-NLS-1$

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
	@Default(value=WIDTH)
	void setWidth(int width);
	
	int getHeight();
		
	@NotNull
	@Default(value=HEIGHT)
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
