package org.kompiro.jamcircle.scripting.ui.internal.ruby.job;

public class ListGemJob extends GemBaseJob {

	private static final String COMMAND_NAME = "list";

	@Override
	protected String getCommand() {
		return COMMAND_NAME;
	}

}
