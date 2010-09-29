package org.kompiro.jamcircle.kanban.ui;

import org.kompiro.jamcircle.kanban.service.BoardConverter;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.scripting.ScriptingService;

public class KanbanUIContext {

	private static KanbanUIContext context;
	private KanbanService kanbanService;
	private BoardConverter boardConverter;
	private ScriptingService scriptingService;

	public KanbanUIContext() {
		KanbanUIContext.context = this;
	}

	public static KanbanUIContext getDefault() {
		if (context == null) {
			KanbanUIContext.context = new KanbanUIContext();
		}
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

	public void setScriptingService(ScriptingService scriptingService) {
		this.scriptingService = scriptingService;
	}

	public void setBoardConverter(BoardConverter boardConverter) {
		this.boardConverter = boardConverter;
	}

	public BoardConverter getBoardConverter() {
		return boardConverter;
	}

}
