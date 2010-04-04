package org.kompiro.jamcircle.scripting.ui.internal.eclipse.ui.console;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.console.IConsoleView;
import org.kompiro.jamcircle.scripting.ui.*;

/**
 * Toggles console auto-scroll
 * 
 * @since 3.1
 * Copied from org.eclipse.ui.console 3.4.0 by kompiro
 */
public class ScrollLockAction extends Action {

    private IConsoleView fConsoleView;
	
	public ScrollLockAction(IConsoleView consoleView) {
		super(Messages.ScrollLockAction_text); 
        fConsoleView = consoleView;
		
		setToolTipText(Messages.ScrollLockAction_tooltip);  
		setHoverImageDescriptor(getImageDescriptor(ScriptingImageEnum.IMG_LCL_LOCK));		
		setDisabledImageDescriptor(getImageDescriptor(ScriptingImageEnum.IMG_DLCL_LOCK));
		setImageDescriptor(getImageDescriptor(ScriptingImageEnum.IMG_ELCL_LOCK));
		boolean checked = fConsoleView.getScrollLock();  
		setChecked(checked);
	}

	private ImageDescriptor getImageDescriptor(ScriptingImageEnum key) {
		return ScriptingUIActivator.getDefault().getImageRegistry().getDescriptor(key.toString());
	}

	/**
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
        fConsoleView.setScrollLock(isChecked());
	}
	
	public void dispose() {
        fConsoleView = null;
	}

}

