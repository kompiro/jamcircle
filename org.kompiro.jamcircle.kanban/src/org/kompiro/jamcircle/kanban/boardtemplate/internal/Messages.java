package org.kompiro.jamcircle.kanban.boardtemplate.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.kompiro.jamcircle.kanban.boardtemplate.internal.messages"; //$NON-NLS-1$
	public static String ColorBoardTemplate_board_name;
	public static String ColorBoardTemplate_description;
	public static String ColorBoardTemplate_error_message;
	public static String NoLaneBoardTemplate_board_name;
	public static String NoLaneBoardTemplate_description;
	public static String TaskBoardTemplate_board_name;
	public static String TaskBoardTemplate_description;
	public static String TaskBoardTemplate_error_message;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
