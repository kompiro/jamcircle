package org.kompiro.jamcircle.kanban.ui.internal.editpart;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.*;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.LayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.kompiro.jamcircle.kanban.command.*;
import org.kompiro.jamcircle.kanban.model.CardContainer;
import org.kompiro.jamcircle.kanban.ui.KanbanImageConstants;
import org.kompiro.jamcircle.kanban.ui.editpart.*;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;
import org.kompiro.jamcircle.kanban.ui.model.TrashModel;
import org.kompiro.jamcircle.kanban.ui.widget.CardListListener;
import org.kompiro.jamcircle.kanban.ui.widget.CardListTableViewer;

public class TrashEditPart extends AbstractIconEditPart implements IconEditPart, CardContainerEditPart {

	private final class TrashLayoutEditPolicy extends LayoutEditPolicy {

		@Override
		protected Command getCreateCommand(CreateRequest request) {
			return null;
		}

		protected Command getAddCommand(Request request) {
			CompoundCommand command = new CompoundCommand();
			command.setDebugLabel("Trash Command"); //$NON-NLS-1$
			GroupRequest req = (GroupRequest) request;
			for (Object obj : req.getEditParts()) {
				if (obj instanceof CardEditPart) {
					CardEditPart cardPart = (CardEditPart) obj;
					command.add(new AddCardToContanerCommand(getTrashModel(), cardPart.getCardModel()));
				}
				if (obj instanceof LaneEditPart) {
					LaneEditPart lanePart = (LaneEditPart) obj;
					command.add(new AddLaneTrashCommand(getTrashModel(), lanePart.getLaneModel()));
				}
			}
			return command;
		}

		@Override
		protected EditPolicy createChildEditPolicy(EditPart child) {
			return null;
		}

		@Override
		protected Command getMoveChildrenCommand(Request request) {
			return null;
		}
	}

	public TrashEditPart(BoardModel board) {
		super(board);
	}

	TrashModel getTrashModel() {
		return (TrashModel) getModel();
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new TrashLayoutEditPolicy());
	}

	@Override
	public void showTargetFeedback(Request request) {
		// #57 setBackgroundColor isn't available to show because background
		// color is overwritten by board image.
		getFigure().setBackgroundColor(ColorConstants.yellow);
		super.showTargetFeedback(request);
	}

	@Override
	public void eraseTargetFeedback(Request request) {
		// #57 setBackgroundColor isn't available to show because background
		// color is overwritten by board image.
		getFigure().setBackgroundColor(null);
		super.eraseTargetFeedback(request);
	}

	@Override
	public void performRequest(Request req) {
		if (REQ_OPEN.equals(req.getType())) {
			CardListListener listener = new CardListListener() {
				public void dragFinished(DragSourceEvent event,
						CardListTableViewer viewer) {
					repaintImage();
				}
			};

			ApplicationWindow window = new ContainerContentsWindow(getViewer().getControl().getShell(),
					getTrashModel(), listener);
			window.create();
			window.open();
		}
	}

	@Override
	protected void refreshVisuals() {
		getFigure().repaint();
	}

	@Override
	public void doPropertyChange(PropertyChangeEvent prop) {
		if (isPropLocation(prop)) {
			figure.setLocation(getTrashModel().getLocation());
			figure.repaint();
		} else if (isPropCard(prop)) {
			repaintImage();
		}
	}

	boolean isPropCard(PropertyChangeEvent prop) {
		return TrashModel.PROP_CARD.equals(prop.getPropertyName());
	}

	public CardContainer getCardContainer() {
		return getTrashModel();
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class key) {
		if (MoveCommand.class.equals(key)) {
			return new MoveIconCommand();
		}
		return super.getAdapter(key);
	}

	@Override
	protected KanbanImageConstants getImageConstants() {
		if (getTrashModel().isEmpty()) {
			return KanbanImageConstants.TRASH_EMPTY_IMAGE;
		}
		return KanbanImageConstants.TRASH_FULL_IMAGE;
	}

	@Override
	protected Point getLocation() {
		return getTrashModel().getLocation();
	}

	@Override
	protected String getImageLabel() {
		return getTrashModel().getContainerName();
	}

	protected void repaintImage() {
		getImageFigure().setImage(createImage());
	}

}
