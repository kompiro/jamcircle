package org.kompiro.jamcircle.xmpp;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.jivesoftware.smack.XMPPException;
import org.kompiro.jamcircle.debug.StandardOutputHandler;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.xmpp.internal.extension.XMPPLoginListenerFactory;
import org.kompiro.jamcircle.xmpp.service.XMPPConnectionService;
import org.kompiro.jamcircle.xmpp.service.XMPPSettings;
import org.kompiro.jamcircle.xmpp.service.XMPPSettings.Setting;
import org.kompiro.jamcircle.xmpp.service.internal.XMPPConnectionServiceImpl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class XMPPActivator extends Plugin {

	public static final String PLUGIN_ID = "org.kompiro.jamcircle.xmpp";

	private static XMPPActivator plugin;
	
//	private ServiceRegistration registerService;
//	
//	private XMPPConnectionServiceImpl service;
//
//	
//	private ServiceTracker kanbanServiceTracker;
//
	private StandardOutputHandler handler;
//
//	private XMPPLoginListenerFactory factory;

	
	public XMPPActivator() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
//		factory.setXMPPConnectionService(service);
//		registerService = context.registerService(XMPPConnectionService.class.getName(),service, null);
//		kanbanServiceTracker = new ServiceTracker(context, KanbanService.class.getName(), null);
//		kanbanServiceTracker.open();
		handler = new StandardOutputHandler();
		XMPPStatusHandler.addStatusHandler(handler);
	}

	public void stop(BundleContext context) throws Exception {
		XMPPStatusHandler.removeStatusHandler(handler);
		plugin = null;
//		factory.reject(service);
//		registerService.unregister();
//		service = null;
//		registerService = null;
		super.stop(context);
	}

	public static XMPPActivator getDefault() {
		return plugin;
	}
		
//	public KanbanService getKanbanService(){
//		KanbanService service = (KanbanService) kanbanServiceTracker.getService();
//		return service;
//	}
	
	public static IStatus createErrorStatus(Throwable e){
		return new Status(IStatus.ERROR, PLUGIN_ID, e.getLocalizedMessage(),e);
	}

	
}
