package org.kompiro.jamcircle.kanban.ui.internal.command;


import org.eclipse.draw2d.geometry.Rectangle;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.ui.KanbanUIStatusHandler;
import org.kompiro.jamcircle.kanban.ui.command.MoveCommand;

public class ChangeLaneConstraintCommand extends MoveCommand {

	private Lane lane;
	private Rectangle rect;
	private Rectangle oldRect;
	
	public ChangeLaneConstraintCommand() {
	}
	
	@Override
	protected void initialize() {
		this.lane = (Lane)getModel();
		this.oldRect = new Rectangle(lane.getX(),lane.getY(),lane.getWidth(),lane.getHeight());
		this.rect = getRectangle();
		if(isChangeSizeMode()){
			setLabel("Change Size '" + lane.getStatus() +"'");
		}else{
			setLabel("Move area '" + lane.getStatus() +"'");			
		}
	}

	private boolean isChangeSizeMode() {
		return oldRect.getLocation().equals(rect.getLocation()) && ! lane.isIconized();
	}

	@Override
	public void move() {
		if (lane != null){
			lane.setX(rect.getLocation().x);
			lane.setY(rect.getLocation().y);
			if( ! lane.isIconized()){
				lane.setWidth(rect.width);
				lane.setHeight(rect.height);
			}
			lane.commitConstraint();
			lane.save();
		}else{
			KanbanUIStatusHandler.fail(new RuntimeException(), "ChangeLocationCommand:0001:Exception is occured");
		}
	}
		
	@Override
	public void undo() {
		if(oldRect == null) return;
		
		if(lane != null){
			lane.setX(oldRect.getLocation().x);
			lane.setY(oldRect.getLocation().y);
			lane.setWidth(oldRect.width);
			lane.setHeight(oldRect.height);
			lane.commitConstraint();
			lane.save();
		}else{
			KanbanUIStatusHandler.fail(new RuntimeException(), "ChangeLocationCommand:0002:Exception is occured");
		}
	}
	
}
