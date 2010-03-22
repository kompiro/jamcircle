package org.kompiro.jamcircle.kanban.ui.internal.editpart;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.*;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.ui.editpart.IBoardCommandExecuter;
import org.kompiro.jamcircle.storage.model.GraphicalEntity;

public class BoardCommandExecuter implements IBoardCommandExecuter {
	
	private final class GraphicalCreationFactory implements
			CreationFactory {
		private GraphicalEntity entity;

		private GraphicalCreationFactory(GraphicalEntity entity) {
			this.entity = entity;
		}

		public Object getObjectType() {
			return null;
		}

		public Object getNewObject() {
			return entity;
		}
	}

	private BoardEditPart part;

	public BoardCommandExecuter(BoardEditPart part){
		this.part = part;
	}

	public void add(Card card) {
		GraphicalCreationFactory factory = new GraphicalCreationFactory(card);
		executeAddCommand(factory);
	}
	
	public void add(Lane lane) {
		GraphicalCreationFactory factory = new GraphicalCreationFactory(lane);
		executeAddCommand(factory);
	}

	private void executeAddCommand(GraphicalCreationFactory factory) {
		CreateRequest request = new CreateRequest();
		request.setType(RequestConstants.REQ_CREATE);
		request.setFactory(factory);
		Command command = part.getCommand(request);
		part.getCommandStack().execute(command);
	}

	public void remove(Card card) {
		GroupRequest request = new GroupRequest(RequestConstants.REQ_ORPHAN_CHILDREN);
		EditPart target = null;
		List<CardEditPart> cardChildren = part.getCardChildren();
		for (CardEditPart cardEditPart : cardChildren) {
			if(card.equals(cardEditPart.getCardModel())){
				target = cardEditPart;
			}
		}
		if(target == null) return;
		request.setEditParts(target);
		Command command = part.getCommand(request);
		part.getCommandStack().execute(command);
	}

	public void remove(Lane lane) {
		GroupRequest request = new GroupRequest(RequestConstants.REQ_ORPHAN_CHILDREN);
		EditPart target = null;
		List<LaneEditPart> laneChildren = part.getLaneChildren();
		for (LaneEditPart laneEditPart : laneChildren) {
			if(lane.equals(laneEditPart.getLaneModel())){
				target = laneEditPart;
			}
		}
		if(target == null) return;
		request.setEditParts(target);
		Command command = part.getCommand(request);
		part.getCommandStack().execute(command);
	}


}
