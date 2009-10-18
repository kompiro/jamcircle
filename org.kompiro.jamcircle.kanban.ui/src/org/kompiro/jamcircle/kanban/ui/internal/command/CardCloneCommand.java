package org.kompiro.jamcircle.kanban.ui.internal.command;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.kompiro.jamcircle.kanban.model.*;
import org.kompiro.jamcircle.kanban.ui.command.AbstractCommand;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.CardEditPart;

public class CardCloneCommand extends AbstractCommand {

	private static final int MOVE_DELTA = 10;
	private static final int INIT = 10;
	private ChangeBoundsRequest request;
	private CardContainer container;
	private List<Card> clonedObjects = new ArrayList<Card>();
	
	public CardCloneCommand(ChangeBoundsRequest request,CardContainer container) {
		this.request = request;
		this.container = container;
	}

	@Override
	public void doExecute() {
		int i = 0;
		for(Card card : clonedObjects){
			Point location = request.getLocation();
			int locationX = location.x + i * MOVE_DELTA - INIT;
			int locationY = location.y + i * MOVE_DELTA - INIT;
			Card targetCard;
			if(card.isTrashed()){
				card.setX(locationX);
				card.setY(locationY);
				card.setTrashed(false);
				targetCard = card;
			}else{
				User user = card.getFrom();
				targetCard = getKanbanService().createClonedCard(container.getBoard(),user , card, locationX, locationY);				
			}
			
			container.addCard(targetCard);
			i++;
		}
		setUndoable(true);
	}
	
	@Override
	public void undo() {
		for(Card cloned : clonedObjects){
			cloned.setDeletedVisuals(true);
			container.removeCard(cloned);
		}
	}
	
	@Override
	public void redo() {
		for(Card cloned : clonedObjects){
			cloned.setDeletedVisuals(false);
			container.addCard(cloned);
		}
	}

	@Override
	protected void initialize() {
		if(request == null || container == null) return;
		List<?> editParts = request.getEditParts();
		for(Object obj:editParts){
			if (obj instanceof CardEditPart) {
				CardEditPart cardPart = (CardEditPart) obj;
				Card card = cardPart.getCardModel();
				clonedObjects.add(card);
			}
		}
		setExecute(true);
	}

}
