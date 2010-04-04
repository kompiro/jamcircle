package org.kompiro.jamcircle.scripting.ui;

public enum ScriptingImageEnum {
	
	SCRIPT_GEAR("script_gear.png"), //$NON-NLS-1$
	IMG_LCL_LOCK("full/clcl16/lock_co.gif"),  //$NON-NLS-1$
	IMG_DLCL_LOCK("full/dlcl16/lock_co.gif"),  //$NON-NLS-1$
	IMG_ELCL_LOCK("full/elcl16/lock_co.gif"); //$NON-NLS-1$
	
	private String path;
	private ScriptingImageEnum(String path){
		this.path = path;
	}

	public String getPath(){
		return "icons/" + this.path; //$NON-NLS-1$
	}

}
