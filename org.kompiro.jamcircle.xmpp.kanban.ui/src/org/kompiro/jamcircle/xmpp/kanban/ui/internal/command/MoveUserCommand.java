package org.kompiro.jamcircle.xmpp.kanban.ui.internal.command;


import org.eclipse.draw2d.geometry.Point;
import org.kompiro.jamcircle.kanban.command.MoveCommand;
import org.kompiro.jamcircle.xmpp.kanban.ui.Messages;
import org.kompiro.jamcircle.xmpp.kanban.ui.model.UserModel;

public class MoveUserCommand extends MoveCommand<UserModel> {

	private UserModel user;
	private Point location ;
	private Point oldLocation ;
	
	public MoveUserCommand() {
	}
		
	@Override
	public void move() {
		moveUser(location);
	}

	@Override
	public void undo() {
		moveUser(oldLocation);
	}

	private void moveUser(Point location) {
		user.setLocation(location);
	}
	
	@Override
	public String getDebugLabel() {
		return String.format("%s [user=%s]",super.getDebugLabel(),user); //$NON-NLS-1$
	}

	@Override
	protected void initialize() {
		this.user = (UserModel)getModel();
		if(user != null && getRectangle() != null){
			setLabel(String.format(Messages.MoveUserCommand_move_label,user.getName()));
			this.oldLocation = user.getLocation();
			this.location = getRectangle().getLocation();
			setExecute(true);
		}
	}
		
}
