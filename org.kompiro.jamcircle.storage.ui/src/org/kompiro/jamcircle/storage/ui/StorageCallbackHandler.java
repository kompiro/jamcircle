package org.kompiro.jamcircle.storage.ui;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.kompiro.jamcircle.storage.IUIStorageCallbackHandler;
import org.kompiro.jamcircle.storage.ui.wizard.StorageSettingWizard;


public class StorageCallbackHandler implements IUIStorageCallbackHandler {

	public StorageCallbackHandler() {
	}

	public void setupStorageSttting() {
		Display display = getDisplay();
		display.syncExec(new Runnable(){

			public void run() {
				StorageSettingWizard wizard = new StorageSettingWizard();
				WizardDialog dialog = new WizardDialog(new Shell(), wizard);
				dialog.open();
			}
		});
	}

	public Display getDisplay(){
		IWorkbench workbench = getWorkbench();
		Display defDisplay = Display.getDefault();
		if(workbench == null){
			return defDisplay;
		}
		try{
			if(workbench == null) return defDisplay;
			Display display = workbench.getDisplay();
			if(display != null) return display;
			IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
			if(activeWorkbenchWindow == null) return defDisplay;
			display = activeWorkbenchWindow.getShell().getDisplay();
			return display;
		}catch(IllegalStateException e){
			return defDisplay;
		}
	}
	
	public IWorkbench getWorkbench() {
		if(Platform.isRunning()){
			if(PlatformUI.isWorkbenchRunning()){
				return PlatformUI.getWorkbench();
			}
		}
		return null;
	}
	
}
