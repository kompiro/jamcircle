package org.eclipse.swtbot.eclipse.gef.finder;

import org.eclipse.gef.EditPart;
import org.eclipse.swtbot.swt.finder.matchers.AbstractMatcher;
import org.hamcrest.*;

/**
 * Tells if a particular edit part is of a specified type.
 * 
 * @author Hiroki Kondo &lt;kompiro [at] gmail [dot] com&gt;
 * @version $Id$
 * @since 2.0
 */
public class EditPartOfType<T extends EditPart> extends AbstractMatcher<T> {

	/**
	 * The type of edit part to match.
	 */
	private Class<? extends EditPart> type;

	/**
	 * Matches a edit part that has the specified type
	 * 
	 * @param type
	 *            the type of the edit part.
	 */
	public EditPartOfType(Class<? extends EditPart> type) {
		this.type = type;
	}

	protected boolean doMatch(Object obj) {
		return type.isInstance(obj);
	}

	public void describeTo(Description description) {
		description.appendText("of type '").appendText(type.getSimpleName()).appendText("'"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Factory
	public static <T extends EditPart> Matcher<T> editPartOfType(Class<T> type) {
		return new EditPartOfType<T>(type);
	}

}
