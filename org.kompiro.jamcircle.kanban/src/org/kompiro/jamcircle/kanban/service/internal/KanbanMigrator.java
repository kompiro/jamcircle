package org.kompiro.jamcircle.kanban.service.internal;

import org.kompiro.jamcircle.kanban.model.*;


class KanbanMigrator {

	private KanbanServiceImpl service;

	KanbanMigrator(KanbanServiceImpl service){
		this.service = service;
	}
	
	@SuppressWarnings("unchecked")
	public void migrate() {
		service.migrate(Board.class,Lane.class,User.class,Card.class,Icon.class);
	}
	
}
