package abbot.swt.gef.finder;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.gef.EditPart;

import abbot.swt.hierarchy.HierarchyImpl;

/**
 * Provides access to a GEF hierarchy. Finders that use this hierarchy will be able to search for
 * GEF objects (in addition to SWT Widgets).
 */

public class EditPartHierarchyImpl extends HierarchyImpl<EditPart> implements EditPartHierarchy {

	private final EditPart root;

	public EditPartHierarchyImpl(EditPart root) {
		if (root == null)
			throw new IllegalArgumentException("root is null");
		this.root = root;
	}

	/**
	 * @see abbot.swt.gef.finder.EditPartHierarchy#getRoot()
	 */
	public EditPart getRoot() {
		return root;
	}

	/**
	 * @see abbot.swt.hierarchy.Hierarchy#getRoots()
	 */
	public Collection<EditPart> getRoots() {
		return Collections.singletonList(root);
	}

	/**
	 * @see abbot.swt.hierarchy.Visitable#getChildren(java.lang.Object)
	 */
	public Collection<EditPart> getChildren(EditPart editPart) {
		return editPart.getChildren();
	}

	/**
	 * @see abbot.swt.hierarchy.Hierarchy#getParent(java.lang.Object)
	 */
	public EditPart getParent(EditPart editPart) {
		if (editPart == root)
			return null;
		return editPart.getParent();
	}

}
