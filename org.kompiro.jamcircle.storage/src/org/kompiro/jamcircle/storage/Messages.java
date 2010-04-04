package org.kompiro.jamcircle.storage;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.kompiro.jamcircle.storage.messages"; //$NON-NLS-1$
	public static String FileStorageServiceImpl_copy_error_message;
	public static String FileStorageServiceImpl_initialized_error_message;
	public static String StorageServiceImpl_connect_database_task;
	public static String StorageServiceImpl_connection_message;
	public static String StorageServiceImpl_disposed_connection_task;
	public static String StorageServiceImpl_load_setting_message;
	public static String StorageServiceImpl_recreated_message;
	public static String StorageServiceImpl_store_setting_message;
	public static String StorageServiceImpl_test_mode_message;
	public static String StorageSettings_error_load_message;
	public static String StorageSettings_error_message;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
