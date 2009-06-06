package abbot.swt.gef.util;

import org.eclipse.gef.EditPart;

import abbot.swt.finder.generic.HierarchyPrinter;
import abbot.swt.gef.finder.EditPartHierarchy;

public class EditPartHierarchyPrinter extends HierarchyPrinter<EditPart> {

	public EditPartHierarchyPrinter(EditPartHierarchy hierarchy, Appendable appendable) {
		super(hierarchy, new EditPartFormatter(appendable));
	}
}
