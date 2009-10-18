package org.kompiro.jamcircle.xmpp.kanban.ui.internal.command;

import org.kompiro.jamcircle.kanban.ui.command.AbstractCommand;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;
import org.kompiro.jamcircle.xmpp.kanban.ui.internal.model.UserModel;

public class RemoveUserCommand extends AbstractCommand {

	private UserModel user;
	private BoardModel board;

	public RemoveUserCommand(UserModel user, BoardModel board) {
		this.user = user;
		this.board = board;
	}

	@Override
	public void doExecute() {
		board.removeIcon(user);
		setUndoable(true);
	}
	
	@Override
	public void undo() {
		board.addIcon(user);
	}

	@Override
	protected void initialize() {
		if(this.user != null && this.board != null)setExecute(true);
	}

}
