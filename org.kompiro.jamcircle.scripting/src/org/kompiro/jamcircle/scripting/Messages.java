package org.kompiro.jamcircle.scripting;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.kompiro.jamcircle.scripting.messages"; //$NON-NLS-1$
	public static String ScriptingEngineInitializerLoaderImpl_initialize_error;
	public static String ScriptingServiceImpl_error_title;
	public static String ScriptingServiceImpl_initialized_error_message;
	public static String ScriptingServiceImpl_reading_template_error_message;
	public static String ScriptingServiceImpl_script_type_error;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
