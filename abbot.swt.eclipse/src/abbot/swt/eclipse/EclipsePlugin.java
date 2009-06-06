package abbot.swt.eclipse;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class EclipsePlugin extends AbstractUIPlugin {

	/**
	 * The singleton instance.
	 */
	private static EclipsePlugin INSTANCE;

	/**
	 * Constructs a new (singleton) instance and records it.
	 */
	public EclipsePlugin() {
		super();
		Assert.isTrue(INSTANCE == null);
		INSTANCE = this;
	}

	/**
	 * Dereferences the singleton instance when the plug-in stops.
	 * 
	 * @see AbstractUIPlugin#stop(BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		INSTANCE = null;
		super.stop(context);
	}

	/**
	 * Returns the singleton instance.
	 */
	public static EclipsePlugin getInstance() {
		return INSTANCE;
	}
	
	public static String getId() {
		return INSTANCE.getBundle().getSymbolicName();
	}
	
}
