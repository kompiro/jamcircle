package org.kompiro.jamcircle.kanban.ui.figure;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Text;

public class CardCellEditorLocator implements org.eclipse.gef.tools.CellEditorLocator {
	private IFigure figure;

	public CardCellEditorLocator(IFigure f) {
		figure = f;
	}

	public void relocate(CellEditor celleditor) {
		Text text = (Text) celleditor.getControl();
		text.computeSize(-1, -1);
		Rectangle rect = figure.getBounds().getCopy();
		figure.translateToAbsolute(rect);
		text.setBounds(rect.x, rect.y, rect.width, rect.height);
	}
}