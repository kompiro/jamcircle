package abbot.swt.finder.matchers;

import org.eclipse.swt.widgets.Widget;

import abbot.swt.finder.generic.IdentityMatcher;

/**
 * An {@link IdentityMatcher} that matches a specific {@link Widget}
 */
public class WidgetIdentityMatcher extends IdentityMatcher<Widget> implements WidgetMatcher {

	public WidgetIdentityMatcher(Widget widget) {
		super(widget);
	}

}
