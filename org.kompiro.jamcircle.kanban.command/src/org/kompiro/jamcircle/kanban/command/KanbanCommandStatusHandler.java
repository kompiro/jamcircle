package org.kompiro.jamcircle.kanban.command;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.eclipse.core.runtime.*;
import org.kompiro.jamcircle.debug.IStatusHandler;

public class KanbanCommandStatusHandler {

	private static final String DATE_FORMATTER = "yyyy/MM/dd hh:mm:ssSSSS"; //$NON-NLS-1$

	private static final String LINE_BREAK = System.getProperty("line.separator"); //$NON-NLS-1$

	private static final String ID_PLUGIN = KanbanCommandActivator.ID_PLUGIN;

	private static Set<IStatusHandler> handlers = new HashSet<IStatusHandler>();
	private static DateFormat formatter = new SimpleDateFormat(DATE_FORMATTER);

	private static final boolean ENABLE_LOGGING;
	private static final boolean ENABLE_UI_DEBUG;

	static {
		ENABLE_LOGGING = !Platform.isRunning() || Boolean.valueOf(Platform.getDebugOption(ID_PLUGIN + "/debug")); //$NON-NLS-1$
		ENABLE_UI_DEBUG = !Platform.isRunning() || Boolean.valueOf(Platform.getDebugOption(ID_PLUGIN + "/debug/ui")); //$NON-NLS-1$
	}

	public static void addStatusHandler(IStatusHandler handler) {
		handlers.add(handler);
	}

	public static void removeStatusHandler(IStatusHandler handler) {
		handlers.remove(handler);
	}

	private static void log(IStatus status) {
		if (ENABLE_LOGGING) {
			if (Platform.isRunning()) {
				getLog().log(status);
			}
		}
		if (status.getException() != null) {
			for (IStatusHandler handler : handlers) {
				handler.fail(status, true);
			}
		} else {
			String message = String.format("%s %s", formatter.format(new Date()), status.getMessage()); //$NON-NLS-1$
			for (IStatusHandler handler : handlers) {
				handler.info(message);
			}
		}
	}

	private static ILog getLog() {
		KanbanCommandStatusHandler plugin = KanbanCommandActivator.getDefault();
		return plugin.getLog();
	}

	public static void debug(String message, Object... objects) {
		// if(ENABLE_DEBUG_LEVEL){
		message = String.format(message, objects);
		log(new Status(IStatus.WARNING, ID_PLUGIN, IStatus.OK, message, null));
		// }
	}

	public static void debugUI(String message, Object... objects) {
		if (ENABLE_UI_DEBUG) {
			log(new Status(IStatus.WARNING, ID_PLUGIN, IStatus.OK, String.format(message, objects), null));
		}
	}

	public static void info(String message, Object... objects) {
		info(String.format(message, objects), null, false);
	}

	public static void info(String message, Object source, boolean informUser) {
		if (source != null)
			message += ", source: " + source.getClass().getName(); //$NON-NLS-1$
		if (informUser) {
			for (IStatusHandler handler : handlers) {
				handler.info(message);
			}
		}
		log(new Status(IStatus.INFO, ID_PLUGIN, IStatus.OK, message, null));
	}

	public static void fail(Throwable throwable, String message) {
		fail(throwable, message, true, Status.ERROR);
	}

	public static void fail(Throwable throwable, String message, boolean informUser) {
		fail(throwable, message, informUser, Status.ERROR);
	}

	public static void fail(Throwable throwable, String message, boolean informUser, int severity) {
		if (message == null)
			message = "no message"; //$NON-NLS-1$
		message += LINE_BREAK;

		final Status status = new Status(severity, ID_PLUGIN, IStatus.OK, message, throwable);

		// for (IStatusHandler handler : handlers) {
		// handler.fail(status, informUser);
		// }
		log(status);
	}

	public static void displayStatus(String title, IStatus status) {
		for (IStatusHandler handler : handlers) {
			handler.displayStatus(title, status);
		}
	}

	public static boolean isDebug() {
		return ENABLE_LOGGING;
	}

	public static IStatus createErrorStatus(Throwable e) {
		return new Status(IStatus.ERROR, ID_PLUGIN, IStatus.ERROR, e.getMessage(), e);
	}

}
