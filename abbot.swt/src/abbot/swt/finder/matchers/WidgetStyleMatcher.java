package abbot.swt.finder.matchers;

import org.eclipse.swt.widgets.Widget;

import abbot.swt.finder.generic.Matcher;
import abbot.swt.utilities.Displays;
import abbot.swt.utilities.Displays.IntResult;

/**
 * A {@link Matcher} that matches {@link Widget}s by their style.
 */
public class WidgetStyleMatcher implements WidgetMatcher {

	/**
	 * The style to match.
	 */
	private final int style;

	/**
	 * If <code>true</code> then the {@link Widget}'s style must match exactly. Otherwise, it
	 * matches if any of its bits match.
	 */
	private final boolean exactMatch;

	/**
	 * Constructs a new {@link WidgetStyleMatcher}.
	 * 
	 * @param style
	 *            the style to match against
	 * @param exactMatch
	 *            if true then a {@link Widget} matches only if its style is equal to the style
	 *            we're matching on. Otherwise, it matches if any of its 1 style bits match any of
	 *            ours.
	 */
	public WidgetStyleMatcher(int style, boolean exactMatch) {
		this.style = style;
		this.exactMatch = exactMatch;
	}

	/**
	 * Constructs a new {@link WidgetStyleMatcher} that requires an exact style match.
	 * 
	 * @param style
	 *            the style to match against
	 */
	public WidgetStyleMatcher(int style) {
		this(style, true);
	}

	/**
	 * @see abbot.swt.finder.generic.Matcher#matches(java.lang.Object)
	 */
	public boolean matches(final Widget widget) {

		// Get the widget's style.
		int widgetStyle = Displays.syncExec(widget.getDisplay(), new IntResult() {
			public int result() {
				return widget.getStyle();
			}
		});

		// Compare appropriately.
		if (exactMatch)
			return widgetStyle == style;
		return (widgetStyle & style) != 0;
	}
}
