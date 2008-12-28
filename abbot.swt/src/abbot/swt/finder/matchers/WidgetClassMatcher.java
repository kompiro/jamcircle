package abbot.swt.finder.matchers;

import org.eclipse.swt.widgets.Widget;

import abbot.swt.finder.generic.ClassMatcher;
import abbot.swt.finder.generic.Matcher;

/**
 * A {@link ClassMatcher} that matches a specified {@link Widget} subclass instance (and,
 * optionally, only if it is visible).
 */
public class WidgetClassMatcher extends ClassMatcher<Widget> implements WidgetMatcher {

	/** If non-null, a {@link Matcher} that only matches <i>visible</i> {@link Widget}s. */
	protected final WidgetVisibleMatcher visibleMatcher;

	/**
	 * Constructs a new {@link ClassMatcher} to match {@link Widget}s of a specified class (or
	 * subclass).
	 * 
	 * @param theClass
	 *            the class to match
	 * @param mustBeShowing
	 *            if <code>true</code> then a candidate {@link Widget} will match only if it is
	 *            visible
	 */
	public WidgetClassMatcher(Class theClass, boolean mustBeShowing) {
		super(theClass);
		this.visibleMatcher = mustBeShowing ? new WidgetVisibleMatcher() : null;
	}

	/**
	 * Constructs a new {@link ClassMatcher} to match {@link Widget}s of the specified class (or
	 * subclass).
	 * 
	 * @param theClass
	 *            the class to match
	 */
	public WidgetClassMatcher(Class theClass) {
		this(theClass, false);
	}

	/**
	 * @see abbot.swt.finder.generic.ClassMatcher#matches(java.lang.Object)
	 */
	public boolean matches(Widget widget) {
		return super.matches(widget) && (visibleMatcher == null || visibleMatcher.matches(widget));
	}
}
