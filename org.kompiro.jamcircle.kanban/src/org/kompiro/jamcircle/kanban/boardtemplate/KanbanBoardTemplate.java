package org.kompiro.jamcircle.kanban.boardtemplate;

import org.kompiro.jamcircle.kanban.model.Board;

public interface KanbanBoardTemplate {
	public void initialize(Board board);
	public String getName();
	public void setName(String name);
	public String getIcon();
	public void setIcon(String icon);
	public void setContributor(String contributor);
}
