package org.kompiro.jamcircle.kanban.model;

import java.beans.PropertyChangeEvent;

import org.kompiro.jamcircle.storage.model.GraphicalEntity;


public class GraphicalImpl extends EntityImpl{
		
	public GraphicalImpl(GraphicalEntity entity) {
		super(entity);
	}

	public void prepareLocation() {
		fireEvent(new PropertyChangeEvent(entity,GraphicalEntity.PROP_PREPARE_LOCATION,null,null));
	}
	
	public void commitLocation() {
		fireEvent(new PropertyChangeEvent(entity,GraphicalEntity.PROP_COMMIT_LOCATION,null,null));
	}

}
