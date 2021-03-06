package org.kompiro.jamcircle.storage;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.*;
import org.kompiro.jamcircle.debug.IStatusHandler;


public class StorageStatusHandler {

	private static final String ID_PLUGIN = "org.kompiro.jamcircle.storage"; //$NON-NLS-1$

	private static Set<IStatusHandler> handlers = new HashSet<IStatusHandler>();
	
	public static final boolean ENABLE_LOGGING;
	public static final boolean ENABLE_DEBUG_LEVEL;

	static{
		ENABLE_LOGGING = !Platform.isRunning() || Boolean.valueOf(Platform.getDebugOption(ID_PLUGIN + "/debug")); //$NON-NLS-1$
		ENABLE_DEBUG_LEVEL = !Platform.isRunning() || Boolean.valueOf(Platform.getDebugOption(ID_PLUGIN + "/debug/debug")); //$NON-NLS-1$
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
				StorageActivator.getDefault().getLog().log(status);			
			}
		}else if(status.getException() != null){
			for(IStatusHandler handler : handlers){
				handler.fail(status, true);
			}
			if(Platform.isRunning()){
				StorageActivator.getDefault().getLog().log(status);			
			}
		}
	}

	public static void debug(String message,Object... objects){
		if(ENABLE_DEBUG_LEVEL){
			message = String.format(message, objects);
			log(new Status(IStatus.WARNING, ID_PLUGIN, IStatus.OK, message, null));
		}
	}
	
	public static void info(String message) {
		info(message,null,true);
	}
	
	public static void info(String message,Object source) {
		info(message,source,false);
	}

	public static void info(String message,boolean informUser) {
		info(message,null,informUser);
	}

	public static void info(String message, Object source,boolean informUser) {
		if (source != null)
			message += ", source: " + source.getClass().getName(); //$NON-NLS-1$
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

	public static void fail(Throwable throwable, String message,Object... params) {
		fail(throwable, String.format(message,params), false, Status.ERROR);
	}
	
	public static void fail(Throwable throwable, String message, boolean informUser) {
		fail(throwable, message, informUser, Status.ERROR);
	}

	public static void fail(Throwable throwable, String message, boolean informUser, int severity) {
		if (message == null)
			message = "no message"; //$NON-NLS-1$
		message += "\n"; //$NON-NLS-1$

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

}
