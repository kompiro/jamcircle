package org.kompiro.jamcircle.kanban.command;

import org.kompiro.jamcircle.kanban.model.Lane;

public class LaneToggleIconizedCommand extends AbstractCommand {

	private Lane lane;
	private boolean oldIconized;

	public LaneToggleIconizedCommand(Lane lane){
		this.lane = lane;
	}
	
	@Override
	public void doExecute() {
		this.oldIconized = lane.isIconized();
		lane.setIconized(!oldIconized);
		lane.save(false);
		setUndoable(true);
	}
	
	@Override
	public void undo() {
		lane.setIconized(oldIconized);
		lane.save(false);
	}

	@Override
	protected void initialize() {
		if(lane != null) setExecute(true);
	}

}
