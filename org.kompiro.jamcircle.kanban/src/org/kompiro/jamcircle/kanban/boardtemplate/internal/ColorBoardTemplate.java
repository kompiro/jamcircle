package org.kompiro.jamcircle.kanban.boardtemplate.internal;

import java.sql.SQLException;

import org.kompiro.jamcircle.kanban.KanbanActivator;
import org.kompiro.jamcircle.kanban.KanbanStatusHandler;
import org.kompiro.jamcircle.kanban.boardtemplate.AbstractBoardTemplate;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.scripting.ScriptTypes;


public class ColorBoardTemplate extends AbstractBoardTemplate {
	
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
			KanbanStatusHandler.fail(e,"error has occured.");
		}
	}
	
	private Lane createRedLane(Board board) throws SQLException{
		return createLane(board,"RED",100,50,200,250,0);
	}

	private Lane createYellowLane(Board board) throws SQLException{
		return createLane(board,"YELLOW",300,50,200,250,1);
	}

	private Lane createGreenLane(Board board) throws SQLException{
		return createLane(board,"GREEN",500,50,200,250,2);
	}

	private Lane createLightGreenLane(Board board) throws SQLException{
		return createLane(board,"LIGHT GREEN",700,50,200,250,3);
	}

	private Lane createLightBlueLane(Board board) throws SQLException{
		return createLane(board,"LIGHT BLUE",100,300,200,250,4);
	}

	private Lane createBlueLane(Board board) throws SQLException{
		return createLane(board,"BLUE",300,300,200,250,5);
	}

	private Lane createPurpleLane(Board board) throws SQLException{
		return createLane(board,"PURPLE",500,300,200,250,6);
	}

	private Lane createRedPurpleLane(Board board) throws SQLException{
		return createLane(board,"RED PURPLE",700,300,200,250,7);
	}

	private Lane createLane(Board board,String status, int x, int y, int width, int height, int colorType) {
		KanbanService kanbanService = KanbanActivator.getKanbanService();
		kanbanService.init();
		Lane lane = kanbanService.createLane(board,status, x, y, width, height);
		lane.setScriptType(ScriptTypes.JavaScript);
		lane.setScript(String.format(readFromResourceString(this.getClass().getResource("colorTemplate.txt")), colorType));
		lane.save(false);
		return lane;
	}

	public String getName(){
		return "Color Board";
	}
	
	public String getIcon(){
		return "icons/color_wheel.png";
	}
	
	@Override
	public String getDescription() {
		return "8 color lanes are defined.These lane have script to change card color.";
	}

}
