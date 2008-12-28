package org.kompiro.jamcircle.kanban.ui.command;


import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.kompiro.jamcircle.kanban.ui.KanbanUIStatusHandler;
import org.kompiro.jamcircle.kanban.ui.model.IconModel;

public class MoveIconCommand extends AbstractCommand {

	private IconModel icon;
	private Point location;
	private Point oldLocation;

	
	public MoveIconCommand(IconModel icon,Rectangle rect){
		setLabel(String.format("Move Icon:'%s'",icon.getClass().getSimpleName()));
		this.icon = icon;
		this.oldLocation = icon.getLocation();
		this.location = rect.getLocation();
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
}
