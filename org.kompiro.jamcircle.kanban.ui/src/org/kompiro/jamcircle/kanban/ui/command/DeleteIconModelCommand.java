package org.kompiro.jamcircle.kanban.ui.command;

import org.kompiro.jamcircle.kanban.ui.model.BoardModel;
import org.kompiro.jamcircle.kanban.ui.model.IconModel;

public class DeleteIconModelCommand<Z extends IconModel> extends DeleteCommand<Z,BoardModel> {

	public DeleteIconModelCommand() {}

	@Override
	public void delete() {
		getContainer().removeIcon(getModel());
	}
	
	@Override
	public void undo() {
		getContainer().addIcon(getModel());
	}

	@Override
	protected void initialize() {
		if(getModel() != null && getContainer() != null)setExecute(true);
	}

}
