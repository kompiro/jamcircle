package org.kompiro.jamcircle.kanban.ui.internal.figure;

import static org.kompiro.jamcircle.kanban.ui.internal.figure.LaneFigure.COLOR_KEY_LANE_BODY;
import static org.kompiro.jamcircle.kanban.ui.internal.figure.LaneFigure.COLOR_KEY_LANE_BORDER;
import static org.kompiro.jamcircle.kanban.ui.internal.figure.LaneFigure.LANE_BORDER_LINE_WIDTH;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.SchemeBorder;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.resource.JFaceResources;

public class LaneIconFigure extends RectangleFigure {
	private Label statusFigure;

	public LaneIconFigure(){
		setLayoutManager(new XYLayout());
		SchemeBorder outer = new ShadowRectangleBoarder();
		setBorder(outer);
		setBackgroundColor(JFaceResources.getColorRegistry().get(COLOR_KEY_LANE_BODY));
		setLineWidth(4);
		setSize(72, 72);
		statusFigure = new Label();
		statusFigure.setTextAlignment(PositionConstants.CENTER);
		add(statusFigure,new Rectangle(0,72-20,72,10));
	}
	
	protected void outlineShape(Graphics graphics) {
		Rectangle f = Rectangle.SINGLETON;
		Rectangle r = getBounds();
		int borderHalfWidth = LANE_BORDER_LINE_WIDTH / 2;
		f.x = r.x + borderHalfWidth;
		f.y = r.y + borderHalfWidth;
		f.width = r.width - LANE_BORDER_LINE_WIDTH;
		f.height = r.height - LANE_BORDER_LINE_WIDTH;
		graphics.setForegroundColor(JFaceResources.getColorRegistry().get(COLOR_KEY_LANE_BORDER));
		graphics.drawRectangle(f);
		graphics.setForegroundColor(ColorConstants.gray);
		graphics.setLineWidth(1);
		Rectangle in = f.getCopy();
		in.x = in.x + borderHalfWidth;
		in.y = in.y + borderHalfWidth;
		in.width = in.width - LANE_BORDER_LINE_WIDTH;
		in.height = in.height - LANE_BORDER_LINE_WIDTH;
		graphics.drawLine(in.getTopLeft(), in.getTopRight());
		graphics.drawLine(in.getTopLeft(), in.getBottomLeft());
	}

	public void setStatus(String status){
		statusFigure.setText(status);
	}
	
	
}
