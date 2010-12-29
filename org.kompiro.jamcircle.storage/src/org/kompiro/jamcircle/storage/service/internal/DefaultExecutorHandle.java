package org.kompiro.jamcircle.storage.service.internal;

import org.kompiro.jamcircle.storage.model.ExecutorHandler;

public class DefaultExecutorHandle implements ExecutorHandler {

	public void handle(final Runnable runnable) {
		StorageServiceImpl.STORAGE_ACCESS_EXECUTOR.execute(runnable);
	}
}