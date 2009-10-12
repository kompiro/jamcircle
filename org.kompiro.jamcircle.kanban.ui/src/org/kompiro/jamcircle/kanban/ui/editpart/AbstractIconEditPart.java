package org.kompiro.jamcircle.kanban.ui.editpart;

import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;
import org.kompiro.jamcircle.kanban.ui.KanbanImageConstants;
import org.kompiro.jamcircle.kanban.ui.command.MoveCommand;
import org.kompiro.jamcircle.kanban.ui.internal.command.MoveIconCommand;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;

public abstract class AbstractIconEditPart extends AbstractEditPart implements
		IconEditPart {

	public AbstractIconEditPart(BoardModel boardModel) {
		super(boardModel);
	}

	@Override
	protected IFigure createFigure() {
		Image image = getImageRegistry().get(getImageConstants().toString());
		ImageFigure imageFigire = new ImageFigure(image);
		imageFigire.setLayoutManager(new XYLayout());
		imageFigire.setSize(72,72);
		Label labelFigure = new Label();
		labelFigure.setTextAlignment(PositionConstants.CENTER);
		labelFigure.setText(getImageLabel());
		imageFigire.add(labelFigure,getLabelConstraint());
		imageFigire.setLocation(getLocation());
		return imageFigire;
	}

	protected abstract Rectangle getLabelConstraint();

	protected abstract KanbanImageConstants getImageConstants();
	
	protected abstract Point getLocation();
	
	protected abstract String getImageLabel();

	
	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class key) {
		if(MoveCommand.class.equals(key)){
			return new MoveIconCommand();
		}
		return super.getAdapter(key);
	}

}
