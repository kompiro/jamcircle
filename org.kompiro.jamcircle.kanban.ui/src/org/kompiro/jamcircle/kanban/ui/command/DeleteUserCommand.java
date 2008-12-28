package org.kompiro.jamcircle.kanban.ui.command;

import org.kompiro.jamcircle.kanban.model.User;
import org.kompiro.jamcircle.kanban.ui.model.UserModel;


public class DeleteUserCommand extends AbstractCommand {
	
	private UserModel userModel;

	public DeleteUserCommand(UserModel userModel){
		this.userModel = userModel;
	}

	@Override
	public void doExecute() {
		User user = userModel.getUser();
		user.setTrashed(true);
		user.save();
	}
	
	@Override
	public void undo() {
		User user = userModel.getUser();
		user.setTrashed(false);
		user.save();
	}

}
