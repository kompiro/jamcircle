package org.kompiro.jamcircle.kanban.ui.widget;

import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.ui.Messages;

public class BoardWrapper implements TableListWrapper {
	private Board board;
	private boolean even;

	BoardWrapper(Board board, boolean even) {
		if (board == null) {
			throw new IllegalArgumentException(Messages.BoardListTableViewer_board_null_error_message);
		}
		this.board = board;
		this.even = even;
	}

	public Board getBoard() {
		return board;
	}

	public boolean isEven() {
		return even;
	}

	@Override
	public String toString() {
		return board.toString();
	}
}