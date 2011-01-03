package org.kompiro.jamcircle.kanban.ui.internal.view;

import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.gef.GraphicalViewer;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;
import org.kompiro.jamcircle.kanban.ui.util.IMonitorDelegator.MonitorRunnable;

class SetContentRunnable extends MonitorRunnable {
	private final BoardModel boardModel;
	private final GraphicalViewer viewer;

	SetContentRunnable(BoardModel boardModel,
			GraphicalViewer viewer) {
		this.boardModel = boardModel;
		this.viewer = viewer;
	}

	public void run() {
		SubMonitor sub = SubMonitor.convert(monitor);
		sub.subTask("set viewer");
		viewer.setContents(boardModel);
	}
}

