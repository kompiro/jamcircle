package org.kompiro.jamcircle.scripting.ui.util;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.*;
import org.kompiro.jamcircle.scripting.ui.internal.ruby.console.RubyScriptingConsole;

public class UIUtil {

	public String inspectionPartId;

	public void inspection() {
		final IWorkbenchPart[] part = new IWorkbenchPart[1];
		sync(new Runnable() {
			public void run() {
				IPartService partService = getWorkbenchWindow().getPartService();
				if (partService == null)
					return;
				part[0] = partService.getActivePart();
			}
		});
		inspectionPartId = part[0].getSite().getId();
	}

	public void openWarning(final String title, final String message) {
		sync(new Runnable() {
			public void run() {
				Shell parent = getShell();
				MessageDialog.openWarning(parent, title, message);
			}
		});
	}

	public ISelection getSelection() {
		final ISelection[] selection = new ISelection[1];
		sync(new Runnable() {
			public void run() {
				ISelectionService selectionService = getWorkbenchWindow().getSelectionService();
				if (selectionService == null)
					return;
				selection[0] = selectionService.getSelection(RubyScriptingConsole.KANBAN_VIEW);
			}
		});
		return selection[0];
	}

	private Display getDisplay() {
		IWorkbench workbench = getWorkbench();
		Display defDisplay = Display.getDefault();
		if (workbench == null) {
			return defDisplay;
		}
		try {
			Display display = workbench.getDisplay();
			if (display != null)
				return display;
			IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
			if (activeWorkbenchWindow == null)
				return defDisplay;
			display = activeWorkbenchWindow.getShell().getDisplay();
			return display;
		} catch (IllegalStateException e) {
			return defDisplay;
		}

	}

	public void async(Runnable runnable) {
		Display display = getDisplay();
		if (display == null) {
			runnable.run();
			return;
		}
		display.asyncExec(runnable);
	}

	public void sync(Runnable runnable) {
		Display display = getDisplay();
		if (display == null) {
			runnable.run();
			return;
		}
		display.syncExec(runnable);
	}

	private IWorkbench getWorkbench() {
		if (Platform.isRunning()) {
			if (PlatformUI.isWorkbenchRunning()) {
				return PlatformUI.getWorkbench();
			}
		}
		return null;
	}

	private IWorkbenchWindow getWorkbenchWindow() {
		IWorkbench workbench = getWorkbench();
		if (workbench == null)
			return null;
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		if (window != null)
			return window;
		if (workbench.getWorkbenchWindowCount() == 0)
			return null;
		return workbench.getWorkbenchWindows()[0];
	}

	private Shell getShell() {
		IWorkbenchWindow window = getWorkbenchWindow();
		if (window == null)
			return null;
		return window.getShell();
	}

}
