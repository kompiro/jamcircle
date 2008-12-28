package org.kompiro.jamcircle.kanban.ui.script;

import java.beans.PropertyChangeEvent;

public class ScriptEvent {
	private PropertyChangeEvent event;

	public ScriptEvent(PropertyChangeEvent event){
		this.event = event;
	}
	
	public boolean isAdded(){
		return event.getNewValue() != null;
	}

	public boolean isRemoved(){
		return event.getOldValue() != null;
	}
	
	public Object getPropertyChangeObject(){
		return event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
	}

}
