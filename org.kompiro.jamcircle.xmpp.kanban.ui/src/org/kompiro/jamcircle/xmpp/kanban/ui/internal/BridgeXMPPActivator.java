package org.kompiro.jamcircle.xmpp.kanban.ui.internal;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.xmpp.service.XMPPConnectionService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class BridgeXMPPActivator implements BundleActivator {
	
	private static final String KEY_OF_XMPPCONNECTION_SERVICE = XMPPConnectionService.class.getName();

	private static final String KEY_OF_KANBAN_SERVICE = KanbanService.class.getName();

	private static final String PLUGIN_ID = "org.kompiro.jamcircle.xmpp.kanban.ui";

	private ServiceTracker xmppServiceTracker;

	private ServiceTracker kanbanServiceTracker;

	
	private static BridgeXMPPActivator activator;


	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		activator = this;
		xmppServiceTracker = new ServiceTracker(context, KEY_OF_XMPPCONNECTION_SERVICE, null);
		xmppServiceTracker.open();
		kanbanServiceTracker = new ServiceTracker(context, KEY_OF_KANBAN_SERVICE, null);
		kanbanServiceTracker.open();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		xmppServiceTracker.close();
		kanbanServiceTracker.close();
		activator = null;
	}
	
	public XMPPConnectionService getConnectionService(){
		return (XMPPConnectionService) xmppServiceTracker.getService();
	}

	public static BridgeXMPPActivator getDefault(){
		return activator;
	}

	public KanbanService getKanbanService() {
		return (KanbanService) kanbanServiceTracker.getService();
	}

	public static IStatus createErrorStatus(Throwable e){
		return new Status(IStatus.ERROR, PLUGIN_ID, "error is occured",e);
	}

}
