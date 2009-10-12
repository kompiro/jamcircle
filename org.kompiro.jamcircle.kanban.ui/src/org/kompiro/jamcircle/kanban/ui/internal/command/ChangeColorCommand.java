package org.kompiro.jamcircle.kanban.ui.internal.command;

import org.kompiro.jamcircle.kanban.model.ColorTypes;
import org.kompiro.jamcircle.kanban.model.HasColorTypeEntity;
import org.kompiro.jamcircle.kanban.ui.command.AbstractCommand;

public class ChangeColorCommand extends AbstractCommand {
	
	private HasColorTypeEntity entity;
	private ColorTypes oldType;
	private ColorTypes type;

	public ChangeColorCommand(HasColorTypeEntity entity,ColorTypes type){
		this.entity = entity;
		this.oldType = entity.getColorType();
		this.type = type;
	}

	@Override
	public void doExecute() {
		entity.setColorType(type);
		entity.save();
	}
	
	@Override
	public void undo() {
		entity.setColorType(oldType);
		entity.save();
	}

}
