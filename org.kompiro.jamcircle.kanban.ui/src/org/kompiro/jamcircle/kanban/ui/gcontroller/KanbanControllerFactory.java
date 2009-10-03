package org.kompiro.jamcircle.kanban.ui.gcontroller;


import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.ui.model.*;

public class KanbanControllerFactory implements EditPartFactory{
	
	private BoardModel board;
	private IPropertyChangeDelegator delegator;

	public KanbanControllerFactory(BoardModel board){
		this.board = board;
	}

	public KanbanControllerFactory(BoardModel board,IPropertyChangeDelegator delegator){
		this(board);
		this.delegator = delegator;
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
		if(delegator != null){
			if (part instanceof AbstractEditPart) {
				AbstractEditPart editPart = (AbstractEditPart) part;
				editPart.setDelegator(delegator);
			}
		}
		part.setModel(model);
		return part;
	}
	
}
