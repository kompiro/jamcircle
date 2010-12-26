package org.kompiro.jamcircle.kanban.ui.wizard;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.progress.UIJob;
import org.kompiro.jamcircle.kanban.boardtemplate.KanbanBoardTemplate;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.*;
import org.kompiro.jamcircle.kanban.ui.util.WorkbenchUtil;

public class BoardNewWizard extends Wizard implements INewWizard {
	private BoardNewWizardPage page;
	private KanbanService kanbanService;

	public BoardNewWizard() {
		super();
		kanbanService = KanbanUIActivator.getDefault().getKanbanService();
		setNeedsProgressMonitor(true);
	}

	public void addPages() {
		page = new BoardNewWizardPage(kanbanService.getKanbanBoardTemplates());
		addPage(page);
	}

	public boolean performFinish() {
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				monitor.beginTask(Messages.BoardNewWizard_create_board_task_message, 10);
				final Board board = kanbanService.createBoard(page.getBoardTitle());
				monitor.internalWorked(3);
				KanbanBoardTemplate initializer = page.getInitializer();
				initializer.initialize(board);
				UIJob job = new UIJob(Messages.BoardNewWizard_create_ui_job_title) {
					public org.eclipse.core.runtime.IStatus runInUIThread(IProgressMonitor monitor) {
						KanbanView view = WorkbenchUtil.findKanbanView();
						if (view != null)
							view.setContents(board, monitor);
						return Status.OK_STATUS;
					};
				};
				job.schedule();
				monitor.done();
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), Messages.BoardNewWizard_error_title, realException.getMessage());
			return false;
		}
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}
}