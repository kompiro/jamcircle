package org.kompiro.jamcircle.scripting;

public enum ScriptTypes {
	JavaScript("javascript"),JRuby("ruby"); //$NON-NLS-1$ //$NON-NLS-2$
	
	private String type;
	
	private ScriptTypes(String type){
		this.type = type;
	}
	
	public String getType(){
		return this.type;
	}
}
