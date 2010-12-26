package org.kompiro.jamcircle.storage.service.internal;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

public class StorageAccessRule implements ISchedulingRule {
	public boolean isConflicting(ISchedulingRule rule) {
		return rule == this;
	}

	public boolean contains(ISchedulingRule rule) {
		return rule == this;
	}
}