package org.kompiro.jamcircle.kanban.ui.action;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.ui.*;
import org.kompiro.jamcircle.kanban.ui.dialog.BoardEditDialog;
import org.kompiro.jamcircle.kanban.ui.internal.OpenBoardRunnableWithProgress;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;
import org.kompiro.jamcircle.scripting.ScriptTypes;

public class EditBoardAction extends Action{

	private KanbanView view;

	public EditBoardAction(KanbanView kanbanView, ImageRegistry imageRegistry) {
		setText("Edit Board");
		setToolTipText("Open board dialog to edit.");
		setImageDescriptor(KanbanImageConstants.EDIT_IMAGE.getImageDescriptor());
		this.view = kanbanView;
	}
	
	@Override
	public void run() {
		Board board = getBoard();
		Shell shell = getShell();
		BoardEditDialog dialog = new BoardEditDialog(shell,board.getTitle(),board.getScript(),board.getScriptType());
		int returnCode = dialog.open();
		if(Dialog.OK == returnCode){
			modifyBoard(board, dialog);
		}
	}

	private void modifyBoard(Board board, BoardEditDialog dialog) {
		String script = dialog.getScript();
		String title = dialog.getTitle();
		ScriptTypes type = dialog.getScriptType();
		board.setScript(script);
		board.setTitle(title);
		board.setScriptType(type);
		board.save();
		IProgressService service = (IProgressService) PlatformUI.getWorkbench().getService(IProgressService.class);
		IRunnableContext context = new ProgressMonitorDialog(getShell());
		try {
			service.runInUI(context,new OpenBoardRunnableWithProgress(board),null);
		} catch (InvocationTargetException ex) {
			KanbanUIStatusHandler.fail(ex.getTargetException(), "Opening Kanban Board is failed.");
		} catch (InterruptedException ex) {
			KanbanUIStatusHandler.fail(ex, "Opening Kanban Board is failed.");
		}
	}

	private Board getBoard() {
		BoardModel boardModel = (BoardModel) view.getAdapter(Board.class);
		Board board = boardModel.getBoard();
		return board;
	}

	private Shell getShell() {
		return view.getViewSite().getShell();
	}

}
