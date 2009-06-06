package abbot.swt.finder.generic;

/**
 * An interface that lets implementors specify whether or not something matches arbitrary criteria.
 * 
 * @see Finder
 */
public interface Matcher<Node> {

	/**
	 * @return <code>true</code> if the specified {@code Node} matches the implementation's
	 *         criteria, <code>false</code> otherwise
	 */
	boolean matches(Node node);

}
