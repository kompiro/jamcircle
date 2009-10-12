package org.kompiro.jamcircle.kanban.ui.internal.editpart;

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.kompiro.jamcircle.kanban.ui.editpart.AbstractEditPart;

class NonResizableEditPolicyFeedbackFigureExtension extends
		NonResizableEditPolicy {
	private final EditPart child;

	NonResizableEditPolicyFeedbackFigureExtension(EditPart child) {
		this.child = child;
	}

	@Override
	protected IFigure createDragSourceFeedbackFigure() {
		EditPart part = child;
		if (part instanceof AbstractEditPart) {
			AbstractEditPart g = (AbstractEditPart) part;
			IFigure copyFigure = g.copyFigureForDragAndDrop();
			addFeedback(copyFigure);
			return copyFigure;
		}
		return null;
	}
	
	@Override
	protected void showSelection() {
		super.showSelection();
		EditPart editPart = getHost().getParent();
		if (getHost() instanceof CardEditPart) {
			AbstractEditPart part = (AbstractEditPart) editPart;
			part.reorder(getHost());
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected List createSelectionHandles() {
		return super.createSelectionHandles();
	}

	@Override
	protected IFigure getFeedbackLayer() {
		return getLayer(LayerConstants.SCALED_FEEDBACK_LAYER);
	}

}
