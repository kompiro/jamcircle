package org.kompiro.jamcircle.kanban.service.internal;

import static java.lang.String.format;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.*;

import org.kompiro.jamcircle.kanban.internal.util.StreamUtil;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.service.BoardConverter;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.service.exception.BoardFileFormatException;
import org.kompiro.jamcircle.scripting.ScriptTypes;

public class BoardConverterImpl implements BoardConverter {

	private static final String EXTENSION_OF_YAML = ".yml";

	private static final String BASE_NAME_OF_BOARD = "board";

	private static final String CONTAINER_OF_LANES = "lanes/";

	private static final String CONTAINER_OF_LANE_ICONS = CONTAINER_OF_LANES + "icons/";

	private static final Pattern iconPathPattern = Pattern.compile(".*/" + CONTAINER_OF_LANE_ICONS + "(.*)");

	private static final Pattern iconNamePattern = Pattern.compile("(\\d+)_(.+)");

	private static final String EXTENSION_OF_RUBY = ".rb";

	private static final String EXTENSION_OF_JAVASCRIPT = ".js";

	private static final Pattern titlePattern = Pattern.compile("board:\\n\\stitle: (.+)\\n");
	// Why \\d isn't use because to throw format error.
	private static final Pattern lanePattern = Pattern.compile("- id: (.+)\\n" +
			"\\s+status: (.+)\\n" +
			"\\s+x: (.+)\\n" +
			"\\s+y: (.+)\\n" +
			"\\s+width: (.+)\\n" +
			"\\s+height: (.+)\\n"
			);

	private KanbanService service;

	public BoardConverterImpl() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.kompiro.jamcircle.kanban.service.internal.BoardConverter#dump(java
	 * .io.File, org.kompiro.jamcircle.kanban.model.Board)
	 */
	public void dump(File file, Board board) {
		ZipOutputStream stream = null;
		try {
			stream = new ZipOutputStream(new FileOutputStream(file));
			String containerName = getContainerName(file);
			createBoardYmlEntry(board, stream, containerName);
			createLanesIconEntry(board, stream, containerName);
			createBoardScriptEntry(board, stream, containerName, BASE_NAME_OF_BOARD);
			createLanesScriptEntry(board, stream, containerName);
		} catch (IOException e) {
		} finally {
			try {
				if (stream != null)
					stream.close();
			} catch (IOException e) {
			}
		}
	}

	private void createLanesScriptEntry(Board board, ZipOutputStream stream, String containerName) {
		Lane[] lanes = board.getLanes();
		if (lanes == null || lanes.length == 0)
			return;
		for (Lane lane : lanes) {
			String fileName = getLaneScriptFileName(lane);
			createScriptEntity(stream, lane.getScript(), lane.getScriptType(), containerName + CONTAINER_OF_LANES,
					fileName);
		}
	}

	private void createIconEntity(ZipOutputStream stream, File customIcon, String containerName, String fileName) {
		ZipEntry entry = new ZipEntry(containerName + fileName);
		try {
			stream.putNextEntry(entry);
			InputStream inputStream = new BufferedInputStream(new FileInputStream(customIcon));
			int len;
			byte[] buf = new byte[8192];
			while ((len = inputStream.read(buf)) != -1) {
				stream.write(buf, 0, len);
			}
		} catch (IOException e) {
		}
	}

	private String getLaneIconFileName(Lane lane) {
		return format("%d_%s", lane.getID(), lane.getCustomIcon().getName());
	}

	private void createLanesIconEntry(Board board, ZipOutputStream stream, String containerName) {
		Lane[] lanes = board.getLanes();
		if (lanes == null || lanes.length == 0)
			return;
		for (Lane lane : lanes) {
			if (lane.getCustomIcon() == null)
				continue;
			String fileName = getLaneIconFileName(lane);
			createIconEntity(stream, lane.getCustomIcon(), containerName + CONTAINER_OF_LANE_ICONS, fileName);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.kompiro.jamcircle.kanban.service.internal.BoardConverter#load(java
	 * .io.File)
	 */
	public Board load(File file) {
		try {
			ZipFile zip = new ZipFile(file);
			String containerName = getContainerName(file);
			Board board = loadBoard(containerName, zip);
			board.save();
			return board;
		} catch (ZipException e) {
		} catch (IOException e) {
		}

		return null;
	}

	private Board loadBoard(String containerName, ZipFile zip) throws IOException {
		String boardFileName = BASE_NAME_OF_BOARD + EXTENSION_OF_YAML;
		ZipEntry entry = zip.getEntry(containerName + boardFileName);

		InputStream stream = zip.getInputStream(entry);
		String boardText = StreamUtil.readFromStream(stream);
		Board board = textToModel(boardText);
		loadCustomIcon(board, containerName, zip);
		loadJRubyScript(board, containerName, zip);
		if (board.getScriptType() == null) {
			loadJavaScriptScript(board, containerName, zip);
		}
		loadLaneScript(board.getLanes(), containerName, zip);
		return board;
	}

	private void loadCustomIcon(Board board, String containerName, ZipFile zip) {
		Lane[] lanes = board.getLanes();
		if (lanes == null)
			return;
		Enumeration<? extends ZipEntry> entries = zip.entries();
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			String name = entry.getName();
			Matcher iconMatcher = iconPathPattern.matcher(name);
			if (iconMatcher.matches()) {
				String iconName = iconMatcher.group(1);
				Matcher nameMatcher = iconNamePattern.matcher(iconName);
				if (nameMatcher.matches()) {
					String iconId = nameMatcher.group(1);
					String fileName = nameMatcher.group(2);
					for (Lane lane : lanes) {
						if (iconId.equals(String.valueOf(lane.getID()))) {
							File icon = null;
							byte[] buf = new byte[8192];
							FileOutputStream outStream = null;
							BufferedInputStream inputStream = null;
							try {
								icon = new File(System.getProperty("java.io.tmpdir"), fileName);
								outStream = new FileOutputStream(icon);
								inputStream = new BufferedInputStream(zip.getInputStream(entry));
								int len;
								while ((len = inputStream.read(buf)) != -1) {
									outStream.write(buf, 0, len);
								}
								outStream.flush();
							} catch (IOException e) {
							}
							if (inputStream != null) {
								try {
									inputStream.close();
								} catch (IOException e) {
								}
							}
							if (outStream != null) {
								try {
									outStream.close();
								} catch (IOException e) {
								}
							}
							if (icon != null) {
								lane.setCustomIcon(icon);
							}
						}
					}
				}
			}
		}
	}

	private void loadLaneScript(Lane[] lanes, String containerName, ZipFile zip) throws IOException {
		if (lanes == null || lanes.length == 0)
			return;
		for (Lane lane : lanes) {
			loadLaneJRubyScript(containerName, zip, lane);
			if (lane.getScriptType() != null) {
				continue;
			}
			loadLaneJavaScript(containerName, zip, lane);
		}
	}

	private void loadLaneJRubyScript(String containerName, ZipFile zip, Lane lane) throws IOException {
		String loadPath = containerName + CONTAINER_OF_LANES + getLaneScriptFileName(lane) + EXTENSION_OF_RUBY;
		ZipEntry entry = zip.getEntry(loadPath);
		if (entry != null) {
			InputStream stream = zip.getInputStream(entry);
			String script = StreamUtil.readFromStream(stream);
			lane.setScript(script);
			lane.setScriptType(ScriptTypes.JRuby);
			lane.save();
		}
	}

	private void loadLaneJavaScript(String containerName, ZipFile zip, Lane lane) throws IOException {
		String loadPath = containerName + CONTAINER_OF_LANES + getLaneScriptFileName(lane) + EXTENSION_OF_JAVASCRIPT;
		ZipEntry entry = zip.getEntry(loadPath);
		if (entry != null) {
			InputStream stream = zip.getInputStream(entry);
			String script = StreamUtil.readFromStream(stream);
			lane.setScript(script);
			lane.setScriptType(ScriptTypes.JavaScript);
		}
	}

	private void loadJavaScriptScript(Board board, String containerName, ZipFile zip) throws IOException {
		ScriptTypes scriptType = ScriptTypes.JavaScript;
		String scriptName = BASE_NAME_OF_BOARD + EXTENSION_OF_JAVASCRIPT;
		loadScript(board, containerName, zip, scriptType, scriptName);
	}

	private void loadJRubyScript(Board board, String containerName, ZipFile zip) throws IOException {
		ScriptTypes scriptType = ScriptTypes.JRuby;
		String scriptName = BASE_NAME_OF_BOARD + EXTENSION_OF_RUBY;
		loadScript(board, containerName, zip, scriptType, scriptName);
	}

	private void loadScript(Board board, String containerName, ZipFile zip, ScriptTypes scriptType, String scriptName)
			throws IOException {
		ZipEntry entry = zip.getEntry(containerName + scriptName);
		if (entry != null) {
			InputStream stream = zip.getInputStream(entry);
			String script = StreamUtil.readFromStream(stream);
			if (script != null) {
				board.setScript(script.trim());
				board.setScriptType(scriptType);
			}
		}
	}

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

	private void createBoardYmlEntry(Board board, ZipOutputStream stream, String containerName) {
		ZipEntry boardEntry = new ZipEntry(containerName + BASE_NAME_OF_BOARD + EXTENSION_OF_YAML);
		try {
			stream.putNextEntry(boardEntry);
			String text = modelToText(board);
			stream.write(text.getBytes(), 0, text.getBytes().length);
		} catch (IOException e) {
		}
	}

	private void createBoardScriptEntry(Board board, ZipOutputStream stream, String containerName, String fileName) {
		String script = board.getScript();
		ScriptTypes scriptType = board.getScriptType();
		createScriptEntity(stream, script, scriptType, containerName, fileName);
	}

	private void createScriptEntity(ZipOutputStream stream, String script, ScriptTypes scriptType,
			String containerName, String fileName) {
		if (scriptType == null || script == null || script.equals(""))
			return;
		switch (scriptType) {
		case JRuby:
			fileName = fileName + EXTENSION_OF_RUBY;
			break;
		case JavaScript:
			fileName = fileName + EXTENSION_OF_JAVASCRIPT;
			break;
		}
		ZipEntry entry = new ZipEntry(containerName + fileName);
		try {
			stream.putNextEntry(entry);
			stream.write(script.getBytes(), 0, script.getBytes().length);
		} catch (IOException e) {
		}
	}

	private String getContainerName(File file) {
		String name = file.getName();
		String containerName = name.substring(0, name.lastIndexOf('.')) + "/";
		return containerName;
	}

	private Lane[] retrieveLanes(Board board, String text) {
		return retrieveLanes(null, board, text);
	}

	private Lane[] retrieveLanes(File file, Board board, String text) {
		Matcher matcher = lanePattern.matcher(text);
		List<Lane> lanes = new ArrayList<Lane>();
		while (matcher.find()) {
			@SuppressWarnings("unused")
			Integer id = transInteger("lane.id", matcher.group(1));
			String status = matcher.group(2);
			Integer x = transInteger("lane.x", matcher.group(3));
			Integer y = transInteger("lane.y", matcher.group(4));
			Integer width = transInteger("lane.width", matcher.group(5));
			Integer height = transInteger("lane.height", matcher.group(6));
			Lane lane = service.createLane(board, status, x, y, width, height);
			lanes.add(lane);
		}

		return lanes.toArray(new Lane[] {});
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

	private String getLaneScriptFileName(Lane lane) {
		return format("%d_%s", lane.getID(), lane.getStatus());
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

	public void setKanbanService(KanbanService service) {
		this.service = service;
	}

}
