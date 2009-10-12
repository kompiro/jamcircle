package org.kompiro.jamcircle.xmpp.kanban.ui.internal;

import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.xmpp.service.XMPPConnectionService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class BridgeXMPPActivator implements BundleActivator {
	
	private static final String KEY_OF_XMPPCONNECTION_SERVICE = XMPPConnectionService.class.getName();

	private ServiceTracker connectionTracker;

	private static BridgeXMPPActivator activator;

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		connectionTracker = new ServiceTracker(context, KEY_OF_XMPPCONNECTION_SERVICE, null);
		activator = this;
		connectionTracker.open();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		connectionTracker.close();
	}
	
	public XMPPConnectionService getConnectionService(){
		return (XMPPConnectionService) connectionTracker.getService();
	}

	public static BridgeXMPPActivator getDefault(){
		return activator;
	}

	public KanbanService getKanbanService() {
		// TODO Auto-generated method stub
		return null;
	}

}
