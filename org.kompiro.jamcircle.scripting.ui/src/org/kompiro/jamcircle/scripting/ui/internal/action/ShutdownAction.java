package org.kompiro.jamcircle.scripting.ui.internal.eclipse.ui.console;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.kompiro.jamcircle.scripting.ui.ScriptingImageEnum;
import org.kompiro.jamcircle.scripting.ui.ScriptingUIActivator;

public class ShutdownAction extends Action {

	private RubyScriptingConsole console;

	public ShutdownAction(RubyScriptingConsole console) {
		this.console = console;
		setToolTipText("shutdown RubyScriptingconsole");
		setHoverImageDescriptor(getImageDescriptor(ScriptingImageEnum.IMG_ELCL_CLOSE));
		setDisabledImageDescriptor(getImageDescriptor(ScriptingImageEnum.IMG_ELCL_CLOSE));
		setImageDescriptor(getImageDescriptor(ScriptingImageEnum.IMG_ELCL_CLOSE));
	}

	private ImageDescriptor getImageDescriptor(ScriptingImageEnum key) {
		return ScriptingUIActivator.getDefault().getImageRegistry().getDescriptor(key.toString());
	}

	@Override
	public void run() {
		console.shutdown();
	}

}
