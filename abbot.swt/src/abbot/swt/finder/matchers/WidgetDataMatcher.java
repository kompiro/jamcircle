package abbot.swt.finder.matchers;

import org.eclipse.swt.widgets.Widget;

import abbot.swt.tester.WidgetTester;

/**
 * A {@link WidgetMatcher} implementation that matches based on a {@link Widget}'s keyed data.
 * 
 * @see Widget#setData(String, Object)
 * @see Widget#getData(String)
 */
public class WidgetDataMatcher extends WidgetClassMatcher {

	/** The default key to use. */
	public static final String DEFAULT_KEY = "abbot.swt.widget.data.key";

	/** The key we'll use for data lookup. */
	protected final String key;

	/** The value to be matched. */
	protected final Object value;

	/** Construct a new {@link WidgetDataMatcher} with the specified key and match value. */
	public WidgetDataMatcher(String key, Object value, Class clazz) {
		super(clazz);
		if (key == null)
			throw new IllegalArgumentException("key is null");
		this.key = key;
		this.value = value;
	}

	/**
	 * Construct a new {@link WidgetDataMatcher} with the specified key and the specified match
	 * value.
	 */
	public WidgetDataMatcher(String key, Object value) {
		this(key, value, Widget.class);
	}

	/** Construct a new {@link WidgetDataMatcher} with the default key and the specified match value. */
	public WidgetDataMatcher(Object value) {
		this(DEFAULT_KEY, value);
	}

	/** Construct a new {@link WidgetDataMatcher} with the default key and the specified match value. */
	public WidgetDataMatcher(Object value, Class clazz) {
		this(DEFAULT_KEY, value, clazz);
	}

	public String getKey() {
		return key;
	}

	public Object getValue() {
		return value;
	}

	/**
	 * @see abbot.swt.finder.matchers.WidgetClassMatcher#matches(org.eclipse.swt.widgets.Widget)
	 */
	public final boolean matches(Widget widget) {
		if (super.matches(widget)) {
			Object widgetValue = WidgetTester.getWidgetTester().getData(widget, key);
			return valueMatches(widgetValue);
		}
		return false;
	}

	protected boolean valueMatches(Object widgetValue) {
		if (widgetValue == null)
			return value == null;
		return widgetValue.equals(value);
	}
}
