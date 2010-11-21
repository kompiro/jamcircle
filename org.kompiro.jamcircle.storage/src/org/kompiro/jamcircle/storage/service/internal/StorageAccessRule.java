package org.kompiro.jamcircle.storage.service.internal;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

public class StorageAccessRule implements ISchedulingRule {
	public boolean isConflicting(ISchedulingRule rule) {
		return rule.getClass().equals(StorageAccessRule.class);
	}

	public boolean contains(ISchedulingRule rule) {
		return rule.getClass().equals(StorageAccessRule.class);
	}
}