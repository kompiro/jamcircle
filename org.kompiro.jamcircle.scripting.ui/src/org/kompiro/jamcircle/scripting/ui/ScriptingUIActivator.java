package org.kompiro.jamcircle.scripting.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.kompiro.jamcircle.scripting.util.ScriptingSecurityHelper;
import org.kompiro.jamcircle.scripting.util.ScriptingSecurityManager;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class ScriptingUIActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.kompiro.jamcircle.scripting.ui"; //$NON-NLS-1$

	// The shared instance
	private static ScriptingUIActivator plugin;

	/**
	 * The constructor
	 */
	public ScriptingUIActivator() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		ScriptingSecurityManager.setScriptingExitHelper(new ScriptingSecurityHelper() {
			public void throwSecurityException() throws SecurityException {
				if (PlatformUI.isWorkbenchRunning()) {
					throw new SecurityException();
				}
			}
		});
		initializeColorRegistry();
		plugin = this;
	}

	private void initializeColorRegistry() {
		for (ScriptingColorEnum e : ScriptingColorEnum.values()) {
			e.initialize();
		}
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static ScriptingUIActivator getDefault() {
		return plugin;
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		for (ScriptingImageEnum e : ScriptingImageEnum.values()) {
			initializeImage(reg, e);
		}
		initializeColorRegistry();
	}

	private void initializeImage(ImageRegistry reg, ScriptingImageEnum constants) {
		String PLUGIN_ID = getBundle().getSymbolicName();
		reg.put(constants.toString(), imageDescriptorFromPlugin(PLUGIN_ID, constants.getPath()));
	}

	public static IStatus createErrorStatus(Throwable e) {
		return new Status(IStatus.ERROR, PLUGIN_ID, Messages.ScriptingUIActivator_error_message, e);
	}

}
