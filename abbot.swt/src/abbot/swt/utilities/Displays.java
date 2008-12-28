package abbot.swt.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.AssertionFailedError;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import abbot.swt.Robot;

/**
 * Provides some fundamental {@link Display}-oriented utility methods that are
 * somewhat higher-level than what {@link Display} itself provides.
 * 
 * @author Gary Johnston
 */
public class Displays {

	/**
	 * @return <code>true</code> if the caller is running on any
	 *         {@link Display}'s {@link Thread}, <code>false</code>
	 *         otherwise.
	 */
	public static boolean isOnDisplayThread() {
		return Display.getCurrent() != null;
	}

	/**
	 * @param display a {@link Display}
	 * @return <code>true</code> if the caller is running on the specified
	 *         {@link Display}'s {@link Thread}, <code>false</code>
	 *         otherwise.
	 */
	public static boolean isOnDisplayThread(Display display) {
		return display.getThread() == Thread.currentThread();
	}

	/**
	 * Gets the default {@link Display}.
	 */
	private static Display getDisplay() {
		return Robot.getDefault().getDisplay();
	}

	/**
	 * Gets all undisposed root {@link Shell}s for the default {@link Display}.
	 */
	public static List getShells() {
		return getShells(getDisplay());
	}

	/**
	 * Gets all undisposed root {@link Shell}s for the specified
	 * {@link Display}.
	 */
	public static List getShells(Display display) {
		Shell[] shells = getShellsArray(display);
		if (shells.length > 0)
			return Arrays.asList(shells);
		return Collections.EMPTY_LIST;
	}

	/**
	 * Gets an array of all undisposed root {@link Shell}s for the specified
	 * {@link Display}.
	 * 
	 * @see #getShells(Display)
	 */
	private static Shell[] getShellsArray(final Display display) {
		if (!display.isDisposed()) {

			// If we're the UI thread then we can call display.getShells()
			// directly.
			if (display.getThread() == Thread.currentThread())
				return display.getShells();

			// We're not the UI thread, so have the UI thread do the call for
			// us.
			return (Shell[]) syncExec(display, new Result() {
				public Object result() {
					return display.getShells();
				}
			});
		}
		return new Shell[0];
	}

	/*
	 * The following methods are not specific to a particular Display.
	 */

	/**
	 * Gets all undisposed {@link Display}s.
	 */
	public static List getDisplays() {
		List displays = new ArrayList();
		for (Thread thread : Threads.all()) {
			Display display = Display.findDisplay(thread);
			if (display != null && !display.isDisposed())
				displays.add(display);
		}
		return displays;
	}

	public static void syncExec(Display display, Runnable runnable) {
		try {
			display.syncExec(runnable);
		} catch (SWTException exception) {
			if (exception.code == SWT.ERROR_FAILED_EXEC
					&& exception.getCause() instanceof AssertionFailedError)
				throw (AssertionFailedError) exception.getCause();
			throw exception;
		}
	}

	public static void syncExec(Runnable runnable) {
		syncExec(getDisplay(), runnable);
	}

	public static interface Result<T> {
		T result();
	}

	/**
	 * See {@link #syncExec(Display, Result)}.
	 */
	private static class Reference<T> {
		public volatile T referent;
	}

	public static <T> T syncExec(Display display, final Result<T> result) {
		final Reference<T> reference = new Reference<T>();
		display.syncExec(new Runnable() {
			public void run() {
				reference.referent = result.result();
			}
		});
		return reference.referent;
	}

	public static interface BooleanResult {
		boolean result();
	}

	public static boolean syncExec(Display display, final BooleanResult result) {
		final boolean[] b = new boolean[1];
		syncExec(display, new Runnable() {
			public void run() {
				b[0] = result.result();
			}
		});
		return b[0];
	}

	public static interface IntResult {
		int result();
	}

	public static int syncExec(Display display, final IntResult result) {
		final int[] i = new int[1];
		syncExec(display, new Runnable() {
			public void run() {
				i[0] = result.result();
			}
		});
		return i[0];
	}

	public static interface CharResult {
		char result();
	}

	public static char syncExec(Display display, final CharResult result) {
		final char[] c = new char[1];
		syncExec(display, new Runnable() {
			public void run() {
				c[0] = result.result();
			}
		});
		return c[0];
	}

	public static interface StringResult extends Result<String> {
		String result();
	}

	public static String syncExec(Display display, final StringResult result) {
		return syncExec(display, (Result<String>) result);
	}

}
