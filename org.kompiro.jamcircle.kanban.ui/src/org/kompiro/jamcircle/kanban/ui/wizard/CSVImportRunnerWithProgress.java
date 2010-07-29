package org.kompiro.jamcircle.kanban.ui.wizard;

import static org.kompiro.jamcircle.kanban.ui.wizard.CSVImportWizard.TYPE_BOARD;
import static org.kompiro.jamcircle.kanban.ui.wizard.CSVImportWizard.TYPE_CARD;
import static org.kompiro.jamcircle.kanban.ui.wizard.CSVImportWizard.TYPE_LANE;
import static org.kompiro.jamcircle.kanban.ui.wizard.CSVImportWizard.TYPE_USER;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.KanbanUIContext;
import org.kompiro.jamcircle.kanban.ui.Messages;

public class CSVImportRunnerWithProgress implements IRunnableWithProgress {

	private String path;
	private String type;
	private KanbanService kanbanService = KanbanUIContext.getDefault().getKanbanService();

	public CSVImportRunnerWithProgress() {
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException {
		if (path == null || type == null) {
			Throwable target = new IllegalArgumentException();
			throw new InvocationTargetException(target);
		}
		monitor.beginTask(Messages.CSVImportWizard_import_task_name, 10);
		File importFile = new File(path);
		if (TYPE_CARD.equals(type)) {
			kanbanService.importCards(importFile);
		} else if (TYPE_BOARD.equals(type)) {
			kanbanService.importBoards(importFile);
		} else if (TYPE_LANE.equals(type)) {
			kanbanService.importLanes(importFile);
		} else if (TYPE_USER.equals(type)) {
			kanbanService.importUsers(importFile);
		}
		monitor.done();
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setKanbanService(KanbanService kanbanService) {
		this.kanbanService = kanbanService;
	}

}
