package org.kompiro.jamcircle.xmpp.kanban.ui.internal.editpart;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.*;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.editpolicies.LayoutEditPolicy;
import org.eclipse.gef.requests.*;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.widgets.*;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.CardContainer;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.command.MoveCommand;
import org.kompiro.jamcircle.kanban.ui.editpart.AbstractEditPart;
import org.kompiro.jamcircle.kanban.ui.editpart.IconEditPart;
import org.kompiro.jamcircle.kanban.ui.model.AbstractModel;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;
import org.kompiro.jamcircle.kanban.ui.widget.CardListListener;
import org.kompiro.jamcircle.kanban.ui.widget.CardListTableViewer;
import org.kompiro.jamcircle.xmpp.kanban.ui.internal.command.*;
import org.kompiro.jamcircle.xmpp.kanban.ui.internal.figure.UserFigure;
import org.kompiro.jamcircle.xmpp.kanban.ui.model.UserModel;

public class UserEditPart extends AbstractEditPart implements IconEditPart{

	@Override
	protected IFigure createFigure() {
		UserModel model = (UserModel) getModel();
		UserFigure userFigure = new UserFigure(model,getImageRegistry());
		userFigure.setLocation(model.getLocation());
		return userFigure;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new LayoutEditPolicy(){
			@Override
			protected EditPolicy createChildEditPolicy(EditPart child) {
				return null;
			}
			@Override
			protected Command getCreateCommand(CreateRequest request) {
				return null;
			}
			@Override
			protected Command getMoveChildrenCommand(Request request) {
				return null;
			}
			@Override
			protected Command getAddCommand(final Request request) {
				CompoundCommand command = new CompoundCommand();
				if (request instanceof ChangeBoundsRequest) {
					ChangeBoundsRequest req = (ChangeBoundsRequest) request;
					for(Object obj : req.getEditParts()){
						if (obj instanceof GraphicalEditPart) {
							GraphicalEditPart part = (GraphicalEditPart) obj;

							EditPart parent = part.getParent();
							if(parent == null || !(parent instanceof GraphicalEditPart)) continue;
							createAddCommand(command,req,part,(GraphicalEditPart)parent);
						}
					}
				}
				return command;
			}

			private void createAddCommand(CompoundCommand command, ChangeBoundsRequest request, GraphicalEditPart part, GraphicalEditPart parent) {
				SendCardCommand sendCommand = new SendCardCommand();
				sendCommand.setPart(part);
				sendCommand.setTarget(getUserModel().getUser());
				command.add(sendCommand);
			}
		});
		
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentEditPolicy(){
			@Override
			protected Command createDeleteCommand(GroupRequest deleteRequest) {
				CompoundCommand command = new CompoundCommand();
				GroupRequest requestToParent = new GroupRequest();
				requestToParent.setEditParts(UserEditPart.this);
				requestToParent.setType(REQ_ORPHAN_CHILDREN);
				command.add(getParent().getCommand(requestToParent));
				command.add(new DeleteUserCommand(getUserModel()));
				return command;
			}
		});
	}
	
	public UserEditPart(BoardModel board){
		super(board);
	}
	
	public UserModel getUserModel(){
		return (UserModel) getModel();
	}

	@Override
	public void showTargetFeedback(Request request) {
		getFigure().setBackgroundColor(ColorConstants.yellow);
		super.showTargetFeedback(request);
	}

	@Override
	public void eraseTargetFeedback(Request request) {
		getFigure().setBackgroundColor(null);
		super.eraseTargetFeedback(request);
	}
			
	@Override
	protected void refreshVisuals() {
		getFigure().repaint();
	}
	
	@Override
	public void doPropertyChange(PropertyChangeEvent prop) {
		if(isPropLocation(prop)){
			figure.setLocation((Point) prop.getNewValue());
			refreshVisuals();			
		}
		else if(isPropPresence(prop)){
			refreshVisuals();
		}
	}

	private boolean isPropLocation(PropertyChangeEvent prop) {
		return AbstractModel.PROP_LOCATION.equals(prop.getPropertyName());
	}

	private boolean isPropPresence(PropertyChangeEvent prop) {
		return UserModel.PROP_PRESENCE.equals(prop.getPropertyName());
	}
	
	@Override
	public void performRequest(Request req) {
		if(RequestConstants.REQ_OPEN.equals(req.getType())){
			Shell shell = getViewer().getControl().getShell();
			ApplicationWindow window = new ApplicationWindow(shell){
				private CardListListener listener;
				private CardListTableViewer viewer;
				protected Control createContents(Composite parent) {
					viewer = new CardListTableViewer(parent);
					CardContainer container = getVirtualCardContainer();
					viewer.setInput(container);
					listener = new CardListListener(){

						public void dragFinished(DragSourceEvent event,
								CardListTableViewer viewer) {
							event.doit = false;
							event.data = null;
						}
						
					};
					viewer.addCardListListener(listener);
					return parent;
				}
				
				@Override
				public boolean close() {
					viewer.removeCardListListener(listener);
					return super.close();
				}
				
				@Override
				protected void configureShell(Shell shell) {
					shell.setText("Send History:" + getUserModel().getName());
					super.configureShell(shell);
				}
			};
			window.create();
			window.open();
		}
	}

	public UserFigure getUserFigure() {
		return (UserFigure) getFigure();
	}

	private CardContainer getVirtualCardContainer() {
		KanbanService service = getKanbanService();
		Card[] cards = service.findCardsSentTo(getUserModel().getUser());
		CardContainer container = new CardContainer.Mock();
		if(cards == null) return container;
		for(Card card : cards){
			container.addCard(card);
		}
		return container;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class key) {
		if(MoveCommand.class.equals(key)){
			return new MoveUserCommand();
		}
		return super.getAdapter(key);
	}

}
