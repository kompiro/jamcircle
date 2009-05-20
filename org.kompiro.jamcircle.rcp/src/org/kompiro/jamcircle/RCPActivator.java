package org.kompiro.jamcircle;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
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
	}

}
