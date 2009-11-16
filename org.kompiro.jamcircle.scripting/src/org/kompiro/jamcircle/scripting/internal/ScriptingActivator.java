package org.kompiro.jamcircle.scripting.internal;

import org.kompiro.jamcircle.scripting.ScriptingEngineInitializerLoader;
import org.kompiro.jamcircle.scripting.ScriptingService;
import org.osgi.framework.*;

public class ScriptingActivator implements BundleActivator {

	private ServiceRegistration scriptingServiceRegistration;
	public static String ID = ScriptingActivator.class.getName();

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		ScriptingEngineInitializerLoader loader = new ScriptingEngineInitializerLoaderImpl(context);
		ScriptingServiceImpl service = new ScriptingServiceImpl(loader.getGrobalValues());
		scriptingServiceRegistration = context.registerService(ScriptingService.class.getName(), service, null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		scriptingServiceRegistration.unregister();
	}

}
