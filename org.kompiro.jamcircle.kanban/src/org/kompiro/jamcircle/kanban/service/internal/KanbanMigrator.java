package org.kompiro.jamcircle.kanban.service.internal;

import java.sql.SQLException;

import org.kompiro.jamcircle.kanban.KanbanStatusHandler;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.Icon;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.model.User;


class KanbanMigrator {

	private KanbanServiceImpl service;

	KanbanMigrator(KanbanServiceImpl service){
		this.service = service;
	}
	
	@SuppressWarnings("unchecked")
	public void migrate() {
		try {
			service.getEntityManager().migrate(Board.class,Lane.class,User.class,Card.class,Icon.class);
		} catch (SQLException e) {
			KanbanStatusHandler.fail(e, "0001:KanbanMigrator:",true);
		}
	}
	
}
