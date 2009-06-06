package abbot.swt.gef.finder;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.draw2d.IFigure;

import abbot.swt.hierarchy.HierarchyImpl;

/**
 * Provides access to a GEF hierarchy. Finders that use this hierarchy will be able to search for
 * GEF objects (in addition to SWT Widgets).
 */

public class FigureHierarchyImpl extends HierarchyImpl<IFigure> implements FigureHierarchy {

	private final IFigure root;

	public FigureHierarchyImpl(IFigure root) {
		if (root == null)
			throw new IllegalArgumentException("root is null");
		this.root = root;
	}

	/**
	 * @see abbot.swt.gef.finder.FigureHierarchy#getRoot()
	 */
	public IFigure getRoot() {
		return root;
	}

	/**
	 * @see abbot.swt.hierarchy.Hierarchy#getRoots()
	 */
	public Collection<IFigure> getRoots() {
		return Collections.singletonList(root);
	}

	/**
	 * @see abbot.swt.hierarchy.Visitable#getChildren(java.lang.Object)
	 */
	public Collection<IFigure> getChildren(IFigure figure) {
		return figure.getChildren();
	}

	/**
	 * @see abbot.swt.hierarchy.Hierarchy#getParent(java.lang.Object)
	 */
	public IFigure getParent(IFigure figure) {
		if (figure == root)
			return null;
		return figure.getParent();
	}

}
