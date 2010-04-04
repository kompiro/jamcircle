package org.kompiro.jamcircle.storage.ui;


import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.kompiro.jamcircle.storage.service.StorageService;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class StorageUIActivator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.kompiro.jamcircle.storage.ui"; //$NON-NLS-1$
	private ServiceTracker storageTracker;

	private static StorageUIActivator plugin;
	
	public StorageUIActivator() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		storageTracker = new ServiceTracker(context,StorageService.class.getName(), null);
		storageTracker.open();
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		storageTracker.close();
		super.stop(context);
	}

	public static StorageUIActivator getDefault() {
		return plugin;
	}

	public StorageService getStorageService() {
		return (StorageService) storageTracker.getService();
	}

}
