package org.kompiro.jamcircle.kanban.ui.internal.figure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SchemeBorder;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

public class ShadowBoarder extends SchemeBorder {
	private Dimension corner;
	public ShadowBoarder(Dimension corner) {
		super(new SchemeBorder.Scheme(
				new Color[]{ColorConstants.lightGray},
				new Color[]{ColorConstants.darkGray}
				));
		this.corner = corner;
	}

	@Override
	protected void paint(Graphics graphics, IFigure fig, Insets insets,
			Color[] tl, Color[] br) {
		graphics.setLineStyle(Graphics.LINE_SOLID);
		graphics.setXORMode(false);
		
		Rectangle rect = getPaintRectangle(fig, insets);
		
		int top    = rect.y;
		int left   = rect.x;
		int bottom = rect.bottom() - 1;
		int right  = rect.right() - 1;
		drawDarkerSide(graphics, br, top, left, bottom, right);
		drawLighterSide(graphics, tl, top, left, bottom, right);
	}

	private void drawDarkerSide(Graphics graphics, Color[] br, int top,
			int left, int bottom, int right) {
		graphics.setLineWidth(1);
		graphics.setAlpha(200);
		Color color;
		for (int i = 0; i < br.length; i++) {
			color = br[i];
			graphics.setForegroundColor(color);
			
			graphics.drawArc(right - i - corner.width, bottom - i - corner.height, corner.width,corner.height, 270, 90);
			Point rightButtomRight = new Point(right - i , bottom - i - corner.height / 2);
			Point rightTopRight = new Point(right - i, top + i + corner.height / 2);
			graphics.drawLine(rightButtomRight, rightTopRight);
			graphics.drawArc(right - i - corner.width, top - i, corner.width, corner.height, 0, 45);
			
			Point rightButtomButtom = new Point(right - i - corner.width / 2, bottom - i);
			Point leftBottomLeft = new Point(left + i + corner.width / 2, bottom - i);
			graphics.drawLine(rightButtomButtom, leftBottomLeft);
			graphics.drawArc(left - i, bottom - i - corner.height, corner.width, corner.height, 180 + 45, 45);
		}
	}

	private void drawLighterSide(Graphics graphics, Color[] tl, int top,
			int left, int bottom, int right) {
		graphics.setAlpha(255);
		right--;
		bottom--;	
		Color color;
		graphics.setLineWidth(1);

		for (int i = 0; i < tl.length; i++) {
			color = tl[i];
			graphics.setForegroundColor(color);

			graphics.drawArc(left - i, top - i, corner.width,corner.height, 90, 90);
			Point leftTopLeft = new Point(left - i , top - i + corner.height / 2);
			Point leftBottomLeft = new Point(left - i, bottom + i - corner.height / 2);
			graphics.drawLine(leftTopLeft, leftBottomLeft);
			graphics.drawArc(left - i , bottom - i - corner.height, corner.width, corner.height, 180, 45);
			
			Point rightTopTop = new Point(right - i - corner.width / 2, top - i);
			Point leftTopTop = new Point(left + i + corner.width / 2, top - i);
			graphics.drawLine(rightTopTop, leftTopTop);
			graphics.drawArc(right - i - corner.width, top - i , corner.width, corner.height, 0 + 45, 45);
		}
	}
}
