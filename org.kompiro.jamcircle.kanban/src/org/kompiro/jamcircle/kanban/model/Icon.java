package org.kompiro.jamcircle.kanban.model;

import net.java.ao.Implementation;
import net.java.ao.Preload;

import org.kompiro.jamcircle.storage.model.GraphicalEntity;

/**
 * This interface describes Icon model.
 * @author kompiro
 *
 */
@Preload
@Implementation(IconImpl.class)
public interface Icon extends GraphicalEntity{
	
	/**
	 * Icon class type field
	 */
	public static final String PROP_TYPE = "classtype"; //$NON-NLS-1$
	
	public void setClassType(String classType);
	
	public String getClassType();
}
