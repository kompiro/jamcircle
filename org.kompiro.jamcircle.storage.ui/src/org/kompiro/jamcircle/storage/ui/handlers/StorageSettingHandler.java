package org.kompiro.jamcircle.storage.ui.handlers;


import org.eclipse.core.commands.*;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.kompiro.jamcircle.storage.ui.wizard.StorageSettingWizard;

public class StorageSettingHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		StorageSettingWizard wizard = new StorageSettingWizard();
		IWorkbenchWindow activeWorkbenchWindow = HandlerUtil.getActiveWorkbenchWindow(event);
		WizardDialog dialog = new WizardDialog(activeWorkbenchWindow.getShell(), wizard);
		dialog.open();
		return null;
	}

}
