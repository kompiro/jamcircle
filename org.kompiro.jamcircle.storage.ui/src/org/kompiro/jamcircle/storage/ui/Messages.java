package org.kompiro.jamcircle.storage.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.kompiro.jamcircle.storage.ui.messages"; //$NON-NLS-1$
	public static String StorageSettingWizard_auth_label;
	public static String StorageSettingWizard_browse_label;
	public static String StorageSettingWizard_current_path_label;
	public static String StorageSettingWizard_delete_label;
	public static String StorageSettingWizard_error_title;
	public static String StorageSettingWizard_not_found_directory_error;
	public static String StorageSettingWizard_password_label;
	public static String StorageSettingWizard_store_path_label;
	public static String StorageSettingWizard_user_label;
	public static String StorageSettingWizard_window_title;
	public static String StorageSettingWizard_wizard_description;
	public static String StorageSettingWizard_wizard_title;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
