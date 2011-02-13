package org.kompiro.jamcircle.kanban.command;

import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.ui.model.TrashModel;

public class AddLaneTrashCommand extends AbstractCommand implements CancelableCommand {

	private Lane lane;
	private TrashModel trash;
	private Card[] cards;

	public AddLaneTrashCommand(TrashModel trash, Lane lane) {
		this.trash = trash;
		this.lane = lane;
	}

	@Override
	public void doExecute() {
		for (Card card : cards) {
			trash.addCard(card);
		}
		trash.addLane(lane);
		setUndoable(true);
	}

	@Override
	public void undo() {
		for (Card card : cards) {
			trash.removeCard(card);
		}
		trash.removeLane(lane);
	}

	public String getComfirmMessage() {
		return String.format(Messages.AddLaneTrashCommand_confirm_message, lane.toString());
	}

	@Override
	protected void initialize() {
		if (lane != null && trash != null) {
			this.cards = lane.getCards();
			setExecute(true);
		}
	}

}
