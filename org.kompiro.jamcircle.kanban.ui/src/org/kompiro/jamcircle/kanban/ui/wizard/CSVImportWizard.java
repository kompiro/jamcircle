package org.kompiro.jamcircle.kanban.ui.wizard;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.kompiro.jamcircle.kanban.ui.KanbanUIActivator;
import org.kompiro.jamcircle.kanban.ui.Messages;

public class CSVImportWizard extends Wizard implements IImportWizard {

	static final String TYPE_CARD = "Card"; //$NON-NLS-1$
	static final String TYPE_BOARD = "Board"; //$NON-NLS-1$
	static final String TYPE_LANE = "Lane"; //$NON-NLS-1$
	static final String TYPE_USER = "User"; //$NON-NLS-1$

	private CSVImportPage page;

	public CSVImportWizard() {
		setWindowTitle(Messages.CSVImportWizard_title);
		setNeedsProgressMonitor(true);
		page = new CSVImportPage();
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		final String path = page.getFileText().getText();
		final String type = page.getTypeCombo().getText();
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				monitor.beginTask(Messages.CSVImportWizard_import_task_name, 10);
				File importFile = new File(path);
				if (TYPE_CARD.equals(type)) {
					KanbanUIActivator.getDefault().getKanbanService().importCards(importFile);
				} else if (TYPE_BOARD.equals(type)) {
					KanbanUIActivator.getDefault().getKanbanService().importBoards(importFile);
				} else if (TYPE_LANE.equals(type)) {
					KanbanUIActivator.getDefault().getKanbanService().importLanes(importFile);
				} else if (TYPE_USER.equals(type)) {
					KanbanUIActivator.getDefault().getKanbanService().importUsers(importFile);
				}
				monitor.done();
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), Messages.CSVImportWizard_error_title, realException.getMessage());
			return false;
		}

		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	public void setRunner(IRunnableWithProgress runner) {
	}

}
