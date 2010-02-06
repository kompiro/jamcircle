package org.kompiro.jamcircle.kanban.boardtemplate.internal;

import org.kompiro.jamcircle.kanban.boardtemplate.AbstractBoardTemplate;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.scripting.ScriptTypes;

public class ScriptBoardTemplate extends AbstractBoardTemplate {

	private ScriptTypes type;
	private String script;
	
	public void initialize(Board board) {
		board.setScript(getScript());
		board.setScriptType(getType());
		board.save(false);
	}

	public ScriptTypes getType() {
		return type;
	}

	public void setType(ScriptTypes type) {
		this.type = type;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}
	
	

}
