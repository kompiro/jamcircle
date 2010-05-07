package org.kompiro.jamcircle.kanban.model;

import net.java.ao.Implementation;
import net.java.ao.Preload;

import org.kompiro.jamcircle.storage.model.GraphicalEntity;

/**
 * This interface describes Icon model.
 * @author kompiro
 */
@Preload
@Implementation(IconImpl.class)
public interface Icon extends GraphicalEntity{
	
	/** Icon class type field */
	public static final String PROP_TYPE = "classtype"; //$NON-NLS-1$

	/** Image source field */
	public static final String PROP_SRC = "src"; //$NON-NLS-1$
	
	/** Board field */
	public static final String PROP_BOARD = "board"; //$NON-NLS-1$
	
	public void setClassType(String classType);
	
	public String getClassType();
	
	public void setSrc(String src);
	
	public String getSrc();
	
	public void setBoard(Board board);
	
	public Board getBoard();
	
}
