package org.kompiro.jamcircle.kanban.model;

/**
 * This interface describes Board Container.
 * @author kompiro
 * TODO extends LaneContainer and CardContainer
 */
public interface BoardContainer {
	public void addCard(Card card);
	public void addLane(Lane lane);
//	public void add(Icon icon);
	public void removeCard(Card card);
	public void removeLane(Lane lane);
//	public void remove(Icon icon);
}
