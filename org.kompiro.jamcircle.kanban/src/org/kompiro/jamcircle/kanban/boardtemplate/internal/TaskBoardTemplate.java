package org.kompiro.jamcircle.kanban.boardtemplate.internal;

import java.sql.SQLException;

import org.kompiro.jamcircle.kanban.KanbanActivator;
import org.kompiro.jamcircle.kanban.KanbanStatusHandler;
import org.kompiro.jamcircle.kanban.boardtemplate.AbstractBoardTemplate;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.service.KanbanService;


public class TaskBoardTemplate extends AbstractBoardTemplate {
	
	public void initialize(Board board) {
		try {
			createTodoLane(board);
			createDoingLane(board);
			createDoneLane(board);
		} catch (SQLException e) {
			KanbanStatusHandler.fail(e,"error has occured.");
		}
	}
	
	private Lane createTodoLane(Board board) throws SQLException {
		return createLane(board,"ToDo",100,50,200,500);
	}

	private Lane createDoingLane(Board board) throws SQLException {
		return createLane(board,"Doing",300,50,200,500);
	}

	private Lane createDoneLane(Board board) {
		Lane lane = createLane(board,"Done",500,50,200,500);
		lane.setScript(readFromResourceString(getClass().getResource("taskDoneTemplate.txt")));
		lane.save(false);
		return lane;
	}

	private Lane createLane(Board board,String status, int x, int y, int width, int height) {
		KanbanService kanbanService = KanbanActivator.getKanbanService();
		Lane lane = kanbanService.createLane(board,status, x, y, width, height);
		return lane;
	}

	public String getName(){
		return "Task Board";
	}
	
	@Override
	public String getDescription() {
		return "This board has 3 lanes.Todo,Doing,Done.";
	}
}
