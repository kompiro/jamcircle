package org.kompiro.jamcircle.kanban.ui.internal.editpart;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.*;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.handles.AbstractHandle;
import org.eclipse.gef.handles.MoveHandle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.kompiro.jamcircle.kanban.ui.editpart.AbstractEditPart;
import org.kompiro.jamcircle.kanban.ui.internal.figure.CardFigureLayer;
import org.kompiro.jamcircle.kanban.ui.util.WorkbenchUtil;

public class NonResizableEditPolicyFeedbackFigureExtension extends
		NonResizableEditPolicy {
	private final class RoundedMoveHandle extends MoveHandle {
		private RoundedMoveHandle(GraphicalEditPart owner) {
			super(owner);
		}

		@Override
		public void paint(Graphics graphics) {
			Rectangle f = Rectangle.SINGLETON;
			IFigure ownerFigure = getOwnerFigure();
			if(ownerFigure instanceof CardFigureLayer){
				ownerFigure = ((CardFigureLayer)ownerFigure).getCardFigure();
			}
			Rectangle r = ownerFigure.getBounds();
			f.x = r.x - 4;
			f.y = r.y - 4;
			f.width = r.width + 8;
			f.height = r.height + 8;
			graphics.setClip(f);
			graphics.setLineWidth(2);
			drawBoundary(graphics, r);
			fillArea(graphics, r);
		}

		private void drawBoundary(Graphics graphics, Rectangle r) {
			Color color = WorkbenchUtil.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);
			graphics.setForegroundColor(color);
			graphics.setAlpha(140);
			graphics.drawRoundRectangle(r, 8, 8);
		}

		private void fillArea(Graphics graphics, Rectangle r) {
			Color color = WorkbenchUtil.getDisplay().getSystemColor(SWT.COLOR_BLUE);
			graphics.setAlpha(5);
			graphics.setBackgroundColor(color);
			graphics.fillRoundRectangle(r, 8, 8);
		}
	}

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
	
	@SuppressWarnings("unchecked")
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
