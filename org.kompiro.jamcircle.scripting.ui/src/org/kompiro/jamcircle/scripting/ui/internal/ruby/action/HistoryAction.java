package org.kompiro.jamcircle.scripting.ui.internal.ruby.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.kompiro.jamcircle.scripting.ui.ScriptingImageEnum;
import org.kompiro.jamcircle.scripting.ui.ScriptingUIActivator;
import org.kompiro.jamcircle.scripting.ui.internal.ruby.console.RubyScriptingConsole;
import org.kompiro.jamcircle.scripting.ui.internal.ruby.dialog.HistoryWindow;

public class HistoryAction extends Action {

	private RubyScriptingConsole console;

	public HistoryAction() {
		setToolTipText("show history of run script");
		setHoverImageDescriptor(getImageDescriptor(ScriptingImageEnum.EDIT));
		setDisabledImageDescriptor(getImageDescriptor(ScriptingImageEnum.EDIT));
		setImageDescriptor(getImageDescriptor(ScriptingImageEnum.EDIT));
	}

	private ImageDescriptor getImageDescriptor(ScriptingImageEnum key) {
		ScriptingUIActivator activator = ScriptingUIActivator.getDefault();
		if (activator == null)
			return null;
		return activator.getImageRegistry().getDescriptor(key.toString());
	}

	@Override
	public void run() {
		HistoryWindow window = new HistoryWindow(null, console.getHistoryList());
		window.create();
		window.open();
	}

	public void setConsole(RubyScriptingConsole console) {
		this.console = console;
	}

}
