package abbot.swt.finder.matchers;

import org.eclipse.swt.widgets.Widget;

import abbot.swt.finder.generic.Matcher;
import abbot.swt.tester.WidgetTester;

/**
 * A {@link Matcher} implementation that matches any {@link Widget} that is visible.
 */
public class WidgetVisibleMatcher implements Matcher<Widget> {

	public boolean matches(final Widget widget) {
		try {
			return WidgetTester.getTester(widget).isVisible(widget);
		} catch (UnsupportedOperationException exception) {
			return false;
		}

		// if (widget instanceof Control)
		// return ControlTester.getControlTester().isVisible((Control) widget);
		//
		// if (widget instanceof Caret)
		// return CaretTester.getCaretTester().isVisible((Caret) widget);
		//
		// // Should we do this?
		// // if (widget instanceof CTabItem)
		// // return CTabItemTester.getCTabItemTester().isShowing((CTabItem) widget);
		//
		// if (widget instanceof MenuItem)
		// return MenuItemTester.getMenuItemTester().isVisible((MenuItem) widget);
		//
		// if (widget instanceof Menu)
		// return MenuTester.getMenuTester().isVisible((Menu) widget);
		//
		// if (widget instanceof ScrollBar)
		// return ScrollBarTester.getScrollBarTester().isVisible((ScrollBar) widget);
		//
		// if (widget instanceof ToolTip)
		// return ToolTipTester.getToolTipTester().isVisible((ToolTip) widget);
		//
		// return false;
	}

}
