package org.kompiro.jamcircle.kanban.ui.model;

import org.kompiro.jamcircle.kanban.model.Icon;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.Messages;


public class DefaultIconModelFactory implements IconModelFactory {
	
	public static final String TYPE_OF_LANE_CREATER = "LaneCreaterModel"; //$NON-NLS-1$
	public static final String TYPE_OF_TRASH = "TrashModel"; //$NON-NLS-1$
	public static final String TYPE_OF_BOARD_SELECTER = "BoardSelecterModel"; //$NON-NLS-1$
	public static final String TYPE_OF_INBOX = "InboxIconModel"; //$NON-NLS-1$
	private KanbanService kanbanService;
	
	public DefaultIconModelFactory(KanbanService kanbanService) {
		this.kanbanService = kanbanService;
	}

	public IconModel create(Icon icon) {
		String type = icon.getClassType();
		if(type == null){
			String message = String.format(Messages.DefaultIconModelFactory_error_message,type);
			throw new IllegalArgumentException(message);
		}
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
		String message = String.format(Messages.DefaultIconModelFactory_error_message,type);
		throw new IllegalArgumentException(message);
	}

}
