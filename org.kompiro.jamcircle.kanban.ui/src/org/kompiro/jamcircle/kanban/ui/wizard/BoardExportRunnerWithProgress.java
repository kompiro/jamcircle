package org.kompiro.jamcircle.kanban.ui.wizard;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.service.BoardConverter;
import org.kompiro.jamcircle.kanban.ui.KanbanUIContext;
import org.kompiro.jamcircle.kanban.ui.Messages;

public class BoardExportRunnerWithProgress implements IRunnableWithProgress {
	private BoardConverter boardConverter = KanbanUIContext.getDefault().getBoardConverter();
	private File file;
	private Board board;

	public BoardExportRunnerWithProgress() {
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException {
		monitor.beginTask(Messages.CSVExportWizard_export_task_name, 10);
		boardConverter.dump(file, board);
		monitor.done();
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public void setFile(File file) {
		this.file = file;
	}

}