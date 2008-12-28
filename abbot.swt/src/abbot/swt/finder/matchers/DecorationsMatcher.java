package abbot.swt.finder.matchers;

import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import abbot.swt.tester.DecorationsTester;

/**
 * A Matcher implementation that matches Decorations.
 * 
 * @deprecated use {@link ShellMatcher} instead. Class {@link Decorations} shouldn't normally be
 *             directly referenced.
 */

public class DecorationsMatcher extends WidgetTextMatcher {

	public DecorationsMatcher(String title, boolean mustBeShowing) {
		super(title, Shell.class, mustBeShowing);
	}

	public DecorationsMatcher(String title) {
		super(title, Shell.class);
	}

	protected String getText(Widget widget) {
		return DecorationsTester.getDecorationsTester().getText((Decorations) widget);
	}

	protected String[] getTextArray(Widget widget) {
		return null;
	}

}
