package org.kompiro.jamcircle.xmpp;


import org.eclipse.core.runtime.*;
import org.kompiro.jamcircle.debug.StandardOutputHandler;
import org.osgi.framework.BundleContext;

public class XMPPActivator extends Plugin {

	public static final String PLUGIN_ID = "org.kompiro.jamcircle.xmpp"; //$NON-NLS-1$

	private static XMPPActivator plugin;
	
	private StandardOutputHandler handler;

	
	public XMPPActivator() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		handler = new StandardOutputHandler();
		XMPPStatusHandler.addStatusHandler(handler);
	}

	public void stop(BundleContext context) throws Exception {
		XMPPStatusHandler.removeStatusHandler(handler);
		plugin = null;
		super.stop(context);
	}

	public static XMPPActivator getDefault() {
		return plugin;
	}
			
	public static IStatus createErrorStatus(Throwable e){
		return new Status(IStatus.ERROR, PLUGIN_ID, e.getLocalizedMessage(),e);
	}

	
}
