package org.kompiro.jamcircle.kanban.boardtemplate;

import org.kompiro.jamcircle.kanban.model.Board;

public interface KanbanBoardTemplate {
	public void initialize(Board board);
	public String getName();

}
