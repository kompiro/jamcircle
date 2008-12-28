package org.kompiro.jamcircle.storage.model;

import org.kompiro.jamcircle.storage.model.GraphicalEntity;


public interface EnumModel extends GraphicalEntity {

	void setState(Niko2State good);
	
	Niko2State getState();

}
