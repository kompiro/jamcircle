package org.kompiro.jamcircle.kanban.ui.widget;

import static java.lang.String.format;
import static org.kompiro.jamcircle.kanban.ui.widget.WidgetConstants.*;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.ui.*;
import org.kompiro.jamcircle.kanban.ui.dialog.BoardEditDialog;
import org.kompiro.jamcircle.kanban.ui.internal.OpenBoardRunnableWithProgress;
import org.kompiro.jamcircle.kanban.ui.model.TrashModel;
import org.kompiro.jamcircle.scripting.ScriptTypes;

public class BoardListTableViewer implements PropertyChangeListener {

	public static final String ID_BOARD_LIST = "board_list";

	public final class BoardWrapper implements TableListWrapper {
		private Board board;
		private boolean even;

		public BoardWrapper(Board board, boolean even) {
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

	private final class BoardListContentProvider implements
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

	private TableViewer viewer;
	private TableViewerColumn titleColumn;
	private TableViewerColumn idColumn;
	private TrashModel trashModel;

	public static int OPERATIONS = DND.DROP_MOVE;

	public BoardListTableViewer(Composite comp) {
		Composite composite = new Composite(comp, SWT.NONE);
		composite.setLayout(new GridLayout());
		viewer = new TableViewer(composite, SWT.BORDER| SWT.FULL_SELECTION | SWT.H_SCROLL| SWT.V_SCROLL | SWT.MULTI);

		viewer.setContentProvider(new BoardListContentProvider());
		Table table = viewer.getTable();
		GridDataFactory.fillDefaults().grab(true, true).hint(400, 400).applyTo(table);
		createIdColumn();
		createStatusColumn();

		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setData(KEY_OF_DATA_ID, ID_BOARD_LIST);
		
		table.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				openBoard();
			}
		});
		
		createMenu();
		Transfer[] types = new Transfer[] {CardObjectTransfer.getTransfer()};
		configurateDragSource(table,types);
	 	configureDropTarget(table, types);
	}

	private void createMenu() {
		final Control control = viewer.getControl();
		Menu menu = new Menu(control);
		control.setMenu(menu);
		createOpenMenu(menu);
		createEditMenu(menu);
		createDeleteMenu(menu);
	}

	private void createDeleteMenu(Menu menu) {
		MenuItem item = new MenuItem(menu,SWT.PUSH);
		item.setText(Messages.BoardListTableViewer_delete_label);
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				StructuredSelection selection = (StructuredSelection)viewer.getSelection();
				Board board = getBoard(selection.getFirstElement());
				trashModel.addBoard(board);
				refreshTableViewer();
			}
		});
		Image image = getImage(KanbanImageConstants.DELETE_IMAGE);
		item.setImage(image);
	}

	private void createEditMenu(Menu menu) {
		MenuItem item = new MenuItem(menu,SWT.PUSH);
		item.setText(Messages.BoardListTableViewer_edit_label);
		item.addSelectionListener(new SelectionAdapter(){
			public void widgetDefaultSelected(SelectionEvent e) {
				StructuredSelection selection = (StructuredSelection)viewer.getSelection();
				Board board = getBoard(selection.getFirstElement());
				final Shell shell = getShell();
				BoardEditDialog dialog = new BoardEditDialog(shell,board.getTitle(),board.getScript(),board.getScriptType());
				int returnCode = dialog.open();
				if(Dialog.OK == returnCode){
					String script = dialog.getScript();
					String title = dialog.getTitle();
					ScriptTypes type = dialog.getScriptType();
					board.setScript(script);
					board.setTitle(title);
					board.setScriptType(type);
					board.save();
					refreshTableViewer();
				}
			}
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}
		});
		Image image = getImage(KanbanImageConstants.EDIT_IMAGE);
		item.setImage(image);
	}

	private void createOpenMenu(Menu menu) {
		MenuItem item = new MenuItem(menu,SWT.PUSH);
		item.setText(Messages.BoardListTableViewer_open_label);
		item.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				openBoard();
			}
		});
		Image image = getImage(KanbanImageConstants.OPEN_IMAGE);
		item.setImage(image);
	}

	private Image getImage(KanbanImageConstants image) {
		if(!Platform.isRunning()) return null;
		return KanbanUIActivator.getDefault().getImageRegistry().get(image.toString());
	}

	private Shell getShell() {
		return viewer.getControl().getShell();
	}
	
	private void configureDropTarget(Table table, Transfer[] types) {
		DropTarget target = new DropTarget(table, OPERATIONS);
		target.setTransfer(types);
		target.addDropListener(new DropTargetAdapter() {
			public void drop(DropTargetEvent event) {
				Object data = event.data;
				if (data instanceof StructuredSelection) {
					StructuredSelection selection = (StructuredSelection) data;
					for (Object obj : selection.toList()) {
						// TODO need to implmenet
						KanbanUIStatusHandler.debug("dropped", obj); //$NON-NLS-1$
					}
				}
			}
		});
	}

	private void configurateDragSource(Table table, Transfer[] types) {
		DragSource source = new DragSource(table, OPERATIONS);
		source.addDragListener(new DragSourceListener() {
			public void dragStart(DragSourceEvent event) {
				event.doit = true;
			}

			public void dragFinished(DragSourceEvent event) {
			}

			public void dragSetData(DragSourceEvent event) {
				event.data = viewer.getSelection();
			}
		});
		source.setTransfer(types);
	}

	private void createIdColumn() {
		idColumn = new TableViewerColumn(viewer, SWT.LEAD);
		idColumn.getColumn().setText(Messages.BoardListTableViewer_id_label);
		idColumn.getColumn().setWidth(40);
		idColumn.setLabelProvider(new TableListColumnLabelProvider(){

			@Override
			public String getText(Object element) {
				return String.valueOf(getBoard(element).getID());
			}
			
		});
		ColumnViewerSorter cSorter = new ColumnViewerSorter(viewer,idColumn) {
			protected int doCompare(Viewer viewer, Object e1, Object e2) {
				Board board1 = getBoard(e1);
				Board board2 = getBoard(e2);
				return board1.getID() - board2.getID();
			}
		};
		cSorter.setSorter(cSorter, ColumnViewerSorter.ASC);

	}

	private void createStatusColumn() {
		titleColumn = new TableViewerColumn(viewer, SWT.LEAD);
		titleColumn.getColumn().setText(Messages.BoardListTableViewer_title_label);
		titleColumn.getColumn().setWidth(360);
		titleColumn.setLabelProvider(new TableListColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return getBoard(element).getTitle();
			}
		});
		new ColumnViewerSorter(viewer,titleColumn) {
			protected int doCompare(Viewer viewer, Object e1, Object e2) {
				Board board1 = getBoard(e1);
				Board board2 = getBoard(e2);
				String title1 = board1.getTitle() == null ? "" :board1.getTitle(); //$NON-NLS-1$
				return title1.compareToIgnoreCase(board2.getTitle());
			}
		};
	}

	private Board getBoard(Object element) {
		if(!(element instanceof BoardWrapper)) throw new IllegalArgumentException();
		return ((BoardWrapper)element).board;
	}

	public void setInput(Board[] boards) {
		viewer.setInput(boards);
	}

	public void dispose() {
		viewer.getTable().dispose();
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() instanceof Board) {
			refreshTableViewer();
		}
	}

	private void refreshTableViewer() {
		Board[] boards = KanbanUIActivator.getDefault().getKanbanService()
				.findAllBoard();
		viewer.setInput(boards);
	}


	public void setTrashModel(TrashModel trashModel) {
		this.trashModel = trashModel;
	}

	private void openBoard() {
		ISelection sel = viewer.getSelection();
		if (!(sel instanceof StructuredSelection))return;
		StructuredSelection selection = (StructuredSelection) sel;
		if(selection.size() != 1) return;
		Object obj = selection.getFirstElement();
		if (!(obj instanceof BoardWrapper)) return;
		BoardWrapper wrapper = (BoardWrapper) obj;
		Board board = wrapper.getBoard();
		if(board == null) return;
		IProgressService service = (IProgressService) PlatformUI.getWorkbench().getService(IProgressService.class);
		IRunnableContext context = new ProgressMonitorDialog(viewer.getControl().getShell());
		try {
			service.runInUI(context,new OpenBoardRunnableWithProgress(board),null);
		} catch (InvocationTargetException ex) {
			KanbanUIStatusHandler.fail(ex.getTargetException(), Messages.BoardListTableViewer_open_failed_error_message);
		} catch (InterruptedException ex) {
			KanbanUIStatusHandler.fail(ex, Messages.BoardListTableViewer_open_failed_error_message);
		}
	}

}
