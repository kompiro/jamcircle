package org.kompiro.jamcircle.kanban.command;


import org.eclipse.draw2d.geometry.Point;
import org.kompiro.jamcircle.kanban.ui.model.IconModel;

public class MoveIconCommand extends MoveCommand<IconModel> {

	private IconModel icon;
	private Point location;
	private Point oldLocation;

	public MoveIconCommand(){
	}
	
	@Override
	public void move() {
		moveIcon(location);
	}
	
	@Override
	public void undo() {
		moveIcon(oldLocation);
	}

	private void moveIcon(Point location) {
		icon.setLocation(location);
	}

	@Override
	protected void initialize() {
		this.icon = getModel();
		if(icon != null && getRectangle() != null){
			this.oldLocation = icon.getLocation();
			setLabel(String.format(Messages.MoveIconCommand_command_label,icon.getClass().getSimpleName()));
			this.location = getRectangle().getLocation();
			setExecute(true);
		}
	}
}
