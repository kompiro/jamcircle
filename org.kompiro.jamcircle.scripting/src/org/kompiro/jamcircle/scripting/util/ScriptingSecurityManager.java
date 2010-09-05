package org.kompiro.jamcircle.scripting.util;

import java.security.Permission;

public class ScriptingSecurityManager extends SecurityManager {

	private static final ScriptingSecurityManager manager = new ScriptingSecurityManager();

	private boolean scriptRunning;
	private static ScriptingSecurityHelper helper;

	private ScriptingSecurityManager() {
		System.setSecurityManager(this);
	}

	@Override
	public void checkExit(int status) {
		if (scriptRunning) {
			if (helper == null)
				throw new SecurityException();
			helper.throwSecurityException();
		}
	}

	@Override
	public void checkPermission(final Permission perm) {
	}

	@Override
	public void checkPermission(final Permission perm, final Object context) {
	}

	private void setScriptRunning(boolean scriptRunning) {
		this.scriptRunning = scriptRunning;
	}

	public static void setScriptingExitHelper(ScriptingSecurityHelper helper) {
		ScriptingSecurityManager.helper = helper;
	}

	public static void runScript() {
		manager.setScriptRunning(true);
	}

	public static void finishedScript() {
		manager.setScriptRunning(false);
	}

}
