package abbot.swt.finder.matchers;

import java.util.Collection;

import org.eclipse.swt.widgets.Widget;

import abbot.swt.finder.generic.Matcher;

/**
 * A {@link Matcher} implementation that composes other {@link Matcher}'s. That is,
 * {@link #matches(Object)} returns <code>true</code> if (and only if) all of the comprising {@link Matcher}s'
 * {@link Matcher#matches(Object)} methods return true.
 * <p>
 * <strong>Note:</strong> Class name <code>CompositeMatcher</code> notwithstanding, this class
 * does not have anything to do with {@link org.eclipse.swt.widgets.Composite}.
 * <p>
 * <strong>Suggestion:</strong> Use {@link Matchers} to conveniently create
 * {@link CompositeMatcher}s.
 * <p>
 * TODO Exploit performance considerations noted by Richard Birenheide with some [contextual
 * clarifications] by me (Gary Johnston): <blockquote>Tests on a first glance show (on my machine)
 * that combining Class type check and name [<code>Widget.getData()</code>] check is
 * considerably faster if combined in the way shown below [i.e., doing them both in a single
 * <code>Display.syncExec()</code> call]. For performance it is necessary to have control that the
 * least cost check is done first, the class check seems to be extremely cheap compared to the name
 * check. This is not necessarily obvious with CompositeMatcher.</blockquote>
 */
public class CompositeMatcher<Node> implements Matcher<Node> {

	/** Our {@link Matcher}s. */
	protected final Matcher<Node>[] matchers;

	/**
	 * Constructs a new {@link CompositeMatcher} with the specified 2 Matchers.
	 * 
	 * @param matcher0
	 *            a Matcher
	 * @param matcher1
	 *            another Matcher
	 */
	public CompositeMatcher(Matcher<Node> matcher0, Matcher<Node> matcher1) {
		this((Matcher<Node>[]) new Matcher[] { matcher0, matcher1 }, false);
	}

	/**
	 * Constructs a new {@link CompositeMatcher} with the specified 3 Matchers.
	 * 
	 * @param matcher0
	 *            a Matcher
	 * @param matcher1
	 *            another Matcher
	 * @param matcher2
	 *            yet another Matcher
	 */
	public CompositeMatcher(Matcher<Node> matcher0, Matcher<Node> matcher1, Matcher<Node> matcher2) {
		this((Matcher<Node>[]) new Matcher[] { matcher0, matcher1, matcher2 }, false);
	}

	/**
	 * Constructs a new {@link CompositeMatcher} with the specified Matchers.
	 * 
	 * @param matchers
	 *            the Matchers
	 */
	public CompositeMatcher(Matcher<Node>[] matchers) {
		this(matchers, true);
	}

	/**
	 * Constructs a new {@link CompositeMatcher} with the specified Matchers.
	 * 
	 * @param matchers
	 *            the Matchers
	 */
	public CompositeMatcher(Collection<Matcher<Node>> matchers) {
		this((Matcher<Node>[]) matchers.toArray(new Matcher[matchers.size()]), false);
	}

	private CompositeMatcher(Matcher<Node>[] matchers, boolean copyMatchers) {
		this.matchers = copyMatchers ? matchers.clone() : matchers;
	}

	/**
	 * Determines whether or not a {@link Widget} matches.
	 * 
	 * @param node
	 *            the Widget to check
	 * @return true if (and only if) all of the comprising {@link Matcher}s
	 *         {@link Matcher#matches(Object)} methods return true. Note that this implies that if
	 *         there are <em>no</em> Matchers then this method will return true.
	 * @see Matcher#matches(Object)
	 */
	public boolean matches(Node node) {
		for (int i = 0; i < matchers.length; i++) {
			if (!matchers[i].matches(node))
				return false;
		}
		return true;
	}
}
