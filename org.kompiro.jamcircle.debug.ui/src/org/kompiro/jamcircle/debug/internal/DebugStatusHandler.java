package org.kompiro.jamcircle.debug.internal;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.statushandlers.AbstractStatusHandler;
import org.eclipse.ui.statushandlers.StatusAdapter;

public class DebugStatusHandler extends AbstractStatusHandler {

	public DebugStatusHandler() {
	}

	@Override
	public void handle(StatusAdapter statusAdapter, int style) {
		System.out.println("DebugStatusHandler.handle() is called.");
		IStatus status = statusAdapter.getStatus();
		if(status != null && status.getSeverity() == IStatus.ERROR){
			Throwable e = status.getException();
			if(e != null){
				e.printStackTrace();
			}
		}
	}

}
