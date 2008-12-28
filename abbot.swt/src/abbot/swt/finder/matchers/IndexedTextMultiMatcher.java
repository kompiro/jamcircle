package abbot.swt.finder.matchers;

import java.util.List;

import org.eclipse.swt.widgets.Widget;

import abbot.swt.finder.generic.MultiMatcher;
import abbot.swt.finder.generic.MultipleFoundException;

/**
 * A {@link TextMultiMatcher} that chooses the {@link Widget} at the specified index as the best
 * match.
 * 
 * @deprecated This class will likely go away soon. Matching by index is discouraged because it is
 *             extremely fragile. Use a different kind of Matcher if you possibly can.
 * @author gjohnsto
 */
public class IndexedTextMultiMatcher extends TextMultiMatcher {

	private final MultiMatcher<Widget> multiMatcher;

	public IndexedTextMultiMatcher(int index, String text, Class clazz, boolean mustBeShowing) {
		super(text, clazz, mustBeShowing);
		multiMatcher = new IndexedMultiMatcher(index);
	}

	public IndexedTextMultiMatcher(int index, String text, boolean mustBeShowing) {
		this(index, text, Widget.class, mustBeShowing);
	}

	public IndexedTextMultiMatcher(int index, String text, Class clazz) {
		this(index, text, clazz, false);
	}

	public IndexedTextMultiMatcher(int index, String text) {
		this(index, text, Widget.class, false);
	}

	public Widget bestMatch(List<Widget> candidates) throws MultipleFoundException {
		return multiMatcher.bestMatch(candidates);
	}

}
