package org.kompiro.jamcircle.kanban.model;

import java.beans.PropertyChangeEvent;

import org.kompiro.jamcircle.storage.model.GraphicalEntity;


public class GraphicalImpl extends EntityImpl{
		
	public GraphicalImpl(GraphicalEntity entity) {
		super(entity);
	}
		
	public void commitLocation() {
		fireEvent(new PropertyChangeEvent(entity,GraphicalEntity.PROP_LOCATION,null,null));
	}

}
