/**
 * 
 */
package org.kompiro.jamcircle.kanban.ui.command.provider;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class MessageDialogConfirmProvider implements ConfirmProvider{
	private Shell shell;
	private String title;
	private String message;

	public MessageDialogConfirmProvider(Shell shell){
		this.shell = shell;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	public boolean confirm() {
		return MessageDialog.openConfirm(shell, title, message);
	}
}