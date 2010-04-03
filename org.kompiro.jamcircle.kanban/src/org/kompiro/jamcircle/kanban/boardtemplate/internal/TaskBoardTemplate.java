package org.kompiro.jamcircle.kanban.boardtemplate.internal;

import java.sql.SQLException;

import org.kompiro.jamcircle.kanban.KanbanActivator;
import org.kompiro.jamcircle.kanban.KanbanStatusHandler;
import org.kompiro.jamcircle.kanban.Messages;
import org.kompiro.jamcircle.kanban.boardtemplate.AbstractBoardTemplate;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.service.KanbanService;


public class TaskBoardTemplate extends AbstractBoardTemplate {
	
	private static final String BOARD_NAME = Messages.TaskBoardTemplate_board_name;
	private static final String THIS_BOARD_HAS_3_LANES_TODO_DOING_DONE = Messages.TaskBoardTemplate_description;
	private static final String DONE_SCRIPT_TEMPLATE_NAME = "taskDoneTemplate.txt"; //$NON-NLS-1$
	private static final String LANE_NAME_TO_DO = "ToDo"; //$NON-NLS-1$
	private static final String LANE_NAME_IN_PROGRESS = "In Progress"; //$NON-NLS-1$
	private static final String LANE_NAME_DONE = "Done"; //$NON-NLS-1$

	public void initialize(Board board) {
		try {
			createTodoLane(board);
			createDoingLane(board);
			createDoneLane(board);
		} catch (SQLException e) {
			KanbanStatusHandler.fail(e,Messages.TaskBoardTemplate_error_message);
		}
	}
	
	private Lane createTodoLane(Board board) throws SQLException {
		return createLane(board,LANE_NAME_TO_DO,100,50,200,500);
	}

	private Lane createDoingLane(Board board) throws SQLException {
		return createLane(board,LANE_NAME_IN_PROGRESS,300,50,200,500);
	}

	private Lane createDoneLane(Board board) {
		Lane lane = createLane(board,LANE_NAME_DONE,500,50,200,500);
		lane.setScript(readFromResourceString(getClass().getResource(DONE_SCRIPT_TEMPLATE_NAME)));
		lane.save(false);
		return lane;
	}

	private Lane createLane(Board board,String status, int x, int y, int width, int height) {
		KanbanService kanbanService = KanbanActivator.getKanbanService();
		Lane lane = kanbanService.createLane(board,status, x, y, width, height);
		return lane;
	}

	public String getName(){
		return BOARD_NAME;
	}
	
	@Override
	public String getDescription() {
		return THIS_BOARD_HAS_3_LANES_TODO_DOING_DONE;
	}
}
