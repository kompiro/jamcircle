package abbot.swt.finder.matchers;

import java.util.Collection;
import java.util.List;

import abbot.swt.finder.generic.Matcher;
import abbot.swt.finder.generic.MultiMatcher;
import abbot.swt.finder.generic.MultipleFoundException;

public class CompositeMultiMatcher<Node> extends CompositeMatcher<Node> implements
		MultiMatcher<Node> {

	public CompositeMultiMatcher(Matcher<Node> matcher0, Matcher<Node> matcher1) {
		super(matcher0, matcher1);
	}

	public CompositeMultiMatcher(Matcher<Node> matcher0, Matcher<Node> matcher1,
			Matcher<Node> matcher2) {
		super(matcher0, matcher1, matcher2);
	}

	public CompositeMultiMatcher(Matcher<Node>[] matchers) {
		super(matchers);
	}

	public CompositeMultiMatcher(Collection<Matcher<Node>> matchers) {
		super(matchers);
	}

	public Node bestMatch(List<Node> nodes) throws MultipleFoundException {

		// Find the node that all our MultiMatchers say is the best one.
		// If there is disagreement then throw a MultipleWidgetsFoundException.
		Node bestMatch = null;
		for (int i = 0; i < matchers.length; i++) {
			if (matchers[i] instanceof MultiMatcher) {
				Node node = ((MultiMatcher<Node>) matchers[i]).bestMatch(nodes);
				if (bestMatch == null) {
					// We found the first MultiMatcher's best match. Remember it.
					bestMatch = node;
				} else if (bestMatch != node) {
					// MultiMatchers disagree over which candidate is best.
					throw new MultipleFoundException("multimatchers disagree");
				}
			}
		}

		// If we got a consensus best match then return it.
		if (bestMatch != null)
			return bestMatch;

		/*
		 * There was no best match so there must have been no MultiMatchers (because if there were
		 * then we wouldn't be here because either we would have already returned or a
		 * MultipleWidgetsFoundException would have been thrown.
		 */

		// If there is exactly one candidate then declare it to be the best.
		if (nodes.size() == 1)
			return nodes.get(0);

		// There were no multimatchers but we have more than 1 candidate, so...
		throw new MultipleFoundException("no multimatchers and multiple candidates");
	}

}
