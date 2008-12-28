package abbot.swt.eclipse.popups;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import abbot.swt.display.DisplayTester;
import abbot.swt.tester.ShellTester;

public class Popups {

	public interface Handler {

		/**
		 * Will be called on a UI thread.
		 */
		boolean canHandle(Event event);

		/**
		 * Will be called on a non-UI thread.
		 */
		void handle(Event event);
	}

	private static final int ANY_MODAL = SWT.APPLICATION_MODAL | SWT.SYSTEM_MODAL
			| SWT.PRIMARY_MODAL;

	private static Set<Handler> handlers;

	private static Listener listener;

	public static synchronized void addHandler(Handler handler) {
		if (handlers == null) {
			handlers = new HashSet<Handler>();
			listener = new Listener() {
				public void handleEvent(Event event) {
					Popups.handleEvent(event);
				}
			};
			DisplayTester.getDefault().addFilter(SWT.Activate, listener);
		}
		handlers.add(handler);
	}

	public static synchronized void removeHandler(Handler handler) {
		if (handlers != null) {
			handlers.remove(handler);
			if (handlers.isEmpty())
				stop();
		}
	}

	public static synchronized void stop() {
		if (handlers != null) {
			DisplayTester.getDefault().removeFilter(SWT.Activate, listener);
			listener = null;
			handlers = null;
		}
	}

	private static synchronized Handler[] getHandlers() {
		if (handlers != null)
			return (Handler[]) handlers.toArray(new Handler[handlers.size()]);
		return null;
	}

	private static void handleEvent(final Event event) {
		if (!DisplayTester.getDefault().isDisplayThread())
			throw new RuntimeException("not on display thread");

		// We care only about visible, modal Shells.
		if (event.widget instanceof Shell) {
			final Shell shell = (Shell) event.widget;
			if (!shell.isDisposed() && (shell.getStyle() & ANY_MODAL) != 0) {

				// Let the first handler that can handle it, um, handle it.
				final Handler handler = getHandler(event);
				if (handler != null) {
					Thread thread = new Thread(getNextThreadName()) {
						public void run() {
							ShellTester.getShellTester().waitVisible(shell, 5000);
							handler.handle(event);
						}
					};
					thread.start();
				}
			}
		}
	}

	private static Handler getHandler(Event event) {
		Handler[] handlers = getHandlers();
		if (handlers != null) {
			for (Handler handler : handlers) {
				if (handler.canHandle(event))
					return handler;
			}
		}
		return null;
	}

	private static int NextThreadNumber;

	private static synchronized String getNextThreadName() {
		return "abbot.swt.popup.handler #" + NextThreadNumber++;
	}

}
