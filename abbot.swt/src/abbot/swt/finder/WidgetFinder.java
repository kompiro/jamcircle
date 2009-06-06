package abbot.swt.finder;

import org.eclipse.swt.widgets.Widget;

import abbot.swt.finder.generic.Finder;
import abbot.swt.finder.matchers.WidgetMatcher;

/**
 * A {@link WidgetFinder} provides support for finding {@link Widget}s in a {@link Widget}
 * hierarchy.
 * 
 * @see WidgetFinderImpl
 * @see WidgetMatcher
 */

public interface WidgetFinder extends Finder<Widget> {

}
