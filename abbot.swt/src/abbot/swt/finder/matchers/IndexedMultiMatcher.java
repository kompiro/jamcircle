package abbot.swt.finder.matchers;

import java.util.List;

import org.eclipse.swt.widgets.Widget;

import abbot.swt.finder.WidgetMultiMatcher;
import abbot.swt.finder.generic.MultipleFoundException;

/**
 * A {@link WidgetMultiMatcher} that matches everything and chooses the {@link Widget} at the
 * specified index as the best match. It is meant to be used with other {@link WidgetMatcher}s in a
 * {@link CompositeMatcher}.
 * 
 * @deprecated This class will likely go away soon. Matching by index is discouraged because it is
 *             extremely fragile. Use a different kind of Matcher if you possibly can.
 * @author gjohnsto
 */
public class IndexedMultiMatcher extends IndexMatcher implements WidgetMultiMatcher {

	/**
	 * @param index
	 *            the index of the {@link Widget} that will be the best match
	 */
	public IndexedMultiMatcher(int index) {
		super(index);
	}

	public IndexedMultiMatcher(WidgetMatcher matcher, int index) {
		super(matcher, index);
	}

	public Widget bestMatch(List<Widget> widgets) throws MultipleFoundException {
		if (index < widgets.size())
			return widgets.get(index);
		throw new MultipleFoundException("index out of range");
	}

}
