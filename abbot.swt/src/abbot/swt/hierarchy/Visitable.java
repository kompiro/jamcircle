package abbot.swt.hierarchy;

import java.util.Collection;

/**
 * A hierarchy of arbitrary node type which has no explicit root(s).
 * 
 * @param <Node>
 *            the type of the nodes
 */

public interface Visitable<Node> {

	/**
	 * Gets the children of a {@code Node}.
	 * 
	 * @param node
	 *            the {@code Node}
	 * @return the children of the {@code Node}
	 */
	Collection<Node> getChildren(Node node);

	/**
	 * Traverses a {@code Node} hierarchy using the "Visitor" pattern.
	 * 
	 * @param node
	 *            the {@code Node} to start at
	 * @param visitor
	 *            the {@code Visitor} to invoke on each {@code Node}.
	 * @see Visitor
	 */
	void accept(Node node, Visitor<Node> visitor);

	/**
	 * @see Visitable#accept(Object, Visitor)
	 */
	public interface Visitor<Node> {

		/**
		 * The possible return values of {@link #visit(Object)}.
		 */
		public enum Result {
			ok, prune, stop
		}

		/**
		 * The method invoked by
		 * {@link Visitable#accept(Object, Visitor)}
		 * on each {@code Node} during traversal.
		 * 
		 * @param node
		 *            the {@code Node}
		 * @return one of the following:
		 *         <ul>
		 *         <li>{@link Result#ok} if traversal should continue normally;</li>
		 *         <li>{@link Result#prune} if traversal should continue
		 *         normally, but without visiting the current node's children;
		 *         or</li>
		 *         <li>{@link Result#stop} if traversal should terminate
		 *         immediately</li>
		 *         </ul>
		 */
		Result visit(Node node);

	}
}
