package org.kompiro.jamcircle.xmpp.kanban.ui.internal.command;


import org.eclipse.draw2d.geometry.Point;
import org.kompiro.jamcircle.kanban.ui.KanbanUIStatusHandler;
import org.kompiro.jamcircle.kanban.ui.command.MoveCommand;
import org.kompiro.jamcircle.xmpp.kanban.ui.internal.model.UserModel;

public class MoveUserCommand extends MoveCommand {

	private UserModel user;
	private Point location ;
	private Point oldLocation ;
	
	public MoveUserCommand() {
	}
		
	@Override
	public void doExecute() {
		moveUser(location);
	}

	@Override
	public void undo() {
		moveUser(oldLocation);
	}

	private void moveUser(Point location) {
		if (user != null){
			user.setLocation(location);
		}else{
			KanbanUIStatusHandler.fail(new RuntimeException(), "MoveUserCommand:0001:Exception is occured");
		}
	}
	
	@Override
	public String getDebugLabel() {
		return String.format("%s [user=%s]",super.getDebugLabel(),user);
	}

	@Override
	protected void initialize() {
		this.user = (UserModel)getModel();
		
		setLabel("Move User '" + user.getName() + "'");
		this.oldLocation = user.getLocation();
		this.location = getRectangle().getLocation();
	}
		
}
