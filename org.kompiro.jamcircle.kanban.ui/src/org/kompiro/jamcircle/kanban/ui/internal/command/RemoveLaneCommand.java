package org.kompiro.jamcircle.kanban.ui.internal.command;

import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.ui.command.AbstractCommand;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;


public class RemoveLaneCommand extends AbstractCommand {

	private Lane lane;
	private BoardModel board;

	public RemoveLaneCommand(Lane lane, BoardModel board) {
		this.lane = lane;
		this.board = board;
	}

	@Override
	protected void initialize() {
		if(this.lane != null && this.board != null) setExecute(true);
	}
	
	@Override
	public void doExecute() {
		board.removeLane(lane);
		setUndoable(true);
	}
	
	@Override
	public void undo() {
		board.addLane(lane);
	}

}
