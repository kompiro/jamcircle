package abbot.swt.hierarchy;

import java.util.Collection;

public abstract class VisitableImpl<Node> implements Visitable<Node> {

	/**
	 * @see Visitable#accept(Object, Visitor)
	 */
	public final void accept(Node node, Visitor<Node> visitor) {
		acceptInternal(node, visitor);
	}

	protected boolean acceptInternal(Node node, Visitor<Node> visitor) {
		switch (visitor.visit(node)) {
			case ok: // Return the result of visiting the children.
				return acceptInternal(getChildren(node), visitor);
			case prune: // Do not visit children, but caller should continue.
				return true;
			case stop: // Do not visit children, and caller should stop.
				return false;
			default: // Unexpected value.
				throw new RuntimeException("unexpected value");
		}
	}

	protected boolean acceptInternal(Collection<Node> nodes, Visitor<Node> visitor) {
		for (Node child : nodes) {
			if (!acceptInternal(child, visitor))
				return false;
		}
		return true;
	}

}
