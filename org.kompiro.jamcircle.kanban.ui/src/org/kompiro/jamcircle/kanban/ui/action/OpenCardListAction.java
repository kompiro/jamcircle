package org.kompiro.jamcircle.kanban.ui.action;

import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.kompiro.jamcircle.kanban.model.CardContainer;
import org.kompiro.jamcircle.kanban.ui.KanbanImageConstants;
import org.kompiro.jamcircle.kanban.ui.KanbanUIActivator;
import org.kompiro.jamcircle.kanban.ui.Messages;
import org.kompiro.jamcircle.kanban.ui.editpart.CardContainerEditPart;
import org.kompiro.jamcircle.kanban.ui.widget.CardListWindow;

public class OpenCardListAction extends SelectionAction {
	
	private CardContainerEditPart targetEditPart;
	private ApplicationWindow window;

	public OpenCardListAction(IWorkbenchPart part){
		super(part);
		setImageDescriptor(KanbanUIActivator.getDefault().getImageRegistry().getDescriptor(KanbanImageConstants.OPEN_LIST_ACTION_IMAGE.toString()));
		setText(Messages.OpenCardListAction_text);
		setToolTipText(Messages.OpenCardListAction_tooltip);
	}
	
	@Override
	public void run() {
		window = new CardListWindow(getShell(), getContainer(), getCommandStack());
		window.open();
	}

	private Shell getShell() {
		return getWorkbenchPart().getSite().getShell();
	}

	private CardContainer getContainer() {
		return targetEditPart.getCardContainer();
	}

	@Override
	protected boolean calculateEnabled() {
		if(window == null || window.getShell() == null || window.getShell().isDisposed()){
			if( getSelectedObjects().size() == 1 && getSelectedObjects().get(0) instanceof CardContainerEditPart){
				targetEditPart = (CardContainerEditPart)getSelectedObjects().get(0);
				return true;
			}			
		}
		return false;
	}
}
