package org.kompiro.jamcircle.debug;

import org.eclipse.core.runtime.IStatus;

public interface IStatusHandler {

	void fail(IStatus status, boolean informUser);

	void displayStatus(String title, IStatus status);

	void info(String message);

}
