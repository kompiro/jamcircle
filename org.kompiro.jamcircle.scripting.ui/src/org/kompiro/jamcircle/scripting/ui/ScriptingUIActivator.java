package org.kompiro.jamcircle.scripting.ui;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class ScriptingUIActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.kompiro.jamcircle.scripting.ui";

	// The shared instance
	private static ScriptingUIActivator plugin;
	
	/**
	 * The constructor
	 */
	public ScriptingUIActivator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static ScriptingUIActivator getDefault() {
		return plugin;
	}
	
	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		for(ScriptingImageEnum e :ScriptingImageEnum.values()){
			initializeImage(reg, e);
		}
	}
	
	private void initializeImage(ImageRegistry reg,ScriptingImageEnum constants) {
		String PLUGIN_ID = getBundle().getSymbolicName();
		reg.put(constants.toString(), imageDescriptorFromPlugin(PLUGIN_ID, constants.getPath()));
	}


}
