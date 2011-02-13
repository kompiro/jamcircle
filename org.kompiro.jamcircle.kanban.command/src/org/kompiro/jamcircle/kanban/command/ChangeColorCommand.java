package org.kompiro.jamcircle.kanban.command;

import org.kompiro.jamcircle.kanban.model.ColorTypes;
import org.kompiro.jamcircle.kanban.model.HasColorTypeEntity;

public class ChangeColorCommand extends AbstractCommand {
	
	private HasColorTypeEntity entity;
	private ColorTypes oldType;
	private ColorTypes type;

	public ChangeColorCommand(HasColorTypeEntity entity,ColorTypes type){
		this.entity = entity;
		this.type = type;
	}

	@Override
	public void doExecute() {
		entity.setColorType(type);
		entity.save(false);
		setUndoable(true);
	}
	
	@Override
	public void undo() {
		entity.setColorType(oldType);
		entity.save(false);
	}

	@Override
	protected void initialize() {
		if(entity != null && type != null){
			this.oldType = entity.getColorType();
			setExecute(true);
		}
	}

}
