package org.kompiro.jamcircle.debug;

import org.eclipse.core.runtime.IProgressMonitor;

public class SysoutProgressMonitor implements IProgressMonitor{
	
	private String taskName;

	public void beginTask(String name, int totalWork) {
		System.out.println(String.format("%s - beginTask[%s]", taskName,name));
	}

	public void done() {
		System.out.println("done!");
	}

	public void internalWorked(double work) {
	}

	public boolean isCanceled() {
		return false;
	}

	public void setCanceled(boolean value) {
	}

	public void setTaskName(String name) {
		taskName = name;
		System.out.println(taskName);
	}

	public void subTask(String name) {
		System.out.println(String.format("%s - subTask[%s]", taskName,name));
	}

	public void worked(int work) {
	}
	
}
