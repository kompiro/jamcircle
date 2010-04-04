package org.kompiro.jamcircle.xmpp.kanban.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.kompiro.jamcircle.xmpp.kanban.ui.messages"; //$NON-NLS-1$
	public static String BridgeXMPPActivator_error_message;
	public static String CardReceiveFileTransferListener_card_error_message;
	public static String CardReceiveFileTransferListener_error_message;
	public static String KanbanXMPPLoginListener_error_message;
	public static String MoveUserCommand_move_label;
	public static String SendCardCommand_confirm_message;
	public static String SendCardCommand_not_available_error_message;
	public static String SendCardCommand_not_supprt_send_error;
	public static String SendCardCommand_target_is_null_error_message;
	public static String SendCardCommand_target_part_null_error;
	public static String UserEditPart_shell_title;
	public static String UserFigure_from_label;
	public static String UserFigure_name_label;
	public static String UserFigure_user_id_label;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
