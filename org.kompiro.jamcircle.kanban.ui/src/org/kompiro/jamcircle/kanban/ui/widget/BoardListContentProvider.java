package org.kompiro.jamcircle.kanban.ui.widget;

import static java.lang.String.format;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.ui.Messages;

public class BoardListContentProvider implements
		IStructuredContentProvider {

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public Object[] getElements(Object inputElement) {
		if (!(inputElement instanceof Board[])) {
			String exceptionMessage = format(
							Messages.BoardListTableViewer_illegal_argument_error_message,
							inputElement);
			throw new IllegalArgumentException(exceptionMessage);
		}
		Board[] boards = (Board[]) inputElement;
		BoardWrapper[] wrappers = new BoardWrapper[boards.length];
		for (int i = 0; i < boards.length; i++) {
			wrappers[i] = new BoardWrapper(boards[i], i % 2 == 0);
		}
		return wrappers;
	}

}