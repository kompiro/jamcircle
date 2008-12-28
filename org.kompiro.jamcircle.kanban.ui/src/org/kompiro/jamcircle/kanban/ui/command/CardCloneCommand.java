package org.kompiro.jamcircle.kanban.ui.command;

import java.util.ArrayList;
import java.util.List;


import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.CardContainer;
import org.kompiro.jamcircle.kanban.model.User;
import org.kompiro.jamcircle.kanban.ui.gcontroller.CardEditPart;

public class CardCloneCommand extends AbstractCommand {

	private static final int MOVE_DELTA = 10;
	private static final int INIT = 10;
	private ChangeBoundsRequest request;
	private CardContainer target;
	private List<?> editParts;
	private List<Card> clonedObjects = new ArrayList<Card>();
	
	public CardCloneCommand(ChangeBoundsRequest request,CardContainer target) {
		this.request = request;
		this.target = target;
		this.editParts = request.getEditParts();
	}

	@Override
	public void doExecute() {
		int i = 0;
		for(Object obj:editParts){
			if (obj instanceof CardEditPart) {
				CardEditPart cardPart = (CardEditPart) obj;
				Card card = cardPart.getCardModel();
				Point location = request.getLocation();
				if(card.isTrashed()){
					card.setX(location.x + i * MOVE_DELTA - INIT);
					card.setY(location.y + i * MOVE_DELTA - INIT);
					card.setTrashed(false);
					target.addCard(card);
					clonedObjects.add(card);
					i++;
					continue;
				}
				int locationX = location.x + i * MOVE_DELTA - INIT;
				int locationY = location.y + i * MOVE_DELTA - INIT;
				User user = getUser();
				Card cloned = getKanbanService().createClonedCard(cardPart.getBoardModel().getBoard(),user , card, locationX, locationY);
				
				target.addCard(cloned);
				clonedObjects.add(cloned);
				i++;
			}
		}
	}
	
	private User getUser() {
		return getConnectionService().getCurrentUser();
	}

	@Override
	public void undo() {
		for(Card cloned : clonedObjects){
			cloned.setDeletedVisuals(true);
			target.removeCard(cloned);
		}
	}
	
	@Override
	public void redo() {
		for(Card cloned : clonedObjects){
			cloned.setDeletedVisuals(false);
			target.addCard(cloned);
		}
	}

}
