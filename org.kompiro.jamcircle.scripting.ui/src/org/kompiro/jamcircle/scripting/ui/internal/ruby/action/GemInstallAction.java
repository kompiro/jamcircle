package org.kompiro.jamcircle.scripting.ui.internal.ruby.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.kompiro.jamcircle.scripting.ui.ScriptingImageEnum;
import org.kompiro.jamcircle.scripting.ui.ScriptingUIActivator;
import org.kompiro.jamcircle.scripting.ui.internal.ruby.job.InstallGemJob;

public class GemInstallAction extends Action {

	private Shell shell;
	private InstallGemJob job = new InstallGemJob();

	public GemInstallAction() {
		setToolTipText("install gem");
		setImageDescriptor(getImageDescriptor(ScriptingImageEnum.RUBY_ADD));
	}

	@Override
	public void run() {
		InputDialog dialog = new InputDialog(shell, "Install gem", "What gem do you want to install?", null, null);
		if (InputDialog.OK != dialog.open()) {
			return;
		}
		String target = dialog.getValue();

		job.setTarget(target);
		job.schedule();
	}

	public void setJob(InstallGemJob job) {
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
