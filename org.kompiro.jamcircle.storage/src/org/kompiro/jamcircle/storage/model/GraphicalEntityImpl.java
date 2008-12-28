package org.kompiro.jamcircle.storage.model;

public class GraphicalEntityImpl {
	
	private boolean deleted = false;
	private GraphicalEntity entity;
	
	public GraphicalEntityImpl(GraphicalEntity entity){
		this.entity = entity;
	}
	
	public void setDeletedVisuals(boolean deleted){
		this.deleted = deleted;
	}
	
	public boolean isDeletedVisuals(){
		return this.deleted;
	}
	
	protected GraphicalEntity getEntity() {
		return entity;
	}
	
	public void commitLocation(){
	}
	
	public boolean isMock(){
		return false;
	}

}
