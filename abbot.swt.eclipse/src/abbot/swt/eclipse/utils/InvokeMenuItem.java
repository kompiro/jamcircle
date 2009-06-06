/*
 * InvokeMenuITem.java Created on May 16, 2005 This utility class can be used to invoke an arbitrary
 * menu item from the menu bar.
 */
package abbot.swt.eclipse.utils;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

import abbot.swt.tester.ActionFailedException;
import abbot.swt.tester.MenuTester;
import abbot.swt.tester.ShellTester;

/**
 * @author Chris Jaun
 */

public class InvokeMenuItem {

	/**
	 * Will invoke an arbitrary menu item from the menu bar.
	 * 
	 * @param menuPath -
	 *            The path to the menu item
	 * @param parentShell -
	 *            The parent window shell
	 */

	public static void invoke(String menuPath, Shell parentShell) {
		
		Menu bar = ShellTester.getShellTester().getMenuBar(parentShell);
		if (bar == null)
			throw new ActionFailedException("no menu bar");
		MenuTester.getMenuTester().actionClickItem(bar, menuPath);
	}

}
