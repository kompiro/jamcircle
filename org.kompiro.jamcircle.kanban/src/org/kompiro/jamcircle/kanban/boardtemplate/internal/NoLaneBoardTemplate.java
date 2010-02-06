package org.kompiro.jamcircle.kanban.boardtemplate.internal;

import org.kompiro.jamcircle.kanban.boardtemplate.AbstractBoardTemplate;
import org.kompiro.jamcircle.kanban.model.Board;

public class NoLaneBoardTemplate extends AbstractBoardTemplate {

	public String getName() {
		return "No Lanes";
	}

	public void initialize(Board board) {
	}
	
	@Override
	public String getDescription() {
		return "no lanes and no script on board.";
	}

}
