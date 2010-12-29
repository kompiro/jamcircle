package org.kompiro.jamcircle.kanban.ui.widget;

import static java.lang.String.format;
import static org.kompiro.jamcircle.kanban.ui.widget.WidgetConstants.KEY_OF_DATA_ID;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.ui.Messages;

public class BoardListTableWidget {
	private TableViewer viewer;

	public BoardListTableWidget(Composite comp) {
		viewer = new TableViewer(comp, SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);

		viewer.setContentProvider(new BoardListContentProvider());
		Table table = viewer.getTable();
		GridDataFactory.fillDefaults().grab(true, true).hint(400, 400).applyTo(table);
		createIdColumn();
		createStatusColumn();

		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setData(KEY_OF_DATA_ID, BoardListTableViewer.ID_BOARD_LIST);

	}

	private void createIdColumn() {
		TableViewerColumn idColumn = new TableViewerColumn(viewer, SWT.LEAD);
		idColumn.getColumn().setText(Messages.BoardListTableViewer_id_label);
		idColumn.getColumn().setWidth(40);
		idColumn.setLabelProvider(new TableListColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return String.valueOf(getBoard(element).getID());
			}

		});
		ColumnViewerSorter cSorter = new ColumnViewerSorter(viewer, idColumn) {
			protected int doCompare(Viewer viewer, Object e1, Object e2) {
				Board board1 = getBoard(e1);
				Board board2 = getBoard(e2);
				return board1.getID() - board2.getID();
			}
		};
		cSorter.setSorter(cSorter, ColumnViewerSorter.ASC);

	}

	private void createStatusColumn() {
		TableViewerColumn titleColumn = new TableViewerColumn(viewer, SWT.LEAD);
		titleColumn.getColumn().setText(Messages.BoardListTableViewer_title_label);
		titleColumn.getColumn().setWidth(360);
		titleColumn.setLabelProvider(new TableListColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return getBoard(element).getTitle();
			}
		});
		new ColumnViewerSorter(viewer, titleColumn) {
			protected int doCompare(Viewer viewer, Object e1, Object e2) {
				Board board1 = getBoard(e1);
				Board board2 = getBoard(e2);
				String title1 = board1.getTitle() == null ? "" : board1.getTitle(); //$NON-NLS-1$
				return title1.compareToIgnoreCase(board2.getTitle());
			}
		};
	}

	private Board getBoard(Object element) {
		if (!(element instanceof BoardWrapper)) {
			String message = format("element is '%s'", element.toString());
			throw new IllegalArgumentException(message);
		}
		return ((BoardWrapper) element).getBoard();
	}

	public void setInput(Board[] boards) {
		this.viewer.setInput(boards);
		this.viewer.getTable().select(0);
	}

	public Table getTable() {
		return this.viewer.getTable();
	}

	public Board getBoard() {
		ISelection selection = this.viewer.getSelection();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;
			return (Board) getBoard(sel.getFirstElement());
		}
		return null;
	}

}