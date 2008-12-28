package org.kompiro.jamcircle.kanban.ui;


import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.kompiro.jamcircle.kanban.KanbanStatusHandler;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.storage.IStatusHandler;
import org.kompiro.jamcircle.storage.StorageStatusHandler;
import org.kompiro.jamcircle.xmpp.service.XMPPConnectionService;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class KanbanUIActivator extends AbstractUIPlugin {

	private static final String KEY_OF_XMPPCONNECTION_SERVICE = "org.kompiro.jamcircle.xmpp.service.XMPPConnectionService";

	private static final String KEY_OF_KANBAN_SERVICE = "org.kompiro.jamcircle.kanban.service.KanbanService";

	private static KanbanUIActivator plugin;

	private ServiceTracker connectionTracker;
	
	private IStatusHandler dialogHandler;

	private ServiceTracker kanbanServiceTracker;


	public KanbanUIActivator() {
		plugin = this;
	}
	

	public void start(BundleContext context) throws Exception {
		super.start(context);
		KanbanJFaceResource.initialize();
		dialogHandler = new IStatusHandler(){

			public void displayStatus(String title, IStatus status) {
//				MessageDialog.openWarning(getShell(), title, status.getMessage());
			}

			public void fail(IStatus status, boolean informUser) {
				Throwable exception = status.getException();
				if(informUser){
					String message = String.format("%s\nException:'%s' reason: %s", status.getMessage(),exception.getClass().getName(),exception.getLocalizedMessage());
					MessageDialog.openError(getShell(), "Unexpected error is occured.",message);
				}
				exception.printStackTrace();
			}

			public void info(String message) {
//				MessageDialog.openInformation(getShell(), "Status Infomation", message);
				System.out.println(message);
			}
			
		};
		StorageStatusHandler.addStatusHandler(dialogHandler);
		KanbanStatusHandler.addStatusHandler(dialogHandler);
		KanbanUIStatusHandler.addStatusHandler(dialogHandler);
		kanbanServiceTracker = new ServiceTracker(context, KEY_OF_KANBAN_SERVICE, null);
		kanbanServiceTracker.open();
		connectionTracker = new ServiceTracker(context, KEY_OF_XMPPCONNECTION_SERVICE, null);
		connectionTracker.open();
//		migrate();
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		KanbanStatusHandler.removeStatusHandler(dialogHandler);
		KanbanUIStatusHandler.removeStatusHandler(dialogHandler);
		StorageStatusHandler.removeStatusHandler(dialogHandler);
		connectionTracker.close();
		kanbanServiceTracker.close();
		super.stop(context);
	}
	
	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		String PLUGIN_ID = getBundle().getSymbolicName();
		reg.put(KanbanImageConstants.OPEN_LIST_ACTION_IMAGE.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/table_go.png"));
		reg.put(KanbanImageConstants.TRASH_FULL_IMAGE.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/recycle-full.png"));
		reg.put(KanbanImageConstants.TRASH_EMPTY_IMAGE.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/recycle-empty.png"));
		reg.put(KanbanImageConstants.SEND_ON_IMAGE.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/send72_on.png"));
		reg.put(KanbanImageConstants.SEND_OFF_IMAGE.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/send72_off.png"));
		reg.put(KanbanImageConstants.CONNECT_IMAGE.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/connect.png"));
		reg.put(KanbanImageConstants.DISCONNECT_IMAGE.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/disconnect.png"));		
		reg.put(KanbanImageConstants.BACKGROUND_IMAGE.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/background/fuzzy-lightgrey.jpg"));
		reg.put(KanbanImageConstants.FILE_LINK_IMAGE.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/folder_link.png"));
		reg.put(KanbanImageConstants.FILE_GO_IMAGE.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/folder_go.png"));
		reg.put(KanbanImageConstants.LANE_ICONIZE_IMAGE.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/application_put.png"));
		reg.put(KanbanImageConstants.LANE_RESTORE_IMAGE.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/application_get.png"));
		reg.put(KanbanImageConstants.USER_IMAGE.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/user.png"));
		reg.put(KanbanImageConstants.COLOR_IMAGE.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/color_wheel.png"));
		reg.put(KanbanImageConstants.ADD_IMAGE.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/add.png"));
		reg.put(KanbanImageConstants.EDIT_IMAGE.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/edit.png"));
		reg.put(KanbanImageConstants.KANBANS_IMAGE.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/kanbans.png"));
		reg.put(KanbanImageConstants.PAGE_IMAGE.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/page.png"));
		reg.put(KanbanImageConstants.COMPLETED_IMAGE.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/tick.png"));
		reg.put(KanbanImageConstants.INBOX_IMAGE.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/inbox.png"));
		reg.put(KanbanImageConstants.CAMERA_IMAGE.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/camera.png"));
		reg.put(KanbanImageConstants.SAVE_IMAGE.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/disk.png"));
		reg.put(KanbanImageConstants.CLOCK_IMAGE.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/clock.png"));
		reg.put(KanbanImageConstants.CLOCK_RED_IMAGE.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/clock_red.png"));
		reg.put(KanbanImageConstants.DELETE_IMAGE.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/cross.png"));
		reg.put(KanbanImageConstants.OPEN_IMAGE.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/door_open.png"));
		reg.put(KanbanImageConstants.MOCK_IMAGE.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/script_gear.png"));
		
	}

	public static KanbanUIActivator getDefault() {
		return plugin;
	}
	
	public XMPPConnectionService getConnectionService(){
		return (XMPPConnectionService) connectionTracker.getService();
	}

	public KanbanService getKanbanService(){
		KanbanService service = (KanbanService)kanbanServiceTracker.getService();
		service.init();
		return  service;
	}

	public Shell getShell(){
		IWorkbench workbench = getWorkbench();
		IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
		return activeWorkbenchWindow.getShell();
	}
	
}
