package abbot.swt.finder.matchers;

import java.util.Collection;

import org.eclipse.swt.widgets.Widget;

import abbot.swt.finder.generic.Matcher;

public class CompositeWidgetMatcher extends CompositeMatcher<Widget> implements WidgetMatcher {

	public CompositeWidgetMatcher(Matcher<Widget> matcher0, Matcher<Widget> matcher1) {
		super(matcher0, matcher1);
	}

	public CompositeWidgetMatcher(Matcher<Widget> matcher0, Matcher<Widget> matcher1,
			Matcher<Widget> matcher2) {
		super(matcher0, matcher1, matcher2);
	}

	public CompositeWidgetMatcher(Matcher<Widget>[] matchers) {
		super(matchers);
	}

	public CompositeWidgetMatcher(Collection<Matcher<Widget>> matchers) {
		super(matchers);
	}

}
