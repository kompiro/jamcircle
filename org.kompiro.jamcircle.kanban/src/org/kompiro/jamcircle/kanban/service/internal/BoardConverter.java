package org.kompiro.jamcircle.kanban.service.internal;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.service.KanbanService;

public class BoardConverter {

	private KanbanService service;
	private Pattern titlePattern = Pattern.compile("title: (.+)\\n");
	private Pattern lanePattern = Pattern.compile("- id: (\\d+)\\n" +
			"\\s+status: (.+)\\n" +
			"\\s+x: (\\d+)\\n" +
			"\\s+y: (\\d+)\\n" +
			"\\s+width: (\\d+)\\n" +
			"\\s+height: (\\d+)\\n"
			);

	public String modelToText(Board board) {
		
		StringBuilder builder = new StringBuilder();
		builder.append(format("board:\n" +
				"	title: %s\n", board.getTitle()));
		Lane[] lanes = board.getLanes();
		if(lanes == null || lanes.length == 0 ) return builder.toString();
		builder.append("	lanes:\n");
		for(Lane lane:lanes){
			builder.append(format("		- id: %d\n",lane.getID()));
			builder.append(format("		  status: %s\n",lane.getStatus()));			
			builder.append(format("		  x: %d\n",lane.getX()));
			builder.append(format("		  y: %d\n",lane.getY()));			
			builder.append(format("		  width: %d\n",lane.getWidth()));
			builder.append(format("		  height: %d\n",lane.getHeight()));			
		}
		return builder.toString();
	}

	public Board textToModel(String text) {
		Board board = service.createBoard(getTitle(text));
		Lane[] lanes = getLanes(board,text);
		if(lanes == null || lanes.length == 0) return board;
		for(Lane lane : lanes){
			board.addLane(lane);
		}
		return board;
	}

	private Lane[] getLanes(Board board,String text) {
		Matcher matcher = lanePattern.matcher(text);
		List<Lane> lanes = new ArrayList<Lane>();
		while(matcher.find()){
			String status = matcher.group(2);
			String locationX = matcher.group(3);
			String locationY = matcher.group(4);
			String width = matcher.group(5);
			String height = matcher.group(6);
			lanes.add(service.createLane(board, status, Integer.valueOf(locationX), Integer.valueOf(locationY), Integer.valueOf(width), Integer.valueOf(height)));
		}
		
		return lanes.toArray(new Lane[]{});
	}

	private String getTitle(String text) {
		Matcher matcher = titlePattern.matcher(text);
		boolean found = matcher.find();
		String title = "";
		if(found){
			title = matcher.group(1);
		}
		return title;
	}
	
	void setKanbanService(KanbanService service){
		this.service = service;
	}

}
