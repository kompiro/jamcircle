package org.kompiro.jamcircle.kanban.command;

import java.io.File;

import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.scripting.ScriptTypes;

public class LaneUpdateCommand extends AbstractCommand {

	private Lane lane;
	private String status;
	private String script;
	private String oldStatus;
	private String oldScript;
	private ScriptTypes oldType;
	private ScriptTypes type;
	private File customIcon;
	private File oldCustomIcon;

	public LaneUpdateCommand(Lane lane, String status, String script, ScriptTypes type, File customIcon) {
		this.lane = lane;
		this.status = status;
		this.script = script;
		this.type = type;
		this.customIcon = customIcon;
	}

	@Override
	public void doExecute() {
		this.oldStatus = lane.getStatus();
		this.oldScript = lane.getScript();
		this.oldType = lane.getScriptType();
		this.oldCustomIcon = lane.getCustomIcon();
		lane.setStatus(this.status);
		lane.setScript(this.script);
		lane.setScriptType(this.type);
		lane.setCustomIcon(this.customIcon);
		lane.save(false);
		setUndoable(true);
	}
	
	@Override
	public void undo() {
		lane.setStatus(this.oldStatus);
		lane.setScript(this.oldScript);
		lane.setScriptType(this.oldType);
		lane.setCustomIcon(this.oldCustomIcon);
		lane.save(false);		
	}

	@Override
	protected void initialize() {
		if(lane != null) setExecute(true);
	}

}
