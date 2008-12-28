package abbot.swt.finder.generic;

import java.util.Collection;
import java.util.Iterator;

import abbot.swt.hierarchy.Hierarchy;

public class HierarchyPrinter<Node> {

	public interface Formatter<Node> {
		public void format(Node node, int indent);
	}

	protected final Formatter<Node> formatter;

	protected final Hierarchy<Node> hierarchy;

	public HierarchyPrinter(Hierarchy<Node> hierarchy, Formatter<Node> formatter) {
		this.formatter = formatter;
		this.hierarchy = hierarchy;
	}

	public void print() {
		print(0);
	}

	public void print(int indent) {
		print(hierarchy.getRoots(), indent);
	}

	public void print(Node node) {
		print(node, 0);
	}

	public void print(Node node, int indent) {

		// The node itself.
		formatter.format(node, indent);

		// The node's children.
		print(hierarchy.getChildren(node), indent + 1);
	}

	protected void print(Collection<Node> nodes, int indent) {
		for (Iterator<Node> iterator = nodes.iterator(); iterator.hasNext();) {
			Node node = iterator.next();
			print(node, indent);
		}
	}

}
