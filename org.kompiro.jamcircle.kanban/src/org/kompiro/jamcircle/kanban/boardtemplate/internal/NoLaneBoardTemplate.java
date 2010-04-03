package org.kompiro.jamcircle.kanban.boardtemplate.internal;

import org.kompiro.jamcircle.kanban.Messages;
import org.kompiro.jamcircle.kanban.boardtemplate.AbstractBoardTemplate;
import org.kompiro.jamcircle.kanban.model.Board;

public class NoLaneBoardTemplate extends AbstractBoardTemplate {

	private static final String BOARD_NAME = Messages.NoLaneBoardTemplate_board_name;
	private static final String DESCRIPTION = Messages.NoLaneBoardTemplate_description;

	public String getName() {
		return BOARD_NAME;
	}

	public void initialize(Board board) {
	}
	
	@Override
	public String getDescription() {
		return DESCRIPTION;
	}

}
