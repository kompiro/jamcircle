package org.kompiro.jamcircle.kanban.ui.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.eclipse.draw2d.geometry.Point;

public abstract class AbstractModel implements Serializable{
	private static final long serialVersionUID = 649516352865020200L;
	public static final String PROP_NULL = "null";
	public static String PROP_LOCATION = "location";

	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);

	protected Point location;
	
	private boolean removed;

	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
	}

	public void firePropertyChange(String propName, Object oldValue, Object newValue) {
		listeners.firePropertyChange(propName, oldValue, newValue);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);
	}
	
	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		Point oldLocation = getLocation();
		this.location = location;
		firePropertyChange(PROP_LOCATION, oldLocation, location);
	}
	
	public boolean isRemoved(){
		return this.removed;
	}
	
	public void setRemoved(boolean removed) {
		this.removed = removed;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
