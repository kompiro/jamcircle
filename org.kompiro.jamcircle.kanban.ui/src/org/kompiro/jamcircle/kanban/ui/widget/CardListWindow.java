package org.kompiro.jamcircle.kanban.ui.widget;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.CardContainer;
import org.kompiro.jamcircle.kanban.ui.KanbanImageConstants;
import org.kompiro.jamcircle.kanban.ui.KanbanUIActivator;
import org.kompiro.jamcircle.kanban.ui.Messages;
import org.kompiro.jamcircle.kanban.ui.command.provider.ConfirmProvider;
import org.kompiro.jamcircle.kanban.ui.command.provider.MessageDialogConfirmProvider;
import org.kompiro.jamcircle.kanban.ui.internal.command.CardUpdateCommand;

public class CardListWindow extends ApplicationWindow {

	private CardListTableViewer viewer;
	private CardContainer container;
	private CommandStack commandStack;

	public CardListWindow(Shell parentShell,CardContainer container,CommandStack commandStack) {
		super(parentShell);
		this.container = container;
		this.commandStack = commandStack;
	}

	@Override
	protected Control createContents(Composite parent) {
		this.viewer = new CardListTableViewer(parent);
		viewer.setInput(getCardContainer());
		getCardContainer().addPropertyChangeListener(viewer);
		viewer.setEditProvider(new CardListEditProvider(){
			public void edit(Card card, String subject,
					String content, Date dueDate, List<File> files) {
				ConfirmProvider provider = new MessageDialogConfirmProvider(getShell());
				CardUpdateCommand command = new CardUpdateCommand(provider ,card,subject,content,dueDate,files);
				commandStack.execute(command);
				viewer.refresh();
			}
		});
		return parent;
	}

	@Override
	public boolean close() {
		getCardContainer().removePropertyChangeListener(viewer);
		return super.close();
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		String title = String.format(Messages.LaneEditPart_viewer_title,getCardContainer().getContainerName());
		shell.setText(title);
		configureDialogImage(shell);
	}

	private void configureDialogImage(Shell shell) {
		KanbanUIActivator activator = KanbanUIActivator.getDefault();
		if(activator == null) return;
		Image image = activator.getImageRegistry().get(KanbanImageConstants.KANBANS_IMAGE.toString());
		shell.setImage(image);
	}
	
	private CardContainer getCardContainer(){
		return this.container;
	}
}
