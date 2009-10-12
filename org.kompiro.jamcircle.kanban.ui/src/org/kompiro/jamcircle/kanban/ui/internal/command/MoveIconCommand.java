package org.kompiro.jamcircle.kanban.ui.internal.command;


import org.eclipse.draw2d.geometry.Point;
import org.kompiro.jamcircle.kanban.ui.KanbanUIStatusHandler;
import org.kompiro.jamcircle.kanban.ui.command.MoveCommand;
import org.kompiro.jamcircle.kanban.ui.model.IconModel;

public class MoveIconCommand extends MoveCommand {

	private IconModel icon;
	private Point location;
	private Point oldLocation;

	public MoveIconCommand(){
	}
	
	@Override
	public void doExecute() {
		moveIcon(location);
	}
	
	@Override
	public void undo() {
		moveIcon(oldLocation);
	}

	private void moveIcon(Point location) {
		if (icon != null){
			icon.setLocation(location);
		}else{
			KanbanUIStatusHandler.fail(new RuntimeException(), "MoveIconCommand:0001:Exception is occured");
		}
	}

	@Override
	protected void initialize() {
		this.icon = (IconModel) getModel();
		setLabel(String.format("Move Icon:'%s'",icon.getClass().getSimpleName()));
		this.oldLocation = icon.getLocation();
		this.location = getRectangle().getLocation();
	}
}
