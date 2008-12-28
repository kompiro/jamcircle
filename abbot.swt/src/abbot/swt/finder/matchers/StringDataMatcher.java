package abbot.swt.finder.matchers;

import abbot.swt.utilities.ExtendedComparator;

public class StringDataMatcher extends WidgetDataMatcher {

	public StringDataMatcher(String key, String value, Class clazz) {
		super(key, value, clazz);
	}

	public StringDataMatcher(String key, String value) {
		super(key, value);
	}

	public StringDataMatcher(String value) {
		super(value);
	}

	public StringDataMatcher(String value, Class clazz) {
		super((Object) value, clazz);
	}

	protected boolean valueMatches(Object widgetValue) {
		if (value != null) {
			if (widgetValue instanceof String)
				return ExtendedComparator.stringsMatch((String) value, (String) widgetValue);
		} else {
			return widgetValue == null;
		}
		return false;
	}

}
