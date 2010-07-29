package org.kompiro.jamcircle.kanban.ui.wizard;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.KanbanUIContext;
import org.kompiro.jamcircle.kanban.ui.Messages;

class CSVExportRunnerWithProgress implements IExportRunnableWithProgress {
	private String path;
	private KanbanService kanbanService = KanbanUIContext.getDefault().getKanbanService();

	CSVExportRunnerWithProgress(String path) {
		this.path = path;
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException {
		monitor.beginTask(Messages.CSVExportWizard_export_task_name, 10);
		doExport(path, monitor);
		monitor.done();
	}

	private void doExport(final String path, IProgressMonitor monitor) {
		File exportFile = new File(path);
		File exportCardFile = new File(exportFile, "cards.csv"); //$NON-NLS-1$
		File exportLaneFile = new File(exportFile, "lanes.csv"); //$NON-NLS-1$
		File exportUserFile = new File(exportFile, "users.csv"); //$NON-NLS-1$
		File exportBoardFile = new File(exportFile, "boards.csv"); //$NON-NLS-1$
		monitor.internalWorked(2);
		kanbanService.exportCards(exportCardFile);
		monitor.internalWorked(2);
		kanbanService.exportLanes(exportLaneFile);
		monitor.internalWorked(2);
		kanbanService.exportUsers(exportUserFile);
		monitor.internalWorked(2);
		kanbanService.exportBoards(exportBoardFile);
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setKanbanService(KanbanService kanbanService) {
		this.kanbanService = kanbanService;
	}
}