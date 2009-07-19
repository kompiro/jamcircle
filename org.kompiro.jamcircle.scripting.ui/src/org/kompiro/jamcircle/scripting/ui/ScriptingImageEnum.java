package org.kompiro.jamcircle.scripting.ui;

public enum ScriptingImageEnum {
	
	SCRIPT_GEAR("script_gear.png");
	
	private String path;
	private ScriptingImageEnum(String path){
		this.path = path;
	}

	public String getPath(){
		return "icons/" + this.path;
	}

}
