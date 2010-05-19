package org.kompiro.jamcircle.kanban.ui.internal.figure;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.SWT;

public class LaneFigureLayer<G extends Figure> extends Layer {

	private static final int ACTION_ICON_SIZE = 16;
	private G figure;
	private IFigure actionArea;

	public LaneFigureLayer(G figure){
		createLayoutManager();
		createFigure(figure);
		createActionArea();
	}

	private void createLayoutManager() {
		GridLayout manager = new GridLayout(1,true);
		manager.marginHeight = 0;
		manager.verticalSpacing = 0;
		manager.marginWidth = 0;
		manager.horizontalSpacing = 0;
		setLayoutManager(manager);
	}

	private void createFigure(G figure) {
		this.figure = figure;
		figure.setVisible(true);
		GridData constraint = new GridData(GridData.FILL_BOTH);
		add(figure,constraint);
	}
	
	private void createActionArea() {
		actionArea = new Layer();
		actionArea.setLayoutManager(new ToolbarLayout(true));
		
		GridData constraint = new GridData();
		constraint.heightHint = ACTION_ICON_SIZE;
		constraint.horizontalAlignment = SWT.RIGHT;
		constraint.verticalAlignment = SWT.CENTER;
		add(actionArea,constraint);		
	}

	public IFigure getActionSection() {
		return actionArea;
	}

	public IFigure getTargetFigure() {
		return this.figure;
	}
	
}
