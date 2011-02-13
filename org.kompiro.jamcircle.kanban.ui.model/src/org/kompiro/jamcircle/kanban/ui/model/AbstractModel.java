package org.kompiro.jamcircle.kanban.ui.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.widgets.Display;

public abstract class AbstractModel implements Serializable {
	private static final long serialVersionUID = 649516352865020200L;
	public static final String PROP_NULL = "null"; //$NON-NLS-1$
	public static final String PROP_LOCATION = "location"; //$NON-NLS-1$

	protected Point location;

	private boolean removed;

	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);

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

	public boolean isRemoved() {
		return this.removed;
	}

	public void setRemoved(boolean removed) {
		this.removed = removed;
	}

	protected Display getDisplay() {
		return Display.getDefault();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
