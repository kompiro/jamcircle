package org.kompiro.jamcircle.storage.ui.handlers;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
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
