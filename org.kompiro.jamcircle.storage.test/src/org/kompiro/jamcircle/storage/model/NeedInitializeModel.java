package org.kompiro.jamcircle.storage.model;

import org.kompiro.jamcircle.storage.model.GraphicalEntity;

public interface NeedInitializeModel extends GraphicalEntity {
	
	void setName(String name);
	
	String getName();

}
