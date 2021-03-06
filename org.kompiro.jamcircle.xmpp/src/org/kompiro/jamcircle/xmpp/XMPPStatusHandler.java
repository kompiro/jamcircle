package org.kompiro.jamcircle.xmpp;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.*;
import org.kompiro.jamcircle.debug.IStatusHandler;


public class XMPPStatusHandler {

	private static final String ID_PLUGIN = "org.kompiro.jamcircle.xmpp"; //$NON-NLS-1$

	private static Set<IStatusHandler> handlers = new HashSet<IStatusHandler>();
	
	private static final boolean ENABLE_LOGGING;
	private static final boolean ENABLE_DEBUG_LEVEL;

	static{
		ENABLE_LOGGING = Boolean.valueOf(Platform.getDebugOption(ID_PLUGIN + "/debug")); //$NON-NLS-1$
		ENABLE_DEBUG_LEVEL = Boolean.valueOf(Platform.getDebugOption(ID_PLUGIN + "/debug/debug")); //$NON-NLS-1$
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
		}else if(status.getException() != null){
			for(IStatusHandler handler : handlers){
				handler.fail(status, true);
			}
		}
		if(Platform.isRunning()){
			XMPPActivator.getDefault().getLog().log(status);			
		}

	}

	public static void debug(String message,Object... objects){
		if(ENABLE_DEBUG_LEVEL){
			message = String.format(message, objects);
			log(new Status(IStatus.WARNING, ID_PLUGIN, IStatus.OK, message, null));
		}
	}
	
	public static void info(String message) {
		info(message,null);
	}
	
	public static void info(String message, Object source) {
		if (source != null)
			message += ", source: " + source.getClass().getName(); //$NON-NLS-1$
		for(IStatusHandler handler : handlers){
			handler.info(message);
		}
		log(new Status(IStatus.INFO, ID_PLUGIN, IStatus.OK, message, null));
	}

	public static void fail(Throwable throwable, String message) {
		fail(throwable, message, false, Status.INFO);
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
