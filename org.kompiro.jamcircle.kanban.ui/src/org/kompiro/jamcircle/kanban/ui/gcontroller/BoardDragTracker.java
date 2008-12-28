package org.kompiro.jamcircle.kanban.ui.gcontroller;

import java.util.Collection;
import java.util.Collections;


import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.tools.MarqueeDragTracker;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;

public class BoardDragTracker extends MarqueeDragTracker {

	public static final String PROPERTY_DRAG_TO_MOVE_VIEWPOINT = "move viewpoint";

	private static final int RIGHT_CLICK = 3;
	
	private KanbanService service;
	private Point startLocation;

	private Point viewLocation;

	public BoardDragTracker(KanbanService service){
		this.service = service;
	}
	
	@Override
	protected boolean handleButtonDown(int button) {
		if(!isGraphicalViewer()) return true;
		startLocation = getCurrentMouseLocation().getCopy();
		if (button == RIGHT_CLICK) {
			if (stateTransition(STATE_INITIAL, STATE_DRAG)) {
				setDefaultCursor(Cursors.HAND);
				viewLocation = getViewLocation();
				dragModeOn();
			}
			return true;
		}
		
		return super.handleButtonDown(button);
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
		}
		return super.handleDrag();
	}
	
	@Override
	protected boolean handleDragInProgress() {
		if(isRightClick()) return true;
		return super.handleDragInProgress();
	}
	
	@Override
	protected boolean handleButtonUp(int button) {
		if(button == RIGHT_CLICK){
			Dimension difference = getDifference();
			if(difference.getArea() <= 4){
				getCurrentViewer().getControl().getMenu().setVisible(true);
			}
			if (stateTransition(STATE_DRAG_IN_PROGRESS, STATE_TERMINAL)) {
			}
			dragModeOff();
			handleFinished();
			return true;
		}
		
		return super.handleButtonUp(button);
	}
	
	@Override
	protected void handleFinished() {
		startLocation = null;
		viewLocation = null;
		super.handleFinished();
	}

	@Override
	protected boolean handleDoubleClick(final int button) {
		if(button == RIGHT_CLICK) return false;
		EditPart target = getCurrentViewer().findObjectAtExcluding(getLocation(),
					getExclusionSet(), getTargetingConditional());
		final CreateRequest request = createCardRequest(target);
		executeCommand(getCommand(target,request));
		target.showTargetFeedback(request);
		return true;
	}
	
	
	private Command getCommand(EditPart target, CreateRequest request){
		return target.getCommand(request);
	}

	protected Collection<?> getExclusionSet() {
		return Collections.EMPTY_LIST;
	}

	protected EditPartViewer.Conditional getTargetingConditional() {
		return new EditPartViewer.Conditional() {
			public boolean evaluate(EditPart editpart) {
				return editpart.isSelectable();
			}
		};
	}
	
	private CreateRequest createCardRequest(EditPart target) {
		if (target instanceof AbstractEditPart) {
			AbstractEditPart boardTarget = (AbstractEditPart) target;
			BoardModel boardModel = boardTarget.getBoardModel();
			final CreateRequest request = new CardCreateRequest(service,boardModel.getBoard());
			Point location = getLocation().getCopy();
			if (target instanceof GraphicalEditPart) {
				location = location.getTranslated(getViewLocation());
			}
			request.setLocation(location);
			return request;
		}
		return null;
	}

	
	private Dimension getDifference() {
		Point mouseLocation = getCurrentMouseLocation();
		Dimension difference = mouseLocation.getDifference(startLocation);
		return difference;
	}

	private boolean isRightClick() {
		return getCurrentInput().isMouseButtonDown(RIGHT_CLICK);
	}

	
	private Point getCurrentMouseLocation() {
		return getCurrentInput().getMouseLocation();
	}
		
	private boolean isGraphicalViewer() {
		return getCurrentViewer() instanceof GraphicalViewer;
	}
	
	private Point getViewLocation() {
		return getViewport().getViewLocation();
	}
	
	private Viewport getViewport() {
		GraphicalEditPart rootEditPart = (GraphicalEditPart) getCurrentViewer()
				.getRootEditPart();
		Viewport port = (Viewport) rootEditPart.getFigure();
		return port;
	}

	private void dragModeOn() {
		getCurrentViewer().setProperty(PROPERTY_DRAG_TO_MOVE_VIEWPOINT, new Object());
	}

	private void dragModeOff() {
		getCurrentViewer().setProperty(PROPERTY_DRAG_TO_MOVE_VIEWPOINT, null);
	}

}
