package abbot.swt.display;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;

import abbot.swt.tester.AbstractTester;

/**
 * I think that this class will eventually replace {@link AbstractTester} and {@link abbot.swt.Robot}.
 * 
 * @author Gary Johnston
 */
public class DisplayTester {

	private static DisplayTester Default;

	public static synchronized DisplayTester getDefault() {
		if (Default == null)
			Default = new DisplayTester(Display.getDefault());
		return Default;
	}

	private final Display display;

	public DisplayTester(Display display) {
		if (display == null)
			throw new IllegalArgumentException("display is null");
		this.display = display;
	}
	
	public boolean isDisplayThread() {
		return display.getThread() == Thread.currentThread();
	}

	public void addFilter(final int eventType, final Listener listener) {
		checkNotNull(listener, "listener");
		syncExec(new Runnable() {
			public void run() {
				display.addFilter(eventType, listener);
			}
		});
	}

	public void removeFilter(final int eventType, final Listener listener) {
		checkNotNull(listener, "listener");
		syncExec(new Runnable() {
			public void run() {
				display.removeFilter(eventType, listener);
			}
		});
	}

	private void syncExec(Runnable runnable) {
		checkThread();
		checkDisplay();
		display.syncExec(runnable);
	}

	private void checkThread() {
		if (isDisplayThread())
			throw new RuntimeException("invalid thread");
	}

	private void checkNotNull(Object object, String name) {
		if (object == null)
			throw new IllegalArgumentException(name + "is null");
	}

	private void checkDisplay() {
		if (display.isDisposed())
			throw new RuntimeException("display is null");
	}
}
