package abbot.swt.eclipse.junit.extensions;

import junit.framework.Assert;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import abbot.swt.junit.extensions.SWTTestSuite;

public class WorkbenchTestSuite extends SWTTestSuite {

	private IWorkbench workbench;

	public WorkbenchTestSuite() {
		super();
	}

	public WorkbenchTestSuite(Class theClass) {
		super(theClass);
	}

	public WorkbenchTestSuite(String name) {
		super(name);
	}

	public WorkbenchTestSuite(Class theClass, String name) {
		super(theClass, name);
	}

	protected Display getDefaultDisplay() {
		return getWorkbench().getDisplay();
	}

	protected final synchronized IWorkbench getWorkbench() {
		if (workbench == null)
			workbench = PlatformUI.getWorkbench();
		Assert.assertNotNull(workbench);
		return workbench;
	}

}
