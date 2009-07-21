package org.kompiro.jamcircle.scripting.ui;

public enum ScriptingImageEnum {
	
	SCRIPT_GEAR("script_gear.png"),
	IMG_LCL_LOCK("full/clcl16/lock_co.gif"), 
	IMG_DLCL_LOCK("full/dlcl16/lock_co.gif"), 
	IMG_ELCL_LOCK("full/elcl16/lock_co.gif");
	
	private String path;
	private ScriptingImageEnum(String path){
		this.path = path;
	}

	public String getPath(){
		return "icons/" + this.path;
	}

}
