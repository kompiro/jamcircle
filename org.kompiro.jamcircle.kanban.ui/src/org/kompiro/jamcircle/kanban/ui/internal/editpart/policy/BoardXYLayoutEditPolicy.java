package org.kompiro.jamcircle.kanban.ui.internal.editpart.policy;

import org.eclipse.draw2d.geometry.*;
import org.eclipse.gef.*;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.GroupRequest;
import org.kompiro.jamcircle.kanban.command.*;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.ui.editpart.IconEditPart;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.*;
import org.kompiro.jamcircle.kanban.ui.internal.figure.*;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;

public class BoardXYLayoutEditPolicy extends XYLayoutEditPolicy {

	private BoardEditPart part;

	private CardAreaCalcurator cardAreaCalcurator = new CardAreaCalcurator();

	private BoardLocalLayout boardLocalLayout;

	private StickyLaneLayout laneLayout;

	public BoardXYLayoutEditPolicy(BoardEditPart part) {
		this.part = part;
		this.boardLocalLayout = new BoardLocalLayout(part.getViewer());
		this.laneLayout = new StickyLaneLayout(part.getBoardModel().getBoard());
	}

	@Override
	protected Command createChangeConstraintCommand(EditPart child,
			Object constraint) {
		if (child.getParent() != part
				&& isNotRectangle(constraint)) {
			return null;
		}
		Object target = child.getModel();
		Rectangle rect = (Rectangle) constraint;
		MoveCommand<Object> command = getMoveCommand(child);
		command.setModel(target);
		command.setRectangle(rect);
		if (target instanceof Lane) {
			Lane lane = (Lane) target;
			Rectangle moved = laneLayout.rideOn(lane, rect);
			if (moved != null) {
				command.setRectangle(moved);
			}
			CompoundCommand compoundCommand = new CompoundCommand();
			compoundCommand.add(command);
			if (child instanceof LaneEditPart && !lane.isIconized()) {
				LaneEditPart part = (LaneEditPart) child;
				cardAreaCalcurator.calc(part, rect, part.getViewer().getVisualPartMap(), compoundCommand);
			}
			return compoundCommand;
		}
		return command;
	}

	@SuppressWarnings("unchecked")
	private MoveCommand<Object> getMoveCommand(EditPart child) {
		return (MoveCommand<Object>) child.getAdapter(MoveCommand.class);
	}

	private boolean isNotRectangle(Object constraint) {
		return !(constraint instanceof Rectangle);
	}

	@Override
	protected EditPolicy createChildEditPolicy(EditPart child) {
		return initializedPrimaryDragEditPolicy(child);
	}

	@SuppressWarnings("rawtypes")
	private EditPolicy initializedPrimaryDragEditPolicy(EditPart child) {
		if (child instanceof LaneEditPart
				&& ((LaneEditPart) child).getFigure() instanceof AnnotationArea
				&& ((AnnotationArea) ((LaneEditPart) child).getFigure()).getTargetFigure() instanceof LaneFigure)
			return new ResizableEditPolicyFeedbackFigureExtension(child);
		return new NonResizableEditPolicyFeedbackFigureExtension(child);
	}

	@Override
	protected Command getCreateCommand(CreateRequest request) {
		Object object = request.getNewObject();
		if (object instanceof Card) {
			Card card = (Card) object;
			Rectangle containerRect = getLayoutContainer().getBounds();
			Dimension cardSize = new Dimension(CardFigure.CARD_WIDTH, CardFigure.CARD_HEIGHT);

			Rectangle rect = new Rectangle(new Point(card.getX(), card.getY()), cardSize);
			boardLocalLayout.calc(rect, containerRect);
			card.setX(rect.x);
			card.setY(rect.y);
			CreateCardCommand command = new CreateCardCommand();
			Object container = getHost().getModel();
			command.setContainer((BoardModel) container);

			command.setModel(card);
			return command;
		} else if (object instanceof Lane) {
			Lane card = (Lane) object;
			CreateLaneCommand command = new CreateLaneCommand();
			command.setContainer(getBoardModel());
			command.setModel(card);
			return command;
		}
		return null;
	}

	@Override
	protected Command getOrphanChildrenCommand(Request request) {
		if (request instanceof GroupRequest) {
			CompoundCommand command = new CompoundCommand();
			GroupRequest req = (GroupRequest) request;
			for (Object o : req.getEditParts()) {
				if (o instanceof CardEditPart) {
					CardEditPart child = (CardEditPart) o;
					command.add(new RemoveCardCommand(child.getCardModel(),
							getBoardModel()));
				} else if (o instanceof LaneEditPart) {
					LaneEditPart child = (LaneEditPart) o;
					command.add(new RemoveLaneCommand(child.getLaneModel(),
							getBoardModel()));
				} else if (o instanceof IconEditPart) {
					EditPart child = (EditPart) o;
					Command deleteCommand = (Command) child.getAdapter(DeleteCommand.class);
					command.add(deleteCommand);
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
		if (!(child instanceof CardEditPart)) {
			return null;
		}
		CardEditPart cardPart = (CardEditPart) child;
		Rectangle containerRect = getLayoutContainer().getBounds();
		boardLocalLayout.calc(rect, containerRect);
		CompoundCommand command = new CompoundCommand();
		command.add(new AddCardToOnBoardContainerCommand(cardPart
				.getCardModel(), rect, getBoardModel()));
		return command;
	}

	private BoardModel getBoardModel() {
		return part.getBoardModel();
	}

	void setLaneLocalLayout(CardAreaCalcurator calc) {
		this.cardAreaCalcurator = calc;
	}

	void setBoardLocalLayout(BoardLocalLayout boardLocalLayout) {
		this.boardLocalLayout = boardLocalLayout;
	}

}
