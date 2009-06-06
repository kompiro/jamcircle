package abbot.swt.finder.matchers;

import java.util.Collection;

import org.eclipse.swt.widgets.Widget;

import abbot.swt.finder.WidgetMultiMatcher;
import abbot.swt.finder.generic.Matcher;

public class CompositeWidgetMultiMatcher extends CompositeMultiMatcher<Widget> implements WidgetMultiMatcher {

	public CompositeWidgetMultiMatcher(Matcher<Widget> matcher0, Matcher<Widget> matcher1) {
		super(matcher0, matcher1);
	}

	public CompositeWidgetMultiMatcher(Matcher<Widget> matcher0, Matcher<Widget> matcher1,
			Matcher<Widget> matcher2) {
		super(matcher0, matcher1, matcher2);
	}

	public CompositeWidgetMultiMatcher(Matcher<Widget>[] matchers) {
		super(matchers);
	}

	public CompositeWidgetMultiMatcher(Collection<Matcher<Widget>> matchers) {
		super(matchers);
	}

}
