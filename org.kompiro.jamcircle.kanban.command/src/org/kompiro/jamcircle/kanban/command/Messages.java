package org.kompiro.jamcircle.kanban.command;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.kompiro.jamcircle.kanban.command.messages"; //$NON-NLS-1$
	public static String AbstractCommand_activator_error_message;
	public static String AbstractCommand_can_not_execute_message;
	public static String AbstractCommand_error_message;
	public static String AbstractCommand_execute_message;
	public static String AddLaneTrashCommand_confirm_message;
	public static String CardUpdateCommand_message;
	public static String CardUpdateCommand_title;
	public static String ChangeLaneConstraintCommand_change_label;
	public static String ChangeLaneConstraintCommand_move_label;
	public static String MoveCardCommand_move_label;
	public static String MoveIconCommand_command_label;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
