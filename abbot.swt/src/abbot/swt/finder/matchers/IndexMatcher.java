package abbot.swt.finder.matchers;

import org.eclipse.swt.widgets.Widget;

import abbot.swt.finder.generic.Matcher;

public class IndexMatcher implements WidgetMatcher {

	protected final Matcher<Widget> matcher;

	protected final int index;

	private int current;

	public IndexMatcher(Matcher<Widget> matcher, int index) {
		if (index < 0)
			throw new IllegalArgumentException("index is negative");
		this.matcher = matcher;
		this.index = index;
		this.current = -1;
	}

	public IndexMatcher(int index) {
		this(null, index);
	}

	public void reset() {
		current = -1;
	}

	public boolean matches(Widget widget) {
		if (matcher == null || matcher.matches(widget)) {
			current++;
			if (current == index)
				return true;
		}
		return false;
	}
}
