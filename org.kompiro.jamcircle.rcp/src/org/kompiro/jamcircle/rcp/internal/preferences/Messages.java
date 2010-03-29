package org.kompiro.jamcircle.rcp.internal.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.kompiro.jamcircle.rcp.internal.preferences.messages"; //$NON-NLS-1$
	public static String RCPPreferencePage_description;
	public static String RCPPreferencePage_minimized_message;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
