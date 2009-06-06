package abbot.swt.hierarchy;

import java.util.Collection;

/**
 * @param <Node>
 *            the type of a node in an IHierarchy
 */

public interface Hierarchy<Node> extends Visitable<Node> {

	/**
	 * Gets the root {@code Node}s of this IHierarchy.
	 * 
	 * @return the root {@code Node}s of this IHierarchy
	 */
	Collection<Node> getRoots();

	/**
	 * Gets the parent of a {@code Node}.
	 * 
	 * @param node
	 *            the {@code Node}
	 * @return the parent of the {@code Node}
	 */
	Node getParent(Node node);

	/**
	 * Determines if a {@code Node} is present in this IHierarchy.
	 * 
	 * @param node
	 *            the {@code Node}
	 * @return <code>true</code> if <code>node</code> is present in this IHierarchy,
	 *         <code>false</code> otherwise.
	 */
	boolean contains(Node node);

	/**
	 * Traverse the {@code Node}s in this IHierarchy using the Visitor pattern.
	 * 
	 * @param visitor
	 *            the {@code Visitor} to invoke on each {@code Node}.
	 * @see Visitable.Visitor
	 */
	void accept(Visitor<Node> visitor);

}
