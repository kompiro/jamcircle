package abbot.swt.eclipse.tester;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;

import abbot.swt.eclipse.utils.WorkbenchUtilities;
import abbot.swt.tester.ItemPath;
import abbot.swt.tester.MenuTester;
import abbot.swt.tester.ShellTester;

public class WorkbenchTester extends JFaceTester {

	public static final WorkbenchTester Default = new WorkbenchTester();

	/**
	 * The label of the "File" menu.
	 */
	public static final String MenuLabel_File = IDEWorkbenchMessages.Workbench_file;

	/**
	 * The label of the "File" menu's "New" menu.
	 * <p>
	 * TODO Find the value dynamically (on-demand?) because the label's accellerator key combination
	 * is calculated at run-time and will probably be different in non-English locale.
	 */
	public static final String MenuLabel_New = IDEWorkbenchMessages.Workbench_new;

	/**
	 * The label of the "File/New" menu's "Project..." menu.
	 */
	public static final String MenuLabel_Project = IDEWorkbenchMessages.NewProjectAction_text;

	/**
	 * The label of the "File/New" menu's "Other..." menu.
	 */
	public static final String MenuLabel_Other = WorkbenchMessages.NewWizardAction_text;

	/**
	 * The path to the "File/New" menu.
	 */
	public static final ItemPath MenuPath_New = new ItemPath(new String[] { MenuLabel_File,
			MenuLabel_New });

	/**
	 * Clicks on the Workbench menu bar item specified by a path.
	 * 
	 * @param path
	 *            the {@link ItemPath} of the item to be clicked on
	 */
	public void actionClickMenuBarItem(ItemPath path) {
		Shell workbenchShell = WorkbenchUtilities.getWorkbenchWindow().getShell();
		Menu menuBar = ShellTester.getShellTester().getMenuBar(workbenchShell);
		MenuTester.getMenuTester().actionClickItem(menuBar, path);
	}

	/**
	 * Launches a wizard from the Workbench File/New menu and waits for it to open.
	 * 
	 * @return the new wizard's {@link Shell}
	 */
	public Shell actionLaunchWizard(String menuLabel, String wizardShellText) {
		actionClickMenuBarItem(MenuPath_New.append(menuLabel));
		return ShellTester.waitVisible(wizardShellText, 60000L);
	}

	/**
	 * Launches the generic "New" wizard from the Workbench "File/New/Other..." menu and waits for
	 * it to open.
	 * 
	 * @return the new wizard's {@link Shell}
	 */
	public Shell actionLaunchNewWizard() {
		return actionLaunchWizard(MenuLabel_Other, WorkbenchMessages.NewWizard_title);
	}

	/**
	 * Launches the generic "New Project" wizard from the "File/New/Project..." menu and waits for
	 * it to open.
	 * 
	 * @return the project wizard's {@link Shell}
	 */
	public Shell actionLaunchProjectWizard() {
		return actionLaunchWizard(MenuLabel_Project, WorkbenchMessages.NewProject_title);
	}

	/**
	 * Launches the generic "New" wizard from the Workbench "File/New/Other..." menu, waits for it
	 * to open, selects the specified wizard, clicks "Next", and waits for the specified next page
	 * to show.
	 * 
	 * @return the new wizard's {@link Shell}
	 */
	public Shell actionLaunchNewWizard(ItemPath wizardPath, String wizardPageTitle) {
		Shell wizardShell = actionLaunchNewWizard();
		WizardTester.Default.actionSelectWizard(wizardShell, wizardPath, wizardPageTitle);
		return wizardShell;
	}

	/**
	 * Launches the "New Project" wizard from the Workbench "File/New/Project..." menu, waits for it
	 * to open, selects the specified wizard, clicks "Next", and waits for the specified next page
	 * to show.
	 * 
	 * @return the new wizard's {@link Shell}
	 */
	public Shell actionLaunchProjectWizard(ItemPath wizardPath, String wizardPageTitle) {
		Shell wizardShell = actionLaunchProjectWizard();
		WizardTester.Default.actionSelectWizard(wizardShell, wizardPath, wizardPageTitle);
		return wizardShell;
	}

}
