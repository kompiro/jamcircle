package org.kompiro.jamcircle.kanban.ui.action;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IWorkbenchPart;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.CardContainer;
import org.kompiro.jamcircle.kanban.ui.*;
import org.kompiro.jamcircle.kanban.ui.command.provider.ConfirmProvider;
import org.kompiro.jamcircle.kanban.ui.command.provider.MessageDialogConfirmProvider;
import org.kompiro.jamcircle.kanban.ui.editpart.CardContainerEditPart;
import org.kompiro.jamcircle.kanban.ui.internal.command.CardUpdateCommand;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.TrashEditPart;
import org.kompiro.jamcircle.kanban.ui.widget.CardListEditProvider;
import org.kompiro.jamcircle.kanban.ui.widget.CardListTableViewer;

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
		Shell shell = getWorkbenchPart().getSite().getShell();
		window = new ApplicationWindow(shell){
			private CardListTableViewer viewer;
//			private CardListListener listener;
			@Override
			protected Control createContents(Composite parent) {
//				listener = new CardListListener(){
//
//					public void dragFinished(DragSourceEvent event,CardListTableViewer viewer) {
//						viewer.refresh();
//					}
//					
//				};
				
				this.viewer = new CardListTableViewer(parent);
//				viewer.addCardListListener(listener);
				viewer.setInput(getContainer());
				getContainer().addPropertyChangeListener(viewer);
				if( ! (targetEditPart instanceof TrashEditPart)){
					viewer.setEditProvider(new CardListEditProvider(){
						public void edit(Card card, String subject,
								String content, Date dueDate, List<File> files) {
							ConfirmProvider provider = new MessageDialogConfirmProvider(getShell());
							CardUpdateCommand command = new CardUpdateCommand(provider ,card,subject,content,dueDate,files);
							CommandStack commandStack = (CommandStack) getWorkbenchPart().getAdapter(CommandStack.class);
							commandStack.execute(command);
							viewer.refresh();
						}
					});
				}
				return parent;
			}
			
			@Override
			public boolean close() {
				getContainer().removePropertyChangeListener(viewer);
//				this.viewer.removeCardListListener(listener);
				OpenCardListAction.this.window = null;
				return super.close();
			}
			
			@Override
			protected void configureShell(Shell shell) {
				super.configureShell(shell);
				String title = String.format(Messages.OpenCardListAction_title,getContainer().getContainerName());
				shell.setText(title);
				Image image = KanbanUIActivator.getDefault().getImageRegistry().get(KanbanImageConstants.KANBANS_IMAGE.toString());
				shell.setImage(image);
			}
		};
		window.create();
		window.open();
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
