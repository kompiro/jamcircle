package org.kompiro.jamcircle.storage.model;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.kompiro.jamcircle.storage.service.internal.StorageAccessRule;

public class DefaultExecutorHandle implements ExecutorHandler {

	public void handle(final Runnable runtime) {
		Job job = new Job("save") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				runtime.run();
				return Status.OK_STATUS;
			}
		};
		ISchedulingRule rule = new StorageAccessRule();
		job.setRule(rule);
		job.setUser(true);
		job.schedule();
	}
}