package org.kompiro.jamcircle.scripting.ui.internal.service;

import org.kompiro.jamcircle.scripting.ScriptingService;
import org.kompiro.jamcircle.scripting.exception.ScriptingException;

public class ScriptingUIContext {

	private static ScriptingUIContext context;
	private ScriptingService scriptingService;

	public ScriptingUIContext() {
		ScriptingUIContext.context = this;
	}

	public void setScriptingService(ScriptingService scriptingService) throws ScriptingException {
		this.scriptingService = scriptingService;
	}

	public static ScriptingUIContext getContext() {
		return context;
	}

	public ScriptingService getScriptingService() {
		return this.scriptingService;
	}

}
