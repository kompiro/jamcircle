package org.kompiro.jamcircle.kanban.ui.model;

import org.kompiro.jamcircle.kanban.model.Icon;

public class BoardSelecterModel extends AbstractIconModel{
	
	public static final String NAME = Messages.BoardSelecterModel_name;

	private static final long serialVersionUID = -7908942406062163577L;

	public BoardSelecterModel(Icon icon){
		super(icon);
	}

	public String getName() {
		return NAME;
	}
	
}
