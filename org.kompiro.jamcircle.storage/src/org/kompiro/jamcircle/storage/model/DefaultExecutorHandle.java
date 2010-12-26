package org.kompiro.jamcircle.storage.model;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.*;
import org.kompiro.jamcircle.storage.service.internal.StorageAccessRule;

public class DefaultExecutorHandle implements ExecutorHandler {
	private static ILock lock = Job.getJobManager().newLock();

	public void handle(final Runnable runtime) {
		Job job = new Job("save entity") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				runtime.run();
				return Status.OK_STATUS;
			}
		};
		ISchedulingRule rule = new StorageAccessRule();
		job.setRule(rule);
		job.setSystem(true);
		try {
			lock.acquire();
			job.schedule();
		} finally {
			lock.release();
		}
	}
}