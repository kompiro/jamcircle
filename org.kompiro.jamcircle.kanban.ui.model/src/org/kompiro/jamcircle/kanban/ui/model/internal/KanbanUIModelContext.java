package org.kompiro.jamcircle.kanban.ui.model.internal;

import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.model.ConfirmStrategy;

public class KanbanUIModelContext {

	private static KanbanUIModelContext context;
	private KanbanService kanbanService;
	private ConfirmStrategy confirmStrategy;

	public KanbanUIModelContext() {
		KanbanUIModelContext.context = this;
	}

	public static KanbanUIModelContext getDefault() {
		if (context == null) {
			KanbanUIModelContext.context = new KanbanUIModelContext();
		}
		return context;
	}

	public KanbanService getKanbanService() {
		return kanbanService;
	}

	public void setKanbanService(KanbanService kanbanService) {
		this.kanbanService = kanbanService;
	}

	public void setConfirmStrategy(ConfirmStrategy confirmStrategy) {
		this.confirmStrategy = confirmStrategy;
	}

	public ConfirmStrategy getConfirmStrategy() {
		return confirmStrategy;
	}

}
