package org.kompiro.jamcircle.kanban.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;

import net.java.ao.Entity;

public class EntityImpl {

	private List<PropertyChangeListener> listeners = new LinkedList<PropertyChangeListener>();
	protected Entity entity;

	public EntityImpl(Entity entity){
		this.entity = entity;
	}
	
	protected List<PropertyChangeListener> getListeners() {
		return listeners;
	}
	
	protected void fireEvent(PropertyChangeEvent event) {
		for(PropertyChangeListener listener:getListeners()){
			listener.propertyChange(event);
		}
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener){
		if(entity != null){
			entity.addPropertyChangeListener(listener);
		}
		this.getListeners().add(listener);
	}
	
	
	public void removePropertyChangeListener(PropertyChangeListener listener){
		this.getListeners().remove(listener);
		if(entity != null){
			entity.removePropertyChangeListener(listener);
		}
	}

	
}
