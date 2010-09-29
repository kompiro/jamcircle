package org.kompiro.jamcircle.scripting.console.internal;

public enum ScriptingImageEnum {

	RUBY_GEAR("ruby_gear.png"); //$NON-NLS-1$

	private String path;

	private ScriptingImageEnum(String path) {
		this.path = path;
	}

	public String getPath() {
		return "icons/" + this.path; //$NON-NLS-1$
	}

}
