package org.kompiro.jamcircle.kanban.ui.internal.command;

import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.ui.command.AbstractCommand;

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
	}
	
	@Override
	public void undo() {
		lane.setIconized(oldIconized);
		lane.save(false);
	}

}
