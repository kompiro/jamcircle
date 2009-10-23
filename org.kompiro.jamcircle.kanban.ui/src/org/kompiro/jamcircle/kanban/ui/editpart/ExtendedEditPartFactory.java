package org.kompiro.jamcircle.kanban.ui.editpart;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

public interface ExtendedEditPartFactory extends EditPartFactory {

	public SupportedClassPair[] supportedClasses();
		
}
