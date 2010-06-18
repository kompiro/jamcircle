package org.kompiro.jamcircle.kanban.ui.util;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.kompiro.jamcircle.kanban.ui.KanbanUIActivator;

public class JobMonitorDelegator implements IMonitorDelegator {
	private String message;

	public JobMonitorDelegator(String message) {
		this.message = message;
	}

	public void run(final MonitorRunnable runner) {
		Job job = new Job(message) {

			@Override
			public IStatus run(IProgressMonitor monitor) {
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
}