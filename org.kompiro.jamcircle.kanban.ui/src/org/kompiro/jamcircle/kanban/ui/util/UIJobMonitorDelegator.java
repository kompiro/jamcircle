package org.kompiro.jamcircle.kanban.ui.util;

import org.eclipse.core.runtime.*;
import org.eclipse.ui.progress.UIJob;
import org.kompiro.jamcircle.kanban.ui.KanbanUIActivator;

public class UIJobMonitorDelegator implements
		IMonitorDelegator {
	private String message;
	private UIJob job;

	public UIJobMonitorDelegator(String message) {
		this.message = message;
	}

	public void run(final MonitorRunnable runner) {
		job = new UIJob(message) {

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				runner.setMonitor(monitor);
				try {
					runner.run();
				} catch (Exception e) {
					return KanbanUIActivator.createErrorStatus(e);
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	public void join() throws InterruptedException {
		if (job != null && job.getThread() != Thread.currentThread()) {
			job.join();
		}
	}
}
