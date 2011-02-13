package org.kompiro.jamcircle.kanban.ui.model;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.kompiro.jamcircle.kanban.ui.model.messages"; //$NON-NLS-1$
	public static String BoardModel_container_name;
	public static String BoardSelecterModel_name;
	public static String DefaultIconModelFactory_error_message;
	public static String InboxIconModel_name;
	public static String LaneCreaterModel_name;
	public static String MessageDialogConfirmStrategy_confirm_title;
	public static String TrashModel_confirm_message;
	public static String TrashModel_container_name;
	public static String TrashModel_name;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
