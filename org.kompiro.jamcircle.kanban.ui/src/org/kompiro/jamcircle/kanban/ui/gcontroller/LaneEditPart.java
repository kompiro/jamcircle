package org.kompiro.jamcircle.kanban.ui.gcontroller;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.bsf.util.IOUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Clickable;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
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
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.CardContainer;
import org.kompiro.jamcircle.kanban.model.ColorTypes;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.model.ScriptTypes;
import org.kompiro.jamcircle.kanban.ui.KanbanImageConstants;
import org.kompiro.jamcircle.kanban.ui.KanbanUIActivator;
import org.kompiro.jamcircle.kanban.ui.KanbanUIStatusHandler;
import org.kompiro.jamcircle.kanban.ui.command.AddCardToOnBoardContainerCommand;
import org.kompiro.jamcircle.kanban.ui.command.AddLaneTrashCommand;
import org.kompiro.jamcircle.kanban.ui.command.CardCloneCommand;
import org.kompiro.jamcircle.kanban.ui.command.CardUpdateCommand;
import org.kompiro.jamcircle.kanban.ui.command.ChangeLaneConstraintCommand;
import org.kompiro.jamcircle.kanban.ui.command.CreateCardCommand;
import org.kompiro.jamcircle.kanban.ui.command.LaneToggleIconizedCommand;
import org.kompiro.jamcircle.kanban.ui.command.LaneUpdateCommand;
import org.kompiro.jamcircle.kanban.ui.command.MoveCardCommand;
import org.kompiro.jamcircle.kanban.ui.command.RemoveCardCommand;
import org.kompiro.jamcircle.kanban.ui.dialog.LaneEditDialog;
import org.kompiro.jamcircle.kanban.ui.figure.CardFigure;
import org.kompiro.jamcircle.kanban.ui.figure.LaneFigure;
import org.kompiro.jamcircle.kanban.ui.figure.LaneIconFigure;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;
import org.kompiro.jamcircle.kanban.ui.model.TrashModel;
import org.kompiro.jamcircle.kanban.ui.script.ScriptEvent;
import org.kompiro.jamcircle.kanban.ui.widget.CardListEditProvider;
import org.kompiro.jamcircle.kanban.ui.widget.CardListTableViewer;
import org.kompiro.jamcircle.storage.model.GraphicalEntity;
public class LaneEditPart extends AbstractEditPart implements CardContainerEditPart{

	private class ScriptExcecuteWithProgress implements IRunnableWithProgress {

		private PropertyChangeEvent evt;

		public ScriptExcecuteWithProgress(PropertyChangeEvent evt) {
			this.evt = evt;
		}

		public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {
			executeScript(evt,monitor);
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
			if(target instanceof Card){
				Card card = (Card)target;
				MoveCardCommand command = new MoveCardCommand(card,rect);
				return command;
			}
			if(target instanceof Lane){
				Lane lane = (Lane)target;
				CompoundCommand command = new CompoundCommand();
				command.add(new ChangeLaneConstraintCommand(lane,rect));
				
				return command;	
			}
			return null;
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

	private ActionListener iconizeListener = new ActionListener(){
		public void actionPerformed(ActionEvent event) {
			getCommandStack().execute(new LaneToggleIconizedCommand(getLaneModel()));
		}
	};
	
	private LaneFigure laneFigure;
	private LaneIconFigure laneIconFigure;
	private TrashModel trash;
	private Clickable iconizeIcon;
	private Clickable editIcon;
	private ActionListener editListener = new ActionListener(){
		public void actionPerformed(ActionEvent event) {
			Shell shell = getViewer().getControl().getShell();
			Lane lane = getLaneModel();
			LaneEditDialog dialog = new LaneEditDialog(shell,lane.getStatus(),lane.getScript(),lane.getScriptType());
			int returnCode = dialog.open();
			if(Dialog.OK == returnCode){
				String status = dialog.getStatusText();
				String script = dialog.getScriptText();
				ScriptTypes type = dialog.getScriptType();
				doUpdateLaneCommand(status,script,type);
			}
		}
	};

	private Clickable openListIcon;
	private ActionListener openListListener = new ActionListener(){
		public void actionPerformed(ActionEvent event) {
			Shell shell = getViewer().getControl().getShell();
			ApplicationWindow window = new ApplicationWindow(shell){
				private CardListTableViewer viewer;
				@Override
				protected Control createContents(Composite parent) {
					this.viewer = new CardListTableViewer(parent);
					viewer.setInput(getCardContainer());
					getCardContainer().addPropertyChangeListener(viewer);
					viewer.setEditProvider(new CardListEditProvider(){
						public void edit(Card card, String subject,
								String content, Date dueDate, List<File> files) {
							CardUpdateCommand command = new CardUpdateCommand(card,subject,content,dueDate,files);
							getCommandStack().execute(command);
							viewer.refresh();
						}
					});
					return parent;
				}
				
				@Override
				public boolean close() {
					getCardContainer().removePropertyChangeListener(viewer);
					return super.close();
				}
				
				@Override
				protected void configureShell(Shell shell) {
					super.configureShell(shell);
					String title = String.format("Card List: %s",getCardContainer().getContainerName());
					shell.setText(title);
					Image image = KanbanUIActivator.getDefault().getImageRegistry().get(KanbanImageConstants.KANBANS_IMAGE.toString());
					shell.setImage(image);
				}
			};
			window.create();
			window.open();
		}
	};

	public LaneEditPart(BoardModel board) {
		super(board);
		this.trash = board.getTrashModel();
	}


	@Override
	protected IFigure createFigure() {
		Lane lane = getLaneModel();
		LaneFigure laneFigure = new LaneFigure();
		laneFigure.setSize(lane.getWidth(), lane.getHeight());
		laneFigure.setOpaque(true);
		laneFigure.setLocation(new Point(lane.getX(),lane.getY()));
		laneFigure.setStatus(lane.getStatus());
		if(this.laneFigure == null){
			this.laneFigure = laneFigure;
		}
				
		LaneIconFigure iconFigure = new LaneIconFigure();
		iconFigure.setLocation(new Point(lane.getX(),lane.getY()));
		iconFigure.setStatus(lane.getStatus());
		if(this.laneIconFigure == null){
			this.laneIconFigure = iconFigure;
		}
		if(lane.isIconized()){
			return iconFigure;
		}
		return laneFigure;
	}


	private void createActionIcons() {
		ImageRegistry imageRegistry = getImageRegistry();
		Image iconizeIconImage = imageRegistry.get(KanbanImageConstants.LANE_ICONIZE_IMAGE.toString());
		iconizeIcon = new Clickable(new Label(iconizeIconImage));
		iconizeIcon.setSize(16,16);
		laneFigure.getActionSection().add(iconizeIcon);
		
		Image editIconImage = imageRegistry.get(KanbanImageConstants.EDIT_IMAGE.toString());
		editIcon = new Clickable(new Label(editIconImage));
		editIcon.setSize(16, 16);
		laneFigure.getActionSection().add(editIcon);
		
		Image openListIconImage = imageRegistry.get(KanbanImageConstants.OPEN_LIST_ACTION_IMAGE.toString());
		openListIcon = new Clickable(new Label(openListIconImage));
		openListIcon.setSize(16, 16);
		laneFigure.getActionSection().add(openListIcon);
	}
	
	@Override
	public void activate() {
		createActionIcons();

		iconizeIcon.addActionListener(iconizeListener);
		editIcon.addActionListener(editListener);
		openListIcon.addActionListener(openListListener);
		hideActionIcons();
		super.activate();
	}
	
	@Override
	public void deactivate() {
		super.deactivate();
		editIcon.removeActionListener(editListener);
		iconizeIcon.removeActionListener(iconizeListener);
		openListIcon.removeActionListener(openListListener);
	}

	@Override
	protected void addChildVisual(EditPart childEditPart, int index) {
		IFigure child = ((GraphicalEditPart)childEditPart).getFigure();
		if (child instanceof CardFigure) {
			CardFigure card = (CardFigure) child;
			GraphicalEntity model = (GraphicalEntity) childEditPart.getModel();
			model.setDeletedVisuals(false);
			card.setRemoved(false);
		}
		getContentPane().add(child,child.getBounds(), -1);
	}
	
	@Override
	protected void removeChildVisual(EditPart childEditPart) {
		IFigure child = ((GraphicalEditPart)childEditPart).getFigure();
		if (child instanceof CardFigure) {
			CardFigure card = (CardFigure) child;
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
		if(Platform.isRunning() && !laneModel.isIconized()){
			Card[] cards = laneModel.getCards();
			KanbanUIStatusHandler.info("LaneEditPart.getModelChildren() lane:'%s' length:'%d'",laneModel.getStatus(),cards.length);
			return Arrays.asList(cards);
		}
		return super.getModelChildren();
	}
	
	@Override
	public IFigure getContentPane() {
		return getLaneFigure().getCardArea();
	}
	
	LaneFigure getLaneFigure(){
		return laneFigure;
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


	public void propertyChange(PropertyChangeEvent evt) {
		GraphicalEditPart parentPart = (GraphicalEditPart) getParent();
		if(isPropConstraint(evt)){
			Lane lane = getLaneModel();
			Rectangle constraint;
			if(lane.isIconized()){
				constraint = new Rectangle(lane.getX(),lane.getY(),72,72);
			}else{
				constraint = new Rectangle(lane.getX(),lane.getY(),lane.getWidth(),lane.getHeight());
			}
			parentPart.setLayoutConstraint(this, getFigure(), constraint);
		}
		else if(isPropStatus(evt)){
			Lane lane = getLaneModel();
			getLaneFigure().setStatus(lane.getStatus());
			getLaneIconFigure().setStatus(lane.getStatus());
		}
		else if(isChildrenChanged(evt)){
			if(!getLaneModel().isIconized()){
				super.propertyChange(evt);
			}
			IProgressService service = (IProgressService) PlatformUI.getWorkbench().getService(IProgressService.class);
			IRunnableContext context = new ProgressMonitorDialog(getShell());
			try {
				service.runInUI(context,new ScriptExcecuteWithProgress(evt),null);
			} catch (InvocationTargetException ex) {
				KanbanUIStatusHandler.fail(ex.getTargetException(), "Moving card is failed.");
			} catch (InterruptedException ex) {
				KanbanUIStatusHandler.fail(ex, "Moving Card is failed.");
			}
		}
		else if(isPropIconized(evt)){
			IFigure parent = getFigure().getParent();
			removeNotify();
			parent.remove(getFigure());
			Lane lane = getLaneModel();
			getContentPane().getChildren().clear();
			if(lane.isIconized()){
				setFigure(laneIconFigure);
				laneIconFigure.setLocation(laneFigure.getLocation());
			}else{
				setFigure(laneFigure);
				laneFigure.setLocation(laneIconFigure.getLocation());
			}
			parent.add(getFigure());
			getBoardModel().setAnimated(false);
			addNotify();
			getBoardModel().setAnimated(true);
		}
	}


	private void executeScript(PropertyChangeEvent evt, IProgressMonitor monitor) {
		Lane lane = getLaneModel();
		String script = lane.getScript();
		if(Lane.PROP_CARD.equals(evt.getPropertyName()) && script != null){
			try{
				Card card = (Card) (evt.getNewValue() != null? evt.getNewValue():evt.getOldValue());
				BSFManager manager = new BSFManager();
				manager.registerBean("card", card);
				manager.registerBean("lane", lane);
				manager.registerBean("event", new ScriptEvent(evt));
				manager.registerBean("monitor", monitor);
				manager.registerBean("RED", ColorTypes.RED);
				manager.registerBean("YELLOW",ColorTypes.YELLOW);
				manager.registerBean("GREEN",ColorTypes.GREEN);
				manager.registerBean("LIGHT_GREEN",ColorTypes.LIGHT_GREEN);
				manager.registerBean("LIGHT_BLUE",ColorTypes.LIGHT_BLUE);
				manager.registerBean("BLUE",ColorTypes.BLUE);
				manager.registerBean("PURPLE",ColorTypes.PURPLE);
				manager.registerBean("RED_PURPLE",ColorTypes.RED_PURPLE);

				
				String scriptName = String.format("Lane '%s' Script",lane.getStatus());
				String templateName = null;
				switch (lane.getScriptType()){
				case JavaScript:
					templateName = "init.js";
					break;
				case JRuby:
					templateName = "init.rb";
					break;
				default:
					String message = String.format("Lane's script type is null.Please check the data. id='%d'",lane.getID());
					throw new IllegalStateException(message);
				}
				InputStreamReader reader = new InputStreamReader(getClass().getResource(templateName).openStream());
				script = IOUtils.getStringFromReader(reader) + script;
				manager.exec(lane.getScriptType().getType(), scriptName, -12, 0, script);
			} catch (BSFException e) {
				KanbanUIStatusHandler.fail(e.getTargetException(), String.format("An Error is occured from lane's script. '%s'",e.getTargetException().getMessage()));
			} catch (IOException e) {
				KanbanUIStatusHandler.fail(e, "An Error is occured when reading template script.");
			}
		}
	}
	
	@Override
	public IFigure getFigure() {
		return super.getFigure();
	}
		
	private boolean isPropIconized(PropertyChangeEvent evt) {
		return Lane.PROP_ICONIZED.equals(evt.getPropertyName());
	}

	private boolean isPropStatus(PropertyChangeEvent evt) {
		return Lane.PROP_STATUS.equals(evt.getPropertyName());
	}

	private boolean isChildrenChanged(PropertyChangeEvent evt) {
		KanbanUIStatusHandler.debug("LaneEditPart.isChildrenChanged() name:" + evt.getPropertyName());
		return Lane.PROP_CARD.equals(evt.getPropertyName());
	}

	private boolean isPropConstraint(PropertyChangeEvent evt) {
		return Lane.PROP_CONSTRAINT.equals(evt.getPropertyName());
	}	
	
	Lane getLaneModel(){
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
	
}
