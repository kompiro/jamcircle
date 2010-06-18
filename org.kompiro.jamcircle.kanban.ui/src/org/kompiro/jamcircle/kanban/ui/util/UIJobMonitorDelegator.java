package org.kompiro.jamcircle.kanban.ui.util;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.progress.UIJob;
import org.kompiro.jamcircle.kanban.ui.KanbanUIActivator;

public class UIJobMonitorDelegator implements
		IMonitorDelegator {
	private String message;
	
	public UIJobMonitorDelegator(String message){
		this.message = message;
	}

	public void run(final MonitorRunnable runner) {
		UIJob job = new UIJob(message){
		
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
}

