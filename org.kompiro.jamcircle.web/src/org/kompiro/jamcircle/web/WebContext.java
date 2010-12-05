package org.kompiro.jamcircle.web;

import org.kompiro.jamcircle.kanban.service.BoardConverter;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.scripting.ScriptingService;

public class WebContext {

	private static WebContext context;
	private KanbanService kanbanService;
	private BoardConverter boardConverter;
	private ScriptingService scriptingService;

	public WebContext() {
		WebContext.context = this;
	}

	public static WebContext getDefault() {
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
