package org.kompiro.jamcircle.kanban.ui.internal.editpart;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.swt.graphics.Image;
import org.kompiro.jamcircle.kanban.ui.KanbanImageConstants;
import org.kompiro.jamcircle.kanban.ui.KanbanUIActivator;
import org.kompiro.jamcircle.kanban.ui.command.MoveCommand;
import org.kompiro.jamcircle.kanban.ui.editpart.AbstractEditPart;
import org.kompiro.jamcircle.kanban.ui.editpart.IconEditPart;
import org.kompiro.jamcircle.kanban.ui.internal.command.MoveIconCommand;
import org.kompiro.jamcircle.kanban.ui.internal.figure.LaneIconFigure;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;
import org.kompiro.jamcircle.kanban.ui.model.LaneCreaterModel;

public class LaneCreaterEditPart extends AbstractEditPart implements IconEditPart {

	public LaneCreaterEditPart(BoardModel board) {
		super(board);
	}

	@Override
	protected IFigure createFigure() {
		LaneIconFigure laneIconFigure = new LaneIconFigure();
		laneIconFigure.setStatus(getLaneCreaterModel().getName());
		Image addImage = KanbanUIActivator.getDefault().getImageRegistry()
				.get(KanbanImageConstants.ADD_IMAGE.toString());
		ImageFigure addFigure = new ImageFigure(addImage);
		laneIconFigure.add(addFigure, new Rectangle(72 - 20, 72 - 20, 16, 16));
		laneIconFigure.setLocation(getLaneCreaterModel().getLocation());
		return laneIconFigure;
	}

	@Override
	protected void createEditPolicies() {
	}

	@Override
	public void performRequest(Request req) {
		if (RequestConstants.REQ_OPEN.equals(req.getType())) {
			LaneCreateRequest request = new LaneCreateRequest(getKanbanService(), getBoardModel().getBoard());
			request.setLocation(figure.getBounds().getLocation().getCopy().getTranslated(64, 64));
			Command command = getParent().getCommand(request);
			getCommandStack().execute(command);
		}
	}

	public LaneCreaterModel getLaneCreaterModel() {
		return (LaneCreaterModel) getModel();
	}

	public void doPropertyChange(PropertyChangeEvent evt) {
		if (isPropLocation(evt)) {
			figure.setLocation(getLaneCreaterModel().getLocation());
			figure.repaint();
		}
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class key) {
		if (MoveCommand.class.equals(key)) {
			return new MoveIconCommand();
		}
		return super.getAdapter(key);
	}

}
