package org.kompiro.jamcircle.kanban.ui.figure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SchemeBorder;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

public class ShadowRectangleBoarder extends SchemeBorder {
	public ShadowRectangleBoarder() {
		super(new SchemeBorder.Scheme(
				new Color[]{ColorConstants.lightGray},
				new Color[]{ColorConstants.darkGray}
				));
	}

	@Override
	protected void paint(Graphics graphics, IFigure fig, Insets insets,
			Color[] tl, Color[] br) {
		graphics.setLineStyle(Graphics.LINE_SOLID);
		graphics.setXORMode(false);
		
		Rectangle rect = getPaintRectangle(fig, insets);
		
		int top    = rect.y;
		int left   = rect.x;
		int bottom = rect.bottom();
		int right  = rect.right();
		drawDarkerSide(graphics, br, top, left, bottom, right);
		drawLighterSide(graphics, tl, top, left, bottom, right);
	}

	private void drawDarkerSide(Graphics graphics, Color[] br, int top,
			int left, int bottom, int right) {
		graphics.setLineWidth(3);
		graphics.setAlpha(200);
		Color color;
		for (int i = 0; i < br.length; i++) {
			color = br[i];
			graphics.setForegroundColor(color);
			
			Point rightButtomRight = new Point(right - i , bottom - i);
			Point rightTopRight = new Point(right - i, top + i);
			graphics.drawLine(rightButtomRight, rightTopRight);
			
			Point rightButtomButtom = new Point(right - i, bottom - i);
			Point leftBottomLeft = new Point(left + i, bottom - i);
			graphics.drawLine(rightButtomButtom, leftBottomLeft);
		}
	}

	private void drawLighterSide(Graphics graphics, Color[] tl, int top,
			int left, int bottom, int right) {
		graphics.setAlpha(200);
		right--;
		bottom--;	
		Color color;
		graphics.setLineWidth(1);

		for (int i = 0; i < tl.length; i++) {
			color = tl[i];
			graphics.setForegroundColor(color);

			Point leftTopLeft = new Point(left - i , top - i);
			Point leftBottomLeft = new Point(left - i, bottom + i);
			graphics.drawLine(leftTopLeft, leftBottomLeft);
			
			Point rightTopTop = new Point(right - i, top - i);
			Point leftTopTop = new Point(left + i, top - i);
			graphics.drawLine(rightTopTop, leftTopTop);
		}
	}
}
