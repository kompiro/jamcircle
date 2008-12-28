package abbot.swt;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class Plugin extends AbstractUIPlugin {
	
	private static Plugin Instance;
	
	public static Plugin getInstance() {
		return Instance;
	}
	
	public Plugin() {
		Instance = this;
	}

	public void stop(BundleContext context) throws Exception {
		Instance = null;
	}

}
