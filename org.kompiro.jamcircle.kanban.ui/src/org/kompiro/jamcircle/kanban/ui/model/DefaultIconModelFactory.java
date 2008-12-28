package org.kompiro.jamcircle.kanban.ui.model;

import org.kompiro.jamcircle.kanban.model.Icon;


public class DefaultIconModelFactory implements IconModelFactory {
	
	public static final String TYPE_OF_LANE_CREATER = "LaneCreaterModel";
	public static final String TYPE_OF_TRASH = "TrashModel";
	public static final String TYPE_OF_BOARD_SELECTER = "BoardSelecterModel";
	public static final String TYPE_OF_INBOX = "InboxIconModel";
	
	public IconModel create(Icon icon) {
//		Class<?> clazz = null;
//		try {
//			clazz = Class.forName(icon.getClassType());
//			Constructor<?> constructor = clazz.getConstructor(Icon.class);
//			return (IconModel)constructor.newInstance(icon);
//		} catch (Exception e) {
//			String errorMessage = String.format("can't create icon model '%s'",icon.getClassType());
//			KanbanUIStatusHandler.fail(e, errorMessage);
//		}
//		return null;
		String type = icon.getClassType();
		type = type.substring(type.lastIndexOf('.') + 1);
		if(TYPE_OF_BOARD_SELECTER.equals(type)){
			BoardSelecterModel model = new BoardSelecterModel(icon);
			return model;
		}else if(TYPE_OF_LANE_CREATER.equals(type)){
			LaneCreaterModel model = new LaneCreaterModel(icon);
			return model;
		}else if(TYPE_OF_TRASH.equals(type)){
			TrashModel model = new TrashModel(icon);
			return model;
		}else if(TYPE_OF_INBOX.equals(type)){
			InboxIconModel model = new InboxIconModel(icon);
			return model;
		}
		throw new IllegalArgumentException("");
	}

}
