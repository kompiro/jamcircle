package org.kompiro.jamcircle.kanban.model;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.kompiro.jamcircle.kanban.model.messages"; //$NON-NLS-1$
	public static String LaneImpl_fire_debug_message;
	public static String LaneImpl_error_sql;
	public static String LaneImpl_error_parent_board;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
