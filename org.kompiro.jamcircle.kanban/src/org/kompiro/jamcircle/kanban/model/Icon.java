package org.kompiro.jamcircle.kanban.model;

import org.kompiro.jamcircle.storage.model.GraphicalEntity;

import net.java.ao.Implementation;
import net.java.ao.Preload;

@Preload
@Implementation(IconImpl.class)
public interface Icon extends GraphicalEntity{
	
	public static final String PROP_TYPE = "classtype";
	
	public void setClassType(String classType);
	
	public String getClassType();
}
