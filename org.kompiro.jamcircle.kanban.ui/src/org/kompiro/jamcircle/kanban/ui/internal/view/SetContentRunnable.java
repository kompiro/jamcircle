package org.kompiro.jamcircle.kanban.ui.internal.view;

import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.gef.GraphicalViewer;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.BoardEditPart;
import org.kompiro.jamcircle.kanban.ui.util.IMonitorDelegator.MonitorRunnable;

class SetContentRunnable extends MonitorRunnable {
	private final BoardEditPart boardEditPart;
	private final GraphicalViewer viewer;

	SetContentRunnable(BoardEditPart boardEditPart,
			GraphicalViewer viewer) {
		this.boardEditPart = boardEditPart;
		this.viewer = viewer;
	}

	public void run() {
		SubMonitor sub = SubMonitor.convert(monitor);
		sub.subTask("set viewer");
		viewer.setContents(boardEditPart);
	}
}
