package org.kompiro.jamcircle.scripting.ui.internal.eclipse.ui.console;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.console.*;
import org.kompiro.jamcircle.scripting.ui.*;

public class ScriptingConsoleFactory implements IConsoleFactory {
	
	public ScriptingConsoleFactory(){
	}
	
	public void openConsole() {
		ImageDescriptor imageDescriptor = getImageRegistry().getDescriptor(ScriptingImageEnum.SCRIPT_GEAR.toString());
		RubyScriptingConsole console = new RubyScriptingConsole(Messages.ScriptingConsoleFactory_console_name, imageDescriptor);
		getConsoleManager().addConsoles(new IConsole[]{console});
    	console.activate();
	}

	private ImageRegistry getImageRegistry() {
		return getActivator().getImageRegistry();
	}

	private ScriptingUIActivator getActivator() {
		return ScriptingUIActivator.getDefault();
	}

	private IConsoleManager getConsoleManager() {
		return ConsolePlugin.getDefault().getConsoleManager();
	}

}
