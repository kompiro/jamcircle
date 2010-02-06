package org.kompiro.jamcircle.kanban;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.*;
import org.kompiro.jamcircle.kanban.service.internal.KanbanServiceImpl;
import org.osgi.framework.BundleContext;

public class KanbanActivator extends Plugin {

	public static final String ID = "org.kompiro.jamcircle.kanban";
	private static KanbanActivator plugin;

	public KanbanActivator() {
		KanbanActivator.plugin = this;
	}

	public static KanbanActivator getDefault() {
		return plugin;
	}
		
	public void start(BundleContext context) throws Exception {
		super.start(context);
		if(KanbanStatusHandler.isDebug()){
			Logger.getLogger("net.java.ao").setLevel(Level.FINE);
		}else{
			Logger.getLogger("net.java.ao").setLevel(Level.OFF);
		}

	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static KanbanServiceImpl getKanbanService(){
		return KanbanServiceImpl.service;
	}
	
	public static IStatus createErrorStatus(String message){
		return new Status(IStatus.ERROR,ID,message);
	}

}
