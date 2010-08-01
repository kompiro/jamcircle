package org.kompiro.jamcircle.kanban.ui.wizard;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.service.BoardConverter;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.KanbanUIContext;
import org.kompiro.jamcircle.kanban.ui.Messages;
import org.kompiro.jamcircle.kanban.ui.widget.MessageDialogHelper;

public class BoardExportWizard extends Wizard implements IExportWizard {

	private KanbanService kanbanService = KanbanUIContext.getDefault().getKanbanService();
	private MessageDialogHelper helper = new MessageDialogHelper();
	private BoardExportWizardPage page;
	private BoardExportRunnerWithProgress runnable = new BoardExportRunnerWithProgress();

	public BoardExportWizard() {
		setWindowTitle(Messages.BoardExportWizard_title);
		page = new BoardExportWizardPage();
		addPage(page);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		page.setBoards(kanbanService.findAllBoard());
	}

	@Override
	public boolean performFinish() {
		Board board = page.getBoard();
		File target = new File(page.getFile(), board.getTitle() + BoardConverter.BOARD_FORMAT_FILE_EXTENSION_NAME);
		runnable.setBoard(board);
		runnable.setFile(target);
		try {
			getContainer().run(true, false, runnable);
		} catch (InvocationTargetException e) {
			helper.openError(getShell(), "ボードをエクスポート中にエラーが発生しました。", e);
			return false;
		} catch (InterruptedException e) {
			// cancel case
			return false;
		}
		return true;
	}

	public void setRunnable(BoardExportRunnerWithProgress runnable) {
		this.runnable = runnable;
	}

	void setHelper(MessageDialogHelper helper) {
		this.helper = helper;
	}

	void setKanbanService(KanbanService kanbanService) {
		this.kanbanService = kanbanService;
	}

}
