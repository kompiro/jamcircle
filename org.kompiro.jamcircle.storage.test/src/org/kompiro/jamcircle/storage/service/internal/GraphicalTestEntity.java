package org.kompiro.jamcircle.storage.service.internal;

import org.kompiro.jamcircle.storage.model.GraphicalEntity;

public interface GraphicalTestEntity extends GraphicalEntity {
	String PROP_NAME = "name";
	String PROP_UUID = "uuid";
	
	public String getName();
	public void setName(String name);
	
	public void setUuid(String uuid);
	public String getUuid();

}
