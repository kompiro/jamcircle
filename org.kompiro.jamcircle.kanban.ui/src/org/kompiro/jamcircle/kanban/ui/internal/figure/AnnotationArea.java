package org.kompiro.jamcircle.kanban.ui.internal.figure;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.SWT;

public class AnnotationArea<FIG extends Figure> extends Layer {

	public static final int ACTION_ICON_SIZE = 16;
	public static final int ANNOTATION_HEIGHT = ACTION_ICON_SIZE * 2;
	private FIG figure;
	private IFigure statusArea;
	private IFigure actionArea;

	public AnnotationArea(FIG figure){
		createLayoutManager();
		createStatusArea();
		setFigure(figure);
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

	private void createStatusArea() {
		statusArea = new Layer();
		statusArea.setLayoutManager(new ToolbarLayout(true));
		
		GridData constraint = new GridData();
		constraint.heightHint = ACTION_ICON_SIZE;
		constraint.horizontalAlignment = SWT.RIGHT;
		constraint.verticalAlignment = SWT.CENTER;
		add(statusArea,constraint);		
	}

	private void setFigure(FIG figure) {
		this.figure = figure;
		setSize(figure.getSize().getCopy().expand(0, ANNOTATION_HEIGHT));
		setLocation(figure.getLocation());
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
	
	public IFigure getStatusSection() {
		return statusArea;
	}

	public FIG getTargetFigure() {
		return this.figure;
	}
		
}
