package org.kompiro.jamcircle.xmpp;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.kompiro.jamcircle.xmpp.messages"; //$NON-NLS-1$
	public static String XMPPConnectionServiceImpl_begin_connecting_message;
	public static String XMPPConnectionServiceImpl_connected_task;
	public static String XMPPConnectionServiceImpl_connecting_message;
	public static String XMPPConnectionServiceImpl_disconnect_error_message;
	public static String XMPPConnectionServiceImpl_disconnect_task_name;
	public static String XMPPConnectionServiceImpl_initialize_connection_error_message;
	public static String XMPPConnectionServiceImpl_logged_in_task;
	public static String XMPPConnectionServiceImpl_message_body;
	public static String XMPPConnectionServiceImpl_send_failed_error_message;
	public static String XMPPLoginListenerFactory_initialized_error_message;
	public static String XMPPSettings_load_error_message;
	public static String XMPPSettings_store_error_message;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
