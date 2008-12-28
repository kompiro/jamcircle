package abbot.swt.finder.matchers;

import java.util.ArrayList;
import java.util.List;

import abbot.swt.finder.generic.Matcher;

/**
 * A convenience class for building a Matcher (possibly a CompositeMatcher) when your code needs to
 * dynamically decide what Matchers are needed in a particular situation. The typical usaage pattern
 * is to create a new instance (using the default, zero-argument constructor), call
 * {@link #add(Matcher)} zero or more times to add whatever Matchers are needed, and then call
 * {@link #get()} to get the resultin Matcher.
 * 
 * @author gjohnsto
 */
public class Matchers<Node> {

	private final List<Matcher<Node>> matchers = new ArrayList<Matcher<Node>>();

	public Matchers add(Matcher<Node> matcher) {
		if (matcher == null)
			throw new IllegalArgumentException("matcher is null");
		matchers.add(matcher);
		return this;
	}

	public Matcher<Node> get() {
		switch (matchers.size()) {
			case 0:
				return null;
			case 1:
				return matchers.get(0);
			default:
				return new CompositeMatcher<Node>(matchers);
		}
	}

}
