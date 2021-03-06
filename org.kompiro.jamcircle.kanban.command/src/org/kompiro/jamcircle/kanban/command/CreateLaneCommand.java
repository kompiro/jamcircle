package org.kompiro.jamcircle.kanban.command;

import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;

public class CreateLaneCommand extends AbstractCommand {
	private BoardModel board;

	private Lane model;

	public void doExecute() {
		board.addLane(model);
		setUndoable(true);
	}

	@Override
	public void undo() {
		board.removeLane(model);
	}

	@Override
	public void redo() {
		board.addLane(model);
	}

	public void setContainer(BoardModel board) {
		this.board = board;
	}

	public void setModel(Lane model) {
		this.model = model;
	}

	public Lane getModel() {
		return model;
	}

	@Override
	protected void initialize() {
		if (board != null && getModel() != null)
			setExecute(true);
	}

}