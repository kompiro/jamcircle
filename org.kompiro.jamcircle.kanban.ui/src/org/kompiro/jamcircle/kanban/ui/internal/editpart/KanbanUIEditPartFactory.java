package org.kompiro.jamcircle.kanban.ui.internal.editpart;


import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.ui.editpart.AbstractEditPart;
import org.kompiro.jamcircle.kanban.ui.editpart.IPropertyChangeDelegator;
import org.kompiro.jamcircle.kanban.ui.model.*;

public class KanbanUIEditPartFactory implements EditPartFactory{
	private BoardModel board;
	private IPropertyChangeDelegator delegator;
	private KanbanUIExtensionEditPartFactory extensionFactory;

	public KanbanUIEditPartFactory(BoardModel board){
		this.board = board;
	}

	/**
	 * For Test Constructor
	 * @param board
	 * @param delegator
	 */
	KanbanUIEditPartFactory(BoardModel board,IPropertyChangeDelegator delegator){
		this(board);
		this.delegator = delegator;
	}

	
	public EditPart createEditPart(EditPart context, Object model) {
		EditPart part;
		if (model instanceof Card) {
			part = new CardEditPart(board);
		}else if (model instanceof Lane){
			part = new LaneEditPart(board);				
		// FIXME extension factory
//		}else if (model instanceof UserModel){
//			part = new UserEditPart(board);				
		}else if (model instanceof TrashModel){
			part = new TrashEditPart(board);
		}else if (model instanceof LaneCreaterModel){
			part = new LaneCreaterEditPart(board);
		}else if(model instanceof BoardSelecterModel){
			part = new BoardSelecterEditPart(board);			
		}else if(model instanceof InboxIconModel){
			part = new InboxEditPart(board);
		}else if(model instanceof BoardModel){
			part = new BoardEditPart(board);
		}else{
			part = createFromExtension(context,model);
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

	private EditPart createFromExtension(EditPart context,Object model) {
		if(extensionFactory == null){
			return null;
		}
		return extensionFactory.createEditPart(context, model);
	}
	
	public void setExtensionFactory(KanbanUIExtensionEditPartFactory extensionFactory) {
		this.extensionFactory = extensionFactory;
		if(this.extensionFactory != null){
			this.extensionFactory.initialize();
		}
	}

}
