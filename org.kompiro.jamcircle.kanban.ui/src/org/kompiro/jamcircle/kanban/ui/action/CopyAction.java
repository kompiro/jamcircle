package org.kompiro.jamcircle.kanban.ui.action;

import java.util.List;


import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.CardEditPart;

public class CopyAction extends SelectionAction{

	public CopyAction(IWorkbenchPart editor) {
		super(editor);
		setId(ActionFactory.COPY.getId());
		setText("Copy");
		setToolTipText("Copy");
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
		setDisabledImageDescriptor(sharedImages.getImageDescriptor(
				ISharedImages.IMG_TOOL_COPY_DISABLED));

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
		Clipboard.getDefault().setContents(getSelectedObjects());
	}


}
