package org.kompiro.jamcircle.kanban;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.kompiro.jamcircle.kanban.messages"; //$NON-NLS-1$
	public static String LaneImpl_fire_debug_message;
	public static String LaneImpl_error_sql;
	public static String LaneImpl_error_parent_board;
	public static String BoardScriptTemplateDescriptor_error_message;
	public static String ColorBoardTemplate_board_name;
	public static String ColorBoardTemplate_description;
	public static String ColorBoardTemplate_error_message;
	public static String KanbanServiceImpl_database_initialize_message;
	public static String KanbanServiceImpl_error_load_template;
	public static String KanbanServiceImpl_error_trash_count;
	public static String KanbanServiceImpl_infomation_call_forceInit;
	public static String KanbanServiceImpl_sample_board_name;
	public static String NoLaneBoardTemplate_board_name;
	public static String NoLaneBoardTemplate_description;
	public static String TaskBoardTemplate_board_name;
	public static String TaskBoardTemplate_description;
	public static String TaskBoardTemplate_error_message;
	public static String StreamReadWrapper_error_message;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
