package org.kompiro.jamcircle.kanban.ui.internal.editpart;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.*;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.handles.AbstractHandle;
import org.kompiro.jamcircle.kanban.ui.editpart.AbstractEditPart;

public class NonResizableEditPolicyFeedbackFigureExtension extends
		NonResizableEditPolicy {
	private final EditPart child;

	public NonResizableEditPolicyFeedbackFigureExtension(EditPart child) {
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

	@SuppressWarnings("rawtypes")
	@Override
	protected List createSelectionHandles() {
		List<AbstractHandle> handles = new ArrayList<AbstractHandle>();
		handles.add(new RoundedMoveHandle((GraphicalEditPart) getHost()));
		return handles;
	}

	@Override
	protected IFigure getFeedbackLayer() {
		return getLayer(LayerConstants.SCALED_FEEDBACK_LAYER);
	}

}
