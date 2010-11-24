package org.kompiro.jamcircle.scripting.ui.internal.job;


public class InstallGemJob extends GemBaseJob {

	@Override
	protected String getCommand() {
		return "install";
	}

}
