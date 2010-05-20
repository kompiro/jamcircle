package org.kompiro.jamcircle.kanban.model;

import java.beans.PropertyChangeEvent;

import org.kompiro.jamcircle.storage.model.GraphicalEntity;

/**
 * This implementation describes Graphical implementation and shows on board.
 * When you add some model of GraphicalEntity and use Implementation wrapper,extends this class.
 * @author kompiro
 *
 */
public class GraphicalImpl extends EntityImpl{
		
	public GraphicalImpl(GraphicalEntity entity) {
		super(entity);
	}

	public void prepareLocation() {
		fireEvent(new PropertyChangeEvent(entity,GraphicalEntity.PROP_PREPARE_LOCATION,null,null));
	}
	
	public void commitLocation(Object location) {
		fireEvent(new PropertyChangeEvent(entity,GraphicalEntity.PROP_COMMIT_LOCATION,null,location));
	}

}
