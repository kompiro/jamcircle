package org.kompiro.jamcircle.kanban.ui.model;

import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.geometry.Point;
import org.kompiro.jamcircle.kanban.model.Icon;

public abstract class AbstractIconModel extends AbstractModel implements IconModel {
	private static final long serialVersionUID = 6404250553691972190L;
	private Icon icon;

	public AbstractIconModel(Icon icon){
		this.icon = icon;
	}

	@Override
	public Point getLocation() {
		return new Point(icon.getX(),icon.getY());
	}
	
	@Override
	public void setLocation(Point location) {
		Point oldLocation = getLocation();
		icon.setX(location.x);
		icon.setY(location.y);
		icon.commitLocation(location);
		icon.save(false);
		firePropertyChange(PROP_LOCATION, oldLocation, location);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		super.addPropertyChangeListener(listener);
		icon.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		super.addPropertyChangeListener(listener);
		icon.removePropertyChangeListener(listener);
	}

}
