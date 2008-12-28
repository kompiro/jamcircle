package org.kompiro.jamcircle.kanban.ui.command;


import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.kompiro.jamcircle.kanban.ui.KanbanUIStatusHandler;
import org.kompiro.jamcircle.kanban.ui.model.UserModel;

public class MoveUserCommand extends AbstractCommand {

	private UserModel user;
	private Point location ;
	private Point oldLocation ;
	
	public MoveUserCommand(UserModel user, Rectangle rect) {
		setLabel("Move User '" + user.getName() + "'");
		this.oldLocation = user.getLocation();
		this.location = rect.getLocation();
		this.user = user;
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
		
}
