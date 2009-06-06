package abbot.swt.gef.finder;

import org.eclipse.gef.EditPart;

import abbot.swt.hierarchy.Hierarchy;

/**
 * Provides access to all {@link EditPart}s in a hierarchy.
 */
public interface EditPartHierarchy extends Hierarchy<EditPart> {

	/**
	 * Get the root {@link EditPart} of this {@link EditPartHierarchy}. This is just a convenience
	 * method provided because a {@link EditPartHierarchy} always has exactly one root
	 * {@link EditPart}.
	 * 
	 * @return the root {@link EditPart} of the receiver
	 */
	EditPart getRoot();
}
