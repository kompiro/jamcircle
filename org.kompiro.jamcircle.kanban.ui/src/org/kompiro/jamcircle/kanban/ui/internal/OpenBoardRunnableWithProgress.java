package org.kompiro.jamcircle.kanban.ui.internal;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.ui.KanbanView;
import org.kompiro.jamcircle.kanban.ui.util.WorkbenchUtil;

public class OpenBoardRunnableWithProgress implements
		IRunnableWithProgress {
	private Board board;

	public OpenBoardRunnableWithProgress(Board board){
		this.board = board;
	}
	
	public void run(IProgressMonitor monitor)	throws InvocationTargetException, InterruptedException {
		monitor.beginTask("change board", 21);
		monitor.subTask("get KanbanView");
		KanbanView view = WorkbenchUtil.findKanbanView();
		if(view == null) throw new IllegalStateException("can't find KanbanView");
		monitor.internalWorked(3);
		view.setContents(board,monitor);
		monitor.done();
	}

}
