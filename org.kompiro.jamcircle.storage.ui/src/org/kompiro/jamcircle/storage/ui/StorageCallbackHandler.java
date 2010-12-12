package org.kompiro.jamcircle.storage.ui;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.*;
import org.kompiro.jamcircle.storage.IUIStorageCallbackHandler;
import org.kompiro.jamcircle.storage.ui.wizard.StorageSettingWizard;

public class StorageCallbackHandler implements IUIStorageCallbackHandler {

	public StorageCallbackHandler() {
	}

	public void setupStorageSetting() {
		Display display = getDisplay();
		display.syncExec(new Runnable() {

			public void run() {
				StorageSettingWizard wizard = new StorageSettingWizard();
				WizardDialog dialog = new WizardDialog(new Shell(), wizard);
				dialog.open();
			}
		});
	}

	public Display getDisplay() {
		IWorkbench workbench = getWorkbench();
		try {
			if (workbench == null)
				return null;
			Display display = workbench.getDisplay();
			if (display != null)
				return display;
			IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
			if (activeWorkbenchWindow == null)
				return null;
			display = activeWorkbenchWindow.getShell().getDisplay();
			return display;
		} catch (IllegalStateException e) {
			return null;
		}
	}

	public IWorkbench getWorkbench() {
		if (Platform.isRunning()) {
			if (PlatformUI.isWorkbenchRunning()) {
				return PlatformUI.getWorkbench();
			}
		}
		return null;
	}

}
