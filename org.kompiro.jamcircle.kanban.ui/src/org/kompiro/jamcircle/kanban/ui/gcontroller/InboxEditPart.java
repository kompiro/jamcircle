package org.kompiro.jamcircle.kanban.ui.gcontroller;

import java.beans.PropertyChangeEvent;


import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.LayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.graphics.Image;
import org.kompiro.jamcircle.kanban.model.CardContainer;
import org.kompiro.jamcircle.kanban.ui.KanbanImageConstants;
import org.kompiro.jamcircle.kanban.ui.command.AddCardToContanerCommand;
import org.kompiro.jamcircle.kanban.ui.model.AbstractModel;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;
import org.kompiro.jamcircle.kanban.ui.model.InboxIconModel;

public class InboxEditPart extends AbstractEditPart implements IconEditPart,CardContainerEditPart {

	public InboxEditPart(BoardModel boardModel) {
		super(boardModel);
	}

	@Override
	protected IFigure createFigure() {
		Image inbox = getImageRegistry().get(KanbanImageConstants.INBOX_IMAGE.toString());
		ImageFigure inboxImage = new ImageFigure(inbox);
		inboxImage.setLayoutManager(new XYLayout());
		inboxImage.setSize(72,72);
		Label labelFigure = new Label();
		labelFigure.setTextAlignment(PositionConstants.CENTER);
		labelFigure.setText("INBOX");
		inboxImage.add(labelFigure,new Rectangle(0,72-10,72,10));
		inboxImage.setLocation(getInboxIconModel().getLocation());
		return inboxImage;
	}

	private InboxIconModel getInboxIconModel() {
		return (InboxIconModel)getModel();
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE,new LayoutEditPolicy(){

			@Override
			protected Command getCreateCommand(CreateRequest request) {
				return null;
			}
			
			protected Command getAddCommand(Request request) {
				CompoundCommand command = new CompoundCommand();
				GroupRequest req = (GroupRequest) request;
				command.setDebugLabel("INBOX Command");
				for(Object obj : req.getEditParts()){
					if (obj instanceof CardEditPart) {
						CardEditPart cardPart = (CardEditPart) obj;
						command.add(new AddCardToContanerCommand(getInboxIconModel(),cardPart.getCardModel()));
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
		if(REQ_OPEN.equals(req.getType())){
			ApplicationWindow window = new ContainerContentsWindow(getViewer().getControl().getShell(),getInboxIconModel(),null);
			window.create();
			window.open();
		}
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent prop) {
		if(isPropLocation(prop)){
			figure.setLocation(getInboxIconModel().getLocation());
			figure.repaint();
		}
	}


	private boolean isPropLocation(PropertyChangeEvent prop) {
		return AbstractModel.PROP_LOCATION.equals(prop.getPropertyName());
	}

	public CardContainer getCardContainer() {
		return getInboxIconModel();
	}

}
