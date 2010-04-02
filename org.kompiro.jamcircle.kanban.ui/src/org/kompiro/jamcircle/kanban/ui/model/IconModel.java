package org.kompiro.jamcircle.kanban.ui.model;

import org.eclipse.draw2d.geometry.Point;

/**
 * This interface indicates model of Icon.
 * @author kompiro
 */
public interface IconModel {

	public Point getLocation();

	public void setLocation(Point location);
	
	/**
	 * return Icon's name label
	 * @return Icon's name label
	 */
	public String getName();
}
