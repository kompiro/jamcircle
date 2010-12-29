package org.kompiro.jamcircle.storage;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.*;
import org.kompiro.jamcircle.debug.IStatusHandler;
import org.kompiro.jamcircle.debug.StandardOutputHandler;
import org.osgi.framework.BundleContext;

public class StorageActivator extends Plugin {

	public static final String ID = "org.kompiro.jamcircle.storage"; //$NON-NLS-1$

	private static final String KEY_OF_LOG_NET_JAVA_AO = "net.java.ao"; //$NON-NLS-1$

	private static StorageActivator plugin;

	private IStatusHandler handler;

	public StorageActivator() {
	}

	public void start(BundleContext context) throws Exception {
		plugin = this;
		super.start(context);
		handler = new StandardOutputHandler();
		StorageStatusHandler.addStatusHandler(handler);
		if (StorageStatusHandler.isDebug()) {
			Logger.getLogger(KEY_OF_LOG_NET_JAVA_AO).setLevel(Level.FINE);
		} else {
			Logger.getLogger(KEY_OF_LOG_NET_JAVA_AO).setLevel(Level.OFF);
		}
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static StorageActivator getDefault() {
		return plugin;
	}

	public IStatus createErrorStatus(Exception e) {
		return new Status(IStatus.ERROR, ID, e.getLocalizedMessage(), e);
	}

}
