package org.kompiro.jamcircle.xmpp.kanban.ui.internal.command;

import org.kompiro.jamcircle.kanban.command.AbstractCommand;
import org.kompiro.jamcircle.kanban.model.User;
import org.kompiro.jamcircle.xmpp.kanban.ui.model.UserModel;


public class DeleteUserCommand extends AbstractCommand {
	
	private UserModel userModel;

	public DeleteUserCommand(UserModel userModel){
		this.userModel = userModel;
	}

	@Override
	public void doExecute() {
		User user = userModel.getUser();
		user.setTrashed(true);
		user.save(false);
		setUndoable(true);
	}
	
	@Override
	public void undo() {
		User user = userModel.getUser();
		user.setTrashed(false);
		user.save(false);
	}

	@Override
	protected void initialize() {
		if(userModel != null && userModel.getUser() != null) setExecute(true);
	}

}
