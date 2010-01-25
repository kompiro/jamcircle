package org.kompiro.jamcircle.kanban;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.java.ao.EntityManager;

import org.eclipse.core.runtime.Plugin;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.service.internal.KanbanServiceImpl;
import org.kompiro.jamcircle.storage.service.StorageService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class KanbanActivator extends Plugin {

//	private ServiceTracker storageTracker;

//	private KanbanServiceImpl service;

//	private ServiceRegistration kanbanServiceRegistration;
	
	private static KanbanActivator plugin;

	public KanbanActivator() {
		KanbanActivator.plugin = this;
	}

	public static KanbanActivator getDefault() {
		return plugin;
	}
	
//	public EntityManager getEntityManager(){
//		StorageService service = getStorageService();
//		return service != null ? service.getEntityManager() : null;
//	}

//	public StorageService getStorageService() {
//		return (StorageService) storageTracker.getService();
//	}
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
//		storageTracker = new ServiceTracker(context,StorageService.class.getName(), null);
//		storageTracker.open();
//		service = new KanbanServiceImpl();
//		service.setStorageService(getStorageService());
//		getStorageService().addStorageChangeListener(service);

//		kanbanServiceRegistration = context.registerService(KanbanService.class.getName(), service, null);
		if(KanbanStatusHandler.isDebug()){
			Logger.getLogger("net.java.ao").setLevel(Level.FINE);
		}else{
			Logger.getLogger("net.java.ao").setLevel(Level.OFF);
		}

	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
//		service.dispose();
//		storageTracker.close();
//		kanbanServiceRegistration.unregister();
		super.stop(context);
	}

	public static KanbanServiceImpl getKanbanService(){
		return KanbanServiceImpl.service;
	}
	

}
