package org.kompiro.jamcircle.scripting.ui.internal.ruby.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.kompiro.jamcircle.scripting.ui.ScriptingImageEnum;
import org.kompiro.jamcircle.scripting.ui.ScriptingUIActivator;
import org.kompiro.jamcircle.scripting.ui.internal.ruby.console.RubyScriptingConsole;

public class ShutdownAction extends Action {

	private RubyScriptingConsole console;

	public ShutdownAction() {
		setToolTipText("Shutdown RubyScriptingConsole");
		setHoverImageDescriptor(getImageDescriptor(ScriptingImageEnum.IMG_ELCL_CLOSE));
		setDisabledImageDescriptor(getImageDescriptor(ScriptingImageEnum.IMG_ELCL_CLOSE));
		setImageDescriptor(getImageDescriptor(ScriptingImageEnum.IMG_ELCL_CLOSE));
	}

	private ImageDescriptor getImageDescriptor(ScriptingImageEnum key) {
		ScriptingUIActivator activator = ScriptingUIActivator.getDefault();
		if (activator == null)
			return null;
		return activator.getImageRegistry().getDescriptor(key.toString());
	}

	@Override
	public void run() {
		console.shutdown();
	}

	public void setConsole(RubyScriptingConsole console) {
		this.console = console;
	}

}
