package org.kompiro.jamcircle.kanban.ui.internal.editpart;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.*;
import org.eclipse.gef.*;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.tools.SelectionTool;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.editpart.AbstractEditPart;
import org.kompiro.jamcircle.kanban.ui.internal.figure.LaneFigure;

/**
 * @deprecated now, it is more useful BoardDragTracker
 */
public class BoardMouseDragTracker extends SelectionTool implements DragTracker {

	private static final Dimension MIN_SIZE_TO_CREATE_LANE = new Dimension(100,100);
	public static final String PROPERTY_DRAG_TO_MOVE_VIEWPOINT = "move viewpoint";
	private static final int RIGHT_CLICK = 3;
	private Point startLocation;
	private Point viewLocation;
	private KanbanService service;
	private LaneFigure laneRectangleFigure;
	
	public BoardMouseDragTracker(KanbanService service) {
		this.service = service;
	}

	@Override
	protected boolean handleButtonDown(int button) {
		if (!isGraphicalViewer())
			return false;
		startLocation = getCurrentMouseLocation().getCopy();
		if (button != RIGHT_CLICK) {
			setState(STATE_INVALID);
			return handleInvalidInput();
		}
		if (stateTransition(STATE_INITIAL, STATE_DRAG)) {
			setDefaultCursor(Cursors.HAND);
			viewLocation = getViewLocation();
			dragModeOn();
		}
		return true;
	}


	private Point getCurrentMouseLocation() {
		return getCurrentInput().getMouseLocation();
	}
	
	@Override
	protected boolean handleDrag() {
		if (isInState(STATE_DRAG | STATE_DRAG_IN_PROGRESS) && isRightClick()) {
			if(isInState(STATE_DRAG)){
				stateTransition(STATE_DRAG, STATE_DRAG_IN_PROGRESS);
			}
			Dimension difference = getDifference();
			final Point translated = viewLocation.getTranslated(difference.negate());
			getViewport().setViewLocation(translated);
		}else{
			showLaneFeedback();
		}
		return super.handleDrag();
	}

	private Dimension getDifference() {
		Point mouseLocation = getCurrentMouseLocation();
		Dimension difference = mouseLocation.getDifference(startLocation);
		return difference;
	}

	private boolean isRightClick() {
		return getCurrentInput().isMouseButtonDown(RIGHT_CLICK);
	}

	private Viewport getViewport() {
		GraphicalEditPart rootEditPart = (GraphicalEditPart) getCurrentViewer()
				.getRootEditPart();
		Viewport port = (Viewport) rootEditPart.getFigure();
		return port;
	}

	@Override
	protected boolean handleButtonUp(final int button) {
		if(button != RIGHT_CLICK){
			performLaneCreate();
			eraseLaneFeedback();
		}else{
			Dimension difference = getDifference();
			if(difference.getArea() <= 4){
				getCurrentViewer().getControl().getMenu().setVisible(true);
			}
		}

		if (stateTransition(STATE_DRAG_IN_PROGRESS, STATE_TERMINAL)) {
		}
//		if(startLocation != null && startLocation.equals(getCurrentMouseLocation())){
//		}
		dragModeOff();
		handleFinished();
		return true;
	}

	private void performLaneCreate() {
		EditPart target = getTargetEditPart();
		if (target == null) {
			target = getCurrentViewer().findObjectAtExcluding(getLocation(),
					getExclusionSet(), getTargetingConditional());
			setTargetEditPart(target);
		}
		CreateRequest request = createLaneRequest();
		if(request == null) return;
		Request current = getTargetRequest();
		setTargetRequest(request);
		executeCommand(getCommand());
		showTargetFeedback();
		setTargetRequest(current);
	}
	
	@Override
	protected void handleFinished() {
		startLocation = null;
		viewLocation = null;
		laneRectangleFigure = null;
		super.handleFinished();
	}
	
	private CreateRequest createLaneRequest() {
		if(laneRectangleFigure == null || MIN_SIZE_TO_CREATE_LANE.contains(laneRectangleFigure.getSize())) return null;
		EditPart target = getTargetEditPart();
		if (!(target instanceof AbstractEditPart)) return null;
		AbstractEditPart part = (AbstractEditPart) target;
		final CreateRequest request = new LaneCreateRequest(service,part.getBoardModel().getBoard());
		Point location = startLocation.getCopy();
		while (target != null && !(target instanceof BoardEditPart)){
			target = target.getParent();
		}
		if(target == null) return null;
		location = location.getTranslated(getViewLocation());
		lockTargetEditPart(target);
		request.setLocation(location);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(LaneCreateRequest.EXPAND_DATA_KEY_SIZE, laneRectangleFigure.getSize());
		request.setExtendedData(map);
		return request;
	}

	@Override
	protected boolean handleDoubleClick(final int button) {
		if(button == RIGHT_CLICK) return false;
		EditPart target = getTargetEditPart();
		if (target == null) {
			target = getCurrentViewer().findObjectAtExcluding(getLocation(),
					getExclusionSet(), getTargetingConditional());
			setTargetEditPart(target);
		}
		final CreateRequest request = createCardRequest();
		final Request current = getTargetRequest();
		setTargetRequest(request);
		executeCommand(getCommand());
		showTargetFeedback();
		setTargetRequest(current);
		return true;
	}

	private CreateRequest createCardRequest() {
		final EditPart target = getTargetEditPart();
		if(target instanceof AbstractEditPart){
			AbstractEditPart part = (AbstractEditPart) target;
			final CreateRequest request = new CardCreateRequest(service,part.getBoardModel().getBoard());
			Point location = getLocation().getCopy();
			if (target instanceof GraphicalEditPart) {
				location = location.getTranslated(getViewLocation());
				lockTargetEditPart(target);
			}
			request.setLocation(location);
			return request;
		}
		return null;
	}

	private Point getViewLocation() {
		return getViewport().getViewLocation();
	}

	private boolean isGraphicalViewer() {
		return getCurrentViewer() instanceof GraphicalViewer;
	}
	
	private void showLaneFeedback() {
		Rectangle rect = getLaneSelectionRectangle().getCopy();
		getLaneFeedbackFigure().translateToRelative(rect);
		getLaneFeedbackFigure().setBounds(rect);
	}

	
	private Rectangle getLaneSelectionRectangle() {
		return new Rectangle(getStartLocation(), getLocation());
	}
	
	private IFigure getLaneFeedbackFigure() {
		if (laneRectangleFigure == null) {
			laneRectangleFigure = new LaneFigure();
			laneRectangleFigure.setCreating(true);
			laneRectangleFigure.setStatus("new status");
			addFeedback(laneRectangleFigure);
		}
		return laneRectangleFigure;
	}
	
	private void eraseLaneFeedback() {
		if (laneRectangleFigure != null) {
			removeFeedback(laneRectangleFigure);
			laneRectangleFigure = null;
		}
	}
	
	private void dragModeOn() {
		getCurrentViewer().setProperty(PROPERTY_DRAG_TO_MOVE_VIEWPOINT, new Object());
	}

	private void dragModeOff() {
		getCurrentViewer().setProperty(PROPERTY_DRAG_TO_MOVE_VIEWPOINT, null);
	}


}
