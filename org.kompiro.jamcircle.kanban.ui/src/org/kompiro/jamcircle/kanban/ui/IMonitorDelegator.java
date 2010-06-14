package org.kompiro.jamcircle.kanban.ui;

import org.eclipse.core.runtime.IProgressMonitor;

public interface IMonitorDelegator {

	void run(MonitorRunnable runner);
	
	public abstract class MonitorRunnable implements Runnable {
		protected  IProgressMonitor monitor;

		public MonitorRunnable(IProgressMonitor monitor) {
			this.monitor = monitor;
		}

		public void setMonitor(IProgressMonitor monitor) {
			this.monitor = monitor;
		}

	}

}
