package org.kompiro.jamcircle.scripting.ui.internal.ruby.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.kompiro.jamcircle.scripting.ui.ScriptingImageEnum;
import org.kompiro.jamcircle.scripting.ui.ScriptingUIActivator;
import org.kompiro.jamcircle.scripting.ui.internal.ruby.job.ListGemJob;

public class GemListAction extends Action {

	private ListGemJob job = new ListGemJob();

	public GemListAction() {
		setImageDescriptor(getImageDescriptor(ScriptingImageEnum.RUBY_GO));
	}

	@Override
	public void run() {
		job.schedule();
	}

	public void setJob(ListGemJob job) {
		this.job = job;
	}

	private ImageDescriptor getImageDescriptor(ScriptingImageEnum key) {
		ScriptingUIActivator activator = ScriptingUIActivator.getDefault();
		if (activator == null) {
			return null;
		}
		return activator.getImageRegistry().getDescriptor(key.toString());
	}

}
