package org.kompiro.jamcircle.kanban.ui.action;

import java.io.File;
import java.util.Date;
import java.util.List;


import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.CardContainer;
import org.kompiro.jamcircle.kanban.ui.KanbanImageConstants;
import org.kompiro.jamcircle.kanban.ui.KanbanUIActivator;
import org.kompiro.jamcircle.kanban.ui.command.CardUpdateCommand;
import org.kompiro.jamcircle.kanban.ui.gcontroller.CardContainerEditPart;
import org.kompiro.jamcircle.kanban.ui.gcontroller.TrashEditPart;
import org.kompiro.jamcircle.kanban.ui.widget.CardListEditProvider;
import org.kompiro.jamcircle.kanban.ui.widget.CardListTableViewer;

public class OpenCardListAction extends SelectionAction {
	
	private CardContainerEditPart targetEditPart;
	private ApplicationWindow window;

	public OpenCardListAction(IWorkbenchPart part){
		super(part);
		setImageDescriptor(KanbanUIActivator.getDefault().getImageRegistry().getDescriptor(KanbanImageConstants.OPEN_LIST_ACTION_IMAGE.toString()));
		setText("Open Card List");
		setToolTipText("Open Card List Window");
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
							CardUpdateCommand command = new CardUpdateCommand(card,subject,content,dueDate,files);
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
				String title = String.format("Card List: %s",getContainer().getContainerName());
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
