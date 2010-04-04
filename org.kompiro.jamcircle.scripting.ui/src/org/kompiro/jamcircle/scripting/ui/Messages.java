package org.kompiro.jamcircle.scripting.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.kompiro.jamcircle.scripting.ui.messages"; //$NON-NLS-1$
	public static String RubyScriptingConsole_initialize_message;
	public static String RubyScriptingConsole_initialize_runtime_message;
	public static String RubyScriptingConsole_join_error_title;
	public static String RubyScriptingConsole_no_context_error_message;
	public static String RubyScriptingConsole_run_irb_message;
	public static String ScriptingConsoleFactory_console_name;
	public static String ScriptingUIActivator_error_message;
	public static String ScrollLockAction_text;
	public static String ScrollLockAction_tooltip;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
