package org.kompiro.jamcircle.kanban.ui.internal.figure;

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.LayoutListener;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.kompiro.jamcircle.kanban.ui.internal.figure.LaneFigure.CardArea;

public class LaneFigureLayer extends Layer {

	private LaneFigure laneFigure;
	private IFigure actionArea;

	public LaneFigureLayer(){		
		createLayoutManager();
		createLaneFigure();
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

	private void createLaneFigure() {
		laneFigure = new LaneFigure();
		laneFigure.setVisible(true);
		LayoutListener listener = new LayoutListener.Stub() {
			@Override
			public void postLayout(IFigure container) {
				Rectangle rect = container.getBounds().getCopy();
				laneFigure.setSize(rect.getSize().expand(0, - 16));
				laneFigure.repaint();
				Point location = actionArea.getBounds().getLocation().getCopy();
				actionArea.setLocation(new Point(location.x, rect.getBottomRight().y - 16));
				actionArea.repaint();
			}
		};
		addLayoutListener(listener);
		GridData constraint = new GridData();
		add(laneFigure,constraint);
	}
	
	private void createActionArea() {
		actionArea = new Layer();
		actionArea.setLayoutManager(new ToolbarLayout(true));
		
		GridData constraint = new GridData();
		constraint.heightHint = 16;
		constraint.grabExcessHorizontalSpace = true;
		constraint.grabExcessVerticalSpace = true;
		constraint.horizontalAlignment = SWT.RIGHT;
		constraint.verticalAlignment = SWT.CENTER;
		add(actionArea,constraint);		
	}

	
	@Override
	public void setSize(int w, int h) {
		super.setSize(w, h + 16);
		laneFigure.setSize(w, h);
		laneFigure.setPreferredSize(w,h);
	}

	public void setStatus(String status) {
		laneFigure.setStatus(status);
	}

	public IFigure getActionSection() {
		return actionArea;
	}

	public CardArea getCardArea() {
		return laneFigure.getCardArea();
	}

	public int getMaxCardLocationX(Dimension sourceSize, Dimension targetSize) {
		return laneFigure.getMaxCardLocationX(sourceSize, targetSize);
	}

	public int getMaxCardLocationY(Dimension sourceSize, Dimension targetSize) {
		return laneFigure.getMaxCardLocationY(sourceSize, targetSize);
	}
	
}
