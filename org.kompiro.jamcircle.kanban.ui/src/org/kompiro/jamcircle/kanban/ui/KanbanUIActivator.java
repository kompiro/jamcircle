package org.kompiro.jamcircle.kanban.ui;


import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.*;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.kompiro.jamcircle.debug.IStatusHandler;
import org.kompiro.jamcircle.kanban.KanbanStatusHandler;
import org.kompiro.jamcircle.kanban.model.ColorTypes;
import org.kompiro.jamcircle.kanban.model.FlagTypes;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.scripting.ScriptingService;
import org.kompiro.jamcircle.scripting.exception.ScriptingException;
import org.kompiro.jamcircle.storage.StorageStatusHandler;
import org.kompiro.jamcircle.xmpp.service.XMPPConnectionService;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class KanbanUIActivator extends AbstractUIPlugin {
	
	public static final String ID_PLUGIN = "org.kompiro.jamcircle.kanban.ui";

	private static final String KEY_OF_XMPPCONNECTION_SERVICE = "org.kompiro.jamcircle.xmpp.service.XMPPConnectionService";

	private static final String KEY_OF_KANBAN_SERVICE = "org.kompiro.jamcircle.kanban.service.KanbanService";

	private static final String KEY_OF_SCRIPTING_SERVICE = ScriptingService.class.getName();

	private static KanbanUIActivator plugin;

	private ServiceTracker connectionTracker;
	
	private ServiceTracker kanbanServiceTracker;

	private ServiceTracker scriptingServiceTracker;

	private IStatusHandler dialogHandler;


	public KanbanUIActivator() {
		plugin = this;
	}
	

	public void start(BundleContext context) throws Exception {
		super.start(context);
		KanbanJFaceResource.initialize();
 		dialogHandler = new IStatusHandler(){
			 
 			public void displayStatus(String title, IStatus status) {
 			}
 
 			public void fail(IStatus status, boolean informUser) {
 				Throwable exception = status.getException();
 				if(informUser){
 					String message = String.format("%s\nException:'%s' reason: %s", status.getMessage(),exception.getClass().getName(),exception.getLocalizedMessage());
 					Shell shell = getShell();
 					if(shell != null){
 						MessageDialog.openError(shell, "Unexpected error is occured.",message);
 					}
 				}
 			}
 
 			public void info(String message) {
 			}
 			
 		};
 		StorageStatusHandler.addStatusHandler(dialogHandler);
 		KanbanStatusHandler.addStatusHandler(dialogHandler);
 		KanbanUIStatusHandler.addStatusHandler(dialogHandler);

		kanbanServiceTracker = new ServiceTracker(context, KEY_OF_KANBAN_SERVICE, null);
		kanbanServiceTracker.open();
		connectionTracker = new ServiceTracker(context, KEY_OF_XMPPCONNECTION_SERVICE, null);
		connectionTracker.open();
		scriptingServiceTracker = new ServiceTracker(context, KEY_OF_SCRIPTING_SERVICE, null);
		scriptingServiceTracker.open();
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
		initializeImage(reg,KanbanImageConstants.OPEN_LIST_ACTION_IMAGE);
		initializeImage(reg,KanbanImageConstants.TRASH_FULL_IMAGE);
		initializeImage(reg,KanbanImageConstants.TRASH_EMPTY_IMAGE);
		initializeImage(reg,KanbanImageConstants.SEND_ON_IMAGE);
		initializeImage(reg,KanbanImageConstants.SEND_OFF_IMAGE);
		initializeImage(reg,KanbanImageConstants.CONNECT_IMAGE);
		initializeImage(reg,KanbanImageConstants.DISCONNECT_IMAGE);
		initializeImage(reg,KanbanImageConstants.BACKGROUND_IMAGE);
		initializeImage(reg,KanbanImageConstants.FILE_LINK_IMAGE);
		initializeImage(reg,KanbanImageConstants.FILE_GO_IMAGE);
		initializeImage(reg,KanbanImageConstants.LANE_ICONIZE_IMAGE);
		initializeImage(reg,KanbanImageConstants.LANE_RESTORE_IMAGE);
		initializeImage(reg,KanbanImageConstants.USER_IMAGE);
		initializeImage(reg,KanbanImageConstants.COLOR_IMAGE);
		initializeImage(reg,KanbanImageConstants.ADD_IMAGE);
		initializeImage(reg,KanbanImageConstants.EDIT_IMAGE);
		initializeImage(reg,KanbanImageConstants.KANBANS_IMAGE);
		initializeImage(reg,KanbanImageConstants.PAGE_IMAGE);
		initializeImage(reg,KanbanImageConstants.COMPLETED_IMAGE);
		initializeImage(reg,KanbanImageConstants.INBOX_IMAGE);
		initializeImage(reg,KanbanImageConstants.CAMERA_IMAGE);
		initializeImage(reg,KanbanImageConstants.SAVE_IMAGE);
		initializeImage(reg,KanbanImageConstants.CLOCK_IMAGE);
		initializeImage(reg,KanbanImageConstants.CLOCK_RED_IMAGE);
		initializeImage(reg,KanbanImageConstants.DELETE_IMAGE);
		initializeImage(reg,KanbanImageConstants.OPEN_IMAGE);
		initializeImage(reg,KanbanImageConstants.MOCK_IMAGE);
		initializeImage(reg,KanbanImageConstants.FLAG_BLUE_IMAGE);
		initializeImage(reg,KanbanImageConstants.FLAG_GREEN_IMAGE);
		initializeImage(reg,KanbanImageConstants.FLAG_ORANGE_IMAGE);
		initializeImage(reg,KanbanImageConstants.FLAG_RED_IMAGE);
		initializeImage(reg,KanbanImageConstants.FLAG_WHITE_IMAGE);
	}


	private void initializeImage(ImageRegistry reg,KanbanImageConstants constants) {
		String PLUGIN_ID = getBundle().getSymbolicName();
		reg.put(constants.toString(), imageDescriptorFromPlugin(PLUGIN_ID, constants.getPath()));
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
		return service;
	}

	public Shell getShell(){
		if(!PlatformUI.isWorkbenchRunning()) return null;
		IWorkbench workbench = getWorkbench();
		IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
		if(activeWorkbenchWindow != null){
			return activeWorkbenchWindow.getShell();
		}
		return workbench.getDisplay().getActiveShell();
	}


	public ScriptingService getScriptingService() throws ScriptingException {
		ScriptingService service = (ScriptingService)scriptingServiceTracker.getService();
		if(service == null) throw new IllegalStateException("scripting service is not enabled.");
		Map<String,Object> beans= new HashMap<String, Object>();
		beans.put("RED", ColorTypes.RED);
		beans.put("YELLOW",ColorTypes.YELLOW);
		beans.put("GREEN",ColorTypes.GREEN);
		beans.put("LIGHT_GREEN",ColorTypes.LIGHT_GREEN);
		beans.put("LIGHT_BLUE",ColorTypes.LIGHT_BLUE);
		beans.put("BLUE",ColorTypes.BLUE);
		beans.put("PURPLE",ColorTypes.PURPLE);
		beans.put("RED_PURPLE",ColorTypes.RED_PURPLE);

		beans.put("FLAG_RED", FlagTypes.RED);
		beans.put("FLAG_WHITE",FlagTypes.WHITE);
		beans.put("FLAG_GREEN",FlagTypes.GREEN);
		beans.put("FLAG_BLUE",FlagTypes.BLUE);
		beans.put("FLAG_ORANGE",FlagTypes.ORANGE);
		
		service.init(beans);
		return service;
	}
	
}
