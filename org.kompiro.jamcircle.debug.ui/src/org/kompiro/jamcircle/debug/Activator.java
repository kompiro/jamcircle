package org.kompiro.jamcircle.debug;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.kompiro.jamcircle.kanban.KanbanStatusHandler;
import org.kompiro.jamcircle.kanban.ui.KanbanUIStatusHandler;
import org.kompiro.jamcircle.storage.StorageStatusHandler;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.kompiro.jamcircle.debug";

	// The shared instance
	private static Activator plugin;
	
	private IStatusHandler dialogHandler;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		dialogHandler = new IStatusHandler(){
		
			public void displayStatus(String title, IStatus status) {
//						MessageDialog.openWarning(getShell(), title, status.getMessage());
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
//						MessageDialog.openInformation(getShell(), "Status Infomation", message);
				System.out.println(message);
			}
			
		};
		StorageStatusHandler.addStatusHandler(dialogHandler);
		KanbanStatusHandler.addStatusHandler(dialogHandler);
		KanbanUIStatusHandler.addStatusHandler(dialogHandler);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	
	public Shell getShell(){
		IWorkbench workbench = getWorkbench();
		IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
		return activeWorkbenchWindow.getShell();
	}

}
