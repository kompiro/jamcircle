package org.kompiro.jamcircle.kanban.ui;

public enum KanbanImageConstants {
	
	OPEN_LIST_ACTION_IMAGE("table_go.png"),
	TRASH_FULL_IMAGE("recycle-full.png"),
	TRASH_EMPTY_IMAGE("recycle-empty.png"),
	SEND_OFF_IMAGE("send72_off.png"),
	SEND_ON_IMAGE("send72_on.png"),
	BACKGROUND_IMAGE("background/fuzzy-lightgrey.jpg"),
	CONNECT_IMAGE("connect.png"),
	DISCONNECT_IMAGE("disconnect.png"),
	FILE_LINK_IMAGE("folder_link.png"),
	FILE_GO_IMAGE("folder_go.png"),
	LANE_ICONIZE_IMAGE("application_put.png"),
	LANE_RESTORE_IMAGE("application_get.png"),
	USER_IMAGE("user.png"),
	COLOR_IMAGE("color_wheel.png"),
	ADD_IMAGE("add.png"),
	EDIT_IMAGE("edit.png"), 
	KANBANS_IMAGE("kanbans.png"),
	PAGE_IMAGE("page.png"),
	COMPLETED_IMAGE("tick.png"),
	INBOX_IMAGE("inbox.png"), 
	CAMERA_IMAGE("camera.png"),
	SAVE_IMAGE("disk.png"),
	CLOCK_IMAGE("clock.png"),
	CLOCK_RED_IMAGE("clock_red.png"),
	DELETE_IMAGE("cross.png"),
	OPEN_IMAGE("door_open.png"),
	MOCK_IMAGE("script_gear.png"),
	FLAG_BLUE_IMAGE("flags/flag_blue.gif"),
	FLAG_GREEN_IMAGE("flags/flag_green.gif"),
	FLAG_ORANGE_IMAGE("flags/flag_orange.gif"),
	FLAG_RED_IMAGE("flags/flag_red.gif"),
	FLAG_WHITE_IMAGE("flags/flag_white.gif");
	
	private String path;
	private KanbanImageConstants(String path){
		this.path = path;
	}

	public String getPath(){
		return "icons/" + this.path;
	}
}
