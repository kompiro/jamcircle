package org.kompiro.jamcircle.kanban.ui.gcontroller;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editpolicies.ResizableEditPolicy;

class ResizableEditPolicyFeedbackFigureExtension extends
		ResizableEditPolicy {
	private final EditPart child;

	ResizableEditPolicyFeedbackFigureExtension(EditPart child) {
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
		if (editPart instanceof AbstractEditPart) {
			AbstractEditPart part = (AbstractEditPart) editPart;
			part.reorder(getHost());
		}
	}
	
	@Override
	protected IFigure getFeedbackLayer() {
		return getLayer(LayerConstants.SCALED_FEEDBACK_LAYER);
	}


}

