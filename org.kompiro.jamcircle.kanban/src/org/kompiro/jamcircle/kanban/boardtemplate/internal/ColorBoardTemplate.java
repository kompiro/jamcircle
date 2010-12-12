package org.kompiro.jamcircle.kanban.boardtemplate.internal;

import java.sql.SQLException;

import org.kompiro.jamcircle.kanban.*;
import org.kompiro.jamcircle.kanban.Messages;
import org.kompiro.jamcircle.kanban.boardtemplate.AbstractBoardTemplate;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.scripting.ScriptTypes;

public class ColorBoardTemplate extends AbstractBoardTemplate {

	private static final String BOARD_NAME = Messages.ColorBoardTemplate_board_name;
	private static final String DESCRIPTION = Messages.ColorBoardTemplate_description;

	private static final String ICON_PATH = "icons/color_wheel.png"; //$NON-NLS-1$
	private static final String READ_TEMPLATE_NAME = "colorTemplate.txt"; //$NON-NLS-1$

	private static final String LANE_NAME_RED = "RED"; //$NON-NLS-1$
	private static final String LANE_NAME_YELLOW = "YELLOW"; //$NON-NLS-1$
	private static final String LANE_NAME_GREEN = "GREEN"; //$NON-NLS-1$
	private static final String LANE_NAME_LIGHT_GREEN = "LIGHT GREEN"; //$NON-NLS-1$
	private static final String LANE_NAME_LIGHT_BLUE = "LIGHT BLUE"; //$NON-NLS-1$
	private static final String LANE_NAME_BLUE = "BLUE"; //$NON-NLS-1$
	private static final String LANE_NAME_PURPLE = "PURPLE"; //$NON-NLS-1$
	private static final String LANE_NAME_RED_PURPLE = "RED PURPLE"; //$NON-NLS-1$
	private KanbanService kanbanService;

	public void initialize(Board board) {
		try {
			createRedLane(board);
			createYellowLane(board);
			createGreenLane(board);
			createLightGreenLane(board);
			createLightBlueLane(board);
			createBlueLane(board);
			createPurpleLane(board);
			createRedPurpleLane(board);
		} catch (SQLException e) {
			KanbanStatusHandler.fail(e, Messages.ColorBoardTemplate_error_message);
		}
	}

	void setKanbanService(KanbanService kanbanService) {
		this.kanbanService = kanbanService;
	}

	private Lane createRedLane(Board board) throws SQLException {
		return createLane(board, LANE_NAME_RED, 100, 50, 200, 250, 0);
	}

	private Lane createYellowLane(Board board) throws SQLException {
		return createLane(board, LANE_NAME_YELLOW, 300, 50, 200, 250, 1);
	}

	private Lane createGreenLane(Board board) throws SQLException {
		return createLane(board, LANE_NAME_GREEN, 500, 50, 200, 250, 2);
	}

	private Lane createLightGreenLane(Board board) throws SQLException {
		return createLane(board, LANE_NAME_LIGHT_GREEN, 700, 50, 200, 250, 3);
	}

	private Lane createLightBlueLane(Board board) throws SQLException {
		return createLane(board, LANE_NAME_LIGHT_BLUE, 100, 300, 200, 250, 4);
	}

	private Lane createBlueLane(Board board) throws SQLException {
		return createLane(board, LANE_NAME_BLUE, 300, 300, 200, 250, 5);
	}

	private Lane createPurpleLane(Board board) throws SQLException {
		return createLane(board, LANE_NAME_PURPLE, 500, 300, 200, 250, 6);
	}

	private Lane createRedPurpleLane(Board board) throws SQLException {
		return createLane(board, LANE_NAME_RED_PURPLE, 700, 300, 200, 250, 7);
	}

	private Lane createLane(Board board, String status, int x, int y, int width, int height, int colorType) {
		KanbanService kanbanService = getKanbanService();
		Lane lane = kanbanService.createLane(board, status, x, y, width, height);
		lane.setScriptType(ScriptTypes.JavaScript);
		lane.setScript(String
				.format(readFromResourceString(this.getClass().getResource(READ_TEMPLATE_NAME)), colorType));
		lane.save(false);
		return lane;
	}

	private KanbanService getKanbanService() {
		if (kanbanService == null) {
			kanbanService = KanbanActivator.getKanbanService();
			kanbanService.init();
		}
		return kanbanService;
	}

	public String getName() {
		return BOARD_NAME;
	}

	public String getIcon() {
		return ICON_PATH;
	}

	@Override
	public String getDescription() {
		return DESCRIPTION;
	}

}
