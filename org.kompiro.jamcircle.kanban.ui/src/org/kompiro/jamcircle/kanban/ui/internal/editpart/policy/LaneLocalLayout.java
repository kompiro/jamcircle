package org.kompiro.jamcircle.kanban.ui.internal.editpart.policy;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

public class LaneLocalLayout {

	public synchronized void calc(Rectangle targetRect,
			Rectangle containerRect) {
		Point start = targetRect.getLocation();
		if(targetRect.getTop().y < 0){
			start.y = 0;
		}
		if(containerRect.height < targetRect.getBottom().y){
			start.y = containerRect.height - targetRect.getSize().height; 
		}
		if( containerRect.width < targetRect.getRight().x){
			start.x = containerRect.width - targetRect.getSize().width;				
		}
		if(targetRect.getLeft().x < 0){
			start.x = 0;
		}
		if(start.x < 0 ){
			start.x = 0;
		}
		if(start.y < 0 ){
			start.y = 0;
		}
		targetRect.setLocation(start);
	}

}
