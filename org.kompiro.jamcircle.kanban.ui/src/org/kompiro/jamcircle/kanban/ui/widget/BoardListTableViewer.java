package org.kompiro.jamcircle.kanban.ui.widget;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.viewers.StructuredSelection;
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

	public static final String ID_BOARD_LIST = "board_list"; //$NON-NLS-1$
	public static int OPERATIONS = DND.DROP_MOVE;

	private TrashModel trashModel;
	private Table table;
	private BoardListTableWidget widget;

	public BoardListTableViewer(Composite comp) {
		Composite composite = new Composite(comp, SWT.NONE);
		composite.setLayout(new GridLayout());
		widget = new BoardListTableWidget(composite);
		table = widget.getTable();
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				openBoard();
			}
		});

		createMenu();
		Transfer[] types = new Transfer[] { CardObjectTransfer.getTransfer() };
		configurateDragSource(table, types);
		configureDropTarget(table, types);
	}

	private void createMenu() {
		Menu menu = new Menu(table);
		table.setMenu(menu);
		createOpenMenu(menu);
		createEditMenu(menu);
		createDeleteMenu(menu);
	}

	private void createDeleteMenu(Menu menu) {
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText(Messages.BoardListTableViewer_delete_label);
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				Board board = widget.getBoard();
				trashModel.addBoard(board);
				refreshTableViewer();
			}
		});
		Image image = getImage(KanbanImageConstants.DELETE_IMAGE);
		item.setImage(image);
	}

	private void createEditMenu(Menu menu) {
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText(Messages.BoardListTableViewer_edit_label);
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				Board board = widget.getBoard();
				final Shell shell = getShell();
				BoardEditDialog dialog = new BoardEditDialog(shell, board.getTitle(), board.getScript(), board
						.getScriptType());
				int returnCode = dialog.open();
				if (Dialog.OK == returnCode) {
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
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText(Messages.BoardListTableViewer_open_label);
		item.addSelectionListener(new SelectionAdapter() {
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
		if (!Platform.isRunning())
			return null;
		return KanbanUIActivator.getDefault().getImageRegistry().get(image.toString());
	}

	private Shell getShell() {
		return table.getShell();
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
				event.data = widget.getBoard();
			}
		});
		source.setTransfer(types);
	}

	public void setInput(Board[] boards) {
		widget.setInput(boards);
	}

	public void dispose() {
		table.dispose();
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() instanceof Board) {
			refreshTableViewer();
		}
	}

	private void refreshTableViewer() {
		Board[] boards = KanbanUIActivator.getDefault().getKanbanService()
				.findAllBoard();
		widget.setInput(boards);
	}

	public void setTrashModel(TrashModel trashModel) {
		this.trashModel = trashModel;
	}

	private void openBoard() {
		Board board = widget.getBoard();
		if (board == null)
			return;
		IProgressService service = (IProgressService) PlatformUI.getWorkbench().getService(IProgressService.class);
		IRunnableContext context = new ProgressMonitorDialog(getShell());
		try {
			service.runInUI(context, new OpenBoardRunnableWithProgress(board), null);
		} catch (InvocationTargetException ex) {
			KanbanUIStatusHandler
					.fail(ex.getTargetException(), Messages.BoardListTableViewer_open_failed_error_message);
		} catch (InterruptedException ex) {
			KanbanUIStatusHandler.fail(ex, Messages.BoardListTableViewer_open_failed_error_message);
		}
	}

}
