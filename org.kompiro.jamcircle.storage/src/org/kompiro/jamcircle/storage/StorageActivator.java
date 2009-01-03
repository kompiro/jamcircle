package org.kompiro.jamcircle.storage;

import java.util.logging.Level;
import java.util.logging.Logger;


import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Plugin;
import org.kompiro.jamcircle.storage.exception.StorageConnectException;
import org.kompiro.jamcircle.storage.service.StorageService;
import org.kompiro.jamcircle.storage.service.StorageSetting;
import org.kompiro.jamcircle.storage.service.StorageSettings;
import org.kompiro.jamcircle.storage.service.internal.StorageServiceImpl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class StorageActivator extends Plugin {

	public static final String ID = "org.kompiro.jamcircle.storage";
	
	private static final String KEY_OF_LOG_NET_JAVA_AO = "net.java.ao";

	private static StorageActivator plugin;
	
	private ServiceRegistration storageServiceRegistration;
	private IStatusHandler handler;

	private StorageSettings settings = new StorageSettings();
	
	private StorageCallbackHandlerLoader loader = new StorageCallbackHandlerLoader();
	
	private StorageServiceImpl service;

	public StorageActivator() {
	}


	public void start(BundleContext context) throws Exception {
		plugin = this;
		super.start(context);
		handler = new StandardOutputHandler();
		StorageStatusHandler.addStatusHandler(handler);
		service = new StorageServiceImpl();
		storageServiceRegistration = context.registerService(StorageService.class.getName(), service, null);
		loadStorageSetting();
		if(StorageStatusHandler.isDebug()){
			Logger.getLogger(KEY_OF_LOG_NET_JAVA_AO).setLevel(Level.FINE);
		}else{
			Logger.getLogger(KEY_OF_LOG_NET_JAVA_AO).setLevel(Level.OFF);
		}
	}

	private void loadStorageSetting() {
		settings.loadSettings();
		if(settings.size() == 0){
			String uri = service.getDefaultStoreRoot();
			settings.add(-1,uri,StorageServiceImpl.MODE.FILE.toString(), "sa", "");
		}
		StorageSetting setting = settings.get(0);
		try {
			service.loadStorage(setting,new NullProgressMonitor());
		} catch (StorageConnectException e) {
			if(!(StorageServiceImpl.testmode)){
				loader.setupStorageSetting();
			}else{
				System.err.println("can't connect storage. and now it set testmode.");
			}
		}
	}


	public void stop(BundleContext context) throws Exception {
		plugin = null;
		settings.storeSttings();
		StorageStatusHandler.removeStatusHandler(handler);
		storageServiceRegistration.unregister();
		super.stop(context);
	}
	
	public static StorageActivator getDefault() {
		return plugin;
	}
	
	StorageService getService() {
		return service;
	}


	public StorageSettings getStorageSettings() {
		return this.settings;
	}

}
