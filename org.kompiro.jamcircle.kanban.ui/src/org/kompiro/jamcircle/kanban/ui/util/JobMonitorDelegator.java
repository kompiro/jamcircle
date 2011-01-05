package org.kompiro.jamcircle.kanban.ui.util;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.kompiro.jamcircle.kanban.ui.KanbanUIActivator;

public class JobMonitorDelegator implements IMonitorDelegator {
	private String message;
	private Job job;

	public JobMonitorDelegator(String message) {
		this.message = message;
	}

	public void run(final MonitorRunnable runner) {
		job = new Job(message) {

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

	public void join() throws InterruptedException {
		if (job != null && job.getThread() != Thread.currentThread()) {
			job.join();
		}
	}
}