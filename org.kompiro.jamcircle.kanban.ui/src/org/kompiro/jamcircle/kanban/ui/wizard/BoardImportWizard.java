package org.kompiro.jamcircle.kanban.ui.wizard;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.service.BoardConverter;
import org.kompiro.jamcircle.kanban.ui.KanbanUIContext;
import org.kompiro.jamcircle.kanban.ui.Messages;
import org.kompiro.jamcircle.kanban.ui.internal.OpenBoardRunnableWithProgress;

public class BoardImportWizard extends Wizard implements IImportWizard {

	class WizardContainerDelegator {

		boolean run(Board board) {
			final OpenBoardRunnableWithProgress runnable = new OpenBoardRunnableWithProgress(board);
			try {
				getContainer().run(false, false, runnable);
			} catch (InvocationTargetException e) {
				MessageDialog
						.openError(getShell(), Messages.BoardImportWizard_error_message, e.getCause()
								.getMessage());
				return false;
			} catch (InterruptedException e) {
				// this statement hasn't call.
			}
			return true;
		}

	}

	private BoardImportWizardPage page;
	private BoardConverter boardConverter = KanbanUIContext.getDefault().getBoardConverter();
	private WizardContainerDelegator delegator = new WizardContainerDelegator();

	public BoardImportWizard() {
		setWindowTitle(Messages.BoardImportWizard_title);
		page = new BoardImportWizardPage();
		addPage(page);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	public boolean performFinish() {
		File file = page.getFile();
		Board board;
		try {
			board = boardConverter.load(file);
		} catch (Exception e) {
			MessageDialog.openError(getShell(), Messages.BoardImportWizard_error_message, e.getMessage());
			return false;
		}
		delegator.run(board);
		return true;
	}

	public void setConverter(BoardConverter boardConverter) {
		this.boardConverter = boardConverter;
	}

	public void setDelegator(WizardContainerDelegator delegator) {
		this.delegator = delegator;
	}

}
