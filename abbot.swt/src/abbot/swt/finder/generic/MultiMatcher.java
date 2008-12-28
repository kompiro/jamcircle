package abbot.swt.finder.generic;

import java.util.List;


/**
 * Provides a way to specify a {@link Matcher} that can pick the best match among one or more
 * matches.
 * 
 * @see Finder
 */

public interface MultiMatcher<Node> extends Matcher<Node> {

	/**
	 * Gets the best match from a list of matches.
	 * 
	 * @throws MultipleFoundException
	 *             if there is no best match
	 */
	Node bestMatch(List<Node> nodes) throws MultipleFoundException;
}
