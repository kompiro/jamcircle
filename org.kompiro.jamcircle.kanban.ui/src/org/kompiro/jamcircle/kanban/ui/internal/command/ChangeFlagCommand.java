package org.kompiro.jamcircle.kanban.ui.internal.command;

import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.FlagTypes;
import org.kompiro.jamcircle.kanban.ui.command.AbstractCommand;


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
		entity.save(false);
	}
	
	@Override
	public void undo() {
		entity.setFlagType(oldType);
		entity.save(false);
	}

}
