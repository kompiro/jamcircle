package org.kompiro.jamcircle.storage.service;

import org.eclipse.core.runtime.IProgressMonitor;

public interface StorageChageListener{
	public void changedStorage(IProgressMonitor monitor);
	public Integer getPriority();
}
