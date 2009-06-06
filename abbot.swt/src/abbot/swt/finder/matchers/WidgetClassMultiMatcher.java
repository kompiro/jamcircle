package abbot.swt.finder.matchers;

import abbot.swt.finder.WidgetMultiMatcher;

/**
 * An abstract {@link WidgetMultiMatcher} based on {@link WidgetClassMatcher}.
 * 
 * @author Henry McEuen
 * @author Gary Johnston
 */
public abstract class WidgetClassMultiMatcher extends WidgetClassMatcher implements
		WidgetMultiMatcher {

	public WidgetClassMultiMatcher(Class clazz, boolean mustBeShowing) {
		super(clazz, mustBeShowing);
	}

	public WidgetClassMultiMatcher(Class clazz) {
		super(clazz);
	}

}
