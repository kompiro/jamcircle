package org.kompiro.jamcircle.scripting.ui.internal.handlers;

import org.eclipse.core.commands.*;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.kompiro.jamcircle.scripting.ui.internal.job.InstallGemJob;

public class GemInstallHandler extends AbstractHandler {
	public GemInstallHandler() {
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell parentShell = HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell();
		InputDialog dialog = new InputDialog(parentShell, "Install gem", "What gem do you want to install?", null, null);
		if (InputDialog.OK != dialog.open()) {
			return null;
		}
		String target = dialog.getValue();

		InstallGemJob job = new InstallGemJob();
		job.setTarget(target);
		job.schedule();
		return null;
	}
}
