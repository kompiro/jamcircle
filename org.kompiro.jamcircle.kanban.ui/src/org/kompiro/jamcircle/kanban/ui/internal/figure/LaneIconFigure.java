package org.kompiro.jamcircle.kanban.ui.internal.figure;

import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.resource.JFaceResources;

public class LaneIconFigure extends RectangleFigure {
	private Label statusFigure;

	public LaneIconFigure(){
		setLayoutManager(new XYLayout());
		SchemeBorder outer = new ShadowRectangleBoarder();
		setBorder(outer);
		setBackgroundColor(JFaceResources.getColorRegistry().get(LaneFigure.COLOR_KEY_LANE_BODY));
		setLineWidth(4);
		setSize(72, 72);
		statusFigure = new Label();
		statusFigure.setTextAlignment(PositionConstants.CENTER);
		add(statusFigure,new Rectangle(0,72-20,72,10));
	}
	
	protected void outlineShape(Graphics graphics) {
		Rectangle f = Rectangle.SINGLETON;
		Rectangle r = getBounds();
		f.x = r.x + lineWidth / 2;
		f.y = r.y + lineWidth / 2;
		f.width = r.width - lineWidth;
		f.height = r.height - lineWidth;
		graphics.setForegroundColor(JFaceResources.getColorRegistry().get(LaneFigure.COLOR_KEY_LANE_BORDER));
		graphics.drawRectangle(f);
	}

	public void setStatus(String status){
		if(statusFigure == null) throw new IllegalStateException("LaneFigure:statusFigure is not initialized.");
		statusFigure.setText(status);
	}
	
	
}
