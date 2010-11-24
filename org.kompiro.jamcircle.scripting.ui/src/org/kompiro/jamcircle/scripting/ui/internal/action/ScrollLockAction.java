package org.kompiro.jamcircle.scripting.ui.internal.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.console.IConsoleView;
import org.kompiro.jamcircle.scripting.ui.*;

/**
 * Toggles console auto-scroll
 * 
 * @since 3.1
 *        Copied from org.eclipse.ui.console 3.4.0 by kompiro
 */
public class ScrollLockAction extends Action {

	private IConsoleView consoleView;

	public ScrollLockAction() {
		super(Messages.ScrollLockAction_text);
		setToolTipText(Messages.ScrollLockAction_tooltip);
		setHoverImageDescriptor(getImageDescriptor(ScriptingImageEnum.IMG_LCL_LOCK));
		setDisabledImageDescriptor(getImageDescriptor(ScriptingImageEnum.IMG_DLCL_LOCK));
		setImageDescriptor(getImageDescriptor(ScriptingImageEnum.IMG_ELCL_LOCK));
	}

	private ImageDescriptor getImageDescriptor(ScriptingImageEnum key) {
		ScriptingUIActivator activator = ScriptingUIActivator.getDefault();
		if (activator == null)
			return null;
		return activator.getImageRegistry().getDescriptor(key.toString());
	}

	/**
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		consoleView.setScrollLock(isChecked());
	}

	public void setConsoleView(IConsoleView consoleView) {
		this.consoleView = consoleView;
		boolean checked = consoleView.getScrollLock();
		setChecked(checked);
	}

	public void dispose() {
		consoleView = null;
	}

}
