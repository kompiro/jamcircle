package abbot.swt.hierarchy;

public abstract class HierarchyImpl<Node> extends VisitableImpl<Node> implements Hierarchy<Node> {

	/**
	 * @see abbot.swt.hierarchy.Hierarchy#contains(java.lang.Object)
	 */
	public final boolean contains(Node node) {
		for (Node root : getRoots()) {
			if (contains(root, node))
				return true;
		}
		return false;
	}

	/**
	 * 
	 */
	private boolean contains(Node root, Node node) {
		if (node.equals(root))
			return true;
		for (Node child : getChildren(root)) {
			if (contains(child, node))
				return true;
		}
		return false;
	}

	public final void accept(Visitor<Node> visitor) {
		acceptInternal(getRoots(), visitor);
	}

}
