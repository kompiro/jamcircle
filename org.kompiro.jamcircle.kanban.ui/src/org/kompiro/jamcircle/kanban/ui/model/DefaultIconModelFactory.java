package org.kompiro.jamcircle.kanban.ui.model;

import org.kompiro.jamcircle.kanban.model.Icon;
import org.kompiro.jamcircle.kanban.service.KanbanService;


public class DefaultIconModelFactory implements IconModelFactory {
	
	public static final String TYPE_OF_LANE_CREATER = "LaneCreaterModel";
	public static final String TYPE_OF_TRASH = "TrashModel";
	public static final String TYPE_OF_BOARD_SELECTER = "BoardSelecterModel";
	public static final String TYPE_OF_INBOX = "InboxIconModel";
	private KanbanService kanbanService;
	
	public DefaultIconModelFactory(KanbanService kanbanService) {
		this.kanbanService = kanbanService;
	}

	public IconModel create(Icon icon) {
		String type = icon.getClassType();
		type = type.substring(type.lastIndexOf('.') + 1);
		if(TYPE_OF_BOARD_SELECTER.equals(type)){
			BoardSelecterModel model = new BoardSelecterModel(icon);
			return model;
		}else if(TYPE_OF_LANE_CREATER.equals(type)){
			LaneCreaterModel model = new LaneCreaterModel(icon);
			return model;
		}else if(TYPE_OF_TRASH.equals(type)){
			TrashModel model = new TrashModel(icon,kanbanService);
			return model;
		}else if(TYPE_OF_INBOX.equals(type)){
			InboxIconModel model = new InboxIconModel(icon);
			return model;
		}
		throw new IllegalArgumentException("");
	}

}
