package org.kompiro.jamcircle.scripting.ui.internal.job;

public class UninstallGemJob extends GemBaseJob {

	private static final String COMMAND_NAME = "uninstall";

	@Override
	protected String getCommand() {
		return COMMAND_NAME;
	}

}
