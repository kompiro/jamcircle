package org.kompiro.jamcircle.storage.ui;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.kompiro.jamcircle.storage.IUIStorageCallbackHandler;
import org.kompiro.jamcircle.storage.ui.wizard.StorageSettingWizard;


public class StorageCallbackHandler implements IUIStorageCallbackHandler {

	public StorageCallbackHandler() {
	}

	public void setupStorageSttting() {
		Display display = PlatformUI.getWorkbench().getDisplay();
		display.syncExec(new Runnable(){

			public void run() {
				StorageSettingWizard wizard = new StorageSettingWizard();
				WizardDialog dialog = new WizardDialog(new Shell(), wizard);
				dialog.open();
			}
		});
	}

}
