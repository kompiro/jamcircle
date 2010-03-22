package org.kompiro.jamcircle.kanban.ui.action;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.kompiro.jamcircle.kanban.ui.*;
import org.kompiro.jamcircle.kanban.ui.widget.CommandListTableViewer;

public class OpenCommandListAction extends Action {

	private KanbanView part;

	public OpenCommandListAction(KanbanView part){
		this.part = part;
		ImageRegistry imageRegistry = KanbanUIActivator.getDefault().getImageRegistry();
		setImageDescriptor(imageRegistry.getDescriptor(KanbanImageConstants.OPEN_LIST_ACTION_IMAGE.toString()));
		setText("Open Command List");
		setToolTipText("Open Command List Window");
	}

	@Override
	public void run() {
		ApplicationWindow window = new ApplicationWindow(part.getViewSite().getShell()){
			@Override
			protected Control createContents(Composite parent) {
				CommandListTableViewer viewer = new CommandListTableViewer();
				viewer.createPartControl(parent);
				return parent;
			}
		};
		window.create();
		window.getShell().setText("Command List");
		window.open();
	}

}
