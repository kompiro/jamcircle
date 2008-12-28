package org.kompiro.jamcircle.kanban.ui.command;

import org.kompiro.jamcircle.kanban.ui.model.BoardModel;
import org.kompiro.jamcircle.kanban.ui.model.UserModel;

public class RemoveUserCommand extends AbstractCommand {

	private UserModel user;
	private BoardModel board;

	public RemoveUserCommand(UserModel user, BoardModel board) {
		this.user = user;
		this.board = board;
	}

	@Override
	public void doExecute() {
		board.removeUser(user);
	}
	
	@Override
	public void undo() {
		board.addUser(user);
	}

}
