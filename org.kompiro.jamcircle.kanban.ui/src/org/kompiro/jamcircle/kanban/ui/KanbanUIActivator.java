package org.kompiro.jamcircle.kanban.ui;


import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.kompiro.jamcircle.debug.IStatusHandler;
import org.kompiro.jamcircle.kanban.KanbanStatusHandler;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.util.WorkbenchUtil;
import org.kompiro.jamcircle.scripting.ScriptingService;
import org.kompiro.jamcircle.scripting.exception.ScriptingException;
import org.kompiro.jamcircle.storage.StorageStatusHandler;
import org.osgi.framework.BundleContext;

public class KanbanUIActivator extends AbstractUIPlugin {
	
	private final class MessageDialogStatusHandler implements IStatusHandler {
		public void displayStatus(String title, IStatus status) {
		}

		public void fail(IStatus status, boolean informUser) {
			Throwable exception = status.getException();
			if(informUser && ! getKanbanService().isTestMode()){
				String message = String.format("%s\nException:'%s' reason: %s", status.getMessage(),exception.getClass().getName(),exception.getLocalizedMessage());
				Shell shell = getShell();
				if(shell != null){
					MessageDialog.openError(shell, "Unexpected error is occured.",message);
				}
			}
		}

		public void info(String message) {
		}
	}

	public static final String ID_PLUGIN = "org.kompiro.jamcircle.kanban.ui";

	private static KanbanUIActivator plugin;

	private IStatusHandler dialogHandler;

	public KanbanUIActivator() {
	}
	

	public void start(BundleContext context) throws Exception {
		plugin = this;
		super.start(context);

		handleDialogForStatusHandler();
//		migrate();
	}


	private void handleDialogForStatusHandler() {
		dialogHandler = new MessageDialogStatusHandler();
 		StorageStatusHandler.addStatusHandler(dialogHandler);
 		KanbanStatusHandler.addStatusHandler(dialogHandler);
 		KanbanUIStatusHandler.addStatusHandler(dialogHandler);
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;

		KanbanStatusHandler.removeStatusHandler(dialogHandler);
		KanbanUIStatusHandler.removeStatusHandler(dialogHandler);
		StorageStatusHandler.removeStatusHandler(dialogHandler);
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
	
	public KanbanService getKanbanService(){
		KanbanService service = KanbanUIContext.getDefault().getKanbanService();
		if(service == null) throw new IllegalStateException("kanban service is not enabled.");		
		service .init();
		return service;
	}

	public Shell getShell(){
		return WorkbenchUtil.getShell();
	}


	public ScriptingService getScriptingService() throws ScriptingException {
		ScriptingService service = KanbanUIContext.getDefault().getScriptingService();
		if(service == null) throw new IllegalStateException("scripting service is not enabled.");		
		return service;
	}
	
	public static IStatus createErrorStatus(Throwable e){
		return new Status(IStatus.ERROR, ID_PLUGIN, e.getLocalizedMessage(),e);
	}

}
