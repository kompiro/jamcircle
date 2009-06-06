package abbot.swt.eclipse.utils;

import junit.framework.TestCase;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;

import abbot.swt.finder.WidgetFinder;
import abbot.swt.finder.WidgetFinderImpl;
import abbot.swt.finder.generic.FinderException;
import abbot.swt.finder.matchers.WidgetClassMatcher;
import abbot.swt.tester.ActionFailedException;
import abbot.swt.tester.ItemPath;
import abbot.swt.tester.MenuTester;
import abbot.swt.tester.ShellTester;
import abbot.swt.tester.TreeItemTester;
import abbot.swt.tester.TreeTester;
import abbot.swt.tester.WidgetTester;

/**
 * This utility will invoke a new wizard by clicking File > New > Other, then navigating the tree of
 * available wizards.
 * 
 * @author Chris Jaun
 * @version $Id: InvokeNewWizard.java 2445 2007-07-11 19:13:27Z gjohnsto $
 */

public class InvokeNewWizard extends TestCase {

	// private static final String NEW_WIZARD_TITLE = "New";

	// File/New/Other...
	private static final ItemPath FileNewOtherPath = new ItemPath(new String[] {
			IDEWorkbenchMessages.Workbench_file,
			IDEWorkbenchMessages.Workbench_new + "\tAlt+Shift+N",
			WorkbenchMessages.NewWizardAction_text });

	// private static final String FILE_NEW_OTHER_PATH = "&File/&New\tAlt+Shift+N/&Other...";

	// testers used to find new wizard
	final static TreeItemTester treeItemTester = (TreeItemTester) WidgetTester
			.getTester(TreeItem.class);

	/**
	 * This method will invoke a new wizard by going to File > New > Other and then navigate the
	 * wizard tree.
	 * 
	 * @param wizardTreePath
	 *            the tree path to the wizard
	 * @param shell
	 *            the parent shell
	 */

	public static void invoke(String wizardTreePath, Shell shell) {
		invoke(wizardTreePath, "/", shell);
	}

	/**
	 * This method will invoke a new wizard by going to File > New > Other and then navigated the
	 * wizard tree.
	 * 
	 * @param path
	 *            the tree path to the wizard
	 * @param delimiter
	 *            a custom delimeter used when parsing path to tree item
	 * @param shell
	 *            the parent shell
	 */
	public static void invoke(String path, String delimiter, Shell shell) {

		// Launch the "New" wizard and wait for it to open.
		Menu bar = ShellTester.getShellTester().getMenuBar(shell);
		if (bar == null)
			throw new ActionFailedException("no menu bar");
		MenuTester.getMenuTester().actionClickItem(bar, FileNewOtherPath);
		Shell wizardShell = ShellTester.waitVisible(WorkbenchMessages.NewWizard_title);

		// Get the wizard's wizard selection tree.
		Tree tree = findTree(wizardShell);

		// Double-click the specified wizard tree item.
		TreeTester treeTester = TreeTester.getTreeTester();
		treeTester.actionDoubleClickItem(tree, path, delimiter);
	}

	private static Tree findTree(Shell shell) {
		try {
			WidgetFinder finder = WidgetFinderImpl.getDefault();
			return (Tree) finder.find(shell, new WidgetClassMatcher(Tree.class));
		} catch (FinderException exception) {
			throw new ActionFailedException(exception);
		}
	}
}
