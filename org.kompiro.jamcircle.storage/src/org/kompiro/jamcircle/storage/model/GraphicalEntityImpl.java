package org.kompiro.jamcircle.storage.model;

import java.beans.PropertyChangeEvent;

public class GraphicalEntityImpl extends EntityImpl {

	private boolean deleted = false;
	private GraphicalEntity entity;

	public GraphicalEntityImpl(GraphicalEntity entity) {
		super(entity);
		this.entity = entity;
	}

	public void setDeletedVisuals(boolean deleted) {
		this.deleted = deleted;
	}

	public boolean isDeletedVisuals() {
		return this.deleted;
	}

	protected GraphicalEntity getEntity() {
		return entity;
	}

	public void commitLocation() {
	}

	public boolean isMock() {
		return false;
	}

	public void prepareLocation() {
		fireEvent(new PropertyChangeEvent(entity, GraphicalEntity.PROP_PREPARE_LOCATION, null, null));
	}

	public void commitLocation(Object location) {
		fireEvent(new PropertyChangeEvent(entity, GraphicalEntity.PROP_COMMIT_LOCATION, null, location));
	}

}
