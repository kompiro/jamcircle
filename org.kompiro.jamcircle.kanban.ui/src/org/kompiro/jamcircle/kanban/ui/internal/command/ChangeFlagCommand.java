package org.kompiro.jamcircle.kanban.ui.internal.command;

import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.FlagTypes;
import org.kompiro.jamcircle.kanban.ui.command.AbstractCommand;


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
		if(card != null && type != null){
			this.oldType = card.getFlagType();
			setExecute(true);
		}
	}

}
