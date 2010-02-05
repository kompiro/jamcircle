package org.kompiro.jamcircle.kanban.ui.model;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.kompiro.jamcircle.kanban.ui.util.WorkbenchUtil;

public class MessageDialogConfirmStrategy implements ConfirmStrategy {

	public boolean confirm(String message) {
		return MessageDialog.openConfirm(getShell(), "Confirm", message);
	}

	private Shell getShell() {
		return WorkbenchUtil.getShell();
	}

}
