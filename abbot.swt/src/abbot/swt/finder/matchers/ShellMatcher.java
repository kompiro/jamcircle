package abbot.swt.finder.matchers;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import abbot.swt.tester.ShellTester;

public class ShellMatcher extends WidgetTextMatcher {

	public ShellMatcher(String title, boolean mustBeShowing) {
		super(title, Shell.class, mustBeShowing);
	}

	public ShellMatcher(String title) {
		super(title, Shell.class);
	}

	protected String getText(Widget widget) {
		return ShellTester.getShellTester().getText((Shell) widget);
	}

	protected String[] getTextArray(Widget widget) {
		return null;
	}

}
