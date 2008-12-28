package org.kompiro.jamcircle.kanban.ui;


import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.ui.gcontroller.BoardEditPart;
import org.kompiro.jamcircle.kanban.ui.gcontroller.BoardSelecterEditPart;
import org.kompiro.jamcircle.kanban.ui.gcontroller.CardEditPart;
import org.kompiro.jamcircle.kanban.ui.gcontroller.InboxEditPart;
import org.kompiro.jamcircle.kanban.ui.gcontroller.LaneCreaterEditPart;
import org.kompiro.jamcircle.kanban.ui.gcontroller.LaneEditPart;
import org.kompiro.jamcircle.kanban.ui.gcontroller.TrashEditPart;
import org.kompiro.jamcircle.kanban.ui.gcontroller.UserEditPart;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;
import org.kompiro.jamcircle.kanban.ui.model.BoardSelecterModel;
import org.kompiro.jamcircle.kanban.ui.model.InboxIconModel;
import org.kompiro.jamcircle.kanban.ui.model.LaneCreaterModel;
import org.kompiro.jamcircle.kanban.ui.model.TrashModel;
import org.kompiro.jamcircle.kanban.ui.model.UserModel;

public class KanbanControllerFactory implements EditPartFactory{
	
	private BoardModel board;

	public KanbanControllerFactory(BoardModel board){
		this.board = board;
	}

	public EditPart createEditPart(EditPart context, Object model) {
		EditPart part;
		if (model instanceof Card) {
			part = new CardEditPart(board);
		}else if (model instanceof Lane){
			part = new LaneEditPart(board);				
		}else if (model instanceof UserModel){
			part = new UserEditPart(board);				
		}else if (model instanceof TrashModel){
			part = new TrashEditPart(board);
		}else if (model instanceof LaneCreaterModel){
			part = new LaneCreaterEditPart(board);
		}else if(model instanceof BoardSelecterModel){
			part = new BoardSelecterEditPart(board);			
		}else if(model instanceof InboxIconModel){
			part = new InboxEditPart(board);
		}else{
			part = new BoardEditPart(board);
		}
		part.setModel(model);
		return part;
	}
	
}
