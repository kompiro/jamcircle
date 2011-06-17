package org.kompiro.jamcircle.kanban.ui.widget;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * This class to provide for help to show MessageDialog for test.<br>
 * When you implement some wizard class,you can use this class to mock to show
 * MessageDialog.
 */
public class MessageDialogHelper {

	/**
	 * call {@link MessageDialog#openError(Shell, String, String)}
	 * 
	 * @param shell
	 *            parent shell
	 * @param message
	 *            error message
	 * @param e
	 *            target {@link Throwable}. this implement to show target's
	 *            message.
	 */
	public void openError(Shell shell, String message, Throwable e) {
		String errorMessage = "";
		if (e != null) {
			errorMessage = e.getMessage();
		}
		MessageDialog.openError(shell, message, errorMessage);
	}

}
