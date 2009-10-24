package org.kompiro.jamcircle.xmpp.kanban.ui.internal;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.*;
import org.eclipse.ui.statushandlers.StatusManager;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.KanbanView;
import org.kompiro.jamcircle.kanban.ui.util.WorkbenchUtil;
import org.kompiro.jamcircle.xmpp.service.XMPPConnectionService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class BridgeXMPPActivator implements BundleActivator, IPartListener {
	
	private static final String KEY_OF_XMPPCONNECTION_SERVICE = XMPPConnectionService.class.getName();

	private static final String KEY_OF_KANBAN_SERVICE = KanbanService.class.getName();

	private static final String PLUGIN_ID = "org.kompiro.jamcircle.xmpp.kanban.ui";

	private ServiceTracker xmppServiceTracker;

	private ServiceTracker kanbanServiceTracker;

	
	private static BridgeXMPPActivator activator;

	private KanbanXMPPLoginListener listener;

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
		initializeListener();
	}

	private void initializeListener() {
		listener = new KanbanXMPPLoginListener();
		XMPPConnectionService connectionService = getConnectionService();
		if(connectionService == null) {
			IllegalStateException e = new IllegalStateException("ConnectionService is null when stating BridgeXMPPActivator.");
			IStatus status = createErrorStatus(e);
			StatusManager.getManager().handle(status);
			return;
		}
		connectionService.addXMPPLoginListener(listener);
//		IPartService partService = (IPartService)PlatformUI.getWorkbench().getService(IPartService.class);
//		partService.addPartListener(this);
		listener.setKanbanView(WorkbenchUtil.findKanbanView());
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		getConnectionService().removeXMPPLoginListener(listener);
		listener = null;
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

	public void partActivated(IWorkbenchPart part) {
		if (part instanceof KanbanView) {
			KanbanView view = (KanbanView) part;
			listener.setKanbanView(view);
		}
	}

	public void partBroughtToTop(IWorkbenchPart part) {}

	public void partClosed(IWorkbenchPart part) {
		if (part instanceof KanbanView) {
			listener.setKanbanView(null);
		}
	}

	public void partDeactivated(IWorkbenchPart part) {
	}

	public void partOpened(IWorkbenchPart part) {}

}
