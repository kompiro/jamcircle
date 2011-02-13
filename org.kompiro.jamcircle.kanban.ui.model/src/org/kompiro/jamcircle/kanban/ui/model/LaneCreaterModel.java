package org.kompiro.jamcircle.kanban.ui.model;

import org.kompiro.jamcircle.kanban.model.Icon;



public class LaneCreaterModel extends AbstractIconModel{
	public static final String NAME = Messages.LaneCreaterModel_name; 
	private static final long serialVersionUID = 6579717315112641292L;

	public LaneCreaterModel(Icon icon){
		super(icon);
	}

	public String getName() {
		return NAME;
	}

}
