package abbot.swt.finder.generic;

/**
 * A {@link Matcher} that matches based on class.
 * 
 * @param <Node>
 */
public class ClassMatcher<Node> implements Matcher<Node> {

	/** The class to match. */
	protected final Class theClass;

	/**
	 * Constructs a new {@link ClassMatcher} to match {@code Node}s of a specified class (or
	 * subclass).
	 * 
	 * @param theClass
	 *            the class to match
	 */
	public ClassMatcher(Class theClass) {
		this.theClass = theClass;
	}

	/**
	 * @see Matcher
	 */
	public boolean matches(Node node) {
		return theClass.isAssignableFrom(node.getClass());
	}

}
