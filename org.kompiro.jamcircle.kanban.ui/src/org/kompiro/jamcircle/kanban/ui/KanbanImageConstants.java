package org.kompiro.jamcircle.kanban.ui;

import static java.lang.String.format;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.*;
import org.eclipse.swt.graphics.Image;

public enum KanbanImageConstants {
	
	OPEN_LIST_ACTION_IMAGE("table_go.png"), //$NON-NLS-1$
	TRASH_FULL_IMAGE("recycle-full.png"), //$NON-NLS-1$
	TRASH_EMPTY_IMAGE("recycle-empty.png"), //$NON-NLS-1$
	SEND_OFF_IMAGE("send72_off.png"), //$NON-NLS-1$
	SEND_ON_IMAGE("send72_on.png"), //$NON-NLS-1$
	BACKGROUND_IMAGE("background/fuzzy-lightgrey.jpg"), //$NON-NLS-1$
	CONNECT_IMAGE("connect.png"), //$NON-NLS-1$
	DISCONNECT_IMAGE("disconnect.png"), //$NON-NLS-1$
	FILE_LINK_IMAGE("folder_link.png"), //$NON-NLS-1$
	FILE_GO_IMAGE("folder_go.png"), //$NON-NLS-1$
	LANE_ICONIZE_IMAGE("application_put.png"), //$NON-NLS-1$
	LANE_RESTORE_IMAGE("application_get.png"), //$NON-NLS-1$
	USER_IMAGE("user.png"), //$NON-NLS-1$
	COLOR_IMAGE("color_wheel.png"), //$NON-NLS-1$
	ADD_IMAGE("add.png"), //$NON-NLS-1$
	EDIT_IMAGE("edit.png"),  //$NON-NLS-1$
	KANBANS_IMAGE("kanbans.png"), //$NON-NLS-1$
	PAGE_IMAGE("page.png"), //$NON-NLS-1$
	COMPLETED_IMAGE("tick.png"), //$NON-NLS-1$
	INBOX_IMAGE("inbox.png"),  //$NON-NLS-1$
	CAMERA_IMAGE("camera.png"), //$NON-NLS-1$
	SAVE_IMAGE("disk.png"), //$NON-NLS-1$
	CLOCK_IMAGE("clock.png"), //$NON-NLS-1$
	CLOCK_RED_IMAGE("clock_red.png"), //$NON-NLS-1$
	DELETE_IMAGE("cross.png"), //$NON-NLS-1$
	OPEN_IMAGE("door_open.png"), //$NON-NLS-1$
	MOCK_IMAGE("script_gear.png"), //$NON-NLS-1$
	DB_ADD_IMAGE("database_add.png"), //$NON-NLS-1$
	FLAG_BLUE_IMAGE("flags/flag_blue.gif"), //$NON-NLS-1$
	FLAG_GREEN_IMAGE("flags/flag_green.gif"), //$NON-NLS-1$
	FLAG_ORANGE_IMAGE("flags/flag_orange.gif"), //$NON-NLS-1$
	FLAG_RED_IMAGE("flags/flag_red.gif"), //$NON-NLS-1$
	FLAG_WHITE_IMAGE("flags/flag_white.gif"); //$NON-NLS-1$
	
	private String path;
	private KanbanImageConstants(String path){
		this.path = path;
	}

	public String getPath(){
		return "icons/" + this.path; //$NON-NLS-1$
	}
	
	public URL getFileURL(){
		URL url = FileLocator.find(KanbanUIActivator.getDefault().getBundle(), new Path(getPath()), null);
		try {
			url = FileLocator.toFileURL(url);
		} catch (IOException e) {
			KanbanUIStatusHandler.fail(e, format("can't get file URL '%s'",getPath())); //$NON-NLS-1$
		}
		return url;
	}
	
	public Image getIamge(){
		return getImageRegistry().get(name());
	}
	
	private ImageRegistry getImageRegistry() {
		KanbanUIActivator activator = KanbanUIActivator.getDefault();
		if(activator == null) return JFaceResources.getImageRegistry();
		return activator.getImageRegistry();
	}

	public ImageDescriptor getImageDescriptor() {
		return getImageRegistry().getDescriptor(name());
	}

}
