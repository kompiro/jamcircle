package abbot.swt.utilities;

import java.util.Formatter;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

public class EventLogger implements Listener {

	public static EventLogger add(final Display display, final EventLogger logger) {
		if (isOnDisplayThread(display)) {
			for (int eventType = EventFormattable.MinEventType; eventType <= EventFormattable.MaxEventType; eventType++) {
				display.addFilter(eventType, logger);
			}
		} else {
			display.syncExec(new Runnable() {
				public void run() {
					add(display, logger);
				}
			});
		}
		return logger;
	}

	public static void remove(final Display display, final EventLogger logger) {
		if (isOnDisplayThread(display)) {
			for (int eventType = EventFormattable.MinEventType; eventType <= EventFormattable.MaxEventType; eventType++) {
				display.removeFilter(eventType, logger);
			}
		} else {
			display.syncExec(new Runnable() {
				public void run() {
					remove(display, logger);
				}
			});
		}
	}

	public static EventLogger add(final Widget widget, final EventLogger logger) {
		Display display = widget.getDisplay();
		if (isOnDisplayThread(display)) {
			for (int eventType = EventFormattable.MinEventType; eventType <= EventFormattable.MaxEventType; eventType++) {
				widget.addListener(eventType, logger);
			}
		} else {
			display.syncExec(new Runnable() {
				public void run() {
					add(widget, logger);
				}
			});
		}
		return logger;
	}

	public static void remove(final Widget widget, final EventLogger logger) {
		Display display = widget.getDisplay();
		if (isOnDisplayThread(display)) {
			for (int eventType = EventFormattable.MinEventType; eventType <= EventFormattable.MaxEventType; eventType++) {
				widget.removeListener(eventType, logger);
			}
		} else {
			display.syncExec(new Runnable() {
				public void run() {
					remove(widget, logger);
				}
			});
		}
	}

	private static boolean isOnDisplayThread(Display display) {
		return display.getThread() == Thread.currentThread();
	}

	private final EventFormatter formatter;

	public EventLogger(EventFormatter formatter) {
		this.formatter = formatter;
	}

	public EventLogger(Formatter formatter) {
		this(new EventFormatter(formatter));
	}

	public EventLogger(Appendable appendable) {
		this(new EventFormatter(appendable));
	}

	public EventLogger() {
		this(System.err);
	}

	/**
	 * Default implementation always returns <code>true</code>.
	 */
	protected boolean isSignificant(Event event) {
		return true;
	}

	public void handleEvent(Event event) {
		if (isSignificant(event))
			formatter.format("<%s\n", event);
	}

}
