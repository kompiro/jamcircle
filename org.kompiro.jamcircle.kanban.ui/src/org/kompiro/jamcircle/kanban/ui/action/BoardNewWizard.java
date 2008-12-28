package org.kompiro.jamcircle.kanban.ui.action;

import java.lang.reflect.InvocationTargetException;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.kompiro.jamcircle.kanban.boardtemplate.KanbanBoardTemplate;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.KanbanUIActivator;
import org.kompiro.jamcircle.kanban.ui.KanbanView;
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
		page = new BoardNewWizardPage(kanbanService.getKanbanDataInitializers());
		addPage(page);
	}

	public boolean performFinish() {
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				monitor.beginTask("Create New Board...", 10);
				Board board = kanbanService.createBoard(page.getBoardTitle());
				monitor.internalWorked(3);
				KanbanBoardTemplate initializer = page.getInitializer();
				initializer.initialize(board);
				KanbanView view = WorkbenchUtil.findKanbanView();
				if(view != null) view.setContents(board, monitor);
				monitor.done();
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}
}