package org.kompiro.jamcircle.kanban.ui.command;

import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.FlagTypes;


public class ChangeFlagCommand extends AbstractCommand {

	private Card entity;
	private FlagTypes type;
	private FlagTypes oldType;
	
	public ChangeFlagCommand(Card entity, FlagTypes type) {
		this.entity = entity;
		this.oldType = entity.getFlagType();
		this.type = type;
	}

	@Override
	public void doExecute() {
		entity.setFlagType(type);
		entity.save();
	}
	
	@Override
	public void undo() {
		entity.setFlagType(oldType);
		entity.save();
	}

}
