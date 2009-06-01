package org.kompiro.jamcircle.kanban.ui.gcontroller;

import java.beans.PropertyChangeEvent;
import java.util.*;

import org.eclipse.core.runtime.Platform;
import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.*;
import org.eclipse.gef.*;
import org.eclipse.gef.EditPartViewer.Conditional;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.*;
import org.eclipse.gef.requests.*;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.kompiro.jamcircle.kanban.model.*;
import org.kompiro.jamcircle.kanban.ui.KanbanImageConstants;
import org.kompiro.jamcircle.kanban.ui.KanbanUIStatusHandler;
import org.kompiro.jamcircle.kanban.ui.command.*;
import org.kompiro.jamcircle.kanban.ui.figure.*;
import org.kompiro.jamcircle.kanban.ui.figure.LaneFigure.CardArea;
import org.kompiro.jamcircle.kanban.ui.model.*;
import org.kompiro.jamcircle.storage.model.GraphicalEntity;

public class BoardEditPart extends AbstractEditPart implements CardContainerEditPart{
	public static final String TITLE_FONT_KEY = "org.kompiro.jamcircle.TITLE_FONT_KEY";

	private static final String LAYER_KEY_CARD = "card";
	private static final String LAYER_KEY_BOARD = "background";
	private static final String LAYER_KEY_BOARD_TITLE = "title";
	

	private final class CardLayer extends Layer {
		
		public CardLayer(){
			setLayoutManager(new FreeformLayout());
			setOpaque(false);
		}
		
		@Override
		protected boolean useLocalCoordinates() {
			return true;
		}
	}

	private final class IconLayer extends Layer {
		
		public IconLayer(){
			setLayoutManager(new FreeformLayout());
			setOpaque(false);
		}
		@Override
		protected boolean useLocalCoordinates() {
			return true;
		}

	}

	private final class BoardXYLayoutEditPolicy extends XYLayoutEditPolicy {
		@Override
		protected Command createChangeConstraintCommand(EditPart child, Object constraint) {
			if(child.getParent() != BoardEditPart.this && isNotRectangle(constraint)) return null;
			Object target = child.getModel();
			Rectangle rect = (Rectangle) constraint;
			if(target instanceof UserModel){
				UserModel user = (UserModel) target;
				MoveUserCommand command = new MoveUserCommand(user,rect);
				return command;
			}
			if(target instanceof Card){
				Card card = (Card)target;
				MoveCardCommand command = new MoveCardCommand(card,rect);
				return command;
			}
			if(target instanceof Lane){
				Lane lane = (Lane)target;
				CompoundCommand command = new CompoundCommand();
				command.add(new ChangeLaneConstraintCommand(lane,rect));
				if (child instanceof LaneEditPart && ! lane.isIconized()) {
					LaneEditPart part = (LaneEditPart) child;
					calculateCardArea(command ,rect, part);
				}
				return command;	
			}
			if(target instanceof IconModel){
				IconModel icon = (IconModel) target;
				MoveIconCommand command = new MoveIconCommand(icon,rect);
				return command;
			}
			return null;
		}

		private void calculateCardArea(CompoundCommand command, Rectangle rect,LaneEditPart part) {
			LaneFigure laneFigure = part.getLaneFigure();
			CardArea area = laneFigure.getCardArea();
			for(Object o:area.getChildren()){
				if (o instanceof CardFigure) {
					CardFigure cardFigure = (CardFigure) o;
					Dimension size = cardFigure.getSize();
					Point translate = cardFigure.getLocation().getCopy().translate(size);
					Rectangle localRect = rect.getCopy();
					localRect.setLocation(0, 0);
					if(!localRect.contains(translate)){
						ChangeBoundsRequest request = new ChangeBoundsRequest();
						request.setConstrainedMove(true);
						EditPart card = (EditPart) part.getViewer().getVisualPartMap().get(cardFigure);
						request.setEditParts(card);
						Point p = cardFigure.getLocation().getCopy();
						if(translate.x + size.width > localRect.width){
							p.x = laneFigure.getMaxCardLocationX(rect.getSize(),size); 
						}
						if(translate.y + size.height > localRect.height){
							p.y = laneFigure.getMaxCardLocationY(rect.getSize(),size); 
						}
						
						request.setMoveDelta(cardFigure.getLocation().translate(p.getNegated()).getNegated());
						request.setType(RequestConstants.REQ_RESIZE_CHILDREN);
						command.add(part.getCommand(request));
					}
				}
			}
		}
		
		private boolean isNotRectangle(Object constraint) {
			return !(constraint instanceof Rectangle);
		}

		@Override
		protected EditPolicy createChildEditPolicy(final EditPart child) {
			if(child instanceof LaneEditPart) return new ResizableEditPolicyFeedbackFigureExtension(child);
			return new NonResizableEditPolicyFeedbackFigureExtension(child);
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
			}else if(object instanceof Lane){
				Lane card = (Lane) object;
				CreateLaneCommand command = new CreateLaneCommand();
				Object container = getHost().getModel();
				command.setContainer((BoardModel)container);
				command.setModel(card);
				return command;				
			}
			return null;
		}
		
		@Override
		protected Command getOrphanChildrenCommand(Request request) {
			if(request instanceof GroupRequest){
				CompoundCommand command = new CompoundCommand();
				GroupRequest req = (GroupRequest) request;
				for(Object o : req.getEditParts()){
					if (o instanceof CardEditPart) {
						CardEditPart child = (CardEditPart) o;
						command.add(new RemoveCardCommand(child.getCardModel(),getBoardModel()));
					}else if(o instanceof LaneEditPart){
						LaneEditPart child = (LaneEditPart) o;
						command.add(new RemoveLaneCommand(child.getLaneModel(),getBoardModel()));
					}else if(o instanceof UserEditPart){
						UserEditPart child = (UserEditPart) o;
						command.add(new RemoveUserCommand(child.getUserModel(),getBoardModel()));
					}
				}
				return command;
			}
			return null;
		}
		
		@Override
		protected Command createAddCommand(EditPart child, Object constraint) {
			if (!(constraint instanceof Rectangle)) {
				return null;
			}
			Rectangle rect = (Rectangle) constraint;
			if(!(child instanceof CardEditPart)){
				return null;
			}
			CardEditPart cardPart = (CardEditPart) child;
			CompoundCommand command = new CompoundCommand();
			command.add(new AddCardToOnBoardContainerCommand(cardPart.getCardModel(), rect, getBoardModel()));
			return command;
		}

	}

	private Layer board;
	private Layer iconLayer;
	private Layer cardLayer;
	private LayeredPane wallboard;
	private ImageFigure backImageFigure;
	private Conditional userEditPartCondition;
	private Layer boardStatus;

	private Label boardTitle;
	
	public BoardEditPart(BoardModel board) {
		super(board);
		userEditPartCondition = new Conditional(){
			public boolean evaluate(EditPart editPart) {
				KanbanUIStatusHandler.debugUI("userEditPartCondition#evaluate '%s'",editPart.getClass().getName());
				return isIconEditPart(editPart);
			}
		};
	}
	
	@Override
	public void activate() {
		super.activate();
		getBoardModel().setAnimated(true);
	}
		
	
	@Override
	protected IFigure createFigure() {
		wallboard = new LayeredPane();
		wallboard.setOpaque(false);
		createBoardBackground();
		createStatusLayer();
		createIconLayer();
		createCardLayer();
		setPaintBackgroundListener();
		return wallboard;
	}

	private void setPaintBackgroundListener() {
		board.addLayoutListener(new LayoutListener.Stub(){
			public void postLayout(IFigure container) {
				backImageFigure.setBounds(container.getBounds());
			}
		});
	}

	private void createStatusLayer() {
		boardStatus = new Layer();
		GridLayout manager = new GridLayout();
		boardStatus.setLayoutManager(manager);
		boardTitle = new Label(){
			@Override
			public void paint(Graphics graphics) {
				graphics.setAlpha(128);
				super.paint(graphics);
			}
		};
		Font font = JFaceResources.getFontRegistry().get(TITLE_FONT_KEY);
		boardTitle.setFont(font);
		boardTitle.setText(getBoardModel().getBoard().getTitle());
		GridData constraint = new GridData();
		constraint.grabExcessHorizontalSpace = true;
		constraint.grabExcessVerticalSpace = true;
		constraint.horizontalAlignment = SWT.END;
		constraint.verticalAlignment = SWT.END;
		boardStatus.add(boardTitle,constraint);
		wallboard.add(boardStatus,LAYER_KEY_BOARD_TITLE, 1);
	}

	private void createBoardBackground() {
		board = new Layer();
		board.setLayoutManager(new FreeformLayout());
		board.setBorder(new LineBorder(ColorConstants.lightGray,1));
		Image backImage = getImageRegistry().get(KanbanImageConstants.BACKGROUND_IMAGE.toString());
		backImageFigure = new ImageFigure(backImage){
			@Override
			protected void paintFigure(Graphics graphics) {
				Image image = getImage();
				if(image == null) return;
				org.eclipse.swt.graphics.Rectangle rect = image.getBounds();
				for(int x = 0; x < getBounds().width; x = x + rect.width){
					for(int y = 0; y < getBounds().height; y = y + rect.height){
						graphics.drawImage(image, x, y);
					}
				}
			}
		};
		board.add(backImageFigure);
		wallboard.add(board,LAYER_KEY_BOARD,0);
	}

	private void createIconLayer() {
		iconLayer = new IconLayer();
		wallboard.add(iconLayer);
		
	}
	
	private void createCardLayer() {
		cardLayer = new CardLayer();
		wallboard.addLayerAfter(cardLayer,LAYER_KEY_CARD,LAYER_KEY_BOARD_TITLE);

	}
	
	@Override
	public IFigure getContentPane() {
		return board;
	}
	
	@Override
	public DragTracker getDragTracker(Request request) {
//		return new BoardMouseDragTracker(getKanbanService());
		return new BoardDragTracker(getKanbanService());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List getModelChildren() {
		BoardModel model = (BoardModel) getModel();
		return model.getChildren();
	}
	
	public List<CardEditPart> getCardChildren(){
		List<CardEditPart> results = new ArrayList<CardEditPart>();
		for(Object o : getChildren()){
			if(o instanceof CardEditPart) results.add((CardEditPart)o);
		}
		return results;
	}

	public Map<UserModel,UserEditPart> getUserChildren(){
		Map<UserModel,UserEditPart> results = new HashMap<UserModel,UserEditPart>();
		for(Object o : getChildren()){
			if(o instanceof UserEditPart){
				UserEditPart user = (UserEditPart)o;
				results.put(user.getUserModel(),user);
			}
		}
		return results;
	}

	
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new BoardXYLayoutEditPolicy());
		installEditPolicy(EditPolicy.CONTAINER_ROLE, new ContainerEditPolicy(){

			@Override
			protected Command getCreateCommand(CreateRequest request) {
				return null;
			}
			
			@Override
			protected Command getCloneCommand(ChangeBoundsRequest request) {
				return new CardCloneCommand(request,getBoardModel());
			}
			
		});
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		Object newValue = evt.getNewValue();
		Object oldValue = evt.getOldValue();
		KanbanUIStatusHandler.debug(String.format("BoardEditPart propertyChange %s newValue:'%s' oldValue:'%s'", evt.getPropertyName(),newValue,oldValue));
		super.propertyChange(evt);
		if(isPropUser(evt)){
			if(newValue != null){
				if(newValue instanceof Collection<?>){
					KanbanUIStatusHandler.debugUI("Locating Users '%s'",newValue);
					evaluateUserLocation((Collection<?>) newValue);
				}else if(newValue instanceof UserModel){
					KanbanUIStatusHandler.debugUI("Locating User '%s'",newValue);
					evaluateUserLocation((UserModel) newValue);
				}
			}
		}else if(isPropTitle(evt)){
			if(newValue != null){
				boardTitle.setText(newValue.toString());
			}
		}
	}

	private boolean isPropTitle(PropertyChangeEvent evt) {
		return Board.PROP_TITLE.equals(evt.getPropertyName());
	}

	private void evaluateUserLocation(Collection<?> users) {
		for(Object obj : users){
			if (obj instanceof UserModel) {
				UserModel user = (UserModel) obj;
				if( ! evaluateUserLocation(user)){
					continue;
				}
			}
		}
	}
	
	private boolean evaluateUserLocation(UserModel user){
		Map<UserModel, UserEditPart> userChildren = getUserChildren();
		UserEditPart userPart = userChildren.get(user);
		if(userPart == null){
			return false;
		}
		evaluateUserLocation(userPart);
		return true;
	}

	private void evaluateUserLocation(UserEditPart userPart){
		UserModel userModel = userPart.getUserModel();
		KanbanUIStatusHandler.debugUI("evaluateUserLocation '%s' location:'%s'.",userModel.getUserId(),userModel.getLocation());
		int column = 0;
		int row = 0;
		UserFigure userFigure = userPart.getUserFigure();
		Dimension iconSize = userFigure.getSize();
		Point translate = userModel.getLocation().getCopy().translate(10,10);
		while(findEditPart(translate) != null ){
			translate.translate(0,iconSize.height);
			row++;
			if(!wallboard.containsPoint(translate.getCopy())){
				KanbanUIStatusHandler.debugUI("move to next column");
				row = 0;
				column++;
				translate = new Point(iconSize.width * column, iconSize.height * row);
			}
		}
		translate = userModel.getLocation().getCopy().translate(iconSize.width * column, iconSize.height * row);
		Display display = getDisplay();
		final UserEditPart fUserPart = userPart;
		final Point fTranslate = translate;
		if(Platform.isRunning()){
			display.asyncExec(new Runnable() {
				public void run() {
					KanbanUIStatusHandler.debugUI("UserEditPart move to : '%s'", fTranslate);
					fUserPart.getUserModel().setLocation(fTranslate);
					fUserPart.refresh();
				}
			});
		}else{
			KanbanUIStatusHandler.debugUI("UserEditPart move to : '%s'", fTranslate);
			fUserPart.getUserModel().setLocation(fTranslate);
			fUserPart.refresh();
		}		
	}

	private Display getDisplay() {
		Control control = getViewer().getControl();
		if(control == null) throw new IllegalStateException();
		return control.getDisplay();
	}

	private GraphicalEditPart findEditPart(Point target) {
		EditPartViewer viewer = getViewer();
		HashSet<IFigure> exclusionSet = new HashSet<IFigure>();
		exclusionSet.add(this.getFigure());
		exclusionSet.add(((GraphicalEditPart)viewer.getRootEditPart()).getFigure());
		GraphicalEditPart editPart = (GraphicalEditPart)viewer.findObjectAtExcluding(target, exclusionSet, userEditPartCondition);
		if(editPart != null){
			KanbanUIStatusHandler.debugUI("found EditPart '%s'",editPart.getClass().getName());
		}
		if(editPart instanceof BoardEditPart || editPart instanceof RootEditPart) return null;
		return editPart;
	}
		
	private boolean isPropUser(PropertyChangeEvent evt) {
		return BoardModel.PROP_USER.equals(evt.getPropertyName());
	}

	@Override
	protected void addChildVisual(EditPart childEditPart, int index) {
		if(childEditPart instanceof IconEditPart){
			IFigure child = ((GraphicalEditPart)childEditPart).getFigure();
			iconLayer.add(child);
		}else{
			IFigure child = ((GraphicalEditPart)childEditPart).getFigure();
			Layer layer = wallboard.getLayer(LAYER_KEY_CARD);
			layer.add(child,child.getBounds(),-1);
		}
	}
		
	@Override
	protected void removeChildVisual(EditPart childEditPart) {
		IFigure child = ((GraphicalEditPart)childEditPart).getFigure();
		// Tips : for Animation 
		Layer layer = wallboard.getLayer(LAYER_KEY_CARD);
		if (child instanceof CardFigure) {
			CardFigure card = (CardFigure) child;
			GraphicalEntity model = (GraphicalEntity) childEditPart.getModel();
			if(model.isDeletedVisuals()){
				card.setRemoved(true);
				card.repaint();
			}
			if(!child.isShowing()){
				layer.remove(child);
			}
		}
		else if(childEditPart instanceof IconEditPart){
			iconLayer.remove(child);
		}
		else{
			layer.remove(child);
		}
	}

	public BoardModel getBoardModel() {
		return (BoardModel) getModel();
	}
	
	private boolean isIconEditPart(EditPart editpart) {
		return (editpart instanceof IconEditPart);
	}

	public CardContainer getCardContainer() {
		return getBoardModel();
	}
	
}
