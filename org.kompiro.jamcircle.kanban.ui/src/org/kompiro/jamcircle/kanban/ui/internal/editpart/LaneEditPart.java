package org.kompiro.jamcircle.kanban.ui.internal.editpart;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.Clickable;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.editpolicies.ContainerEditPolicy;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.gef.requests.SelectionRequest;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.CardContainer;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.ui.KanbanImageConstants;
import org.kompiro.jamcircle.kanban.ui.KanbanUIActivator;
import org.kompiro.jamcircle.kanban.ui.KanbanUIStatusHandler;
import org.kompiro.jamcircle.kanban.ui.Messages;
import org.kompiro.jamcircle.kanban.ui.command.MoveCommand;
import org.kompiro.jamcircle.kanban.ui.dialog.LaneEditDialog;
import org.kompiro.jamcircle.kanban.ui.editpart.AbstractEditPart;
import org.kompiro.jamcircle.kanban.ui.editpart.CardContainerEditPart;
import org.kompiro.jamcircle.kanban.ui.figure.ClickableActionIcon;
import org.kompiro.jamcircle.kanban.ui.internal.command.AddCardToOnBoardContainerCommand;
import org.kompiro.jamcircle.kanban.ui.internal.command.AddLaneTrashCommand;
import org.kompiro.jamcircle.kanban.ui.internal.command.CardCloneCommand;
import org.kompiro.jamcircle.kanban.ui.internal.command.ChangeLaneConstraintCommand;
import org.kompiro.jamcircle.kanban.ui.internal.command.CreateCardCommand;
import org.kompiro.jamcircle.kanban.ui.internal.command.LaneToggleIconizedCommand;
import org.kompiro.jamcircle.kanban.ui.internal.command.LaneUpdateCommand;
import org.kompiro.jamcircle.kanban.ui.internal.command.RemoveCardCommand;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.policy.LaneLocalLayout;
import org.kompiro.jamcircle.kanban.ui.internal.figure.CardFigureLayer;
import org.kompiro.jamcircle.kanban.ui.internal.figure.LaneFigure;
import org.kompiro.jamcircle.kanban.ui.internal.figure.ActionArea;
import org.kompiro.jamcircle.kanban.ui.internal.figure.LaneIconFigure;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;
import org.kompiro.jamcircle.kanban.ui.model.TrashModel;
import org.kompiro.jamcircle.kanban.ui.script.ScriptEvent;
import org.kompiro.jamcircle.kanban.ui.widget.CardListWindow;
import org.kompiro.jamcircle.scripting.ScriptTypes;
import org.kompiro.jamcircle.scripting.ScriptingService;
import org.kompiro.jamcircle.scripting.exception.ScriptingException;
import org.kompiro.jamcircle.storage.model.GraphicalEntity;

/**
 * Controller for Lane model.
 * @author kompiro
 */
public class LaneEditPart extends AbstractEditPart implements CardContainerEditPart{

	private final class OpenListActionIcon extends
			ClickableActionIcon {

		private OpenListActionIcon() {
			super(getIconImage(KanbanImageConstants.OPEN_LIST_ACTION_IMAGE.toString()));
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			ApplicationWindow window = new CardListWindow(getShell(),getCardContainer(),getCommandStack());
			window.create();
			window.open();
		}
	}

	private final class EditActionIcon extends ClickableActionIcon {

		public EditActionIcon() {
			super(getIconImage(KanbanImageConstants.EDIT_IMAGE.toString()));
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			Shell shell = getViewer().getControl().getShell();
			Lane lane = getLaneModel();
			LaneEditDialog dialog = new LaneEditDialog(shell,lane.getStatus(),lane.getScript(),lane.getScriptType());
			int returnCode = dialog.open();
			if(Dialog.OK == returnCode){
				String status = dialog.getStatus();
				String script = dialog.getScript();
				ScriptTypes type = dialog.getScriptType();
				doUpdateLaneCommand(status,script,type);
			}
		}
	}

	private final class IconizeActionIcon extends ClickableActionIcon {
		
		public IconizeActionIcon() {
			super(getIconImage(KanbanImageConstants.LANE_ICONIZE_IMAGE.toString()));
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			getCommandStack().execute(new LaneToggleIconizedCommand(getLaneModel()));
		}
	}

	private final class LaneXYLayoutEditPolicy extends XYLayoutEditPolicy {
		
		public LaneXYLayoutEditPolicy(){
		}

		@Override
		protected Command createChangeConstraintCommand(EditPart child,
				Object constraint) {
			if(child.getParent() != LaneEditPart.this && isNotRectangle(constraint)) return null;
			Object target = child.getModel();
			Rectangle rect = (Rectangle) constraint;
			LaneLocalLayout layout = new LaneLocalLayout();
			layout.calc(rect, getLaneFigure().getCardArea().getBounds());
			MoveCommand<Object> command = getMoveCommand(child);
			command.setModel(target);
			command.setRectangle(rect);
			return command;
		}

		@SuppressWarnings("unchecked")
		private MoveCommand<Object> getMoveCommand(EditPart child) {
			return (MoveCommand<Object>)child.getAdapter(MoveCommand.class);
		}

		private boolean isNotRectangle(Object constraint) {
			return !(constraint instanceof Rectangle);
		}

		@Override
		protected Command getCreateCommand(CreateRequest request) {
			Object object = request.getNewObject();
			if(object instanceof Card){
				Card card = (Card) object;
				CreateCardCommand command = new CreateCardCommand();
				Object container = getHost().getModel();
				command.setContainer((CardContainer)container);
				command.setModel(card);
				return command;				
			}
			return null;
		}
		
		@Override
		protected EditPolicy createChildEditPolicy(EditPart child) {
			return new NonResizableEditPolicyFeedbackFigureExtension(child);
		}

		@Override
		protected Command createAddCommand(EditPart child,Object constraint) {
			if (!(constraint instanceof Rectangle)) {
				return null;
			}
			Rectangle rect = (Rectangle) constraint;
			if(!(child instanceof CardEditPart)){
				return null;
			}
			CardEditPart cardPart = (CardEditPart) child;
			LaneLocalLayout layout = new LaneLocalLayout();
			layout.calc(rect, getLaneFigure().getCardArea().getBounds());
			CompoundCommand command = new CompoundCommand();
			command.add(new AddCardToOnBoardContainerCommand(cardPart.getCardModel(), rect, getLaneModel()));
			return command;
		}
		
		@Override
		protected Command getOrphanChildrenCommand(Request request) {
			if(request instanceof GroupRequest){
				CompoundCommand command = new CompoundCommand();
				GroupRequest req = (GroupRequest) request;
				for(Object o : req.getEditParts()){
					if (o instanceof CardEditPart) {
						CardEditPart child = (CardEditPart) o;
						command.add(new RemoveCardCommand(child.getCardModel(),getLaneModel()));
					}
				}
				return command;
			}
			return null;
		}
	}

	
	private ActionArea<IFigure> actionLayer;
	private LaneIconFigure laneIconFigure;
	private TrashModel trash;
	
	private Clickable iconizeIcon;

	private Clickable editIcon;

	private Clickable openListIcon;
	private LaneFigure laneFigure;
	private ActionArea<IFigure> laneIconActionLayer;

	public LaneEditPart(BoardModel board) {
		super(board);
		this.trash = board.getTrashModel();
	}

	@Override
	public IFigure createFigure() {
		laneFigure = createLaneFigure();
		actionLayer = createActionLayer(laneFigure);
		laneIconFigure = createIconFigure();
		laneIconActionLayer = createActionLayer(laneIconFigure);
		
		if(getLaneModel().isIconized()){
			return laneIconActionLayer;
		}
		return actionLayer;
	}


	private LaneIconFigure createIconFigure() {
		LaneIconFigure laneIconFigure = new LaneIconFigure();			
		Lane lane = getLaneModel();
		laneIconFigure.setLocation(new Point(lane.getX(),lane.getY()));
		laneIconFigure.setStatus(lane.getStatus());
		return laneIconFigure;
	}


	private ActionArea<IFigure> createActionLayer(IFigure figure) {
		ActionArea<IFigure> actionLayer = new ActionArea<IFigure>(figure);
		Lane lane = getLaneModel();
		actionLayer.setLocation(new Point(lane.getX(),lane.getY()));
		return actionLayer;
	}


	private LaneFigure createLaneFigure() {
		LaneFigure laneFigure = new LaneFigure();
		laneFigure.setOpaque(true);
		Lane lane = getLaneModel();
		laneFigure.setSize(lane.getWidth(), lane.getHeight());
		laneFigure.setStatus(lane.getStatus());
		return laneFigure;
	}
	
	@Override
	protected IFigure copiedFigure() {
		if(getLaneModel().isIconized()){
			return createIconFigure();
		}
		return createActionLayer(createLaneFigure());
	}

	private void createActionIcons() {
		iconizeIcon = new IconizeActionIcon();
		editIcon = new EditActionIcon();
		openListIcon = new OpenListActionIcon();
		
		detatchActionIcon();
		
	}

	private void detatchActionIcon() {
		if(getLaneModel().isIconized()){
			IFigure actionSection = laneIconActionLayer.getActionSection();
			actionSection.add(iconizeIcon);
			actionSection.add(editIcon);
			actionSection.add(openListIcon);
		}else{
			IFigure actionSection = actionLayer.getActionSection();
			actionSection.add(iconizeIcon);
			actionSection.add(editIcon);
			actionSection.add(openListIcon);
		}
	}


	private Image getIconImage(String key) {
		return getImageRegistry().get(key);
	}
	
	@Override
	public void activate() {
		createActionIcons();
		hideActionIcons();
		super.activate();
	}
	
	@Override
	protected void addChildVisual(EditPart childEditPart, int index) {
		IFigure child = ((GraphicalEditPart)childEditPart).getFigure();
		if (child instanceof CardFigureLayer) {
			CardFigureLayer card = (CardFigureLayer) child;
			GraphicalEntity model = (GraphicalEntity) childEditPart.getModel();
			model.setDeletedVisuals(false);
			card.setRemoved(false);
			card.setAdded(false);
		}
		getContentPane().add(child,child.getBounds(), -1);
	}
	
	@Override
	protected void removeChildVisual(EditPart childEditPart) {
		IFigure child = ((GraphicalEditPart)childEditPart).getFigure();
		if (child instanceof CardFigureLayer) {
			CardFigureLayer card = (CardFigureLayer) child;
			GraphicalEntity model = (GraphicalEntity) childEditPart.getModel();
			if(model.isDeletedVisuals()){
				card.setRemoved(true);
			}
			card.repaint();
		}
		// Tips : for Animation 
		if(!child.isShowing()){
			getContentPane().remove(child);
		}
	}
	
	@Override
	protected List<?> getModelChildren() {
		Lane laneModel = getLaneModel();
		if(
//				Platform.isRunning() &&
				!laneModel.isIconized()){
			Card[] cards = laneModel.getCards();
			KanbanUIStatusHandler.info("LaneEditPart.getModelChildren() lane:'%s' length:'%d'",laneModel.getStatus(),cards.length); //$NON-NLS-1$
			return Arrays.asList(cards);
		}
		return super.getModelChildren();
	}
	
	@Override
	public IFigure getContentPane() {
		return getLaneFigure().getCardArea();
	}
	
	public LaneFigure getLaneFigure() {
		return this.laneFigure;
	}


	public ActionArea<IFigure> getLaneFigureLayer(){
		return actionLayer;
	}
	

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new LaneXYLayoutEditPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentEditPolicy(){
			@Override
			protected Command createDeleteCommand(GroupRequest deleteRequest) {
				CompoundCommand command = new CompoundCommand();
				GroupRequest requestToParent = new GroupRequest();
				requestToParent.setEditParts(LaneEditPart.this);
				requestToParent.setType(REQ_ORPHAN_CHILDREN);
				command.add(getParent().getCommand(requestToParent));
				command.add(new AddLaneTrashCommand(trash,getLaneModel()));
				return command;
			}
		});
		installEditPolicy(EditPolicy.CONTAINER_ROLE, new ContainerEditPolicy(){

			@Override
			protected Command getCreateCommand(CreateRequest request) {
				return null;
			}
			
			@Override
			protected Command getCloneCommand(ChangeBoundsRequest request) {
				Point targetLocation = request.getLocation();
				targetLocation.translate(getLaneFigure().getCardArea().getLocation().negate());
				targetLocation.translate(new Point(LaneFigure.MARGIN,LaneFigure.MARGIN).negate());
				return new CardCloneCommand(request,getLaneModel());
			}
			
		});
	}
	
	@Override
	public void performRequest(Request req) {
		if(RequestConstants.REQ_OPEN.equals(req.getType())){
			Lane lane = getLaneModel();
			if(lane.isIconized()){
				getCommandStack().execute(new LaneToggleIconizedCommand(getLaneModel()));
				return;
			}
			if (req instanceof SelectionRequest) {
				SelectionRequest sel = (SelectionRequest) req;
				CardCreateRequest cardCreateRequest = new CardCreateRequest(getKanbanService(),getBoardModel().getBoard());
				Point contentPaneLocation = getContentPane().getBounds().getLocation().negate();
				Point targetLocation = sel.getLocation().getCopy().translate(contentPaneLocation);
				targetLocation.translate(-10, -10);
				cardCreateRequest.setLocation(targetLocation);
				getCommandStack().execute(getCommand(cardCreateRequest));
			}
		}
	}
	
	private void doUpdateLaneCommand(String status, String script,ScriptTypes type) {
		getCommandStack().execute(new LaneUpdateCommand(getLaneModel(),status,script,type));
	}

	protected void doPropertyChange(final PropertyChangeEvent evt) {
		GraphicalEditPart parentPart = (GraphicalEditPart) getParent();
		Lane lane = getLaneModel();
		if(isPropConstraint(evt)){
			Rectangle constraint = (Rectangle) evt.getNewValue();
			parentPart.setLayoutConstraint(this, getFigure(), constraint);
		}
		else if(isPropStatus(evt)){
			getLaneFigure().setStatus(lane.getStatus());
			getLaneIconFigure().setStatus(lane.getStatus());
		}
		else if(isChildrenChanged(evt)){
			if(!lane.isIconized()){
				super.doPropertyChange(evt);
			}
			String message = String.format(Messages.LaneEditPart_execute_script_message, lane.getStatus());
			Job scriptJob = new Job(message){
				@Override
				public IStatus run(IProgressMonitor monitor) {
					return 	executeScript(evt,monitor);
				}
			};
			scriptJob.schedule();
		}
		else if(isPropIconized(evt)){
			IFigure parent = getFigure().getParent();
			removeNotify();
			parent.remove(getFigure());
			getContentPane().getChildren().clear();
			if(lane.isIconized()){
				setFigure(laneIconActionLayer);
				laneIconFigure.setLocation(actionLayer.getLocation());
			}else{
				setFigure(actionLayer);
				actionLayer.setLocation(laneIconFigure.getLocation());
			}
			detatchActionIcon();
			parent.add(getFigure());
			getBoardModel().setAnimated(false);
			addNotify();
			getBoardModel().setAnimated(true);
		}
		else if(isPropIconSrc(evt)){
		}
	}


	private IStatus executeScript(PropertyChangeEvent evt, IProgressMonitor monitor) {
		Lane lane = getLaneModel();
		String script = lane.getScript();
		if(Lane.PROP_CARD.equals(evt.getPropertyName()) && script != null){
			Card card = (Card) (evt.getNewValue() != null? evt.getNewValue():evt.getOldValue());
			Map<String,Object> beans= new HashMap<String, Object>();
			beans.put("card", card); //$NON-NLS-1$
			beans.put("lane", lane); //$NON-NLS-1$
			beans.put("event", new ScriptEvent(evt)); //$NON-NLS-1$
			beans.put("monitor", monitor); //$NON-NLS-1$
			
			String scriptName = String.format(Messages.LaneEditPart_script_name,lane.getStatus());
			try {
				getScriptingService().eval(lane.getScriptType(), scriptName, script,beans);
			} catch (ScriptingException e) {
				KanbanUIStatusHandler.fail(e, e.getMessage());
				return new Status(Status.ERROR, KanbanUIActivator.ID_PLUGIN, IStatus.OK, e.getMessage(), e);
			}
		}
		return Status.OK_STATUS;
	}
	
	private boolean isPropIconized(PropertyChangeEvent evt) {
		return Lane.PROP_ICONIZED.equals(evt.getPropertyName());
	}

	private boolean isPropStatus(PropertyChangeEvent evt) {
		return Lane.PROP_STATUS.equals(evt.getPropertyName());
	}

	private boolean isChildrenChanged(PropertyChangeEvent evt) {
		KanbanUIStatusHandler.debug("LaneEditPart.isChildrenChanged() name:" + evt.getPropertyName()); //$NON-NLS-1$
		return Lane.PROP_CARD.equals(evt.getPropertyName());
	}

	private boolean isPropConstraint(PropertyChangeEvent evt) {
		return Lane.PROP_CONSTRAINT.equals(evt.getPropertyName());
	}	

	private boolean isPropIconSrc(PropertyChangeEvent evt) {
		return Lane.PROP_ICON_SRC.equals(evt.getPropertyName());
	}	

	
	public Lane getLaneModel(){
		return (Lane) getModel();
	}

	public CardContainer getCardContainer() {
		return getLaneModel();
	}
	
	@Override
	public void showTargetFeedback(Request request) {
		showActionIcons();
		super.showTargetFeedback(request);
	}


	private void showActionIcons() {
		iconizeIcon.setVisible(true);
		editIcon.setVisible(true);
		openListIcon.setVisible(true);
	}

	@Override
	public void eraseTargetFeedback(Request request) {
		hideActionIcons();
		super.eraseTargetFeedback(request);
	}


	private void hideActionIcons() {
		iconizeIcon.setVisible(false);
		editIcon.setVisible(false);
		openListIcon.setVisible(false);
	}
	
	public LaneIconFigure getLaneIconFigure() {
		return laneIconFigure;
	}
	
	private ScriptingService getScriptingService() throws ScriptingException {
		return KanbanUIActivator.getDefault().getScriptingService();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class key) {
		if(MoveCommand.class.equals(key)){
			return new ChangeLaneConstraintCommand();
		}
		return super.getAdapter(key);
	}
	
}
