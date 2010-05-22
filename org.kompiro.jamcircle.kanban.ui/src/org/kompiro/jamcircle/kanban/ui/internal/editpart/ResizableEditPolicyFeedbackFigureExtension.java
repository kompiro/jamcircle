package org.kompiro.jamcircle.kanban.ui.internal.editpart;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.*;
import org.eclipse.gef.*;
import org.eclipse.gef.editpolicies.ResizableEditPolicy;
import org.eclipse.gef.handles.AbstractHandle;
import org.eclipse.gef.handles.RelativeHandleLocator;
import org.eclipse.gef.handles.ResizeHandle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.kompiro.jamcircle.kanban.ui.editpart.AbstractEditPart;
import org.kompiro.jamcircle.kanban.ui.internal.figure.AnnotationArea;
import org.kompiro.jamcircle.kanban.ui.util.WorkbenchUtil;

public class ResizableEditPolicyFeedbackFigureExtension extends
		ResizableEditPolicy {
	
	
	/**
	 * TODO Refactoring
	 */
	private final class LaneResizeHandle extends ResizeHandle {
		private LaneResizeHandle(GraphicalEditPart owner, int direction) {
			super(owner, direction);
			IFigure figure = owner.getFigure();
			if (figure instanceof AnnotationArea<?>) {
				AnnotationArea<?> layer = (AnnotationArea<?>) figure;
				figure = layer.getTargetFigure();
				setLocator(new RelativeHandleLocator(figure, direction));
			}
		}

		@Override
		protected Color getFillColor() {
			return WorkbenchUtil.getDisplay().getSystemColor(SWT.COLOR_GRAY);
		}

		@Override
		public void paintFigure(Graphics g) {
			g.setAlpha(140);
			super.paintFigure(g);
		}
	}


	private final EditPart child;

	public ResizableEditPolicyFeedbackFigureExtension(EditPart child) {
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
	
	@SuppressWarnings("unchecked")
	@Override
	protected List createSelectionHandles() {
		List<AbstractHandle> handles = new ArrayList<AbstractHandle>();
		GraphicalEditPart part = (GraphicalEditPart) getHost();
		handles.add(new RoundedMoveHandle(part));
		handles.add(createHandle(part, PositionConstants.EAST));
		handles.add(createHandle(part, PositionConstants.SOUTH_EAST));
		handles.add(createHandle(part, PositionConstants.SOUTH));
		handles.add(createHandle(part, PositionConstants.SOUTH_WEST));
		handles.add(createHandle(part, PositionConstants.WEST));
		handles.add(createHandle(part, PositionConstants.NORTH_WEST));
		handles.add(createHandle(part, PositionConstants.NORTH));
		handles.add(createHandle(part, PositionConstants.NORTH_EAST));

		return handles;
	}

	private AbstractHandle createHandle(GraphicalEditPart owner, int direction) {
		ResizeHandle handle = new LaneResizeHandle(owner, direction);
		return handle;
	}

	
	@Override
	protected IFigure getFeedbackLayer() {
		return getLayer(LayerConstants.SCALED_FEEDBACK_LAYER);
	}


}

