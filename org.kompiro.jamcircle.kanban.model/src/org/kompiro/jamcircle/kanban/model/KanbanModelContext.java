package org.kompiro.jamcircle.kanban.model;

import org.kompiro.jamcircle.storage.service.StorageService;

public class KanbanModelContext {

	private static KanbanModelContext context;
	private StorageService storageService;

	public KanbanModelContext() {
		KanbanModelContext.context = this;
	}

	public static KanbanModelContext getDefault() {
		if (context == null) {
			KanbanModelContext.context = new KanbanModelContext();
		}
		return context;
	}

	public void setStorageService(StorageService storageService) {
		this.storageService = storageService;
	}

	public StorageService getStorageService() {
		return storageService;
	}

}
