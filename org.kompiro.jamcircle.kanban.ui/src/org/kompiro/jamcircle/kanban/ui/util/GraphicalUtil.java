package org.kompiro.jamcircle.kanban.ui.util;

import org.eclipse.draw2d.geometry.Point;
import org.kompiro.jamcircle.storage.model.GraphicalEntity;


public class GraphicalUtil {

	public static Point currentLocation(GraphicalEntity entity){
		return new Point(entity.getX(),entity.getY());
	}
	
}
