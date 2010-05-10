package org.kompiro.jamcircle.kanban.ui.internal.figure;

import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.Layer;

public class LaneFigureLayer extends Layer {

	private LaneFigure laneFigure;

	public LaneFigureLayer(){
		FlowLayout mainManager = new FlowLayout();
		mainManager.setMajorSpacing(0);
		mainManager.setMinorSpacing(0);
		setLayoutManager(mainManager);
		laneFigure = new LaneFigure();
		laneFigure.setSize(200,500);
		setSize(200,500);
		add(laneFigure);
	}

	public void setStatus(String status) {
		laneFigure.setStatus(status);
	}
	
}
