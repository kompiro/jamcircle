package org.kompiro.jamcircle.scripting.ui.internal.ruby.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.kompiro.jamcircle.scripting.ui.*;
import org.kompiro.jamcircle.scripting.ui.internal.ruby.job.UninstallGemJob;

public class GemUninstallAction extends Action {

	private Shell shell;
	private UninstallGemJob job = new UninstallGemJob();

	public GemUninstallAction() {
		setToolTipText(Messages.GemUninstallAction_tooltip);
		setImageDescriptor(getImageDescriptor(ScriptingImageEnum.RUBY_DELETE));
	}

	@Override
	public void run() {
		InputDialog dialog = new InputDialog(shell, Messages.GemUninstallAction_dialog_title, Messages.GemUninstallAction_dialog_message, null, null);
		if (InputDialog.OK != dialog.open()) {
			return;
		}
		String target = dialog.getValue();

		job.setTarget(target);
		job.schedule();
	}

	public void setJob(UninstallGemJob job) {
		this.job = job;
	}

	public void setShell(Shell shell) {
		this.shell = shell;
	}

	private ImageDescriptor getImageDescriptor(ScriptingImageEnum key) {
		ScriptingUIActivator activator = ScriptingUIActivator.getDefault();
		if (activator == null) {
			return null;
		}
		return activator.getImageRegistry().getDescriptor(key.toString());
	}

}
