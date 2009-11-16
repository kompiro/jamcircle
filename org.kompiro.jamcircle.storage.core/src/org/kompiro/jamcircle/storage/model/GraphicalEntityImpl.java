package org.kompiro.jamcircle.storage.model;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.kompiro.jamcircle.storage.service.internal.StorageServiceImpl;

public class GraphicalEntityImpl {
	
	private static final ExecutorService executor = Executors.newSingleThreadExecutor();
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
	
	public void save(boolean directExecution){
		if(directExecution || StorageServiceImpl.testmode){
			entity.save();
			return;
		}
		Runnable runnable = new Runnable() {
			public void run() {
				entity.save();
			}
		};
		executor.execute(runnable);
	}

}
