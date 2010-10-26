package org.kompiro.jamcircle.scripting.console.internal;

import java.io.OutputStream;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.console.*;
import org.kompiro.jamcircle.scripting.IScriptingEngineStreamLoader;

public class ScriptingStreamInitializer implements IScriptingEngineStreamLoader {

	static IOConsoleOutputStream outputStream;
	static IOConsoleOutputStream errorStream;

	public static ScriptingStreamInitializer initializer;

	public ScriptingStreamInitializer() {
		initializer = this;
		// ImageDescriptor desc = getImageDescriptor();
		ImageDescriptor desc = null;
		IOConsole console = new IOConsole("Scripting stream", desc);
		ScriptingStreamInitializer.outputStream = console.newOutputStream();
		ScriptingStreamInitializer.errorStream = console.newOutputStream();
		getConsoleManager().addConsoles(new IConsole[] { console });
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public OutputStream getErrorStream() {
		return errorStream;
	}

	private IConsoleManager getConsoleManager() {
		return ConsolePlugin.getDefault().getConsoleManager();
	}

}
