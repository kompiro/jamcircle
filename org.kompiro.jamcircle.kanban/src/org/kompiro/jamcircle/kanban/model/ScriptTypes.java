package org.kompiro.jamcircle.kanban.model;

public enum ScriptTypes {
	JavaScript("javascript"),JRuby("ruby");
	
	private String type;
	
	private ScriptTypes(String type){
		this.type = type;
	}
	
	public String getType(){
		return this.type;
	}
}
