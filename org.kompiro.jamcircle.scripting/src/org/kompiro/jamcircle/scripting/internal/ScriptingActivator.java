package org.kompiro.jamcircle.scripting.internal;

import org.kompiro.jamcircle.scripting.ScriptingService;
import org.osgi.framework.*;

public class ScriptingActivator implements BundleActivator {

	private ServiceRegistration scriptingServiceRegistration;

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		ScriptingServiceImpl service = new ScriptingServiceImpl();
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
