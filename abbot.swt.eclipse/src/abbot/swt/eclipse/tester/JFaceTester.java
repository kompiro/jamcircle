package abbot.swt.eclipse.tester;

import org.eclipse.swt.widgets.Widget;

import abbot.swt.finder.WidgetFinderImpl;
import abbot.swt.finder.generic.FinderException;
import abbot.swt.finder.generic.Matcher;

public abstract class JFaceTester {

	protected static Widget find(Widget root, Matcher<Widget> matcher) {
		try {
			return WidgetFinderImpl.getDefault().find(root, matcher);
		} catch (FinderException e) {
			throw new RuntimeException(e);
		}
	}
}
