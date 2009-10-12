package org.kompiro.jamcircle.kanban.ui.action;

import java.util.List;


import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.CardContainer;
import org.kompiro.jamcircle.kanban.ui.editpart.CardContainerEditPart;
import org.kompiro.jamcircle.kanban.ui.internal.command.CutCardCommand;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.CardEditPart;

public class CutAction extends SelectionAction {

	public CutAction(IWorkbenchPart part) {
		super(part);
		setId(ActionFactory.CUT.getId());
		setText("Cut");
		setToolTipText("Cut");
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_CUT));
		setDisabledImageDescriptor(sharedImages.getImageDescriptor(
				ISharedImages.IMG_TOOL_CUT_DISABLED));
	}

	protected boolean calculateEnabled() {
		List<?> selectedObjects = getSelectedObjects();
		if(selectedObjects.isEmpty()) return false;
		if(selectedObjects.size() == 1) return true;
		for(Object obj : selectedObjects){
			if( ! (obj instanceof CardEditPart)) return false;
		}
		return true;
	}
	
	public void run() {
		List<?> selectedObjects = getSelectedObjects();
		CompoundCommand command = new CompoundCommand();
		for(Object obj : selectedObjects){
			if (obj instanceof CardEditPart) {
				CardEditPart cardEditPart = (CardEditPart) obj;
				EditPart part = cardEditPart.getParent();
				Card card = cardEditPart.getCardModel();
				if (part instanceof CardContainerEditPart) {
					CardContainerEditPart containerPart = (CardContainerEditPart) part;
					CardContainer container = containerPart.getCardContainer();
					command.add(new CutCardCommand(container,card));
				}
			}
		}
		getCommandStack().execute(command);
		Clipboard.getDefault().setContents(selectedObjects);
	}


}
