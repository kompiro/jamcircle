package org.kompiro.jamcircle.kanban.model;

/**
 * This interface describes Lane Container.
 * @author kompiro
 */
public interface LaneContainer {
	
	boolean addLane(Lane lane);

	boolean removeLane(Lane lane);
	
	public boolean containLane(Lane lane);
	
	Lane[] getLanes();

}
