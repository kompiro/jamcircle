package abbot.swt.finder;

import org.eclipse.swt.widgets.Widget;

import abbot.swt.finder.generic.MultiMatcher;
import abbot.swt.finder.matchers.WidgetMatcher;

/**
 * Provides a way to specify a {@link WidgetMatcher} that can indicate the best match among one or
 * more matching {@link Widget}s.
 * 
 * @see WidgetMatcher
 * @see WidgetFinder
 */

public interface WidgetMultiMatcher extends WidgetMatcher, MultiMatcher<Widget> {

}
