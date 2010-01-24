package org.kompiro.jamcircle.kanban.ui;

import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.scripting.ScriptingService;

public class KanbanUIContext {

	private static KanbanUIContext context;
	private KanbanService kanbanService;
	private ScriptingService scriptingService;

	public KanbanUIContext() {
		KanbanUIContext.context = this;
	}
	
	public static KanbanUIContext getDefault(){
		return context;
	}

	public KanbanService getKanbanService() {
		return kanbanService;
	}

	public void setKanbanService(KanbanService kanbanService) {
		this.kanbanService = kanbanService;
	}

	public ScriptingService getScriptingService() {
		return scriptingService;
	}

	public void setScriptingService(ScriptingService scriptService) {
		this.scriptingService = scriptService;
	}

}
