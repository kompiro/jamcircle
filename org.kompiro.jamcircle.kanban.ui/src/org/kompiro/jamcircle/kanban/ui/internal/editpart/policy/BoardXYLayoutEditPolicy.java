package org.kompiro.jamcircle.kanban.ui.internal.editpart.policy;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.*;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.GroupRequest;
import org.kompiro.jamcircle.kanban.model.*;
import org.kompiro.jamcircle.kanban.ui.command.DeleteCommand;
import org.kompiro.jamcircle.kanban.ui.command.MoveCommand;
import org.kompiro.jamcircle.kanban.ui.editpart.IconEditPart;
import org.kompiro.jamcircle.kanban.ui.internal.command.*;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.*;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;

public class BoardXYLayoutEditPolicy extends XYLayoutEditPolicy {
	
	private BoardEditPart part;
	
	private CardAreaCalcurator calc = new CardAreaCalcurator();

	public BoardXYLayoutEditPolicy(BoardEditPart part){
		this.part = part;
	}

	@Override
	protected Command createChangeConstraintCommand(EditPart child,
			Object constraint) {
		if (child.getParent() !=part
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
			CompoundCommand compoundCommand = new CompoundCommand();
			compoundCommand.add(command);
			if (child instanceof LaneEditPart && !lane.isIconized()) {
				LaneEditPart part = (LaneEditPart) child;
				calc.calc(part,rect,part.getViewer().getVisualPartMap(),compoundCommand);
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
	protected EditPolicy createChildEditPolicy(final EditPart child) {
		if (child instanceof LaneEditPart)
			return new ResizableEditPolicyFeedbackFigureExtension(child);
		return new NonResizableEditPolicyFeedbackFigureExtension(child);
	}

	@Override
	protected Command getCreateCommand(CreateRequest request) {
		Object object = request.getNewObject();
		if (object instanceof Card) {
			Card card = (Card) object;
			CreateCardCommand command = new CreateCardCommand();
			Object container = getHost().getModel();
			command.setContainer((CardContainer) container);
			command.setModel(card);
			return command;
		} else if (object instanceof Lane) {
			Lane card = (Lane) object;
			CreateLaneCommand command = new CreateLaneCommand();
			Object container = getHost().getModel();
			command.setContainer((BoardModel) container);
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
					command
							.add((Command) child
									.getAdapter(DeleteCommand.class));
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
		CompoundCommand command = new CompoundCommand();
		command.add(new AddCardToOnBoardContainerCommand(cardPart
				.getCardModel(), rect, getBoardModel()));
		return command;
	}

	private BoardModel getBoardModel() {
		return part.getBoardModel();
	}

	void setCalc(CardAreaCalcurator calc) {
		this.calc = calc;
	}
	
}
