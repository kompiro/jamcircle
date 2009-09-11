package org.kompiro.jamcircle.xmpp;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.jivesoftware.smack.XMPPException;
import org.kompiro.jamcircle.debug.StandardOutputHandler;
import org.kompiro.jamcircle.kanban.service.KanbanService;
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

	private ServiceRegistration registerService;
	
	private XMPPConnectionServiceImpl service = new XMPPConnectionServiceImpl();

	private XMPPSettings settings = new XMPPSettings();
	
	private ServiceTracker kanbanTracker;

	private StandardOutputHandler handler;

	
	public XMPPActivator() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		createConnection();			
		registerService = context.registerService(XMPPConnectionService.class.getName(),service, null);
		kanbanTracker = new ServiceTracker(context, KanbanService.class.getName(), null);
		kanbanTracker.open();
		handler = new StandardOutputHandler();
		XMPPStatusHandler.addStatusHandler(handler);
	}

	private void createConnection() {
		System.setProperty(XMPPConnectionServiceImpl.KEY_OF_SYSTEM_PROP_XMPP_CONNECT, String.valueOf(false));
		settings.loadSettings();
		if(service.getSettings().size() != 0) {
			final Setting setting = service.getSettings().get(0);
			final String host = setting.getHost();
			final String message = String.format("Connectiong to %s ...",host);
			XMPPStatusHandler.debug(message);
			Job job = new Job(message) {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					monitor.beginTask(String.format(message,host), 100);
					try {
						String resource = setting.getResource();
						String serviceName = setting.getServiceName();
						String username =  setting.getUsername();
						String password = setting.getPassword();
						int port = setting.getPort();
						service.login(monitor, host, resource, serviceName, port, username, password);
					} catch (XMPPException e) {
						XMPPStatusHandler.debug("Can't create initialize Connection.",e);
					} finally {
						monitor.done();
					}
					return Status.OK_STATUS;
				}
			};
			job.schedule();	
		}
	}

	public void stop(BundleContext context) throws Exception {
		XMPPStatusHandler.removeStatusHandler(handler);
		plugin = null;
		if(settings.size() != 0){
			settings.storeSttings();
		}
		registerService.unregister();
		service = null;
		registerService = null;
		super.stop(context);
	}

	public static XMPPActivator getDefault() {
		return plugin;
	}
		
	public KanbanService getKanbanService(){
		KanbanService service = (KanbanService) kanbanTracker.getService();
		return service;
	}

	public XMPPSettings getSettings() {
		return settings;
	}
	
}
