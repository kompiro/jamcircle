package abbot.swt.finder.generic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import abbot.swt.hierarchy.Hierarchy;
import abbot.swt.hierarchy.Visitable.Visitor;

/**
 * A {@link Finder} implementation. Possibly the only one you'll ever need.
 */

public class FinderImpl<Node> implements Finder<Node> {

	private final Hierarchy<Node> hierarchy;

	/**
	 * Constructs a new {@link FinderImpl} on a {@link Hierarchy}.
	 * 
	 * @param hierarchy
	 *            the {@link Hierarchy}
	 */
	public FinderImpl(Hierarchy<Node> hierarchy) {
		this.hierarchy = hierarchy;
	}

	/**
	 * @see Finder#find(Matcher)
	 */
	public Node find(final Matcher<Node> matcher) throws NotFoundException, MultipleFoundException {

		// Collect matches.
		final Set<Node> found = new HashSet<Node>();
		hierarchy.accept(new Visitor<Node>() {
			public Result visit(Node candidate) {
				if (matcher.matches(candidate))
					found.add(candidate);
				return Result.ok;
			}
		});

		// Check and return result.
		return found(found, matcher);

	}

	/**
	 * @see Finder#find(Object, Matcher)
	 */
	public Node find(Node node, final Matcher<Node> matcher) throws NotFoundException,
			MultipleFoundException {

		// Collect matches.
		final Set<Node> found = new HashSet<Node>();
		hierarchy.accept(node, new Visitor<Node>() {
			public Result visit(Node candidate) {
				if (matcher.matches(candidate))
					found.add(candidate);
				return Result.ok;
			}
		});

		// Check and return result.
		return found(found, matcher);

	}

	private Node found(Set<Node> found, Matcher<Node> matcher) throws NotFoundException,
			MultipleFoundException {

		// If there were no matches, throw a NotFoundException.
		if (found.size() == 0)
			throw new NotFoundException();

		/*
		 * If there were multiple matches: If the Matcher is a MultiMatcher then let it pick the
		 * best match; otherwise, throw a MultipleFoundException.
		 */
		if (found.size() > 1) {
			if (!(matcher instanceof MultiMatcher))
				throw new MultipleFoundException();
			return ((MultiMatcher<Node>) matcher).bestMatch(new ArrayList<Node>(found));
		}

		// If we got here then there was a single matching widget. Return it.
		return found.iterator().next();

	}

	// private Class findClass(Collection nodes) {
	// Iterator iterator = nodes.iterator();
	// Object node = iterator.next();
	// Class c = node.getClass();
	// while (iterator.hasNext()) {
	// node = iterator.next();
	// Class d = node.getClass();
	// while (!(c.isAssignableFrom(d)))
	// c = c.getSuperclass();
	// }
	// return c;
	// }

}
