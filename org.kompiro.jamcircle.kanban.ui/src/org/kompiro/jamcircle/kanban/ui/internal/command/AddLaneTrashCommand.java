package org.kompiro.jamcircle.kanban.ui.internal.command;

import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.ui.command.AbstractCommand;
import org.kompiro.jamcircle.kanban.ui.command.CancelableCommand;
import org.kompiro.jamcircle.kanban.ui.model.TrashModel;


public class AddLaneTrashCommand extends AbstractCommand implements CancelableCommand{

	private Lane lane;
	private TrashModel trash;
	private Card[] cards;

	public AddLaneTrashCommand(TrashModel trash, Lane lane) {
		this.trash = trash;
		this.lane = lane;
	}

	@Override
	public void doExecute() {
		
		cards = lane.getCards();
		for(Card card : cards){
			trash.addCard(card);
		}
		trash.addLane(lane);
	}
	
	@Override
	public void undo() {
		for(Card card : cards){
			trash.removeCard(card);
		}
		trash.removeLane(lane);
	}

	public String getComfirmMessage() {
		return String.format("Do you want to move %s to TrashBox?", lane.toString());
	}

}
