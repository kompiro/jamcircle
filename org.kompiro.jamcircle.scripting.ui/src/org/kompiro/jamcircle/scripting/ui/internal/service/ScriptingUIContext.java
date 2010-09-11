package org.kompiro.jamcircle.scripting.ui.internal.service;

import java.io.PrintStream;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.*;
import org.kompiro.jamcircle.scripting.ScriptingService;
import org.kompiro.jamcircle.scripting.exception.ScriptingException;
import org.kompiro.jamcircle.scripting.ui.*;

public class ScriptingUIContext {

	private static ScriptingUIContext context;
	private ScriptingService scriptingService;

	public ScriptingUIContext() {
		ScriptingUIContext.context = this;
	}

	public void activate() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench == null)
			return;
		Display display = workbench.getDisplay();
		if (display == null)
			return;
		display.asyncExec(new Runnable() {
			public void run() {
				IOConsole console = new IOConsole("Scripting stream", getScriptGearImageDescriptor());
				IOConsoleOutputStream outputStream = console.newOutputStream();
				outputStream.setColor(ScriptingColorEnum.OUTPUT_STREAM_COLOR.getColor());
				scriptingService.setOutputStream(new PrintStream(outputStream));
				IOConsoleOutputStream errorStream = console.newOutputStream();
				errorStream.setColor(ScriptingColorEnum.ERROR_STREAM_COLOR.getColor());
				scriptingService.setErrorStream(new PrintStream(errorStream));
				getConsoleManager().addConsoles(new IConsole[] { console });
				try {
					scriptingService.initialize();
				} catch (ScriptingException e) {
				}
			}
		});
	}

	private ImageDescriptor getScriptGearImageDescriptor() {
		return getImageRegistry().getDescriptor(ScriptingImageEnum.SCRIPT_GEAR.getPath());
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

	public void setScriptingService(ScriptingService scriptingService) {
		this.scriptingService = scriptingService;
	}

	public static ScriptingUIContext getContext() {
		return context;
	}

	public ScriptingService getScriptingService() {
		return this.scriptingService;
	}

}
