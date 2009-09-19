package org.kompiro.jamcircle.debug;

import org.eclipse.core.runtime.IStatus;

public class StandardOutputHandler implements IStatusHandler {
	public void displayStatus(String title, IStatus status) {
		System.out.println(status);
	}

	public void fail(IStatus status, boolean informUser) {
		status.getException().printStackTrace();
	}

	public void info(String message) {
		System.out.println(message);
	}
}