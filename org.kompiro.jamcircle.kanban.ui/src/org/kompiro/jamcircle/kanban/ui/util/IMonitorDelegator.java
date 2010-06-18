package org.kompiro.jamcircle.kanban.ui.util;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

public interface IMonitorDelegator {

	public class DirectExecute implements IMonitorDelegator {

		public void run(MonitorRunnable runner) {
			runner.setMonitor(new NullProgressMonitor());
			runner.run();
		}

	}

	void run(MonitorRunnable runner);
	
	public abstract class MonitorRunnable implements Runnable {
		protected  IProgressMonitor monitor;

		public MonitorRunnable() {
		}

		public void setMonitor(IProgressMonitor monitor) {
			this.monitor = monitor;
		}

	}

}
