package org.kompiro.jamcircle.kanban.model.mock;

import org.kompiro.jamcircle.storage.model.GraphicalEntity;

public class MockGraphicalEntity extends MockEntity implements GraphicalEntity {
	
	private int x;
	private int y;
	private boolean deleted;
	private boolean trashed;
	
	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public boolean isDeletedVisuals() {
		return this.deleted;
	}

	public void setDeletedVisuals(boolean deleted) {
		this.deleted = deleted;
	}

	public void setX(int x) {
		int oldX = this.x;
		this.x = x;
		fireProperty(PROP_LOCATION_X,oldX, x);
	}

	public void setY(int y) {
		int oldY = this.y;
		this.y = y;
		fireProperty(PROP_LOCATION_Y, oldY, y);
	}

	public boolean isTrashed() {
		return this.trashed;
	}

	public void setTrashed(boolean trashed) {
		this.trashed = trashed;
	}
	
	public void save(boolean directExecution){
		save();
	}

	public void commitLocation() {
	}

	public void prepareLocation() {
	}

}
