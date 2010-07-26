package org.kompiro.jamcircle.kanban.service;

import java.io.File;

import org.kompiro.jamcircle.kanban.model.Board;

/**
 * This class provides to dump from board model to board file format,
 * and to load from board file to board model.
 * 
 * The file format is zip. Some files are frozen inside.
 * 
 * <pre>
 * /the_file_name
 *  |-board.yml
 *  |-board.js (1)
 *  /-lanes
 *   |-1_todo.js (2)
 *   |-2_doing.js (3)
 * </pre>
 * 
 * (1) board's script file.<br>
 * (2) lane's script file.The file name shows lane's id and summary.The file's
 * extension is js or rb)<br>
 * (3) similar above file.<br>
 */
public interface BoardConverter {

	public static final String BOARD_FORMAT_FILE_EXTENSION_NAME = ".zip";

	/**
	 * dump board model to the file
	 * 
	 * @param file
	 *            target file.
	 * @param board
	 *            target board.
	 */
	public void dump(File file, Board board);

	/**
	 * load from board file to the model
	 * 
	 * @param file
	 *            target file
	 * @return board model
	 */
	public Board load(File file);

}