package org.kompiro.jamcircle.kanban.ui.internal.command;

import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.ui.command.AbstractCommand;
import org.kompiro.jamcircle.scripting.ScriptTypes;

public class LaneUpdateCommand extends AbstractCommand {

	private Lane lane;
	private String status;
	private String script;
	private String oldStatus;
	private String oldScript;
	private ScriptTypes oldType;
	private ScriptTypes type;

	public LaneUpdateCommand(Lane lane, String status, String script, ScriptTypes type) {
		this.lane = lane;
		this.status = status;
		this.script = script;
		this.type = type;
	}

	@Override
	public void doExecute() {
		this.oldStatus = lane.getStatus();
		this.oldScript = lane.getScript();
		this.oldType = lane.getScriptType();
		lane.setStatus(this.status);
		lane.setScript(this.script);
		lane.setScriptType(this.type);
		lane.save();
	}
	
	@Override
	public void undo() {
		lane.setStatus(this.oldStatus);
		lane.setScript(this.oldScript);
		lane.setScriptType(this.oldType);
		lane.save();		
	}

}
