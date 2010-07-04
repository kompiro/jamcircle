package org.kompiro.jamcircle.kanban.service.internal;

import static java.lang.String.format;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.service.exception.BoardFileFormatException;

public class BoardConverter {

	private KanbanService service;
	private Pattern titlePattern = Pattern.compile("board:\\n\\stitle: (.+)\\n");
	// Why \\d isn't use because to throw format error.
	private Pattern lanePattern = Pattern.compile("- id: (.+)\\n" +
			"\\s+status: (.+)\\n" +
			"\\s+x: (.+)\\n" +
			"\\s+y: (.+)\\n" +
			"\\s+width: (.+)\\n" +
			"\\s+height: (.+)\\n"
			);

	public String modelToText(Board board) {
		if (board == null)
			throw new IllegalArgumentException("board is null");

		StringBuilder builder = new StringBuilder();
		builder.append(format("board:\n" +
				"	title: %s\n", board.getTitle()));
		Lane[] lanes = board.getLanes();
		if (lanes == null || lanes.length == 0)
			return builder.toString();
		builder.append("	lanes:\n");
		for (Lane lane : lanes) {
			builder.append(format("		- id: %d\n", lane.getID()));
			builder.append(format("		  status: %s\n", lane.getStatus()));
			builder.append(format("		  x: %d\n", lane.getX()));
			builder.append(format("		  y: %d\n", lane.getY()));
			builder.append(format("		  width: %d\n", lane.getWidth()));
			builder.append(format("		  height: %d\n", lane.getHeight()));
		}
		return builder.toString();
	}

	public Board textToModel(String text) {
		String title = getTitle(text);
		Board board = service.createBoard(title);
		if (board == null)
			throw new BoardFileFormatException(format("Can't parse board from '%s'", text));
		Lane[] lanes = retrieveLanes(board, text);
		if (lanes == null || lanes.length == 0)
			return board;
		for (Lane lane : lanes) {
			board.addLane(lane);
		}
		return board;
	}

	private Lane[] retrieveLanes(Board board, String text) {
		return retrieveLanes(null, board, text);
	}

	private Lane[] retrieveLanes(File file, Board board, String text) {
		Matcher matcher = lanePattern.matcher(text);
		List<Lane> lanes = new ArrayList<Lane>();
		while (matcher.find()) {
			Integer id = transInteger("lane.id", matcher.group(1));
			String status = matcher.group(2);
			Integer x = transInteger("lane.x", matcher.group(3));
			Integer y = transInteger("lane.y", matcher.group(4));
			Integer width = transInteger("lane.width", matcher.group(5));
			Integer height = transInteger("lane.height", matcher.group(6));
			Lane lane = service.createLane(board, status, x, y, width, height);
			String script = loadScript(file, id);
			if (script != null) {
				lane.setScript(script);
			}
			lanes.add(lane);
		}

		return lanes.toArray(new Lane[] {});
	}

	private String loadScript(File file, Integer id) {
		return null;
	}

	private Integer transInteger(String key, String value) {
		try {
			Integer result = Integer.valueOf(value);
			return result;
		} catch (NumberFormatException e) {
			throw new BoardFileFormatException(format(
					"%s's value:\"%s\" is illegal.The value needs Number.Please check.", key, value));
		}
	}

	private String getTitle(String text) {
		Matcher matcher = titlePattern.matcher(text);
		boolean found = matcher.find();
		String title = "";
		if (found) {
			title = matcher.group(1);
		}
		return title;
	}

	void setKanbanService(KanbanService service) {
		this.service = service;
	}

}
