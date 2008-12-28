package org.kompiro.jamcircle.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.update.ui.UpdateManagerUI;

public class ManageExtensionsAction extends Action {

	private IWorkbenchWindow window;

	public ManageExtensionsAction(IWorkbenchWindow window){
		super("&Manage Extensions");
		setId("org.kompiro.jamcirc.e.manage.extensions");
		setToolTipText("Manage Installed Extensions");
		this.window = window;
	}
	
	
	@Override
	public void run() {
		BusyIndicator.showWhile(window.getShell().getDisplay(), new Runnable(){
			public void run() {
				UpdateManagerUI.openConfigurationManager(window.getShell());
			}
		});
	}
	
}