package org.kompiro.jamcircle;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.statushandlers.StatusManager;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class RCPActivator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.kompiro.jamcircle.rcp";

	private static RCPActivator plugin;
	
	public RCPActivator() {
		plugin = this;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static RCPActivator getDefault() {
		return plugin;
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		reg.put(ImageConstants.APPLICATION_IMAGE.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/kanban.gif"));
		reg.put(ImageConstants.APPLICATION_OFF_IMAGE.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/kanban_off.gif"));
		reg.put(ImageConstants.EXIT_IMAGE.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/door_out.png"));
	}
	
	public void logError(Exception e){
		logError("error has occured.",e);
	}

	public void logError(String message,Exception e){
		log(new Status(IStatus.ERROR, getPluginID(), IStatus.ERROR, message, e));
	}
	
	public String getPluginID() {
		return PLUGIN_ID;
	}

	private void log(IStatus status){
		StatusManager.getManager().handle(status);
	}

}
