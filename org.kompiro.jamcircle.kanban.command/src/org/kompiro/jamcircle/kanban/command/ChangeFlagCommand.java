package org.kompiro.jamcircle.kanban.command;

import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.FlagTypes;


public class ChangeFlagCommand extends AbstractCommand {

	private Card card;
	private FlagTypes type;
	private FlagTypes oldType;
	
	public ChangeFlagCommand(Card card, FlagTypes type) {
		this.card = card;
		this.type = type;
	}

	@Override
	public void doExecute() {
		card.setFlagType(type);
		card.save(false);
	}
	
	@Override
	public void undo() {
		card.setFlagType(oldType);
		card.save(false);
	}

	@Override
	protected void initialize() {
		if(card != null){
			this.oldType = card.getFlagType();
			setExecute(true);
		}
	}

}
