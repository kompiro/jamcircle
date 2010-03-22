package org.kompiro.jamcircle.kanban.ui;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.eclipse.core.runtime.*;
import org.kompiro.jamcircle.debug.IStatusHandler;


public class KanbanUIStatusHandler {

	private static final String ID_PLUGIN = KanbanUIActivator.ID_PLUGIN;

	private static Set<IStatusHandler> handlers = new HashSet<IStatusHandler>();
	private static DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd hh:mm:ssSSSS");

	
	private static final boolean ENABLE_LOGGING;
	private static final boolean ENABLE_DEBUG_LEVEL;
	private static final boolean ENABLE_UI_DEBUG;

	static{
		ENABLE_LOGGING = !Platform.isRunning() || Boolean.valueOf(Platform.getDebugOption(ID_PLUGIN + "/debug"));
		ENABLE_UI_DEBUG = !Platform.isRunning() || Boolean.valueOf(Platform.getDebugOption(ID_PLUGIN + "/debug/ui"));
		ENABLE_DEBUG_LEVEL = !Platform.isRunning() || Boolean.valueOf(Platform.getDebugOption(ID_PLUGIN + "/debug/debug"));
	}

	public static void addStatusHandler(IStatusHandler handler) {
		handlers.add(handler);
	}

	public static void removeStatusHandler(IStatusHandler handler) {
		handlers.remove(handler);
	}

	private static void log(IStatus status) {
		if(ENABLE_LOGGING){
			if(Platform.isRunning()){
				getLog().log(status);			
			}
		}
		if(status.getException() != null){
			for(IStatusHandler handler : handlers){
				handler.fail(status, true);
			}
		}else{
			String message = String.format("%s %s",formatter.format(new Date()),status.getMessage());
			for(IStatusHandler handler : handlers){
				handler.info(message);
			}			
		}
	}

	private static ILog getLog() {
		return KanbanUIActivator.getDefault().getLog();
	}

	public static void debug(String message,Object ... objects){
		if(ENABLE_DEBUG_LEVEL){
			message = String.format(message,objects);
			log(new Status(IStatus.WARNING, ID_PLUGIN, IStatus.OK, message, null));
		}
	}
	
	public static void debugUI(String message,Object... objects){
		if(ENABLE_UI_DEBUG){
			log(new Status(IStatus.WARNING, ID_PLUGIN, IStatus.OK, String.format(message,objects), null));
		}
	}
	
	public static void info(String message,Object... objects) {
		info(String.format(message, objects),null,false);
	}
	
	public static void info(String message, Object source,boolean informUser) {
		if (source != null)
			message += ", source: " + source.getClass().getName();
		if(informUser){
			for(IStatusHandler handler : handlers){
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
			message = "no message";
		message += "\n";

		final Status status = new Status(severity, ID_PLUGIN, IStatus.OK, message, throwable);

//		for (IStatusHandler handler : handlers) {
//			handler.fail(status, informUser);
//		}
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
	
	public static IStatus createErrorStatus(Throwable e){
		return new Status(IStatus.ERROR, ID_PLUGIN, IStatus.ERROR, e.getMessage(), e);
	}

}
