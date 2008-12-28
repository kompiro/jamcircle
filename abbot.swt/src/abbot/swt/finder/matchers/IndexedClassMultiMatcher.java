package abbot.swt.finder.matchers;

import java.util.List;

import org.eclipse.swt.widgets.Widget;

import abbot.swt.finder.generic.MultiMatcher;
import abbot.swt.finder.generic.MultipleFoundException;

/**
 * A {@link WidgetClassMultiMatcher} that chooses the {@link Widget} at the specified index as the
 * best match.
 * 
 * @deprecated This class will likely go away soon. Matching by index is discouraged because it is
 *             extremely fragile. Use a different kind of Matcher if you possibly can.
 * @author gjohnsto
 */
public class IndexedClassMultiMatcher extends WidgetClassMultiMatcher {

	private final MultiMatcher<Widget> multiMatcher;

	public IndexedClassMultiMatcher(Class cls, int index) {
		super(cls);
		multiMatcher = new IndexedMultiMatcher(index);
	}

	public Widget bestMatch(List<Widget> widgets) throws MultipleFoundException {
		return multiMatcher.bestMatch(widgets);
	}

}
