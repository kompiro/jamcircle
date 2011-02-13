package org.kompiro.jamcircle.kanban.command.internal;

import org.kompiro.jamcircle.kanban.service.KanbanService;

public class KanbanCommandContext {

	private static KanbanCommandContext context;
	private KanbanService kanbanService;

	public KanbanCommandContext() {
		KanbanCommandContext.context = this;
	}

	public static KanbanCommandContext getDefault() {
		if (context == null) {
			KanbanCommandContext.context = new KanbanCommandContext();
		}
		return context;
	}

	public KanbanService getKanbanService() {
		return kanbanService;
	}

	public void setKanbanService(KanbanService kanbanService) {
		this.kanbanService = kanbanService;
	}

}
