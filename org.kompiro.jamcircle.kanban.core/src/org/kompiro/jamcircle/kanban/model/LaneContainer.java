package org.kompiro.jamcircle.kanban.model;

public interface LaneContainer {
	
	boolean addLane(Lane lane);

	boolean removeLane(Lane lane);
	
	Lane[] getLanes();

}
