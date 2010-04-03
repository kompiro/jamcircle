package org.kompiro.jamcircle.kanban;

import static java.lang.String.format;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.*;
import org.kompiro.jamcircle.debug.IStatusHandler;


public class KanbanStatusHandler {


	private static final String LINE_BREAK = System.getProperty("line.separator"); //$NON-NLS-1$
	private static final String NO_MESSAGE = "no message"; //$NON-NLS-1$
	private static final String INFO_MESSAGE_FORMAT = "%s, source:%s"; //$NON-NLS-1$
	private static final String DEBUG = "/debug"; //$NON-NLS-1$
	private static final String DEBUG_LEVEL = "/debug/level"; //$NON-NLS-1$

	private static final String ID_PLUGIN = KanbanActivator.ID;

	private static Set<IStatusHandler> handlers = new HashSet<IStatusHandler>();
	
	private static final boolean ENABLE_LOGGING;
	private static final boolean ENABLE_DEBUG_LEVEL;

	static{
		ENABLE_LOGGING = !Platform.isRunning() || Boolean.valueOf(Platform.getDebugOption(ID_PLUGIN + DEBUG));
		ENABLE_DEBUG_LEVEL = !Platform.isRunning() || Boolean.valueOf(Platform.getDebugOption(ID_PLUGIN + DEBUG_LEVEL));
	}

	public static void addStatusHandler(IStatusHandler handler) {
		handlers.add(handler);
	}

	public static void removeStatusHandler(IStatusHandler handler) {
		handlers.remove(handler);
	}

	public static void log(IStatus status) {
		if(ENABLE_LOGGING){
			for(IStatusHandler handler : handlers){
				handler.info(status.getMessage());
			}
			if(Platform.isRunning()){
				getLog().log(status);			
			}
		}else if(status.getException() != null){
			for(IStatusHandler handler : handlers){
				handler.fail(status, true);
			}
			if(Platform.isRunning()){
				getLog().log(status);			
			}
		}
	}

	public static void debug(String message,Object ... objects){
		if(ENABLE_DEBUG_LEVEL){
			message = String.format(message,objects);
			log(new Status(IStatus.WARNING, ID_PLUGIN, IStatus.OK, message, null));
		}
	}
	
	public static void info(String message) {
		info(message,null);
	}
	
	public static void info(String message, Object source) {
		if (source != null)
			message = format(INFO_MESSAGE_FORMAT,message,source.getClass().getName());
		for(IStatusHandler handler : handlers){
			handler.info(message);
		}
		log(new Status(IStatus.INFO, ID_PLUGIN, IStatus.OK, message, null));
	}

	public static void fail(Throwable throwable, String message,Object... params) {
		fail(throwable, String.format(message,params), false, Status.ERROR);
	}

	public static void fail(Throwable throwable, String message, boolean informUser) {
		fail(throwable, message, informUser, Status.ERROR);
	}

	public static void fail(Throwable throwable, String message, boolean informUser, int severity) {
		if (message == null)
			message = NO_MESSAGE;
		message += LINE_BREAK;

		final Status status = new Status(severity, ID_PLUGIN, IStatus.OK, message, throwable);

		for (IStatusHandler handler : handlers) {
			handler.fail(status, informUser);
		}
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
	
	private static ILog getLog() {
		return KanbanActivator.getDefault().getLog();
	}


}
