package abbot.swt.eclipse.junit.extensions;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import abbot.swt.junit.extensions.SWTTestCase;
import abbot.swt.utilities.Displays;
import abbot.swt.utilities.Displays.Result;

public class WorkbenchTestCase extends SWTTestCase {

	private IWorkbench workbench;

	private Shell shell;

	protected synchronized Shell getShell() {
		if (shell == null) {
			final IWorkbench tempWorkbench = getWorkbench();
			IWorkbenchWindow window = (IWorkbenchWindow) Displays.syncExec(
					getDisplay(),
					new Result() {
						public Object result() {
							return tempWorkbench.getActiveWorkbenchWindow();
						}
					});
			shell = window.getShell();
		}
		assertNotNull(shell);
		return shell;
	}

	public WorkbenchTestCase(String name) {
		super(name);
	}

	public WorkbenchTestCase() {
		super();
	}

	protected Display getDefaultDisplay() {
		return getWorkbench().getDisplay();
	}

	protected synchronized IWorkbench getWorkbench() {
		if (workbench == null)
			workbench = PlatformUI.getWorkbench();
		assertNotNull(workbench);
		return workbench;
	}

}
