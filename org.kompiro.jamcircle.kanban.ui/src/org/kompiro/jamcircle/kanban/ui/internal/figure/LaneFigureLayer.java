package org.kompiro.jamcircle.kanban.ui.internal.figure;

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.LayoutListener;
import org.eclipse.draw2d.geometry.Dimension;
import org.kompiro.jamcircle.kanban.ui.internal.figure.LaneFigure.CardArea;

public class LaneFigureLayer extends Layer {

	private LaneFigure laneFigure;

	public LaneFigureLayer(){
		GridLayout manager = new GridLayout(1,true);
		manager.marginHeight = 0;
		manager.marginWidth = 0;
		setLayoutManager(manager);
		laneFigure = new LaneFigure();
		laneFigure.setOpaque(true);
		laneFigure.setVisible(true);
		LayoutListener listener = new LayoutListener.Stub() {
			@Override
			public void postLayout(IFigure container) {
				laneFigure.setSize(container.getSize());
				laneFigure.repaint();
			}
		};
		addLayoutListener(listener);
		GridData constraint = new GridData();
		add(laneFigure,constraint);
	}
	
	@Override
	public void setSize(int w, int h) {
		super.setSize(w, h);
		laneFigure.setSize(w, h);
		laneFigure.setPreferredSize(w,h);
	}

	public void setStatus(String status) {
		laneFigure.setStatus(status);
	}

	public IFigure getActionSection() {
		return laneFigure.getActionSection();
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
