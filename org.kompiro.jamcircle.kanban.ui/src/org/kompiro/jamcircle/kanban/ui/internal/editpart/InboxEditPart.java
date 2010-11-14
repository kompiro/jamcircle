package org.kompiro.jamcircle.kanban.ui.internal.editpart;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.*;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.LayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.jface.window.ApplicationWindow;
import org.kompiro.jamcircle.kanban.model.CardContainer;
import org.kompiro.jamcircle.kanban.ui.KanbanImageConstants;
import org.kompiro.jamcircle.kanban.ui.Messages;
import org.kompiro.jamcircle.kanban.ui.editpart.AbstractIconEditPart;
import org.kompiro.jamcircle.kanban.ui.internal.command.AddCardToContanerCommand;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;
import org.kompiro.jamcircle.kanban.ui.model.InboxIconModel;

public class InboxEditPart extends AbstractIconEditPart {

	public InboxEditPart(BoardModel boardModel) {
		super(boardModel);
	}

	protected KanbanImageConstants getImageConstants() {
		return KanbanImageConstants.INBOX_IMAGE;
	}

	@Override
	protected String getImageLabel() {
		return InboxIconModel.NAME;
	}

	@Override
	protected Point getLocation() {
		return getInboxIconModel().getLocation();
	}

	private InboxIconModel getInboxIconModel() {
		return (InboxIconModel) getModel();
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new LayoutEditPolicy() {

			@Override
			protected Command getCreateCommand(CreateRequest request) {
				return null;
			}

			protected Command getAddCommand(Request request) {
				CompoundCommand command = new CompoundCommand();
				GroupRequest req = (GroupRequest) request;
				command.setDebugLabel(Messages.InboxEditPart_command_label);
				for (Object obj : req.getEditParts()) {
					if (obj instanceof CardEditPart) {
						CardEditPart cardPart = (CardEditPart) obj;
						command.add(new AddCardToContanerCommand(getInboxIconModel(), cardPart.getCardModel()));
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

		});
	}

	@Override
	public void performRequest(Request req) {
		if (REQ_OPEN.equals(req.getType())) {
			ApplicationWindow window = new ContainerContentsWindow(getViewer().getControl().getShell(),
					getInboxIconModel(), null);
			window.create();
			window.open();
		}
	}

	@Override
	public void doPropertyChange(PropertyChangeEvent prop) {
		if (isPropLocation(prop)) {
			figure.setLocation(getInboxIconModel().getLocation());
			figure.repaint();
		}
	}

	public CardContainer getCardContainer() {
		return getInboxIconModel();
	}

}
