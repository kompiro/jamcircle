package org.kompiro.jamcircle.xmpp.kanban.ui.internal;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.kompiro.jamcircle.xmpp.kanban.ui.Messages;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class BridgeXMPPActivator implements BundleActivator {
	
	private static final String PLUGIN_ID = "org.kompiro.jamcircle.xmpp.kanban.ui"; //$NON-NLS-1$
	
	private static BridgeXMPPActivator activator;


	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		activator = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		activator = null;
	}
	

	public static BridgeXMPPActivator getDefault(){
		return activator;
	}

	public static IStatus createErrorStatus(Throwable e){
		return new Status(IStatus.ERROR, PLUGIN_ID, Messages.BridgeXMPPActivator_error_message,e);
	}

}
