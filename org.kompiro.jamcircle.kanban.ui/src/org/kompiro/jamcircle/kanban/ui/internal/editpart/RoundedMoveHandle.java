package org.kompiro.jamcircle.kanban.ui.internal.editpart;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.handles.MoveHandle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.kompiro.jamcircle.kanban.ui.internal.figure.CardFigureLayer;
import org.kompiro.jamcircle.kanban.ui.internal.figure.ActionArea;
import org.kompiro.jamcircle.kanban.ui.util.WorkbenchUtil;

public class RoundedMoveHandle extends MoveHandle {
	
	private static final int ARC = 8;
	private static final int LINE_WIDTH = 8;

	public RoundedMoveHandle(GraphicalEditPart owner) {
		super(owner);
	}

	@Override
	public void paint(Graphics graphics) {
		Rectangle f = Rectangle.SINGLETON;
		IFigure ownerFigure = getOwnerFigure();
		if(ownerFigure instanceof CardFigureLayer){
			ownerFigure = ((CardFigureLayer)ownerFigure).getCardFigure();
		}else if(ownerFigure instanceof ActionArea){
			ownerFigure = ((ActionArea)ownerFigure).getTargetFigure();
		}
		Dimension ownerR = ownerFigure.getBounds().getSize();
		Rectangle r = getBounds();
		r.setSize(ownerR);
		f.x = r.x ;
		f.y = r.y ;
		f.width = r.width;
		f.height = r.height;
		graphics.setClip(f);
		graphics.setLineWidth(LINE_WIDTH);
		drawBoundary(graphics, r);
		fillArea(graphics, r);
	}

	private void drawBoundary(Graphics graphics, Rectangle r) {
		Color color = WorkbenchUtil.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);
		graphics.setForegroundColor(color);
		graphics.setAlpha(140);
		graphics.drawRoundRectangle(r, ARC, ARC);
	}

	private void fillArea(Graphics graphics, Rectangle r) {
		Color color = WorkbenchUtil.getDisplay().getSystemColor(SWT.COLOR_BLUE);
		graphics.setAlpha(10);
		graphics.setBackgroundColor(color);
		graphics.fillRoundRectangle(r, ARC, ARC);
	}
}
