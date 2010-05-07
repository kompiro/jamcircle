
package org.kompiro.jamcircle.kanban.model.mock;

import org.kompiro.jamcircle.kanban.model.Board;

/**
 * This implementation is mock of Icon and isn't able to store any persistence.
 * @author kompiro
 */
public class Icon extends MockGraphicalEntity implements org.kompiro.jamcircle.kanban.model.Icon{
	
	private String classType;
	private String src;
	private Board board;

	public String getClassType() {
		return this.classType;
	}

	public void setClassType(String classType) {
		this.classType = classType;
	}

	public Board getBoard() {
		return this.board;
	}

	public String getSrc() {
		return this.src;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public void setSrc(String src) {
		this.src = src;
	}
	
}