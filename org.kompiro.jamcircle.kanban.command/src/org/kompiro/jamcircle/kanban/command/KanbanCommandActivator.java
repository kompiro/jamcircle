package org.kompiro.jamcircle.kanban.command;

import org.eclipse.core.runtime.Plugin;

public class KanbanCommandActivator extends Plugin {

	public static final String ID_PLUGIN = "org.kompiro.jamcircle.kanban.command";
	private static KanbanCommandStatusHandler plugin;

	public static KanbanCommandStatusHandler getDefault() {
		return plugin;
	}

}
