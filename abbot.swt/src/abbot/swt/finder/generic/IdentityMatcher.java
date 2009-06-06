package abbot.swt.finder.generic;

public class IdentityMatcher<Node> implements Matcher<Node> {

	private final Node node;

	public IdentityMatcher(Node node) {
		this.node = node;
	}

	public boolean matches(Node node) {
		return this.node == node;
	}

}
