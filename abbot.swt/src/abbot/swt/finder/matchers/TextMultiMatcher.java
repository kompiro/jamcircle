package abbot.swt.finder.matchers;

import org.eclipse.swt.widgets.Widget;

import abbot.swt.finder.WidgetMultiMatcher;

/**
 * An abstract {@link WidgetMultiMatcher} based on {@link WidgetTextMatcher}.
 * 
 * @author Jack Frink
 * @author Gary Johnston
 */
public abstract class TextMultiMatcher extends WidgetTextMatcher implements WidgetMultiMatcher {

	/**
	 * Constructs a new {@link TextMultiMatcher}.
	 */
	public TextMultiMatcher(String text, Class clazz, boolean mustBeShowing) {
		super(text, clazz, mustBeShowing);
	}

	/**
	 * Constructs a new {@link TextMultiMatcher}.
	 */
	public TextMultiMatcher(String text, boolean mustBeShowing) {
		this(text, Widget.class, mustBeShowing);
	}

	/**
	 * Constructs a new {@link TextMultiMatcher}.
	 */
	public TextMultiMatcher(String text, Class clazz) {
		this(text, clazz, false);
	}

	/**
	 * Constructs a new {@link TextMultiMatcher}.
	 */
	public TextMultiMatcher(String text) {
		this(text, Widget.class, false);
	}
}
